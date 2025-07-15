#service/user_service/app/main.py
import os
import uuid
from typing import Annotated, Optional
from fastapi import FastAPI, File, HTTPException, Header, UploadFile, status, Response, Depends, Cookie
from fastapi.staticfiles import StaticFiles
from sqlmodel import SQLModel, select
from sqlmodel.ext.asyncio.session import AsyncSession
from redis.asyncio import Redis

from models import User, UserCreate, UserPublic, UserLogin, UserUpdate, UpdatePassword
from database import init_db, get_session
from auth import get_password_hash, create_session
from redis_client import get_redis
from auth import get_password_hash, create_session, verify_password, get_user_id_from_session, delete_session

# =========================== [FastAPI 앱 생성] ===========================
app = FastAPI(title="User Service")

STATIC_DIR = "/app/static"
PROFILE_IMAGE_DIR = f"{STATIC_DIR}/profiles"

os.makedirs(PROFILE_IMAGE_DIR, exist_ok=True)
app.mount("/static", StaticFiles(directory=STATIC_DIR), name="static")

def create_user_public(user: User) -> UserPublic:
    image_url = f"/static/profiles/{user.profile_image}" if user.profile_image else "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQOtu74pEiq7ofeQeTsco0migV16zZoBwSlGg&s"
    user_dict = user.model_dump()
    user_dict["profile_image"] = image_url
    return UserPublic.model_validate(user_dict)

async def get_current_user_id(
        session_id: Annotated[str | None, Cookie()] = None,
        redis: Annotated[Redis, Depends(get_redis)] = None,
) -> int:
    if not session_id:
        raise HTTPException(status_code=401, detail="Not authenticated")
    user_id_str = await get_user_id_from_session(redis, session_id)
    if not user_id_str:
        raise HTTPException(status_code=401, detail="Invalid session")
    return int(user_id_str)

# =========================== [앱 실행 시 DB 테이블 자동 생성] ===========================
@app.on_event("startup")
async def on_startup():
    await init_db()

# =========================== [헬스 체크 엔드포인트] ===========================
@app.get("/")
def health_check():
    return {"status": "User Service Running"}

# ===============================================================================
#                                회원가입 만들기
# ===============================================================================
@app.post('/api/auth/signup', response_model=UserPublic, status_code=status.HTTP_201_CREATED)
async def create_user(
    response: Response,
    user_data: UserCreate,
    session: Annotated[AsyncSession, Depends(get_session)],
    redis: Annotated[Redis, Depends(get_redis)]
):
    print(UserCreate)
    statement = select(User).where(User.email==user_data.email)
    exist_user_result = await session.exec(statement)

    if exist_user_result.one_or_none():
        raise HTTPException(status_code=status.HTTP_409_CONFLICT, detail="이미 사용중인 이메일입니다.")

    hashed_password = get_password_hash(user_data.password)
    new_user = User.model_validate(user_data, update={"hashed_password":hashed_password})

    session.add(new_user)
    await session.commit()
    await session.refresh(new_user)

    session_id = await create_session(redis, new_user.id)
    response.set_cookie(key="session_id", value=session_id, httponly=True, samesite="lax", max_age=3600, path="/")

    return create_user_public(new_user)


# ===============================================================================
#                                로그인 만들기
# ===============================================================================

@app.post("/api/auth/login")
async def login(
    response: Response,
    user_data: UserLogin,
    session: Annotated[AsyncSession, Depends(get_session)],
    redis: Annotated[Redis, Depends(get_redis)]
):
    # 1) 이메일로 사용자 조회
    result = await session.exec(select(User).where(User.email == user_data.email))
    user = result.one_or_none()
    if not user:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="등록된 계정이 없습니다.")

    if not verify_password(user_data.password, user.hashed_password):
       raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="비밀번호가 일치하지 않습니다.")


    session_id = await create_session(redis, user.id)
    response.set_cookie(key="session_id", value=session_id, httponly=True, samesite="lax", max_age=3600, path="/")
    return {"message": "로그인 성공!"}

@app.get("/api/auth/me", response_model=UserPublic)
async def get_current_user(
    response: Response,
    session: Annotated[AsyncSession, Depends(get_session)],
    redis: Annotated[Redis, Depends(get_redis)],
    session_id: Annotated[Optional[str], Cookie()] = None
):
    # 1) 세션 쿠키가 없으면 401에러 
    if not session_id:
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Not authenticated")

    # 2) Redis에서 user_id 조회
    user_id = await get_user_id_from_session(redis, session_id)
    if not user_id:
        response.delete_cookie("session_id", path="/")
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="User not found redis session")

    user = await session.get(User, int(user_id))
    if not user:
        response.delete_cookie("session_id", path="/")        
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="User not found DB")

    return create_user_public(user)

@app.post("/api/auth/logout")
async def logout(
    response: Response,
    redis: Annotated[Redis, Depends(get_redis)],
    session_id: Annotated[Optional[str], Cookie()] = None
):
    if session_id:
        await delete_session(redis, session_id)
    response.delete_cookie("session_id", path="/")
    return{"message": "로그아웃 성공"}

@app.get("/api/users/{user_id}", response_model=UserPublic)
async def get_user_by_id(
    user_id: int,
    session: Annotated[AsyncSession, Depends(get_session)]
):
    user = await session.get(User, user_id)
    if not user:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="User not found")
    
    return create_user_public(user)

#프로필 사진 업로드
@app.post("/api/users/me/upload_image", response_model=UserPublic)
async def upload_my_profile_image(
    session: Annotated[AsyncSession, Depends(get_session)],
    user_id: Annotated[int, Depends(get_current_user_id)],
    file: UploadFile = File(...)
):
    # 1) 사용자 조회
    db_user = await session.get(User, user_id)
    if not db_user:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="사용자가 없습니다.")
    
    # 2) 파일 저장 (예: static/profiles 디렉토리)
    file_extention = os.path.splitext(file.filename)[1]
    # UUID + 확장자 조합
    unique_filename = f"{uuid.uuid4()}{file_extention}"
    file_path = os.path.join(PROFILE_IMAGE_DIR, unique_filename)

    with open(file_path, "wb") as buffer:
        buffer.write(await file.read())
    
    db_user.profile_image = unique_filename
    await session.commit()
    await session.refresh(db_user)
    return create_user_public(db_user)

#프로필 수정
@app.patch("/api/users/me", response_model=UserPublic)
async def update_my_profile(
    user_data: UserUpdate,
    session: Annotated[AsyncSession, Depends(get_session)],
    user_id: Annotated[int, Depends(get_current_user_id)]
):
    db_user = await session.get(User, user_id)
    if not db_user:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="사용자가 없습니다.")
    
    update_data = user_data.model_dump(exclude_unset=True)
    for key, value in update_data.items():
        setattr(db_user, key, value)
    
    await session.commit()
    await session.refresh(db_user)
    return create_user_public(db_user)

#프로필 수정에서 비밀번호 변경
@app.post("/api/auth/chage_password", status_code=status.HTTP_204_NO_CONTENT)
async def change_password(
    passowrd_data: UpdatePassword,
    session: Annotated[AsyncSession, Depends(get_session)],
    redis: Annotated[Redis, Depends(get_redis)],
    user_id: Annotated[int, Depends(get_current_user_id)],
    session_id: Annotated[Optional[str], Cookie()] = None
):
    db_user = await session.get(User, user_id)
    if not db_user:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="사용자가 없습니다.")

    if not verify_password(passowrd_data.current_password, db_user.hashed_password):
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="비밀번호가 다릅니다.")
    
    db_user.hashed_password = get_password_hash(passowrd_data.new_password)
    await session.commit()

    if session_id:
        await delete_session(redis, session_id)
    
    return Response(status_code=status.HTTP_204_NO_CONTENT)

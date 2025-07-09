# ========================================================================================
#                                    [라이브러리 임포트]
# ========================================================================================
# ========================= [기본 라이브러리 및 의존성 임포트] =========================
from fastapi import FastAPI, Query, Depends, HTTPException, status, Request, Body
from typing import Optional, List

# ========================= [FastAPI 템플릿 및 응답 관련 임포트] =========================
from fastapi.templating import Jinja2Templates  # Jinja2 템플릿 렌더링용
from fastapi.responses import HTMLResponse, JSONResponse, RedirectResponse  # HTML/JSON 응답 반환용
# ========================= [SQLModel, SQLAlchemy 임포트] =========================
from sqlmodel import SQLModel, Field, create_engine, Session, select, Relationship
from sqlalchemy import Column, func
from sqlalchemy.sql.sqltypes import Text, Integer
from sqlalchemy.orm import selectinload
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.exc import OperationalError        

# ========================= [Pydantic BaseModel 임포트] =========================
from pydantic import BaseModel

# ========================= [데이터베이스 연결 및 의존성 함수 임포트] =========================
from database import engine, AsyncSessionLocal, get_session

# ========================= [로깅 설정] =========================
import logging

logger = logging.getLogger(__name__)
logger.setLevel(logging.INFO)
handler = logging.StreamHandler()
formatter = logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s')
handler.setFormatter(formatter)
if not logger.handlers:
    logger.addHandler(handler)

# ========================================================================================
#                                    [데이터베이스 모델 정의]
# ========================================================================================

# ------------------------ [Users 테이블 (SQLModel)] ------------------------
class Users(SQLModel, table=True):
    id: Optional[int] = Field(default=None, primary_key=True)
    username: str
    email: str
    # Profiles와의 1:1 관계 (profile은 Profiles 객체로 반환됨)
    profiles: Optional["Profiles"] = Relationship(
        back_populates="user",
        sa_relationship_kwargs={
            "primaryjoin":"Users.id == Profiles.user_id",
            "foreign_keys":"[Profiles.user_id]",
            "uselist":False,
            "cascade":"all, delete-orphan"
        }
    ) 
    posts: Optional["Posts"] = Relationship(
        back_populates="user",
        sa_relationship_kwargs={
            "primaryjoin":"Users.id == Posts.user_id",
            "foreign_keys":"[Posts.user_id]",
            "uselist":False,
            "cascade":"all, delete-orphan"
        }
    )

# ------------------------ [Profiles 테이블 (SQLModel)] ------------------------
class Profiles(SQLModel, table=True):
    user_id: Optional[int] = Field(default=None, primary_key=True)
    #user_id: Optional[int] = Field(sa_column=Column(Integer, primary_key=True, autoincrement=False))
    bio: Optional[str] = Field(default=None, sa_type=Text, nullable=True)
    phone: Optional[str] = Field(default=None, max_length=20, nullable=True)
    user:Optional["Users"] = Relationship(
        back_populates = "profiles",
        sa_relationship_kwargs={
            "primaryjoin":"Profiles.user_id == Users.id",
            "foreign_keys":"[Profiles.user_id]",
            "uselist":False
        }
    )

# ------------------------ [Posts 테이블 (SQLModel)] ------------------------
class Posts(SQLModel, table=True):
    id: Optional[int] = Field(default=None, primary_key=True)
    title: str = Field(default=None, max_length=100)
    content: Optional[str] = Field(default=None, sa_type=Text, nullable=True)
    user_id: Optional[int] = Field(default=None, nullable=True)
    cnt: Optional[int] = Field(default=0, ge=0)
    user:Optional["Users"] = Relationship(
        back_populates = "posts",
        sa_relationship_kwargs={
            "primaryjoin":"Posts.user_id == Users.id",
            "foreign_keys":"[Posts.user_id]",
        }
    )  

# ------------------------ [Users + Profiles JOIN 출력용] ------------------------
# User와 Profile 정보를 합쳐서 한 번에 리턴할 때 사용 (실제 테이블은 아님)
class UserProfile(SQLModel):
    id: int
    username: str
    email: str
    phone: Optional[str] = None
    bio: Optional[str] = None

# ========================================================================================
#                               [Users 입력 모델 정의(HTML)]
# ========================================================================================
# -------- [User 생성용 입력 모델] --------
class UserCreate(SQLModel):
    username: str
    email: str

# -------- [User 업데이트 모델] --------
class UserUpdate(SQLModel):
    username: Optional[str] = None
    email: Optional[str] = None

# ========================================================================================
#                               [Posts 입력 모델 정의(HTML)]
# ========================================================================================
# -------- [Post 생성용 입력 모델] --------
class PostCreate(SQLModel):
    title: str
    content: Optional[str] = None
    user_id: int


# -------- [Post 업데이트 모델] --------
class PostUpdate(SQLModel):
    title: str
    content: Optional[str] = None
    user_id: int

# ========================================================================================
#                                    [Pydantic 모델 (출력용)]
# ========================================================================================

# ------------------------ [UserRead (BaseModel)] ------------------------
class UserRead(BaseModel):
    id: int
    username: str
    email: str
    class Config:
        from_attributes = True  # SQLModel ORM 객체를 자동 변환

# ------------------------ [ProfileRead (BaseModel)] ------------------------
class ProfileRead(BaseModel):
    user_id: int
    bio: Optional[str] = None
    phone: Optional[str] = None
    class Config:
        from_attributes = True

# ------------------------ [PostRead (BaseModel)] ------------------------
class PostRead(BaseModel):
    id: int
    title: str
    content: Optional[str] = None
    user_id: Optional[int] = None
    cnt: Optional[int] = None
    class Config:
        from_attributes = True

# ========================================================================================
#                              [FastAPI 인스턴스 및 초기화]
# ========================================================================================

app = FastAPI()
templates = Jinja2Templates(directory="templates")
@app.on_event("startup")
async def on_startup():
    async with engine.begin() as conn:
        await conn.run_sync(SQLModel.metadata.create_all)

# ================================================================================================
#                          [ 주요 API: 생성(POST), 조회, 수정, 삭제 등 ]
# ================================================================================================

# ----------- [ 유저 생성(Create) : 비동기/AsyncSession 버전 ] -----------
@app.post("/users/", response_model=Users, status_code=status.HTTP_201_CREATED)
async def create_user(
    user: UserCreate,
    session: AsyncSession = Depends(get_session)
):
    db_user = Users.model_validate(user)
    try:
        session.add(db_user)
        await session.commit()
        await session.refresh(db_user)
        logger.info(f"사용자 명: {db_user.username}")
        # 회원 생성 후 바로 프로필 작성 페이지로 이동
        return {"result": "ok", "id": db_user.id}
    except Exception as e:
        await session.rollback()
        logger.info(f"생성오류: {e}")
        raise HTTPException(status_code=400, detail="사용자 추가 실패")

# ----------- [ 유저 업데이트(수정)(Update) : 비동기/AsyncSession 버전 ] -----------
@app.patch("/users/{user_id}",response_class=JSONResponse)
async def update_user(
    user_id: int,
    user_update: UserUpdate,
    session: AsyncSession=Depends(get_session)
):
    user = await session.get(Users, user_id)
    if not user:
        raise HTTPException(status_code=404, detail="User not Found")
    
    user_data = user_update.model_dump(exclude_unset=True)
    if not user_data:
        raise HTTPException(status_code=400, detail="업데이트할 칼럼이 없습니다.")

    # email이 수정되는 경우, 중복 체크
    if "email" in user_data:
        stmt = select(Users).where(Users.email == user_data["email"], Users.id != user_id)
        result = await session.execute(stmt)
        duplicate_user = result.scalars().first()
        if duplicate_user:
            raise HTTPException(status_code=400, detail="이미 사용 중인 이메일입니다.")

    for key, value in user_data.items():
        setattr(user, key, value)

    session.add(user)
    await session.commit()
    await session.refresh(user)
    return {"result": "ok", "user": user.username}

# ----------- [ 유저 삭제(Delete) : 비동기/AsyncSession 버전 ] -----------
@app.delete("/users/{user_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_user(
    user_id: int, 
    session: AsyncSession = Depends(get_session)
    ):
    user = await session.get(Users, user_id)
    if not user:
        raise HTTPException(status_code=404, detail="User not found")
    await session.delete(user)   # 이렇게 하면 profile도 같이 삭제
    await session.commit()
    return

# ----------- [ Post 수정 업데이트 페이지 렌더링 ] -----------
@app.patch("/posts/{id}")
async def update_post(
    id: int,
    post_update: PostUpdate,
    session: AsyncSession = Depends(get_session)
):
    post = await session.get(Posts, id)

    if not post:
        raise HTTPException(status_code=404, detail="Post not found")
    
    post.title = post_update.title
    post.content = post_update.content
    post.user_id = post_update.user_id  # 이 줄도 있어야 안전!

    session.add(post)
    await session.commit()
    await session.refresh(post)
    return {"result": "ok"}

# ----------- [ Post Delete 삭제 렌더링 ] -----------
@app.delete("/posts/{post_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_post(
    post_id: int, 
    session: AsyncSession = Depends(get_session)
    ):
    post = await session.get(Posts, post_id)
    if not post:
        raise HTTPException(status_code=404, detail="게시글이 존재하지 않습니다.")
    await session.delete(post)
    await session.commit()
    return

# ----------- [ 게시글 생성(Create) : 비동기/AsyncSession 버전 ] -----------
@app.post("/posts/", response_model=Posts, status_code=status.HTTP_201_CREATED)
async def create_post(
    post: PostCreate,
    session: AsyncSession = Depends(get_session)
):
    db_post = Posts.model_validate(post)
    try:
        session.add(db_post)
        await session.commit()
        await session.refresh(db_post)

        logger.info(f"게시글 제목: {db_post.title}, 작성자 ID: {db_post.user_id}")
        return db_post
    except Exception as e:
        await session.rollback()
        logger.info(f"게시글 생성 오류: {e}")
        raise HTTPException(status_code=400, detail="게시글 추가 실패")

# ----------- [ 프로필 생성(Create) : 비동기/AsyncSession 버전 ] -----------
@app.post("/profiles/", response_class=JSONResponse)
async def create_profile(
    data: dict = Body(...),
    session: AsyncSession = Depends(get_session)
):
    user_id = data.get("user_id")
    bio = data.get("bio", "")
    phone = data.get("phone", "")

    exist_profile = await session.get(Profiles, user_id)
    if exist_profile:
        raise HTTPException(status_code=400, detail="이미 프로필이 존재합니다.")

    user = await session.get(Users, user_id)
    if not user:
        raise HTTPException(status_code=404, detail="User not found.")

    new_profile = Profiles(user_id=user_id, bio=bio, phone=phone)
    session.add(new_profile)
    await session.commit()
    await session.refresh(new_profile)
    return {"result": "ok", "profile": {"user_id": user_id, "bio": bio, "phone": phone}}

# ----------- [ 프로필 수정(Create) : 비동기/AsyncSession 버전 ] -----------
@app.patch("/profiles/{user_id}", response_class=JSONResponse)
async def update_profile(
    user_id: int,
    data: dict = Body(...),
    session: AsyncSession = Depends(get_session)
):
    profile = await session.get(Profiles, user_id)
    if not profile:
        raise HTTPException(status_code=404, detail="Profile not found")
    profile.bio = data.get("bio", profile.bio)
    profile.phone = data.get("phone", profile.phone)
    session.add(profile)
    await session.commit()
    await session.refresh(profile)
    return {"result": "ok"}

# ================================================================================================
#                            [ HTML 템플릿 렌더링(GET) 엔드포인트 ]
# ================================================================================================

# ----------- [ Users 회원가입 페이지 렌더링 ] -----------
@app.get("/register", response_class=HTMLResponse)
async def get_register_page(
    request: Request
    ):
    return templates.TemplateResponse("register.html", {"request": request})


# ----------- [ Users 수정 페이지 렌더링 ] -----------
@app.get("/user_edit/{id}", response_class=HTMLResponse)
async def read_html_user(
    request: Request,
    id: int,
    session: AsyncSession = Depends(get_session)
    ):
    user = await session.get(Users, id)
    if not user:
        raise HTTPException(status_code=404, detail="User not found")
    return templates.TemplateResponse(
        "user_edit.html", #register1.html
        {
            "request": request,
            "user": user
        }
    )



# ----------- [ Users List 정보 페이지네이션 렌더링 ] -----------
@app.get("/user_list/", response_class=HTMLResponse)
async def read_user_list(
    request: Request, 
    page: int = 1,
    session: AsyncSession = Depends(get_session)
):
    size = 5  # 한 페이지당 보여줄 회원 수
    offset = (page - 1) * size

    # 전체 회원 수 구하기 (페이지네이션 위한 용도)
    total_count = await session.scalar(select(func.count()).select_from(Users))
    total_page = (total_count + size - 1) // size

    # 해당 페이지의 회원만 가져오기
    statement = select(Users).offset(offset).limit(size)
    result = await session.execute(statement)
    users = result.scalars().all()

    return templates.TemplateResponse(
        "user_list.html",
        {
            "request": request,
            "users": users,
            "page": page,
            "total_page": total_page
        }
    )

# ----------- [ Post list 정보 페이지네이션 렌더링 ] -----------
@app.get("/post_list/", response_class=HTMLResponse)
async def post_list(
    request: Request, 
    page: int = 1,
    session: AsyncSession = Depends(get_session)
):
    size = 10  # 한 페이지당 게시글 수
    offset = (page - 1) * size

    # 전체 게시글 수 구하기
    total_count = await session.scalar(select(func.count()).select_from(Posts))
    total_page = (total_count + size - 1) // size

    # 해당 페이지 게시글만 가져오기
    statement = (
        select(Posts, Users)
        .join(Users, Posts.user_id == Users.id)
        .order_by(Posts.id.asc())
        .offset(offset)
        .limit(size)
    )
    result = await session.execute(statement)
    post_user_list = result.all()

    return templates.TemplateResponse(
        "post_list.html",
        {
            "request": request,
            "post_user_list": post_user_list,
            "page": page,
            "total_page": total_page
        }
    )

# ----------- [ Post 세부 페이지 + 조회수 증가 렌더링 ] -----------
@app.get("/post_detail/{id}", response_class=HTMLResponse)
async def read_post_html(
    request: Request,
    id: int,
    session: AsyncSession = Depends(get_session)
):
    post = await session.get(Posts, id)
    if not post:
        raise HTTPException(status_code=404, detail="Post not found")
    
    # 여기서 조회수 1 증가
    post.cnt = (post.cnt or 0) + 1
    session.add(post)
    await session.commit()
    await session.refresh(post)

    # 작성자 이름도 넘김
    username = None
    if post.user_id:
        user = await session.get(Users, post.user_id)
        if user:
            username = user.username

    return templates.TemplateResponse(
        "post_detail.html",
        {
            "request": request,
            "post": post,
            "username": username
        }
    )

# ----------- [ Post 수정 페이지 렌더링 ] -----------
@app.get("/post_edit/{id}", response_class=HTMLResponse)
async def edit_post_html(
    request: Request,
    id: int,
    session: AsyncSession = Depends(get_session)
):
    post = await session.get(Posts, id)
    if not post:
        raise HTTPException(status_code=404, detail="Post not found")
    
    username = None
    if post.user_id:
        user = await session.get(Users, post.user_id)
        if user:
            username = user.username

    return templates.TemplateResponse(
        "post_edit.html",
        {
            "request": request,
            "post": post,
            "username": username
        }
    )

# ----------- [ Post 생성 페이지 렌더링 ] -----------
@app.get("/post_create", response_class=HTMLResponse)
async def post_create_page(
    request: Request, 
    session: AsyncSession = Depends(get_session)
    ):
    result = await session.execute(select(Users))
    users = result.scalars().all()
    return templates.TemplateResponse(
        "post_create.html", 
        {
            "request": request,
            "users": users
        }
    )

# ----------- [ Profile + User 정보 페이지 렌더링 ] -----------
@app.get("/profile/{user_id}", response_class=HTMLResponse)
async def get_profile_page(
    request: Request, 
    user_id: int,
    session: AsyncSession = Depends(get_session)
):
    # 프로필 정보
    profile = await session.get(Profiles, user_id)
    user = await session.get(Users, user_id)
    if not profile:
        return templates.TemplateResponse("profile_not_found.html", {"request": request, "user": user})
    if not user:
        raise HTTPException(status_code=404, detail="User not found")

    # 해당 유저의 게시글 목록 조회 (최근 글부터)
    statement = select(Posts).where(Posts.user_id == user_id).order_by(Posts.id.asc())
    result = await session.execute(statement)
    posts = result.scalars().all()

    return templates.TemplateResponse(
        "profile_user.html",
        {
            "request": request,
            "profile": profile,
            "user": user,
            "posts": posts,  # 작성글 목록 추가
        }
    )

# ----------- [ Profile 생성 페이지 렌더링 ] -----------
@app.get("/profile_create/{user_id}", response_class=HTMLResponse)
async def profile_create_page(
    request: Request,
    user_id: int,
    session: AsyncSession = Depends(get_session)
):
    # 이미 프로필이 있으면 생성 불가 (수정만 가능)
    profile = await session.get(Profiles, user_id)
    if profile:
        return RedirectResponse(f"/profile/{user_id}")    
    # 유저 정보 체크
    user = await session.get(Users, user_id)
    if not user:
        return templates.TemplateResponse("not_found.html", {"request": request, "message": "해당 유저가 존재하지 않습니다."})
    return templates.TemplateResponse(
        "profile_create.html",
        {
            "request": request,
            "user_id": user_id
        }
    )

# ----------- [ Profile 수정 페이지 렌더링 ] -----------
@app.get("/profile_edit/{user_id}", response_class=HTMLResponse)
async def profile_edit_page(
    request: Request,
    user_id: int,
    session: AsyncSession = Depends(get_session)
):
    profile = await session.get(Profiles, user_id)
    if not profile:
        raise HTTPException(status_code=404, detail="Profile not found")
    return templates.TemplateResponse(
        "profile_edit.html",
        {
        "request": request, 
        "profile": profile
        }
    )
# ./app/main.py
# 도커 초기 실행
# 터미널에서 폴더 경로 접속 후
# docker-compose up --build -d 실행
# 도커 실행 -> localhost:8000으로 들어가기
# docker-compose up --build -d
# 도커 중단
# docker-compose down
from fastapi import FastAPI, Query, Depends, HTTPException, status
from typing import Optional, List, Union
from sqlmodel import Session, select
# from app.database import create_db_tables, get_session



app = FastAPI()

@app.get("/")
async def read_root():
    return {"message":"Hellow everyone"}

# @app.get("/user")
# async def read_user():
#     return {"message":"User를 읽습니다."}

@app.get("/items/{item_id}")
# 유효성 검사
async def read_item(item_id:int):
    return{"item_id": item_id, "message": f"아이템 번호는 {item_id}입니다."}

@app.get("/user/{user_id}/items/{item_id}")
# 유효성 검사
async def read_item(user_id:int, item_id:int):
    return{"item_id": item_id, "user_id": user_id, "message": f"아이템 번호는 {item_id}이고 유저 아니디는 {user_id}입니다."}

@app.get("/user/me")
async def read_user_me():
    return {"user_id":"currnet_user", "message":"나야 나"}

@app.get("/user/{user_id}")
async def read_user_me(user_id:int):
    return {"user_id":"currnet_user", "message":f"{user_id}나야 나"}

#UUID 개념
# 중복될 가능성이 거의 없는 고유한 ID를 자동으로 만들어주는 규칙
# {"product_uuid":"123e4567-e89b-12d3-a456-426614174000","message":"product id by UUID"}
# 반드시 영어 소문자/대문자(a-f, A-F), 숫자(0-9), 그리고 하이픈(-)만 가능
import uuid
@app.get("/products/{product_uuid}")
async def get_product_by_uuid(product_uuid:uuid.UUID):
    return {"product_uuid": str(product_uuid), "message":"product id by UUID"}



# 옵션 주기 
@app.get("/product/")
async def read_products(q:str, skip:int):
    return{"q":f"{q}","skip":f"{skip}"}


# async def read_products(
#     # 실제 요청 예시 /product/?q=apple&short=true&skip=5&limit=20
#     # 문자열로 검색어를 입력 받음(선택사항)
#     q: Optional[str] = None, # 검색어(옵션)
#     # True 면 설명을 안 넣고, False면 description을 결과에 추가
#     short: bool = False, # 설명 포함 여부(옵션)
#     # 페이지네이션용 파라미터(보통 리스트에서 몇 개 건너뛸지, 몇 개 가져올지)
#     skip: int = 0, # 데이터를 몇 개 건너뛸지(옵션, 기본값 0)
#     limit: int = 10 # 몇 개까지 보여줄지(옵션, 기본값 10)
# ):
#     results = {"skip": skip, "limit": limit}
#     if q:
#         results.update({"q":q})
#     if not short:
#         results.update({"description":"dfkjslfjslkfj"})
#     return results


@app.get("/search_items/")
async def search_items(
    keyword:str = Query(..., min_length=3, max_length = 50, description="검색 키워드 3자에서 50자"),
    max_price: Optional[float] = Query(None, gt = 0, description ="최대 가격이 0보다 크거나 같아야 한다."),
    min_price: Optional[float] = Query(None, ge = 0, description ="최소 가격이 0보다 크거나 같아야 한다.")
):
    results = {"keyword":f"{keyword}"}
    if max_price is not None:
        results.update({"max_price":max_price})
    if min_price is not None:
        results.update({"min_price":min_price})
    return results

# ================================================================================================
#                                   [ Users API (SQLModel 직접 반환) ]
# ================================================================================================

# ----------- [단일 조회: SQLModel 직접 반환] -----------
@app.get("/users/{id}", response_model=Users)
def read_user(id: int):
    # 동기 세션을 열고 유저 단일 정보 조회 (없으면 404)
    with Session(engine) as session:
        user = session.get(Users, id)
        if not user:
            raise HTTPException(status_code=404, detail="User not found")
        return user

# ----------- [전체 조회: SQLModel 직접 반환] -----------
@app.get("/users/all/", response_model=List[Users])
def read_all_users():
    # 전체 유저 리스트 조회 (페이징X)
    with Session(engine) as session:
        statement = select(Users)
        users = session.exec(statement).all()
        return users

# ----------- [페이지네이션 조회: SQLModel 직접 반환] -----------
@app.get("/users/", response_model=List[Users])
def read_paging_users(page: int = Query(1, ge=1)):
    # 페이지 단위 유저 조회 (size=10)
    with Session(engine) as session:
        size = 10
        offset = (page-1)*size
        statement = select(Users).offset(offset).limit(size)
        users = session.exec(statement).all()
        return users

# ================================================================================================
#                                [ Profiles API (SQLModel 직접 반환) ]
# ================================================================================================

# ----------- [단일 조회: SQLModel 직접 반환] -----------
@app.get("/profiles/{user_id}", response_model=Profiles)
def read_profile(user_id: int):
    # 프로필 단일 조회 (없으면 404)
    with Session(engine) as session:
        profiles = session.get(Profiles, user_id)
        if not profiles:
            raise HTTPException(status_code=404, detail="Profile not found")
        return profiles

# ----------- [전체 조회: SQLModel 직접 반환] -----------
@app.get("/profiles/all/", response_model=List[Profiles])
def read_all_profile():
    # 전체 프로필 리스트 조회
    with Session(engine) as session:
        statement = select(Profiles)
        profiles = session.exec(statement).all()
        return profiles

# ----------- [페이지네이션 조회: SQLModel 직접 반환] -----------
@app.get("/profiles/", response_model=List[Profiles])
def read_paging_profile(page: int = Query(1, ge=1)):
    # 프로필 페이지네이션 조회
    with Session(engine) as session:
        size = 10
        offset = (page-1)*size
        statement = select(Profiles).offset(offset).limit(size)
        profiles = session.exec(statement).all()
        return profiles

# ================================================================================================
#                                  [ Posts API (SQLModel 직접 반환) ]
# ================================================================================================

# ----------- [단일 조회: SQLModel 직접 반환] -----------
@app.get("/posts/{id}", response_model=Posts)
def read_post(id: int):
    # 게시글 단일 조회 (없으면 404)
    with Session(engine) as session:
        posts = session.get(Posts, id)
        if not posts:
            raise HTTPException(status_code=404, detail="Posts not found")
        return posts

# ----------- [전체 조회: SQLModel 직접 반환] -----------
@app.get("/posts/all/", response_model=List[Posts])
def read_all_post():
    # 전체 게시글 리스트
    with Session(engine) as session:
        statement = select(Posts)
        posts = session.exec(statement).all()
        return posts

# ----------- [페이지네이션 조회: SQLModel 직접 반환] -----------
@app.get("/posts/", response_model=List[Posts])
def read_paging_posts(page: int = Query(1, ge=1)):
    # 게시글 페이지네이션 조회
    with Session(engine) as session:
        size = 10
        offset = (page-1)*size
        statement = select(Posts).offset(offset).limit(size)
        posts = session.exec(statement).all()
        return posts

# ================================================================================================
#                        -[ Users, Profiles Join API (SQLModel 직접 반환) ]
# ================================================================================================

# ----------- [단일 유저-프로필 조인 조회] -----------
@app.get("/users/profiles/{id}", response_model=UserProfile)
def read_user_profile(id: int):
    # Users와 Profiles를 조인해서 한 명의 유저와 해당 프로필 정보를 한 번에 반환
    with Session(engine) as session:
        statement = (
            select(Users, Profiles)
            .join(Profiles, Users.id == Profiles.user_id)
            .where(Users.id == id)
        )
        result = session.exec(statement).first()
        if not result:
            raise HTTPException(status_code=404, detail="Profile not found")
        user, profile = result
        # SQLModel → UserProfile DTO로 매핑
        return UserProfile(
            id=user.id,
            username=user.username,
            email=user.email,
            phone=profile.phone if profile else None,
            bio=profile.bio if profile else None
        )

# ----------- [페이지네이션 유저-프로필 조인 조회] -----------
@app.get("/users/profile/", response_model=List[UserProfile])
def read_paging_user_profile(page: int = Query(1, ge=1)):
    # 여러 명의 유저와 프로필을 JOIN해서 페이지네이션으로 반환
    with Session(engine) as session:
        size = 10
        offset = (page-1)*size
        statement = (
            select(Users, Profiles)
            .join(Profiles, Users.id == Profiles.user_id)
            .offset(offset).limit(size)
        )
        results = session.exec(statement).all()
        user_profiles_list = []
        for user, profile in results:
            # profile이 없을 수도 있으니 체크
            phone_data = profile.phone if profile else None
            bio_data = profile.bio if profile else None
            user_profiles_list.append(
                UserProfile(
                    id=user.id,
                    username=user.username,
                    email=user.email,
                    phone=phone_data,
                    bio=bio_data
                )
            )
        return user_profiles_list

# ================================================================================================
#                 [ SQLModel(동기) & Pydantic BaseModel(비동기) 반환 예시 ]
# ================================================================================================
# ================================================================================================
#                            [ Users: SQLModel vs Pydantic(BaseModel) ]
# ================================================================================================

# ========================= [ 단일 조회 ] =========================

# ----------- [ 동기 / SQLModel 반환 ] -----------
@app.get("/users/{id}", response_model=Users)
def read_user(id: int):
    with Session(engine) as session:
        user = session.get(Users, id)
        if not user:
            raise HTTPException(status_code=404, detail="User not found")
        return user

# ----------- [ 비동기 / Pydantic(BaseModel) 반환 ] -----------
@app.get("/users_basemodel/{id}", response_model=UserRead)
async def read_basemodel_user(
    id: int,
    session: AsyncSession = Depends(get_session)
    ):
    user = await session.get(Users, id)
    if not user:
        raise HTTPException(status_code=404, detail="User not found")
    return user

# ========================= [ 전체 조회 ] =========================

# ----------- [ 동기 / SQLModel 반환 ] -----------
@app.get("/users/all/", response_model=List[Users])
def read_all_users():
    with Session(engine) as session:
        statement = select(Users)
        users = session.exec(statement).all()
        return users

# ----------- [ 비동기 / Pydantic(BaseModel) 반환 ] -----------
@app.get("/users_basemodel/all/", response_model=List[UserRead])
async def read_all_basemodel_users(session: AsyncSession = Depends(get_session)):
    statement = select(Users)
    result = await session.execute(statement)
    users = result.scalars().all()
    return users

# ========================= [ 페이지네이션 조회 ] =========================

# ----------- [ 동기 / SQLModel 반환 ] -----------
@app.get("/users/", response_model=List[Users])
def read_paging_users(page: int = Query(1, ge=1)):
    with Session(engine) as session:
        size = 10
        offset = (page-1)*size
        statement = select(Users).offset(offset).limit(size)
        users = session.exec(statement).all()
        return users

# ----------- [ 비동기 / Pydantic(BaseModel) 반환 ] -----------
@app.get("/users_basemodel/", response_model=List[UserRead])
async def read_paging_basemodel_users(page: int = Query(1, ge=1), session: AsyncSession = Depends(get_session)):
    size = 10
    offset = (page-1)*size
    statement = select(Users).offset(offset).limit(size)
    result = await session.execute(statement)
    users = result.scalars().all()
    return users

# ================================================================================================
#                          [ Profiles: SQLModel vs Pydantic(BaseModel) ]
# ================================================================================================

# ========================= [ 단일 조회 ] =========================

# ----------- [ 동기 / SQLModel 반환 ] -----------
@app.get("/profiles/{user_id}", response_model=Profiles)
def read_profile(user_id: int):
    with Session(engine) as session:
        profiles = session.get(Profiles, user_id)
        if not profiles:
            raise HTTPException(status_code=404, detail="Profile not found")
        return profiles

# ----------- [ 비동기 / Pydantic(BaseModel) 반환 ] -----------
@app.get("/profiles_basemodel/{user_id}", response_model=ProfileRead)
async def read_basemodel_profile(
    user_id: int,
    session: AsyncSession = Depends(get_session)
):
    profiles = await session.get(Profiles, user_id)
    if not profiles:
        raise HTTPException(status_code=404, detail="Profile not found")
    return profiles

# ========================= [ 전체 조회 ] =========================

# ----------- [ 동기 / SQLModel 반환 ] -----------
@app.get("/profiles/all/", response_model=List[Profiles])
def read_all_profile():
    with Session(engine) as session:
        statement = select(Profiles)
        profiles = session.exec(statement).all()
        return profiles

# ----------- [ 비동기 / Pydantic(BaseModel) 반환 ] -----------
@app.get("/profiles_basemodel/all/", response_model=List[ProfileRead])
async def read_all_basemodel_profile(
    session: AsyncSession = Depends(get_session)
):
    statement = select(Profiles)
    result = await session.execute(statement)
    profiles = result.scalars().all()
    return profiles

# ========================= [ 페이지네이션 조회 ] =========================

# ----------- [ 동기 / SQLModel 반환 ] -----------
@app.get("/profiles/", response_model=List[Profiles])
def read_paging_profile(page: int = Query(1, ge=1)):
    # 프로필 페이지네이션 조회
    with Session(engine) as session:
        size = 10
        offset = (page-1)*size
        statement = select(Profiles).offset(offset).limit(size)
        profiles = session.exec(statement).all()
        return profiles

# ----------- [ 비동기 / Pydantic(BaseModel) 반환 ] -----------
@app.get("/profiles_basemodel/", response_model=List[ProfileRead])
async def read_paging_basemodel_profile(
    page: int = Query(1, ge=1),
    session: AsyncSession = Depends(get_session)
):
    size = 10
    offset = (page-1)*size
    statement = select(Profiles).offset(offset).limit(size)
    result = await session.execute(statement)
    profiles = result.scalars().all()
    return profiles

# ================================================================================================
#                           [ Posts: SQLModel vs Pydantic(BaseModel) ]
# ================================================================================================

# ========================= [ 단일 조회 ] =========================

# ----------- [ 동기 / SQLModel 반환 ] -----------
@app.get("/posts/{id}", response_model=Posts)
def read_post(id: int):
    with Session(engine) as session:
        posts = session.get(Posts, id)
        if not posts:
            raise HTTPException(status_code=404, detail="Posts not found")
        return posts

# ----------- [ 비동기 / Pydantic(BaseModel) 반환 ] -----------
@app.get("/posts_basemodel/{id}", response_model=PostRead)
async def read_basemodel_post(id: int, session: AsyncSession = Depends(get_session)):
    posts = await session.get(Posts, id)
    if not posts:
        raise HTTPException(status_code=404, detail="Posts not found")
    return posts

# ========================= [ 전체 조회 ] =========================

# ----------- [ 동기 / SQLModel 반환 ] -----------
@app.get("/posts/all/", response_model=List[Posts])
def read_all_post():
    with Session(engine) as session:
        statement = select(Posts)
        posts = session.exec(statement).all()
        return posts

# ----------- [ 비동기 / Pydantic(BaseModel) 반환 ] -----------
@app.get("/posts_basemodel/all/", response_model=List[PostRead])
async def read_all_basemodel_post(session: AsyncSession = Depends(get_session)):
    statement = select(Posts)
    result = await session.execute(statement)
    profiles = result.scalars().all()
    return profiles

# ========================= [ 페이지네이션 조회 ] =========================

# ----------- [ 동기 / SQLModel 반환 ] -----------
@app.get("/posts/", response_model=List[Posts])
def read_paging_posts(page: int = Query(1, ge=1)):
    # 게시글 페이지네이션 조회
    with Session(engine) as session:
        size = 10
        offset = (page-1)*size
        statement = select(Posts).offset(offset).limit(size)
        posts = session.exec(statement).all()
        return posts

# ----------- [ 비동기 / Pydantic(BaseModel) 반환 ] -----------
@app.get("/posts_basemodel/", response_model=List[PostRead])
async def read_paging_basemodel_posts(
    page: int = Query(1, ge=1),
    session: AsyncSession = Depends(get_session)
):
    size = 10
    offset = (page-1)*size
    statement = select(Posts).offset(offset).limit(size)
    result = await session.execute(statement)
    posts = result.scalars().all()
    return posts

# ================================================================================================
#                   [ Users + Profiles JOIN : SQLModel vs Pydantic(BaseModel) ]
# ================================================================================================

# ========================= [ 단일 조회 ] =========================

# ----------- [ 동기 / SQLModel 반환 ] -----------
@app.get("/users/profiles/{id}", response_model=UserProfile)
def read_user_profile(id: int):
    # Users와 Profiles를 조인해서 한 명의 유저와 해당 프로필 정보를 한 번에 반환
    with Session(engine) as session:
        statement = (
            select(Users, Profiles)
            .join(Profiles, Users.id == Profiles.user_id)
            .where(Users.id == id)
        )
        result = session.exec(statement).first()
        if not result:
            raise HTTPException(status_code=404, detail="Profile not found")
        user, profile = result
        # SQLModel → UserProfile DTO로 매핑
        return UserProfile(
            id=user.id,
            username=user.username,
            email=user.email,
            phone=profile.phone if profile else None,
            bio=profile.bio if profile else None
        )

# ----------- [ 비동기 / Pydantic(BaseModel) 반환 ] -----------
@app.get("/users/profiles/basemodel/{id}", response_model=UserProfileRead)
async def read_user_profile_basemodel(
    id: int,
    session: AsyncSession = Depends(get_session)
):
    statement = (
        select(Users, Profiles)
        .join(Profiles, Users.id == Profiles.user_id)
        .where(Users.id == id)
    )
    result = await session.execute(statement)
    row = result.first()
    if not row:
        raise HTTPException(status_code=404, detail="Profile not found")
    user, profile = row
    return UserProfileRead(
        id=user.id,
        username=user.username,
        email=user.email,
        profile=profile  # profile 전체를 반환 (Profiles 모델)
    )

# ========================= [ 페이지네이션 조회 ] =========================

# ----------- [ 동기 / SQLModel 반환 ] -----------
@app.get("/users/profile/", response_model=List[UserProfile])
def read_paging_user_profile(page: int = Query(1, ge=1)):
    # 여러 명의 유저와 프로필을 JOIN해서 페이지네이션으로 반환
    with Session(engine) as session:
        size = 10
        offset = (page-1)*size
        statement = (
            select(Users, Profiles)
            .join(Profiles, Users.id == Profiles.user_id)
            .offset(offset).limit(size)
        )
        results = session.exec(statement).all()
        user_profiles_list = []
        for user, profiles in results:
            # profile이 없을 수도 있으니 체크
            phone_data = profiles.phone if profiles else None
            bio_data = profiles.bio if profiles else None
            user_profiles_list.append(
                UserProfile(
                    id=user.id,
                    username=user.username,
                    email=user.email,
                    phone=phone_data,
                    bio=bio_data
                )
            )
        return user_profiles_list
    
# ----------- [ 비동기 / Pydantic(BaseModel) 반환 ] -----------
@app.get("/users/profile/basemodel/", response_model=List[UserProfileRead])
async def read_paging_user_profile_basemodel(
    page: int = Query(1, ge=1),
    session: AsyncSession = Depends(get_session)
):
    size = 10
    offset = (page-1) * size
    statement = (
        select(Users, Profiles)
        .join(Profiles, Users.id == Profiles.user_id)
        .offset(offset).limit(size)
    )
    results = await session.execute(statement)
    rows = results.all()
    user_profiles_list = []
    for user, profile in rows:
        user_profiles_list.append(
            UserProfileRead(
                id=user.id,
                username=user.username,
                email=user.email,
                profile=profile  # profile 전체를 넘김
            )
        )
    return user_profiles_list

# ================================================================================================
#                   [ Users + Post JOIN : SQLModel vs Pydantic(BaseModel) ]
# ================================================================================================

# ========================= [ 페이지네이션 조회 ] =========================
# ----------- [ 동기 / SQLModel 반환 ] -----------
@app.get("/users/posts/{user_id}/", response_model=Users)
def read_user_posts(user_id: int, page: int = Query(1, ge=1)):
    size = 10
    offset = (page - 1) * size

    with Session(engine) as session:
        user = session.get(Users, user_id)
        if not user:
            raise HTTPException(status_code=404, detail="사용자를 찾을 수 없습니다.")

        # posts 속성에 자동으로 Relationship된 Posts 목록이 들어감
        # SQLModel의 Relationship을 잘 지정했다면 아래처럼 user.posts로 접근 가능
        posts_stmt = (
            select(Posts)
            .where(Posts.user_id == user_id)
            .offset(offset)
            .limit(size)
        )
        posts = session.exec(posts_stmt).all()

        user.posts = posts

        return user

# ----------- [ 비동기 / Pydantic(BaseModel) 반환 ] -----------
@app.get("/users/posts/{user_id}/", response_model=UserPostRead)
async def read_user_posts(user_id: int, page: int = Query(1, ge=1), session: Session = Depends(get_session)):
    size = 10
    offset = (page - 1) * size

    user_stmt = select(Users).where(Users.id == user_id)
    user_result = await session.execute(user_stmt)
    user = user_result.scalar_one_or_none()
    if not user:
        raise HTTPException(status_code=404, detail="사용자를 찾을 수 없습니다.")

    posts_stmt = (
        select(Posts)
        .where(Posts.user_id == user_id)
        .offset(offset)
        .limit(size)
    )
    posts_result = await session.execute(posts_stmt)
    posts_list_from_db = posts_result.scalars().all()

    return UserPostRead(
        id=user.id,
        username=user.username,
        email=user.email,
        posts=[PostOutput.model_validate(post, from_attributes=True) for post in posts_list_from_db]  
    )




# ------------------------ [Users + Profiles JOIN (BaseModel)] ------------------------
class UserProfileRead(BaseModel):
    id: int
    username: str
    email: str
    profile: Optional[Profiles] = None
    class Config:
        from_attributes = True

class PostOutput(BaseModel):
    id: int
    title: str
    content: Optional[str] = None
    cnt: int
    class config:
        from_attributes:True

class UserPostRead(BaseModel):
    id: int
    username: str
    email: str
    posts: List[PostOutput]
    class Config:
        from_attributes = True

class UserCreate(SQLModel):
    username: str
    email: str


# ----------- [ Users List 정보 페이지 렌더링 ] -----------
# @app.get("/user_list/", response_class=HTMLResponse)
# async def read_user_list(
#     request: Request, 
#     session:AsyncSession = Depends(get_session)
#     ):
#     statement = select(Users)
#     result = await session.execute(statement)
#     users = result.scalars().all()
#     return templates.TemplateResponse(
#         "user_list.html",
#         {
#             "request": request,
#             "users": users
#         }
#     )


# ----------- [ Post list 정보 페이지 렌더링 ] -----------
# @app.get("/post_list/", response_class=HTMLResponse)
# async def post_list(
#     request: Request, 
#     session: AsyncSession = Depends(get_session)
# ):
#     statement = select(Posts, Users).join(Users, Posts.user_id == Users.id).order_by(Posts.id.asc())
#     result = await session.execute(statement)
#     post_user_list = result.all()
#     return templates.TemplateResponse(
#         "post_list.html",
#         {
#             "request": request,
#             "post_user_list": post_user_list
#         }
#     )



# ----------- [ Post 세부 페이지 렌더링 ] -----------
# @app.get("/post_detail/{id}", response_class=HTMLResponse)
# async def read_post_html(
#     request: Request,
#     id: int,
#     session: AsyncSession = Depends(get_session)
# ):
#     post = await session.get(Posts, id)
#     if not post:
#         raise HTTPException(status_code=404, detail="Post not found")
    
#     # 작성자 이름도 넘기고 싶으면!
#     username = None
#     if post.user_id:
#         user = await session.get(Users, post.user_id)
#         if user:
#             username = user.username

#     return templates.TemplateResponse(
#         "post_detail.html",
#         {
#             "request": request,
#             "post": post,
#             "username": username  # 템플릿에서 {{ username }} 으로 사용 가능
#         }
#     )

# serives/user_service/app/models.py
from typing import Optional
from sqlmodel import Field, SQLModel

# =========================== [User 테이블 모델 정의] ===========================
class User(SQLModel, table=True):
    id: Optional[int] = Field(default=None, primary_key=True)
    username: str               # 사용자 이름(닉네임)
    email: str                  # 이메일(아이디)
    hashed_password: str        # 해시된 비밀번호
    profile_image: Optional[str] = None
    bio: Optional[str] = None

class UserPublic(SQLModel):
    id: int
    username: str
    email: str
    profile_image: Optional[str] = None
    bio: Optional[str] = None

class UserCreate(SQLModel):
    username: str
    email: str
    password: str
    profile_image:Optional[str] = None
    bio: Optional[str] = None


class UserLogin(SQLModel):
    email: str
    password: str

class UserUpdate(SQLModel):
    username: Optional[str] = None
    profile_image: Optional[str] = None
    bio: Optional[str] = None

# 자바스크립트에서 보냄
class UpdatePassword(SQLModel):
    current_password: str
    new_password:str
# services/user_service/app/auth.py
# 인증 관련
import secrets
from typing import Optional
from passlib.context import CryptContext
from redis.asyncio import Redis

# redis로 뭘 불러오는데요
# 난수로 생성해서 redis에 넣어서 브라우저가 기억하게 한다음에 
pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")
SESSION_TTL_SECONDS = 3600

def get_password_hash(password:str) -> str:
    return pwd_context.hash(password)

async def create_session(redis: Redis, user_id:int) -> str:
    session_id = secrets.token_hex(16)
    await redis.setex(f"session:{session_id}", SESSION_TTL_SECONDS, user_id) # 3600초 후에 사라짐
    return session_id

async def delete_session(redis: Redis, session_id: str):
    await redis.delete(f"session:{session_id}")

async def verify_password(plain_password: str, hashed_password: str) -> bool:
    return pwd_context.verify(plain_password, hashed_password)

async def get_user_id_from_session(redis:Redis, session_id:str) -> Optional[int]:
    user_id = await redis.get(f"session:{session_id}")
    return user_id if user_id else None
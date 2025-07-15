# services/user_service/app/database.py
import os
from dotenv import load_dotenv
from sqlmodel import SQLModel
from sqlmodel.ext.asyncio.session import AsyncSession
from sqlalchemy.ext.asyncio import create_async_engine

# =========================== [비동기 DB 엔진 생성] ===========================
load_dotenv()
DATABASE_URL = os.getenv("DATABASE_URL")
engine = create_async_engine(
    DATABASE_URL,
    echo=True
)

# =========================== [테이블 자동 생성 함수] ===========================
async def init_db():
    async with engine.begin() as conn:
        await conn.run_sync(SQLModel.metadata.create_all)

# =========================== [세션 생성 의존성 함수] ===========================
async def get_session():
    async with AsyncSession(engine) as session:
        yield session

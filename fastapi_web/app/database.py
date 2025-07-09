#./database.py
# ======================= [DB 연결/세션 팩토리/의존성 함수 정의] =======================
from dotenv import load_dotenv
import os

# 동기 SQLModel 세션/엔진 (거의 안쓰거나 Alembic, 스크립트 등에서만 사용)
from sqlmodel import create_engine, Session as SQLModelSession

# 비동기 SQLAlchemy 엔진/세션/세션팩토리 (FastAPI에서는 이거만 거의 사용)
from sqlalchemy.ext.asyncio import create_async_engine, AsyncSession
from sqlalchemy.orm import sessionmaker


# .env 파일의 환경변수 로드 (DATABASE_URL 등)
load_dotenv()

# 데이터베이스 연결 문자열 로딩
DATABASE_URL = os.getenv("DATABASE_URL")

# 환경 변수 미설정시 예외 처리 (실무 필수)
if not DATABASE_URL:
    raise ValueError("DATABASE_URL 환경변수가 설정되지 않았습니다.")

# 비동기 데이터베이스 엔진 생성
# echo=True: SQL 쿼리를 콘솔에 출력(디버깅용)
engine = create_async_engine(DATABASE_URL, echo=True)

# 비동기 세션 팩토리
# 반드시 class_=AsyncSession 지정! (동기 Session 혼용 금지)
AsyncSessionLocal = sessionmaker(
    engine,
    class_=AsyncSession,
    expire_on_commit=False,  # 커밋 후 ORM 객체 상태 유지
    autocommit=False,
    autoflush=False
)

# FastAPI 의존성 주입용 비동기 세션 생성 함수
# (main.py에서 Depends(get_session)으로 사용)
async def get_session() -> AsyncSession:
    async with AsyncSessionLocal() as session:
        yield session


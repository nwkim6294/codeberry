# services/user_service/app/redis_client.py
import os
from dotenv import load_dotenv
import redis.asyncio as redis

# =========================== [Redis 클라이언트 생성] ===========================
load_dotenv()
REDIS_URL = os.getenv("REDIS_URL")
# Redis 연결 (비동기 클라이언트)
redis_client = redis.from_url(REDIS_URL, decode_responses=True)

# =========================== [FastAPI 의존성 함수] ===========================
# Redis 클라이언트를 의존성으로 주입
async def get_redis():
    yield redis_client

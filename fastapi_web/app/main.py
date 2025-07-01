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

#Dockerfile
# 파이썬 3.10 환경에서, FastAPI 프로젝트를 필요한 패키지까지 설치해서 8000포트로 실행하는 컨테이너
# 도커 이미지를 기반으로 사용
FROM python:3.10-slim-buster

# 작업 디렉토리를 /app으로 설정
# 이후 명령어들은 다 /app 폴더 기준으로 실행됨
WORKDIR /app

# ★★★ mysqlclient 빌드에 필요한 패키지 설치 ★★★
# mysql 클라이언트 라이브러리와 pkg-config 등 빌드 도구 설치
# apt-get update: 패키지 목록 업데이트
# apt-get install -y: 필요한 패키지 설치(-y는 확인 질문 없이 설치)
# default-libmysqlclient-dev : mysql 클라이언트 개발 파일(mysqlclient 빌드에 필요)
# gcc: C 컴파일러(파이썬 C 확장 모듈 빌드에 필요)
# pkg-config : 라이브러리 정보 찾기 도구(mysqlclient 빌드에 필요)
# python3-dev: 파이썬 개발 헤더 파일 (파이썬 C 확장 모듈 빌드에 필요)

RUN apt-get update && apt-get install -y default-libmysqlclient-dev gcc pkg-config python3-dev && rm -rf /var/lib/apt/lists/*

# requirements.txt 파일을 컨테이너의 /app/requirements.txt 위치로 복사
COPY requirements.txt /app/requirements.txt

# 복사한 requirements.txt에 있는 파이썬 패키지들을 설치하는 명령어
# 최신 버전으로 설치(--upgrade)하고, 설치 중에 생기는 임시파일은 저장 안 함
# (--no-cache-dir)
RUN pip install --no-cache-dir --upgrade -r /app/requirements.txt

# ./app 폴더(전체 파일)를 컨테이너의 /app 폴더로 복사
COPY ./app /app

# 컨테이너가 외부와 통신할 포트를 열겠다는 의미
# FastAPI는 기본적으로 8000포트 사용
EXPOSE 8000

# 컨테이너가 시작될 때 실행할 명령어를 지정
# uvicorn을 사용해서 FastAPI 서버를 실행
# main:app -> main.py 파일 안에 있는 app 객체 실행
# --host 0.0.0.0 -> 외부에서 접속 가능하게 모든 주소에서 요청 받기
# --port 8000 -> 8000번 포트에서 서버 열기
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]
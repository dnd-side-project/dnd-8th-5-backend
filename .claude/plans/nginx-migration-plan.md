# Nginx 설정 repo 포함 및 배포 시 빌드

## Context

커밋 `6cba993`에서 nginx 이미지를 빌드/push하면서 Docker Hub의 `modutime-nginx` 이미지가 덮어써졌고, 이후 revert(`131b749`)는 코드만 원복하고 Docker Hub 이미지는 복구하지 못했다. 현재 nginx 설정이 repo에 없어 코드와 Docker Hub 이미지가 분리되어 있으며, 롤백 시에도 nginx 설정이 같이 롤백되지 않는 문제가 있다.

**목표:** nginx 설정을 repo에 포함시켜 매 배포 시 web과 함께 빌드/push하도록 한다.

## 변경 파일

### 1. `nginx/nginx.conf` (신규 생성)

기본 리버스 프록시 설정 (HTTPS 리다이렉트 없음):

```nginx
events {
    worker_connections 1024;
}

http {
    server {
        listen 80;
        server_name _;

        location / {
            proxy_pass http://web:8080;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }
    }
}
```

### 2. `nginx/Dockerfile` (신규 생성)

```dockerfile
FROM nginx:1.27-alpine
COPY nginx.conf /etc/nginx/nginx.conf
```

### 3. `docker-compose.yml` 수정

nginx 서비스를 `image` 기반에서 `build` 기반으로 변경:

```yaml
nginx:
    container_name: nginx
    build:
      context: ./nginx
      dockerfile: Dockerfile
    image: ssssujini99/modutime-nginx  # build + image 둘 다 지정 → 로컬 빌드 후 태깅
    ports:
      - "80:80"
    depends_on:
      - web
    mem_limit: 64m
    restart: unless-stopped
```

### 4. `.github/workflows/deploy-prod.yml` 수정

web 이미지 빌드/push 다음에 nginx 빌드/push 단계 추가:

```yaml
      ## Nginx 이미지 빌드 및 도커허브에 push
      - name: nginx docker build and push
        run: |
          docker build -t ${{ secrets.DOCKER_REPO }}/modutime-nginx ./nginx
          docker push ${{ secrets.DOCKER_REPO }}/modutime-nginx
```

위치: `web docker build and push` 단계 바로 아래에 추가.

## 검증

1. 로컬에서 `docker-compose build nginx` 로 이미지 빌드 확인
2. 배포 후 EC2에서 `docker exec nginx cat /etc/nginx/nginx.conf` 로 설정 확인
3. `curl http://<EC2-IP>/aws` 로 200 응답 확인
4. NLB 타겟 그룹 health check 정상 확인

# AWS 인프라 비용 분석 및 최적화 방안

> 분석일: 2026-04-12

## 현재 인프라 구성

| 서비스 | 리소스 | 사양 | 상태 |
|---|---|---|---|
| EC2 | modutime-api | t2.micro | stopped |
| EC2 | modutime-green-deploy | t2.micro | running (2대) |
| RDS | modutime-db-dev-1 | db.t4g.micro, MySQL 8.0.44, 20GB gp2 | available |
| NLB | prod-modutime-nlb | network | active |
| Elastic IP | 2개 | 1개 연결(modutime-api), 1개 미사용 | - |
| Route 53 | modutime.site | 호스팅 존 1개 | active |

### DNS 구성

```
modutime.site      → CloudFront (프론트엔드)
www.modutime.site  → CloudFront (프론트엔드)
api2.modutime.site → NLB → EC2 (백엔드 API)
```

### 배포 방식

- GitHub Actions 블루-그린 배포 (`deploy-prod.yml`)
- Green EC2 임시 생성 → 배포 → NLB 타겟 전환 → Blue EC2 종료
- EC2는 항상 1대만 유지

## 비용 현황

### AWS 크레딧

| 항목 | 값 |
|---|---|
| 크레딧 종류 | AWS Activate - Founders |
| 총 크레딧 | $1,000 |
| 사용 | $947.29 |
| 잔액 | $52.71 |
| 만료일 | 2027.05.31 |
| 월 소비 | ~$50 |
| **소진 예상** | **2026년 5월 (약 1개월 후)** |

### 프리 티어

- 최초 리소스 생성일: 2024-03-15
- 프리 티어 만료: 2025-03-15 (이미 만료)
- 현재 비용이 $0인 이유: 크레딧으로 상쇄 중

### 크레딧 소진 후 예상 월 비용

| 서비스 | 달러 | 원화 (환율 $1=₩1,500) |
|---|---|---|
| NLB | ~$20 | ₩30,000 |
| RDS (db.t4g.micro + 20GB) | ~$17 | ₩25,500 |
| EC2 (t2.micro 1대) | ~$10 | ₩15,000 |
| EBS/기타 | ~$3 | ₩4,500 |
| **합계** | **~$50** | **₩75,000/월** |

비용 비중: NLB(40%) > RDS(33%) > EC2(20%) > 기타(7%)

## RDS 상세

| 항목 | 값 |
|---|---|
| 총 용량 | 20 GB |
| 사용 중 | 5.16 GB (약 5,285 MB) |
| 남은 공간 | 14.84 GB |
| 사용률 | 25.8% |

## 검토한 최적화 방안

### 1. Supabase 전환

| 플랜 | 용량 | 비용 | 판정 |
|---|---|---|---|
| Free | 500 MB | $0 | **불가** - 현재 5.2GB 사용 중 |
| Pro | 8 GB | $25/월 (₩37,500) | 가능하지만 RDS(₩25,500)보다 비쌈 |

결론: **RDS 유지가 더 저렴**. 단, 순수 앱 데이터가 500MB 이내면 Free tier 가능성 있음 (시스템 로그/바이너리 로그 제외 시).

### 2. NLB 제거 (권장)

NLB는 단일 EC2 환경에서 불필요. 블루-그린 무중단 배포를 포기하는 대신 월 ₩30,000 절감.

대안 배포 방식:
- **단순 재시작 배포**: SSH → JAR 교체 → 재시작 (다운타임 10~30초)
- **DNS 전환**: Green EC2 생성 → DNS 변경 → Blue 종료 (TTL 의존, 비주류)

사이드 프로젝트에서 10~30초 다운타임은 허용 가능.

### 3. Fly.io 등 PaaS 전환

| 장점 | 단점 |
|---|---|
| 인프라 관리 불필요 | Java/Spring Boot는 메모리 1GB 이상 필요 |
| 무중단 배포 기본 제공 | 한국 리전 없음 (도쿄가 최근접) |
| 비용 저렴 (₩10,000~15,000) | DB 별도 해결 필요 (Supabase 조합 시 5.2GB 문제) |

## 권장 방안: NLB 제거

### 전환 후 구성

```
api2.modutime.site → Elastic IP → EC2 1대 → Spring Boot
```

### 전환 작업

1. EC2 정리 — running 2대 중 1대 종료, stopped 1대 종료
2. Elastic IP 정리 — 미사용 EIP 해제 (미연결 EIP는 시간당 과금)
3. EC2 1대에 Elastic IP 연결
4. SSL 처리 — Let's Encrypt + Certbot 또는 Caddy (NLB가 하던 TLS 종료 대체)
5. Route 53 변경 — `api2.modutime.site` A 레코드: NLB Alias → Elastic IP
6. 배포 스크립트 수정 — GitHub Actions에서 SSH → JAR 교체 → 재시작
7. NLB + 타겟 그룹 삭제

### 전환 후 예상 비용

| 서비스 | 월 비용 (₩) |
|---|---|
| EC2 t2.micro 1대 | ₩15,000 |
| Elastic IP (연결됨) | ₩0 |
| RDS db.t4g.micro | ₩25,500 |
| Route 53 | ₩750 |
| **합계** | **₩41,250** |

**현재 대비 월 ₩33,750 절감 (45% 감소)**

### 주의사항

- NLB가 SSL 종료를 담당 중일 가능성 → EC2에서 직접 HTTPS 처리 필요
- 미사용 Elastic IP(3.34.22.132)는 즉시 해제 권장 (미연결 EIP 과금)
- 배포 중 10~30초 다운타임 발생

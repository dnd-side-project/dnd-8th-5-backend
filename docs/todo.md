### 할일 기록
- [] 센트리 연동
- [] 알림서버 구축
- [] 무중단 배포
- 

### available-time 락/커넥션 고갈 후속 (PR #167 — 비관적 락 + diff 갱신)
- [ ] `innodb_lock_wait_timeout` 50 → 10초 조정 (`SET GLOBAL` 즉시 + 파라미터그룹/my.cnf 영구, 앱 재배포로 커넥션 풀에 반영)
- [ ] 배포 후 Sentry MODUTIME-G(데드락)/J·H·N·M·K(커넥션)/P(StaleState) 신규 유입 멈추는지 확인
- [ ] (선택) `transaction_isolation` REPEATABLE-READ → READ-COMMITTED 검토 — 갭락 완화, 단 앱 동작 영향 검토 필요

#### 주기적 모니터링 쿼리 (운영 MySQL)
```sql
-- 락 대기가 timeout(현재 50s) 근처로 치솟지 않는지 주기적으로 확인
SHOW GLOBAL STATUS LIKE 'Innodb_row_lock_time_max';   -- 50000ms 근처면 여전히 장기 락 대기 존재 (커넥션 점유 → 풀 고갈 위험)
SHOW GLOBAL STATUS LIKE 'Innodb_row_lock_waits';      -- 누적 락 대기 횟수, 증가 추세 둔화 기대

-- 데드락/락 그래프 상세 (재발 시 LATEST DETECTED DEADLOCK 섹션 확인)
SHOW ENGINE INNODB STATUS;
```

기준선 (2026-05-31, 수정 전): `Innodb_row_lock_time_max=50119ms`, `Innodb_row_lock_waits=42288`, `innodb_lock_wait_timeout=50s`, isolation=`REPEATABLE-READ`, MySQL `8.0.44`. 운영 데드락 실물(`adjustment_result` row의 S→X 업그레이드)을 INNODB STATUS에서 확인함.

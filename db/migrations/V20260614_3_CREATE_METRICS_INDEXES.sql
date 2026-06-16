# 요약: 어드민 대시보드 메트릭 집계(생성일시 범위 / 활성 방) 성능 인덱스 추가 — 멱등(이미 있으면 건너뜀)
---
-- MySQL 은 CREATE INDEX IF NOT EXISTS 를 지원하지 않으므로, information_schema 로 존재 여부를
-- 확인해 없을 때만 생성한다. (일부 인덱스가 이미 존재하거나 부분 적용된 환경에서도 안전하게 재실행 가능)

-- 방 생성 추이 + 최근 7일 신규 방(newRoomsLast7d): room.created_at 범위 스캔
SET @ddl := IF(
    (SELECT COUNT(*) FROM information_schema.statistics
     WHERE table_schema = DATABASE() AND table_name = 'room' AND index_name = 'idx_room_created_at') = 0,
    'CREATE INDEX idx_room_created_at ON room (created_at)', 'SELECT 1');
PREPARE s FROM @ddl; EXECUTE s; DEALLOCATE PREPARE s;

-- 참여자 추이(전체) + 게스트 추이(user_id IS NULL 잔여 필터): participant.created_at 범위 스캔
SET @ddl := IF(
    (SELECT COUNT(*) FROM information_schema.statistics
     WHERE table_schema = DATABASE() AND table_name = 'participant' AND index_name = 'idx_participant_created_at') = 0,
    'CREATE INDEX idx_participant_created_at ON participant (created_at)', 'SELECT 1');
PREPARE s FROM @ddl; EXECUTE s; DEALLOCATE PREPARE s;

-- 활성 방(activeRooms): modified_at 범위 + room_uuid distinct 커버링 복합 인덱스
SET @ddl := IF(
    (SELECT COUNT(*) FROM information_schema.statistics
     WHERE table_schema = DATABASE() AND table_name = 'time_block' AND index_name = 'idx_time_block_modified_at_room_uuid') = 0,
    'CREATE INDEX idx_time_block_modified_at_room_uuid ON time_block (modified_at, room_uuid)', 'SELECT 1');
PREPARE s FROM @ddl; EXECUTE s; DEALLOCATE PREPARE s;

-- 로그인(회원) 가입 추이: users.created_at 범위 스캔 (deleted_at IS NULL 은 잔여 필터)
SET @ddl := IF(
    (SELECT COUNT(*) FROM information_schema.statistics
     WHERE table_schema = DATABASE() AND table_name = 'users' AND index_name = 'idx_users_created_at') = 0,
    'CREATE INDEX idx_users_created_at ON users (created_at)', 'SELECT 1');
PREPARE s FROM @ddl; EXECUTE s; DEALLOCATE PREPARE s;

-- (선택) 게스트 추이가 무거워지면 user_id+created_at 복합으로 잔여 필터 제거:
-- CREATE INDEX idx_participant_user_id_created_at ON participant (user_id, created_at);

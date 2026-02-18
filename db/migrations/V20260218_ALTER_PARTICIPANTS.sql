# 요약: 서비스 로그인과 약속 방 참여자 연동하기 위한 테이블 변경사항 적용
---
ALTER TABLE participant ADD COLUMN user_id BIGINT NULL;                                                                                                                           │
ALTER TABLE participant MODIFY COLUMN password VARCHAR(255) NULL;                                                                                                                 │
CREATE INDEX idx_participant_room_uuid_user_id ON participant(room_uuid, user_id);

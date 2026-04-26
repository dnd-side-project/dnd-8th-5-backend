# 요약: 회원 탈퇴 기능을 위한 oauth_id, deleted_at 컬럼 추가
---
ALTER TABLE users ADD COLUMN oauth_id VARCHAR(64) NULL;
ALTER TABLE users ADD COLUMN deleted_at DATETIME(6) NULL;
CREATE INDEX idx_users_oauth_id ON users(oauth_id);
CREATE INDEX idx_users_deleted_at ON users(deleted_at);

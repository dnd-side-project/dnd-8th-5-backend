-- 요약: 회원 탈퇴 사유 + 영구삭제 동의 시각 컬럼 추가
ALTER TABLE users ADD COLUMN withdraw_reason VARCHAR(500) NULL;
ALTER TABLE users ADD COLUMN withdraw_consented_at DATETIME(6) NULL;

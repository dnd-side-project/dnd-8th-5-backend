-- 알림 발송 상태 추적을 위한 컬럼 추가
ALTER TABLE notification
    ADD COLUMN sent TINYINT(1) NOT NULL DEFAULT 0 AFTER sender_name,
    ADD COLUMN sent_at DATETIME NULL AFTER sent;

-- 기존 데이터는 이미 발송된 것으로 간주
UPDATE notification SET sent = 1, sent_at = created_at WHERE sent = 0;

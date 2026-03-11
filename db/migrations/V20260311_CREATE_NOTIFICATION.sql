-- 디바이스 토큰 테이블 생성 (FCM 푸시 알림용)
CREATE TABLE device_token
(
    id          bigint auto_increment PRIMARY KEY,
    token       varchar(512) NOT NULL,
    user_id     bigint       NOT NULL,
    device_info varchar(50)  NULL,
    created_by  varchar(50)  NULL COMMENT '생성자',
    created_at  datetime(6)  NULL COMMENT '생성일시',
    modified_by varchar(50)  NULL COMMENT '수정자',
    modified_at datetime(6)  NULL COMMENT '수정일시',
    CONSTRAINT uk_device_token_token UNIQUE (token),
    CONSTRAINT fk_device_token_user_id_ref_users_id
        FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX idx_device_token_user_id ON device_token (user_id);

-- 알림 이력 테이블 생성
CREATE TABLE notification
(
    id           bigint auto_increment PRIMARY KEY,
    type         varchar(50)  NOT NULL COMMENT '알림 유형',
    title        varchar(200) NOT NULL COMMENT '알림 제목',
    message      varchar(500) NOT NULL COMMENT '알림 본문',
    room_uuid    varchar(50)  NULL COMMENT '관련 방 UUID',
    recipient_id bigint       NOT NULL COMMENT '수신자 user_id',
    sender_name  varchar(50)  NULL COMMENT '발송 트리거 참여자 이름',
    is_read      tinyint(1)   NOT NULL DEFAULT 0 COMMENT '읽음 여부',
    read_at      datetime(6)  NULL COMMENT '읽은 시각',
    created_at   datetime(6)  NULL COMMENT '생성일시',
    CONSTRAINT fk_notification_recipient_id_ref_users_id
        FOREIGN KEY (recipient_id) REFERENCES users (id)
);

CREATE INDEX idx_notification_recipient_read ON notification (recipient_id, is_read);
CREATE INDEX idx_notification_recipient_created ON notification (recipient_id, created_at DESC);

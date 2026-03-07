-- users 테이블 생성
CREATE TABLE users
(
    id                    bigint auto_increment PRIMARY KEY,
    name                  varchar(255) NOT NULL,
    email                 varchar(255) NOT NULL,
    profile_image         varchar(255) NOT NULL,
    thumbnail_image       varchar(255) NOT NULL,
    oauth_provider        varchar(50)  NOT NULL,
    refresh_token         varchar(512) NULL,
    token_expiration_time datetime(6)  NULL,
    created_time          datetime(6)  NULL,
    last_modified_time    datetime(6)  NULL,
    created_by            varchar(50)  NULL COMMENT '생성자',
    created_at            datetime(6)  NULL COMMENT '생성일시',
    modified_by           varchar(50)  NULL COMMENT '수정자',
    modified_at           datetime(6)  NULL COMMENT '수정일시',
    CONSTRAINT uniqueEmailAndProvider UNIQUE (email, oauth_provider)
);

-- participant 테이블에 users FK 추가
ALTER TABLE participant
    ADD CONSTRAINT fk_participant_user_id_ref_users_id
        FOREIGN KEY (user_id) REFERENCES users (id);

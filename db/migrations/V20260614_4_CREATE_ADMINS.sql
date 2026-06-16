# 요약: 어드민 인증(#169)용 admins 테이블 생성 — prod(ddl-auto=none)에 누락되어 있던 DDL 보강
---
-- Admin 엔티티(@Table(name="admins"))에 대응. local/test 는 H2 가 엔티티에서 자동 생성하므로
-- 이 스크립트는 prod 수동 적용용이다. 어드민 계정 INSERT 는 평문 비밀번호(운영 비밀)이므로
-- 이 파일에 포함하지 않고, 배포 시 별도 SQL 로 등록한다(비밀번호는 추후 BCrypt 전환 예정).

CREATE TABLE admins (
    id                    BIGINT       NOT NULL AUTO_INCREMENT,
    username              VARCHAR(255) NOT NULL,
    password              VARCHAR(255) NOT NULL,
    refresh_token         VARCHAR(512) NULL,
    token_expiration_time DATETIME(6)  NULL,
    created_by            VARCHAR(50)  NULL COMMENT '생성자',
    created_at            DATETIME(6)  NULL COMMENT '생성일시',
    modified_by           VARCHAR(50)  NULL COMMENT '수정자',
    modified_at           DATETIME(6)  NULL COMMENT '수정일시',
    PRIMARY KEY (id),
    CONSTRAINT uniqueAdminUsername UNIQUE (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 운영 계정 등록 예시 (배포 시 실제 비밀번호로 별도 실행):
-- INSERT INTO admins (username, password, created_by, modified_by, created_at, modified_at)
-- VALUES ('superadmin', '<운영_비밀번호>', 'system', 'system', NOW(6), NOW(6));

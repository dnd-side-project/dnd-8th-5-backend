-- 인앱 피드백 테이블 생성 (prod는 ddl-auto=none 이므로 수동 적용)
CREATE TABLE feedback (
    id                     BIGINT        NOT NULL AUTO_INCREMENT,
    category               VARCHAR(20)   NOT NULL COMMENT 'PRAISE | REVIEW | FEATURE | QUESTION | BUG',
    content                VARCHAR(1000) NOT NULL COMMENT '작성 내용 (trim 후 1~1000자)',
    reply_email            VARCHAR(255)  NULL COMMENT '회신용 이메일 (선택)',
    interview_agreed       TINYINT(1)    NOT NULL COMMENT '인터뷰 참여 동의 여부',
    interview_phone_number VARCHAR(20)   NULL COMMENT '인터뷰 연락처 (agreed=true일 때)',
    responses              TEXT          NOT NULL COMMENT 'Q&A 쌍 JSON 배열',
    snapshot               TEXT          NOT NULL COMMENT '제출 시점 컨텍스트 스냅샷 JSON',
    author_type            VARCHAR(20)   NOT NULL COMMENT 'MEMBER | GUEST | ANONYMOUS (서버 판정)',
    author_user_id         BIGINT        NULL COMMENT '카카오 회원 User PK',
    author_name            VARCHAR(255)  NULL COMMENT '회원명 또는 게스트 참여자명',
    author_email           VARCHAR(255)  NULL COMMENT '회원 이메일',
    created_at             DATETIME(6)   NOT NULL COMMENT '생성일시',
    created_by             VARCHAR(50)   NULL COMMENT '생성자',
    modified_at            DATETIME(6)   NULL COMMENT '수정일시',
    modified_by            VARCHAR(50)   NULL COMMENT '수정자',
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT '인앱 피드백 제출';

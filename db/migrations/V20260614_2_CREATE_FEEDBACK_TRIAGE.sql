-- 어드민 피드백 트리아지(심각도/처리상태) 테이블 생성 (prod는 ddl-auto=none 이므로 수동 적용)
-- 제출 데이터(feedback)는 불변으로 두고, 어드민이 부여하는 상태만 분리 관리한다.
-- feedback 1건당 최대 1행이며, 어드민이 처음 트리아지할 때 생성된다(lazy).
CREATE TABLE feedback_triage (
    id          BIGINT      NOT NULL AUTO_INCREMENT,
    feedback_id BIGINT      NOT NULL COMMENT 'feedback.id (트리아지 대상 피드백)',
    severity    VARCHAR(20) NOT NULL COMMENT 'LOW | MEDIUM | HIGH | CRITICAL',
    status      VARCHAR(20) NOT NULL COMMENT 'OPEN | IN_PROGRESS | RESOLVED | CLOSED',
    created_at  DATETIME(6) NOT NULL COMMENT '생성일시',
    created_by  VARCHAR(50) NULL COMMENT '생성자',
    modified_at DATETIME(6) NULL COMMENT '수정일시 (트리아지 최종 변경 시각)',
    modified_by VARCHAR(50) NULL COMMENT '수정자',
    PRIMARY KEY (id),
    UNIQUE KEY uk_feedback_triage_feedback_id (feedback_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT '어드민 피드백 트리아지 상태';

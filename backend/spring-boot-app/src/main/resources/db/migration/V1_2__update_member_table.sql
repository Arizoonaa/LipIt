-- 1. total_call_duration 컬럼 추가
ALTER TABLE member
    ADD COLUMN total_call_duration INTEGER DEFAULT 0 NOT NULL;

-- 2. report_count → total_report_count 로 컬럼 이름 변경
ALTER TABLE member
    RENAME COLUMN report_count TO total_report_count;

-- V1_1__insert_levels.sql

-- 1. level 테이블이 없으면 생성
CREATE TABLE IF NOT EXISTS level (
                                     level_id SERIAL PRIMARY KEY,
                                     level INTEGER NOT NULL,
                                     min_call_duration INTEGER NOT NULL,
                                     min_report_count INTEGER NOT NULL,
                                     badge_icon TEXT,
                                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. 데이터 삽입
INSERT INTO level (level_id, level, min_call_duration, min_report_count, badge_icon, created_at, updated_at)
VALUES (1, 1, 0, 0, '등급 1 이미지', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (2, 2, 30, 5, '등급 2 이미지', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (3, 3, 60, 10, '등급 3 이미지', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (4, 4, 90, 20, '등급 4 이미지', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (5, 5, 180, 30, '등급 5 이미지', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

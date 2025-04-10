import logging
from datetime import datetime
from apscheduler.schedulers.asyncio import AsyncIOScheduler
from apscheduler.triggers.cron import CronTrigger

from app.db.session import SessionLocal
from app.crud.news import update_news_db, update_weather_db

logger = logging.getLogger(__name__)

# 스케줄러 인스턴스 생성
scheduler = AsyncIOScheduler()

# KBS 뉴스 크롤링 작업
async def crawl_news_job():
    logger.info(f"KBS 인기뉴스 크롤링 시작 - {datetime.now()}")
    
    try:
        async with SessionLocal() as db:
            saved_count = await update_news_db(db)
            logger.info(f"크롤링 완료: {saved_count}개 뉴스 저장됨")
    except Exception as e:
        logger.error(f"크롤링 오류: {str(e)}")

# 날씨 크롤링 작업 
async def crawl_weather_job():
    logger.info(f"기상청 날씨 크롤링 시작 - {datetime.now()}")
    
    try:
        async with SessionLocal() as db:
            saved_count = await update_weather_db(db)
            logger.info(f"날씨 크롤링 완료: {saved_count}개 날씨 정보 저장됨")
    except Exception as e:
        logger.error(f"날씨 크롤링 오류: {str(e)}")

# 스케줄러 초기화
def init_scheduler():
    if scheduler.running:
        return
    
    try:
        # 매일 아침 6시, 저녁 6시 뉴스 크롤링
        scheduler.add_job(
            crawl_news_job,
            CronTrigger(hour='6,18'),
            id="news_crawl_job",
            name="KBS 인기 뉴스 크롤링",
            replace_existing=True,
        )
        
        # 날씨 크롤링: 11시 1분, 17시 1분으로 변경
        scheduler.add_job(
            crawl_weather_job,
            CronTrigger(hour='11,17', minute='1'),
            id="weather_crawl_job",
            name="기상청 날씨 크롤링",
            replace_existing=True,
        )
        
        # 스케줄러 시작
        scheduler.start()
        logger.info("스케줄러 시작됨 - 뉴스(6시,18시), 날씨(11시1분,17시1분)")
    except Exception as e:
        logger.error(f"스케줄러 초기화 오류: {str(e)}")

# 스케줄러 종료
def shutdown_scheduler():
    if scheduler.running:
        scheduler.shutdown()
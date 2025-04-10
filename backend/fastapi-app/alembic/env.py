from logging.config import fileConfig
import logging
import os
import sys

from sqlalchemy import engine_from_config, pool
from alembic import context

# app 경로 추가
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), "..")))

# 실제 프로젝트 설정값 가져오기
from app.core.config import (
    POSTGRES_DB,
    POSTGRES_HOST,
    POSTGRES_PASSWORD,
    POSTGRES_PORT,
    POSTGRES_USER,
)
from app.db.session import Base
import app.model  # 모든 모델 import

# Alembic config 객체 가져오기
config = context.config

# sqlalchemy.url 동적 설정 (.ini 값 무시)
config.set_main_option(
    "sqlalchemy.url",
    f"postgresql+psycopg2://{POSTGRES_USER}:{POSTGRES_PASSWORD}@{POSTGRES_HOST}:{POSTGRES_PORT}/{POSTGRES_DB}",
)

# 로그 설정 및 출력
fileConfig(config.config_file_name)
logger = logging.getLogger("alembic.env")
logger.info("🔧 sqlalchemy.url = %s", config.get_main_option("sqlalchemy.url"))

# 확실한 확인용 출력
sys.stderr.write(
    "🔥 sqlalchemy.url = " + config.get_main_option("sqlalchemy.url") + "\n"
)

# 메타데이터 정의 (모델 기반으로)
target_metadata = Base.metadata


def run_migrations_offline():
    """Offline mode"""
    context.configure(
        url=config.get_main_option("sqlalchemy.url"),
        target_metadata=target_metadata,
        literal_binds=True,
        dialect_opts={"paramstyle": "named"},
    )
    with context.begin_transaction():
        context.run_migrations()


def run_migrations_online():
    """Online mode"""
    connectable = engine_from_config(
        config.get_section(config.config_ini_section),
        prefix="sqlalchemy.",
        poolclass=pool.NullPool,
    )
    with connectable.connect() as connection:
        context.configure(connection=connection, target_metadata=target_metadata)
        with context.begin_transaction():
            context.run_migrations()


# 실행
if context.is_offline_mode():
    run_migrations_offline()
else:
    run_migrations_online()

"""step2: update duration values and set not null

Revision ID: 0002_update_duration_not_null
Revises: 0001_add_duration_nullable
Create Date: 2025-03-29 20:35:00.000000
"""

from alembic import op
import sqlalchemy as sa

revision = "0002_update_duration_not_null"
down_revision = "0001_add_duration_nullable"
branch_labels = None
depends_on = None


def upgrade():
    # Step 1: 기존 null 값에 기본값 0 채우기
    op.execute("UPDATE call SET duration = 0 WHERE duration IS NULL;")

    # Step 2: nullable=False 로 변경
    with op.batch_alter_table("call") as batch_op:
        batch_op.alter_column("duration", nullable=False)


def downgrade():
    with op.batch_alter_table("call") as batch_op:
        batch_op.alter_column("duration", nullable=True)

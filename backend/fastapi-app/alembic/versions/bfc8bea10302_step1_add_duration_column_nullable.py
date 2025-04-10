"""step1: add duration column nullable

Revision ID: 0001_add_duration_nullable
Revises:
Create Date: 2025-03-29 20:30:00.000000
"""

from alembic import op
import sqlalchemy as sa

revision = "0001_add_duration_nullable"
down_revision = None
branch_labels = None
depends_on = None


def upgrade():
    op.add_column("call", sa.Column("duration", sa.Integer(), nullable=True))


def downgrade():
    op.drop_column("call", "duration")

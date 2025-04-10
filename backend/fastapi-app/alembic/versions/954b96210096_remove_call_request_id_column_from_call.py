"""remove call_request_id column from call

Revision ID: 954b96210096
Revises: ff00a53e07cd
Create Date: 2025-04-02 17:34:26.091403

"""

from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision = "954b96210096"  # 새 리비전 ID
down_revision = "ff00a53e07cd"  # 이전 리비전 ID (자동 생성된 리비전 참고)
branch_labels = None
depends_on = None


def upgrade():
    op.drop_column("call", "call_request_id")


def downgrade():
    op.add_column("call", sa.Column("call_request_id", sa.BigInteger(), nullable=False))

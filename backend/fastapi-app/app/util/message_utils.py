# app/util/message_utils.py

from typing import Union, List
from copy import deepcopy
from datetime import datetime

from app.model.call import Call
from app.schema.call import Message
from app.util.datetime_utils import now_kst


def append_messages_to_call(
    call: Call,
    messages: Union[Message, dict, List[Union[Message, dict]]],
    update_timestamp: bool = True,
):
    """
    call.messages (JSONB)에 안전하게 메시지를 추가하고
    SQLAlchemy가 변경을 감지할 수 있도록 새 리스트로 재할당함
    """
    if not isinstance(messages, list):
        messages = [messages]

    # 기존 메시지 리스트 가져오기 (deepcopy로 안정성 확보)
    updated_messages = deepcopy(call.messages) if call.messages else []

    # 메시지 추가
    for msg in messages:
        if isinstance(msg, Message):
            updated_messages.append(msg.dict())
        elif isinstance(msg, dict):
            updated_messages.append(msg)
        else:
            raise ValueError("Message must be a Message or dict type")

    call.messages = updated_messages  # 새 리스트로 재할당해야 변경 감지됨

    if update_timestamp:
        call.updated_at = now_kst()

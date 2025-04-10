from fastapi import Depends
from sqlalchemy.ext.asyncio import AsyncSession

from app.crud.call import get_member_id_by_call_id
from app.db.session import get_db
from app.schema.common import APIResponse
from app.schema.call import StartCallRequest, UserMessageRequest
from app.service.call import start_call, add_message_to_call, end_call
from app.service.voice import get_voice_by_call_id, get_voice_by_member_id


# 통화 시작
async def start_call_endpoint(
    request: StartCallRequest, db: AsyncSession = Depends(get_db)
):
    voice = await get_voice_by_member_id(db, request.memberId)
    call_response = await start_call(
        db, request, request.memberId, voice.voice_name, voice.type
    )
    return APIResponse(
        status=200, message="대화가 성공적으로 시작되었습니다.", data=call_response
    )


# 메시지 추가
async def add_message_to_call_endpoint(
    call_id: int, request: UserMessageRequest, db: AsyncSession = Depends(get_db)
):
    member_id = await get_member_id_by_call_id(db, call_id)
    voice = await get_voice_by_call_id(db, call_id)
    response_data = await add_message_to_call(
        db, call_id, request, member_id, voice.voice_name, voice.type
    )
    return APIResponse(
        status=200, message="대화가 성공적으로 진행되었습니다.", data=response_data
    )


# 통화 종료
async def end_call_endpoint(call_id: int, db: AsyncSession = Depends(get_db)):
    response_data = await end_call(db, call_id)
    return APIResponse(
        status=200, message="대화가 성공적으로 종료되었습니다.", data=response_data
    )

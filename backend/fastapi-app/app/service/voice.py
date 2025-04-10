from sqlalchemy.ext.asyncio import AsyncSession
from app.crud.call import get_call_by_id
from app.crud.member import get_member_by_id
from app.crud.voice import get_voice_by_id
from app.exception.custom_exceptions import APIException
from app.exception.error_code import Error
from app.model.voice import Voice


async def get_voice_by_call_id(db: AsyncSession, call_id: int) -> Voice:
    """
    call_id로 member_id → selected_voice_id → audio_url을 추적해 voiceUrl 반환
    """

    call = await get_call_by_id(db, call_id)
    if not call:
        raise APIException(404, Error.CALL_NOT_FOUND)

    member = await get_member_by_id(db, call.member_id)
    if not member:
        raise APIException(404, Error.MEMBER_NOT_FOUND)

    if not member.selected_voice_id:
        raise APIException(400, Error.MEMBER_NO_SELECTED_VOICE)

    voice = await get_voice_by_id(db, member.selected_voice_id)

    return voice


async def get_voice_by_member_id(db: AsyncSession, member_id: int) -> str:
    """
    member_id를 기반으로 사용자의 selected_voice_id → audio_url 반환
    """

    member = await get_member_by_id(db, member_id)
    if not member:
        raise APIException(404, Error.MEMBER_NOT_FOUND)

    if not member.selected_voice_id:
        raise APIException(400, Error.MEMBER_NO_SELECTED_VOICE)

    voice = await get_voice_by_id(db, member.selected_voice_id)

    if not voice:
        raise APIException(404, Error.VOICE_NOT_FOUND)

    return voice

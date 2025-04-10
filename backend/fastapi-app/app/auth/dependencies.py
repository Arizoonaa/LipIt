# app/auth/dependencies.py
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from fastapi import Depends
from jose import jwt, JWTError, ExpiredSignatureError
from app.exception.custom_exceptions import APIException
from app.exception.error_code import Error
from app.core.config import JWT_SECRET, JWT_ALGORITHM

bearer_scheme = HTTPBearer(auto_error=False)  # 토큰이 없을 때도 우리가 처리


def get_current_user(
    credentials: HTTPAuthorizationCredentials = Depends(bearer_scheme),
):
    if not credentials:
        raise APIException(401, Error.AUTH_TOKEN_MISSING)

    token = credentials.credentials
    try:
        payload = jwt.decode(token, JWT_SECRET, algorithms=[JWT_ALGORITHM])
        return payload
    except ExpiredSignatureError:
        raise APIException(401, Error.AUTH_EXPIRED_TOKEN)
    except JWTError:
        raise APIException(401, Error.AUTH_INVALID_TOKEN)

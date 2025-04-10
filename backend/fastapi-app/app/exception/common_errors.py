from app.exception.error_code import Error
from app.util.docs.swagger_response import error_response


COMMON_ERRORS = {
    401: error_response(401, Error.AUTH_INVALID_TOKEN),
    422: error_response(422, Error.REQUEST_VALIDATION_ERROR),
}

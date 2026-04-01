import type {ValidationError} from "./ValidationError.ts";
import type {ApiErrorType} from "./ApiErrorType.ts";
import type {DetailError} from "./DetailError.ts";

export function isValidationError(error: ApiErrorType): error is ValidationError {
    return "errors" in error && Array.isArray(error.errors);
}

export function isDetailError(error: ApiErrorType): error is DetailError {
    return "detail" in error;
}
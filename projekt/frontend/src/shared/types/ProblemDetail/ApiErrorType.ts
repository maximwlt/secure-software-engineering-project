import type {ValidationError} from "./ValidationError.ts";
import type {DetailError} from "./DetailError.ts";

/**
 * The ApiErrorType is a union type that represents either a ValidationError or a DetailError.
 */
export type ApiErrorType = ValidationError | DetailError;
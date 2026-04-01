import type {ValidationFieldError} from "./ValidationFieldError.ts";
import type {ProblemDetailBaseType} from "./ProblemDetailBaseType.ts";

/**
 * The ValidationError interface represents a specific type of problem detail response that includes validation errors for specific fields.
 * It extends the ProblemDetailBaseType interface, inheriting its common fields (instance, status, title) and adding a new field called errors,
 * which is an array of ValidationFieldError objects. Each ValidationFieldError object contains information about a specific field that failed validation,
 * including the field name and the corresponding error message.
 */
export interface ValidationError extends ProblemDetailBaseType {
    errors: ValidationFieldError[];
}
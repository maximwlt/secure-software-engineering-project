/**
 * Represents a validation error for a specific field in a problem detail response.
 */
export interface ValidationFieldError {
    field: string;
    message: string;
}
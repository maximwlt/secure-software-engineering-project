/**
 * ErrorType defines the structure of an error object
 * It is oriented on the structure of the ProblemDetail object defined in the backend
 */
export type ErrorType = {
    status: number; // HTTP status code
    title: string; // Short, human-readable summary of the problem
    detail?: string; // Detailed description of the problem (optional)
    path? : string; // The request path that caused the error (optional)
}
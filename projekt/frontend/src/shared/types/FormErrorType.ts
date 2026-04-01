import type {ApiErrorType} from "./ProblemDetail/ApiErrorType.ts";

export type FormErrorType = {
    api?: ApiErrorType
    general?: string;
}
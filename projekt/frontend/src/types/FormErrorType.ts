import type {ErrorType} from "./ErrorType.ts";

export type FormErrorType = {
    api?: Partial<ErrorType>
    general?: string;
}
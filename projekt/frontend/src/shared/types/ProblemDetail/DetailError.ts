import type {ProblemDetailBaseType} from "./ProblemDetailBaseType.ts";

/**
 * The DetailError interface represents a specific type of problem detail response that includes a detailed error message.
 * It extends the ProblemDetailBaseType interface, inheriting its common fields (instance, status, title) and adding a new field called detail,
 * which contains a string describing the specific error in more detail.
 */
export interface DetailError extends ProblemDetailBaseType {
    detail: string;
}
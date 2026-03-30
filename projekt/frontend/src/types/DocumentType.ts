import type {DocumentInput} from "./DocumentInput.ts";

export interface DocumentType extends DocumentInput {
    noteId: string;
    userId: string;
    created_at: string;
    updated_at: string;
}
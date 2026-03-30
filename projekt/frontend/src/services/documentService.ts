import type {DocumentInput} from "../types/DocumentInput.ts";
import {ApiAdapter} from "../adapters/api.ts";
import type {DocumentType} from "../types/DocumentType.ts";

export const createDocumentService = (api : ApiAdapter) => ({
    getAll: () => api.get<{ documents: DocumentType[] }>("/documents"),
    getById: (id: string) => api.get<DocumentType>(`/documents/${id}`),
    create: (data: DocumentInput) => api.post<DocumentType>("/documents", data),
    update: (id: string, data: Partial<DocumentInput>) => api.put<DocumentType>(`/documents/${id}`, data),
    delete: (id: string) => api.delete<void>(`/documents/${id}`),
});
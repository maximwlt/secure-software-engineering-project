
/*
export const createDocumentService = (api) => ({
    getAll: () => api.get<{ documents: Document[] }>("/documents"),

    getById: (id: string) => api.get<Document>(`/documents/${id}`),

    create: (data: DocumentInput) =>
        api.post<Document>("/documents", data),

    update: (id: string, data: Partial<DocumentInput>) =>
        api.put<Document>(`/documents/${id}`, data),

    delete: (id: string) =>
        api.delete<void>(`/documents/${id}`),
});
*/
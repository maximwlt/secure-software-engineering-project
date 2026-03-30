import { useState, useCallback, useEffect } from "react";
import { createApiAdapter } from "../adapters/api";
import {useAuth} from "../utils/useAuth.ts";
import {createDocumentService} from "../services/documentService.ts";
import type {DocumentInput} from "../types/DocumentInput.ts";
import type {DocumentType} from "../types/DocumentType.ts";

interface UseDocumentsState {
    data: DocumentType | null;
    isLoading: boolean;
    error: string | null;
}

export const useGetDocument = (id: string) => {
    const auth = useAuth();
    const [state, setState] = useState<UseDocumentsState>({
        data: null,
        isLoading: true,
        error: null,
    });

    useEffect(() => {
        if (!id) return;

        const fetchDocument = async () => {
            try {
                setState(prev => ({ ...prev, isLoading: true }));
                const api = createApiAdapter(auth);
                const service = createDocumentService(api);
                const document = await service.getById(id);
                setState({ data: document, isLoading: false, error: null });
            } catch (err) {
                setState({
                    data: null,
                    isLoading: false,
                    error: err instanceof Error ? err.message : "Fehler beim Laden",
                });
            }
        };

        fetchDocument();
    }, [id, auth]);

    return state;
};

export const useCreateDocument = () => {
    const auth = useAuth();
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const create = useCallback(
        async (data: DocumentInput): Promise<DocumentType | null> => {
            try {
                setIsLoading(true);
                setError(null);
                const api = createApiAdapter(auth);
                const service = createDocumentService(api);
                return await service.create(data);
            } catch (err) {
                const message = err instanceof Error ? err.message : "Fehler beim Erstellen";
                setError(message);
                throw err;
            } finally {
                setIsLoading(false);
            }
        },
        [auth]
    );

    return { create, isLoading, error };
};

export const useUpdateDocument = () => {
    const auth = useAuth();
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const update = useCallback(
        async (id: string, data: Partial<DocumentInput>): Promise<DocumentType> => {
            try {
                setIsLoading(true);
                setError(null);
                const api = createApiAdapter(auth);
                const service = createDocumentService(api);
                return await service.update(id, data);
            } catch (err) {
                const message = err instanceof Error ? err.message : "Fehler beim Aktualisieren";
                setError(message);
                throw err;
            } finally {
                setIsLoading(false);
            }
        },
        [auth]
    );

    return { update, isLoading, error };
};

export const useDeleteDocument = () => {
    const auth = useAuth();
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const delete_ = useCallback(
        async (id: string): Promise<void> => {
            try {
                setIsLoading(true);
                setError(null);
                const api = createApiAdapter(auth);
                const service = createDocumentService(api);
                await service.delete(id);
            } catch (err) {
                const message = err instanceof Error ? err.message : "Fehler beim Löschen";
                setError(message);
                throw err;
            } finally {
                setIsLoading(false);
            }
        },
        [auth]
    );

    return { delete: delete_, isLoading, error };
};

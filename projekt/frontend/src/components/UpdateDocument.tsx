import { useParams } from 'react-router';
import { useEffect, useState } from 'react';
import { DocumentForm } from './DocumentForm.tsx';
import Navbar from "./Navbar.tsx";
import { useAuth } from '../utils/useAuth';
import { apiFetch } from '../utils/apiFetch';
import type { ApiErrorType } from "../types/ProblemDetail/ApiErrorType.ts";
import type { DetailError } from "../types/ProblemDetail/DetailError.ts";

interface FormData {
    title: string;
    mdContent: string;
    isPrivate: boolean;
}

interface Document {
    noteId: string;
    title: string;
    md_content: string;
    is_private: boolean;
    created_at: string;
    updated_at: string;
    userId: string;
}

function UpdateDocument() {
    const auth = useAuth();
    const { documentId } = useParams<{ documentId: string }>();
    const [document, setDocument] = useState<Document | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<ApiErrorType | null>(null);

    useEffect(() => {
        const fetchDocument = async () => {
            try {
                setIsLoading(true);
                const response = await apiFetch(auth, `/api/documents/${documentId}`);

                if (!response.ok) {
                    const errData = await response.json();
                    setError(errData);
                    return;
                }

                const data = await response.json();
                setDocument(data);
            } catch (err) {
                // Fehler in DetailError umwandeln
                const errorMessage = err instanceof Error ? err.message : 'Error loading document';
                setError({
                    title: 'Error',
                    detail: errorMessage,
                    status: 500
                } as DetailError);
            } finally {
                setIsLoading(false);
            }
        };

        fetchDocument();
    }, [documentId, auth]);

    if (isLoading) return <><Navbar /><div className="text-center mt-8">Loading...</div></>;
    if (error) return <><Navbar /><div className="text-red-500 text-center mt-8"><ApiErrorMessageDisplay error={error} /></div></>;
    if (!document) return <><Navbar /><div className="text-center mt-8">Document not found</div></>;

    const initialData: FormData = {
        title: document.title,
        mdContent: document.md_content,
        isPrivate: document.is_private
    };

    return (
        <>
            <Navbar />
            <DocumentForm docId={documentId!} initialData={initialData} isEdit={true} />
        </>
    );
}

// Helper Component für Error Display
const ApiErrorMessageDisplay = ({ error }: { error: ApiErrorType }) => {
    if ('detail' in error) {
        return <>{(error as DetailError).detail}</>;
    }
    if ('errors' in error) {
        return <>{error.title}</>;
    }
    return <>Error</>;
};

export { UpdateDocument };

import {useEffect, useState} from 'react';
import { useParams, useNavigate } from 'react-router';
import { apiFetch } from '../utils/apiFetch';
import { useAuth } from '../utils/useAuth';
import { isValidUUID } from '../utils/validation';
import '../styling/DocumentDetailPage.css';
import {SafeMarkdown} from "./SafeMarkdown.tsx";
import Navbar from "./Navbar.tsx";
import {jwtDecode, type JwtPayload} from "jwt-decode";
import DOMPurify from "dompurify";
import {faGlobe, faLock, faTriangleExclamation} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import LoadingBar from "./LoadingBar.tsx";
import BackButton from "./BackButton.tsx";
import DeleteButton from "./DeleteButton.tsx";
import EditButton from "./EditButton.tsx";

interface DocumentDetail {
    noteId: string;
    title: string;
    userId: string;
    md_content?: string;
    created_at?: string;
    updated_at?: string;
    is_private?: boolean;
}

function DocumentDetailPage() {
    const auth = useAuth();
    const navigate = useNavigate();
    const { documentId } = useParams<{ documentId: string }>();
    const [document, setDocument] = useState<DocumentDetail | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    const isOwner = (() => {
        if (!auth?.token || !document) return false;
        try {
            const decodedToken = jwtDecode<JwtPayload>(auth.token);
            return decodedToken.sub === document.userId;
        } catch {
            return false;
        }
    })();

    useEffect(() => {
        if (!documentId || !isValidUUID(documentId)) {
            setError('Ungültige Dokument-ID');
            setIsLoading(false);
            return;
        }

        const fetchDocument = async () => {
            try {
                setIsLoading(true);
                setError(null);

                const response = await apiFetch(auth, `/api/documents/${documentId}`);

                if (!response.ok) {
                    if (response.status === 404) {
                        throw new Error('Document not found');
                    }
                    throw new Error('Error loading document');
                }

                const data = await response.json();
                setDocument(data);
            } catch (err) {
                setError(err instanceof Error ? err.message : 'Ein Fehler ist aufgetreten');
            } finally {
                setIsLoading(false);
            }
        };

        fetchDocument();
    }, [documentId, auth]);


    const handleDelete = async () => {
        if (!documentId) return;

        try {
            const response = await apiFetch(
                auth,
                `/api/documents/${documentId}`, {
                    method: "DELETE"
                }
            );
            if (!response.ok) {
                throw new Error("Failed to delete document");
            }
            navigate(-1);
        } catch (err) {
            alert(err instanceof Error ? err.message : "Error occurred while deleting document");
        }
    };

    if (isLoading) {
        return (
            <LoadingBar />
        );
    }

    if (error) {
        return (
            <div className="page-container">
                <div className="error-message"><FontAwesomeIcon icon={faTriangleExclamation} /> {error}</div>
                <BackButton />
            </div>
        );
    }

    if (!document) {
        return (
            <div className="page-container">
                <div className="error-message"><FontAwesomeIcon icon={faTriangleExclamation} /> Document not found</div>
                <BackButton />
            </div>
        );
    }

    const sanitizedTitle = DOMPurify.sanitize(document.title);

    return (
        <>
            <Navbar />
            <div className="page-container">

                <BackButton />

                <div className="bg-white border border-gray-200 rounded-xl p-8 overflow-hidden md:p-6 sm:p-4 shadow-sm">
                    <header className="flex justify-between items-start gap-4 mb-8 pb-6 border-b-2 border-gray-300 md:flex-col">
                        <h1 className="text-4xl md:text-2xl sm:text-xl font-bold text-gray-900 leading-snug break-all flex-1">
                            {sanitizedTitle}
                        </h1>
                        {
                            document.is_private ? (
                                <span className="bg-gray-200 text-gray-700 px-3 py-1 rounded-full text-xs font-semibold uppercase tracking-wide whitespace-nowrap">
                                    <FontAwesomeIcon icon={faLock} /> Private
                                </span>
                            ) :
                            <p className="bg-green-600 text-white px-3 py-1 rounded-full text-xs font-semibold uppercase tracking-wide whitespace-nowrap">
                                <FontAwesomeIcon icon={faGlobe} /> Public
                            </p>
                        }
                    </header>
                    <div className="mt-8 pt-8 break-normal border-gray-100">
                        {document.md_content ? (
                            <SafeMarkdown markdown={document.md_content}/>
                        ) : (
                            <p className="text-base text-gray-700 leading-relaxed">No content available.</p>
                        )}
                    </div>
                </div>

                <div className="flex justify-end mt-2 gap-2">
                    { isOwner && (<EditButton docId={documentId} />)}
                    { isOwner && (<DeleteButton title="Delete document" message="Are you sure you want to delete this document? This action cannot be undone." onDeleteClick={handleDelete} />)}
                </div>

            </div>
        </>
    );
}

export default DocumentDetailPage

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

    const jwttoken = auth.token;
    const decodedToken = jwtDecode<JwtPayload>(jwttoken || '') || {};
    const isOwner  = decodedToken.sub === document?.userId;

    useEffect(() => {
        if (!documentId || !isValidUUID(documentId)) {
            setError('Ungültige Dokument-ID');
            setIsLoading(false);
            return;
        }
        if (auth == null) {
            setError('Authentifizierung erforderlich!');
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
                        throw new Error('Dokument nicht gefunden');
                    }
                    throw new Error('Fehler beim Laden des Dokuments');
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

    const handleBack = () => {
        navigate(-1); // Vorherige Seite in der Historie
    };

    const handleDelete = async () => {
        if (!documentId) return;

        const confirmed = window.confirm(
            "Willst du dieses Dokument wirklich löschen?"
        );
        if (!confirmed) return;

        try {
            const response = await apiFetch(
                auth,
                `/api/documents/${documentId}`, {
                    method: "DELETE"
                }
            );
            if (!response.ok) {
                throw new Error("Löschen fehlgeschlagen");
            }
            navigate(-1);
        } catch (err) {
            alert(err instanceof Error ? err.message : "Fehler beim Löschen");
        }
    };

    if (isLoading) {
        return (
            <div className="document-detail-container">
                <div className="loading">Lädt Dokument...</div>
            </div>
        );
    }

    if (error || !document) {
        return (
            <div className="document-detail-container">
                <div className="error-message">⚠️ {error || 'Dokument nicht gefunden'}</div>
                <button onClick={handleBack} className="back-button">
                    ← Zurück zur Übersicht
                </button>
            </div>
        );
    }

    const sanitizedTitle = DOMPurify.sanitize(document.title);

    return (
        <>
            <Navbar />
            <div className="document-detail-container">
                <button onClick={handleBack} className="back-button">
                    ← Zurück zur Übersicht
                </button>

                <div className="document-detail-card">
                    <header className="document-header">
                        <h1 className="document-title">
                            {sanitizedTitle}
                        </h1>
                        {document.is_private ? (
                                <span className="content-badge">🔒 Privat</span>
                            ) :
                            <p className="content-text">Öffentlich</p>}
                    </header>
                    <div className="document-content">
                        {document.md_content ? (
                            <SafeMarkdown markdown={document.md_content}/>
                        ) : (
                            <p className="content-text">Kein Inhalt vorhanden</p>
                        )}
                    </div>


                </div>

                { isOwner && (
                    <button onClick={handleDelete} className="delete-button">
                        Löschen
                    </button>)
                }

            </div>
        </>
    );
}



export default DocumentDetailPage

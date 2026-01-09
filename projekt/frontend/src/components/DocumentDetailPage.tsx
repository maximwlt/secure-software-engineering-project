import {useEffect, useState} from 'react';
import { useParams, useNavigate } from 'react-router';
import { apiFetch } from '../utils/apiFetch';
import { useAuth } from '../utils/useAuth';
import { isValidUUID } from '../utils/validation';
import '../styling/DocumentDetailPage.css';
import {SafeMarkdown} from "./SafeMarkdown.tsx";
import Navbar from "./Navbar.tsx";

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
        // navigate('/documents/public');
        navigate(-1); // Vorherige Seite in der Historie
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
                            <SafeMarkdown markdown={document.title}/>
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
            </div>
        </>
    );
}

export default DocumentDetailPage


/*

<SafeMarkdown markdown={document.md_content} />
                <div className="document-meta">
                    <div className="meta-item">
                        <span className="meta-label">Dokument-ID:</span>
                        <span className="meta-value">{document.noteId}</span>
                    </div>
                    {document.createdAt && (
                        <div className="meta-item">
                            <span className="meta-label">Erstellt am:</span>
                            <span className="meta-value">
                                {new Date(document.createdAt).toLocaleString('de-DE')}
                            </span>
                        </div>
                    )}
                    {document.updatedAt && (
                        <div className="meta-item">
                            <span className="meta-label">Aktualisiert am:</span>
                            <span className="meta-value">
                                {new Date(document.updatedAt).toLocaleString('de-DE')}
                            </span>
                        </div>
                    )}
                </div>
 */
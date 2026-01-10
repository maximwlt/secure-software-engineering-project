import { useEffect, useState } from 'react';
import {useNavigate, useSearchParams} from 'react-router';
import { apiFetch } from '../utils/apiFetch';
import { useAuth } from '../utils/useAuth';
import '../styling/PublicDocumentsPage.css';
import Navbar from "./Navbar.tsx";
import SearchBar from "./Searchbar.tsx";
import ErrorMessage from "./ErrorMessage.tsx";

interface PublicDocument {
    noteId: string;
    title: string;
    userId: string;
}

export function PublicDocumentsPage() {
    const auth = useAuth();
    const [searchParams, setSearchParams] = useSearchParams();
    const navigate = useNavigate();
    const [documents, setDocuments] = useState<PublicDocument[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [inputValue, setInputValue] = useState(searchParams.get("q") || "");

    useEffect(() => {
        const query = searchParams.get("q") || "";

        const fetchDocuments = async () => {
            try {
                setIsLoading(true);
                setError(null);

                const url : string = query ?
                    `/api/documents/public/search?q=${encodeURIComponent(query)}` :
                    '/api/documents/public';

                const response = await apiFetch(auth, url);

                if (!response.ok) {
                    setError("Fehler beim Laden der Dokumente");
                }
                else {
                    const data = await response.json();
                    setDocuments(data);
                }
            } catch (err) {
                setError(err instanceof Error ? err.message : 'Ein Fehler ist aufgetreten');
            } finally {
                setIsLoading(false);
            }
        };

        fetchDocuments();
    }, [auth, searchParams]);

    const handleCardClick = (noteId: string) => {
        navigate(`/documents/${noteId}`);
    };

    const handleSearchSubmit = () => {
        if (inputValue) {
            setSearchParams({q: inputValue});
        } else {
            setSearchParams({});
        }
    };

    if (isLoading) {
        return (
            <div className="public-documents-container">
                <div className="loading">Lädt Dokumente...</div>
            </div>
        );
    }


    return (
        <>
            <Navbar/>
            <SearchBar value={inputValue} onChange={setInputValue} onSubmit={handleSearchSubmit} />
            <div className="public-documents-container">
                <h1 className="page-title">Öffentliche Dokumente</h1>

                {error && <ErrorMessage message={error} type="general" />}

                {!error && documents.length === 0 ? (
                    <div className="empty-state">
                        <p>Keine öffentlichen Dokumente vorhanden</p>
                    </div>
                ) : (
                    <div className="documents-grid">
                        {documents.map((doc) => (
                            <div
                                key={doc.noteId}
                                className="document-card"
                                onClick={() => handleCardClick(doc.noteId)}
                            >
                                <div className="card-header">
                                    <h2 className="card-title">{doc.title}</h2>
                                </div>
                                <div className="card-body">
                                    <div className="card-info">
                                        <span className="info-label">Dokument-ID:</span>
                                        <span className="info-value">{doc.noteId}</span>
                                    </div>
                                </div>
                                <div className="card-footer">
                                    <span className="view-link">Ansehen →</span>
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </>
    );
}
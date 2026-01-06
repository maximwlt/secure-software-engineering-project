import {useAuth} from "../utils/useAuth.ts";
import {useNavigate} from "react-router";
import {useEffect, useState} from "react";
import {apiFetch} from "../utils/apiFetch.ts";
import Navbar from "./Navbar.tsx";
import SearchBar from "./Searchbar.tsx";

interface PublicDocument {
    noteId: string;
    title: string;
    userId: string;
}



function UserDocumentsPage()  {
    const auth = useAuth();
    const navigate = useNavigate();
    const [documents, setDocuments] = useState<PublicDocument[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [inputValue, setInputValue] = useState("");
    const [query, setQuery] = useState("");

    useEffect(() => {
        const fetchDocuments = async () => {
            try {
                setIsLoading(true);
                setError(null);

                const url = query ?
                    `/api/documents/user/search?q=${encodeURIComponent(query)}` :
                    '/api/documents/user';

                const response = await apiFetch(auth, url);

                if (!response.ok) {
                    setError("Fehler beim Laden der Dokumente");
                }

                const data = await response.json();
                setDocuments(data);
            } catch (err) {
                setError(err instanceof Error ? err.message : 'Ein Fehler ist aufgetreten');
            } finally {
                setIsLoading(false);
            }
        };

        fetchDocuments();
    }, [auth, query]);

    const handleCardClick = (noteId: string) => {
        navigate(`/documents/${noteId}`);
    };

    if (isLoading) {
        return (
            <div className="public-documents-container">
                <div className="loading">Lädt Dokumente...</div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="public-documents-container">
                <div className="error-message">⚠️ {error}</div>
            </div>
        );
    }

    return (
        <>
            <Navbar/>
            <SearchBar value={inputValue} onChange={setInputValue} onSubmit={() => setQuery(inputValue)} />
            <div className="public-documents-container">
                <h1 className="page-title">Meine Dokumente</h1>

                {documents.length === 0 ? (
                    <div className="empty-state">
                        <p>Keine eigenen Dokumente vorhanden</p>
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

export default UserDocumentsPage;
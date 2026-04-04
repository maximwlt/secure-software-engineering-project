import {useAuth} from "../shared/utils/useAuth.ts";
import {useNavigate, useSearchParams} from "react-router";
import {useEffect, useState} from "react";
import {apiFetch} from "../shared/utils/apiFetch.ts";
import Navbar from "./Navbar.tsx";
import SearchBar from "./Searchbar.tsx";
import ErrorMessage from "./ErrorMessage.tsx";
import LoadingBar from "../shared/components/LoadingBar.tsx";
import OverviewPage from "./OverviewPage.tsx";
import type {PublicDocument} from "../shared/types/PublicDocument.ts";


function UserDocumentsPage()  {
    const auth = useAuth();
    const navigate = useNavigate();
    const [documents, setDocuments] = useState<PublicDocument[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [inputValue, setInputValue] = useState("");
    const [searchParams, setSearchParams] = useSearchParams();

    useEffect(() => {
        const query = searchParams.get("q") || "";
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
            setSearchParams({ q: inputValue });
        } else {
            setSearchParams({});
        }
    }

    if (isLoading) {
        return <LoadingBar />;
    }


    return (
        <>
            <Navbar/>
            <SearchBar value={inputValue} onChange={setInputValue} onSubmit={handleSearchSubmit} />
            <div className="max-w-350 mx-auto py-8 px-4 md:px-2">
                <h1 className="text-4xl md:text-3xl sm:text-2xl font-bold text-gray-900 mb-8 md:mb-6 text-center">My documents</h1>

                {error && <ErrorMessage message={error} type="general" />}

                {!error && documents.length === 0 ? (
                    <div className="text-center bg-gray-100 rounded-lg py-6 px-4 text-lg text-gray-500">
                        <p>There are no documents.</p>
                    </div>
                ) : (
                   <OverviewPage documents={documents} onCardClick={handleCardClick} />
                )}
            </div>
        </>
    );
}

export default UserDocumentsPage;
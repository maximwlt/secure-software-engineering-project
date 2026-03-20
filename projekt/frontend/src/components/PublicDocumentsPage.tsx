import { useEffect, useState } from 'react';
import {useNavigate, useSearchParams} from 'react-router';
import { apiFetch } from '../utils/apiFetch';
import { useAuth } from '../utils/useAuth';
import Navbar from "./Navbar.tsx";
import SearchBar from "./Searchbar.tsx";
import ErrorMessage from "./ErrorMessage.tsx";
import LoadingBar from "./LoadingBar.tsx";
import type {PublicDocument} from "../types/PublicDocument.ts";
import OverviewPage from "./OverviewPage.tsx";


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
                    setError("Error loading documents");
                }
                const data = await response.json();
                setDocuments(data);

            } catch (err) {
                setError(err instanceof Error ? err.message : 'An error occurred');
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
        return <LoadingBar />;
    }


    return (
        <>
            <Navbar/>
            <SearchBar value={inputValue} onChange={setInputValue} onSubmit={handleSearchSubmit} />
            <div className="max-w-350 mx-auto py-8 px-4 md:px-2">
                <h1 className="text-4xl md:text-3xl sm:text-2xl font-bold text-gray-900 mb-8 md:mb-6 text-center">
                    Public documents
                </h1>

                {error && <ErrorMessage message={error} type="general" />}

                {!error && documents.length === 0 ? (
                    <div className="text-center bg-gray-100 rounded-lg py-12 px-4 text-lg text-gray-500">
                        <p>No public documents exist.</p>
                    </div>
                ) : (
                    <OverviewPage documents={documents} onCardClick={handleCardClick} />
                )}
            </div>
        </>
    );
}
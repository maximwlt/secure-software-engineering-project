import React, { useState } from "react";
import ErrorMessage from "./ErrorMessage.tsx";
import "../styling/Searchbar.css";

type SearchBarProps = {
    value: string;
    onChange: (v: string) => void;
    onSubmit: (s: string) => void;
};
const PATTERN : RegExp = /^[a-zA-Z0-9äöüÄÖÜß\s-_.]*$/;
const MAX_LENGTH = 50;

function SearchBar({ value, onChange, onSubmit }: SearchBarProps) {
    const [error, setError] = useState<string | null>(null);


    const handleSearch = async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        setError(null);

        if (value.length > MAX_LENGTH) {
            setError(`Die Suchanfrage darf maximal ${MAX_LENGTH} Zeichen lang sein.`);
            return;
        }
        if (!PATTERN.test(value)) {
            setError("Die Suchanfrage enthält ungültige Zeichen. Nur Buchstaben, Zahlen und Leerzeichen sind erlaubt.");
            return;
        }
        onSubmit(encodeURIComponent(value.trim()));

    };

    return (
        <>
            <form className="search-bar" onSubmit={handleSearch}>
                <input
                    className="search-input"
                    type="text"
                    name="searchQuery"
                    placeholder="Eingabe..."
                    value={value}
                    maxLength={MAX_LENGTH}
                    onChange={(e) => onChange(e.target.value)}
                />
                <button
                    className="search-button"
                    type="submit"
                    disabled={value.length > MAX_LENGTH}
                >
                    Suchen
                </button>
            </form>

            <ErrorMessage
                message={error ?? undefined}
                type="general"
            />
        </>
    );
}

export default SearchBar;

import React, { useState } from "react";
import ErrorMessage from "./ErrorMessage.tsx";

type SearchBarProps = {
    value: string;
    onChange: (v: string) => void;
    onSubmit: (s: string) => void;
};
const MAX_LENGTH = 50;

function SearchBar({ value, onChange, onSubmit }: SearchBarProps) {
    const [error, setError] = useState<string | null>(null);


    const handleSearch = async (event: React.SubmitEvent<HTMLFormElement>) => {
        event.preventDefault();
        setError(null);

        if (value.length > MAX_LENGTH) {
            setError(`The search query must be at most ${MAX_LENGTH} characters long.`);
            return;
        }
        onSubmit(encodeURIComponent(value.trim()));
    };

    return (
        <>
            <form className="max-w-225 mx-auto my-6 px-5 py-4 flex gap-3 items-center bg-white border border-gray-200 rounded-xl shadow-sm" onSubmit={handleSearch}>
                <input
                    className="flex-1 px-3 py-2.5 text-base text-gray-900 border border-gray-300 rounded-lg outline-none placeholder-gray-400 transition duration-200 focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20"
                    type="text"
                    name="searchQuery"
                    placeholder="Enter..."
                    value={value}
                    maxLength={MAX_LENGTH}
                    onChange={(e) => onChange(e.target.value)}
                />
                <button
                    className="px-5 py-2.5 text-[0.95rem] font-semibold text-white bg-blue-500 rounded-lg cursor-pointer transition duration-200 hover:bg-blue-700 active:translate-y-px disabled:bg-blue-300 disabled:cursor-not-allowed"
                    type="submit"
                    disabled={value.length > MAX_LENGTH}
                >
                    Search
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

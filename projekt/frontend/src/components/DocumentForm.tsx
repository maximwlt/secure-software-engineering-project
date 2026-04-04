import { useAuth } from '../shared/utils/useAuth';
import React, { useState } from 'react';
import { useNavigate } from 'react-router';
import ErrorMessage from './ErrorMessage';
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faBan, faCirclePlus, faPenToSquare} from "@fortawesome/free-solid-svg-icons";
import { apiFetch } from '../shared/utils/apiFetch';
import type {AuthContextType} from "../shared/types/AuthContextType.ts";
import type { ApiErrorType } from "../shared/types/ProblemDetail/ApiErrorType.ts";
import type { DetailError } from "../shared/types/ProblemDetail/DetailError.ts";
import ApiErrorMessage from "../shared/components/ApiErrorMessage.tsx";

interface FormData {
    title: string;
    mdContent: string;
    isPrivate: boolean;
}

interface Errors {
    title?: string;
    content?: string;
}

interface DocumentFormProps {
    docId?: string;
    initialData?: FormData;
    isEdit?: boolean;
}

function DocumentForm({ docId, initialData, isEdit = false }: DocumentFormProps) {
    const auth: AuthContextType = useAuth();
    const navigate = useNavigate();

    const [formData, setFormData] = useState<FormData>(
        initialData || {
            title: '',
            mdContent: '',
            isPrivate: false
        }
    );

    const [errors, setErrors] = useState<Errors>({});
    const [apiError, setApiError] = useState<ApiErrorType | null>(null);
    const [isLoading, setIsLoading] = useState(false);

    const handleChange = (
        e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
    ): void => {
        const { name, value, type } = e.target;

        if (type === 'checkbox') {
            setFormData(prev => ({
                ...prev,
                [name]: (e.target as HTMLInputElement).checked
            }));
        } else {
            setFormData(prev => ({
                ...prev,
                [name]: value
            }));
        }

        if (errors[name as keyof Errors]) {
            setErrors(prev => ({
                ...prev,
                [name]: ''
            }));
        }
    };

    const validateForm = (): boolean => {
        const newErrors: Errors = {};
        if (!formData.title.trim()) {
            newErrors.title = 'Title is required.';
        }
        else if (formData.title.length > 100) {
            newErrors.title = 'Title cannot exceed 100 characters.';
        }

        if (!formData.mdContent.trim()) {
            newErrors.content = 'Content is required.';
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async (e: React.SubmitEvent<HTMLFormElement>): Promise<void> => {
        e.preventDefault();
        setApiError(null);

        if (!auth.isAuthenticated) {
            setApiError({
                title: 'Not Authenticated',
                detail: 'You have to be authenticated to create a document.',
                status: 401
            } as DetailError);
            return;
        }

        if (!validateForm()) {
            return;
        }

        setIsLoading(true);

        try {
            const body = {
                title: formData.title,
                mdContent: formData.mdContent,
                isPrivate: formData.isPrivate
            };

            let response: Response;

            if (isEdit && docId) {
                // UPDATE
                response = await apiFetch(auth, `/api/documents/${docId}`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(body)
                });
            } else {
                // CREATE
                response = await apiFetch(auth, '/api/documents', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(body)
                });
            }

            if (!response.ok) {
                const error = await response.json();
                setApiError(error);
                return;
            }

            const result = await response.json();
            navigate(`/documents/${result.noteId || result.id}`);

        } catch (err) {
            const errorMessage = err instanceof Error ? err.message : "An error occurred.";
            setApiError({
                title: 'Error',
                detail: errorMessage,
                status: 500
            } as DetailError);
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="max-w-3xl mx-auto mt-8 px-4">
            <h1 className="text-2xl font-bold mb-6">
                {isEdit ? 'Edit document' : 'Create new document'}
            </h1>

            {apiError && <ApiErrorMessage error={apiError} />}

            <form className="w-full" onSubmit={handleSubmit}>
                <div className="mb-6">
                    <label className="block mb-2 font-semibold">Title *</label>
                    <input
                        className="w-full px-3 py-3 text-base border border-gray-300 rounded-md disabled:bg-gray-100"
                        type="text"
                        name="title"
                        value={formData.title}
                        onChange={handleChange}
                        placeholder="Insert document title"
                        disabled={isLoading}
                    />
                    <ErrorMessage message={errors.title} type="field" />
                </div>

                <div className="mb-6">
                    <div className="flex justify-between items-center mb-2">
                        <label className="block mb-2 font-semibold">
                            Content * (Markdown supported)
                        </label>
                        <p>
                            Chars: {formData.mdContent.length} / 10000
                        </p>
                    </div>
                    <textarea
                        className="w-full px-3 py-3 text-base border border-gray-300 rounded-md font-mono resize-y disabled:bg-gray-100"
                        name="mdContent"
                        value={formData.mdContent}
                        onChange={handleChange}
                        rows={15}
                        placeholder="Insert document content. You can use Markdown syntax to format your text."
                        disabled={isLoading}
                    />
                    <ErrorMessage message={errors.content} type="field" />
                </div>

                <div className="mb-6">
                    <label className="inline-flex items-center gap-2 cursor-pointer font-semibold">
                        <input
                            className="w-4 h-4 disabled:cursor-not-allowed"
                            type="checkbox"
                            name="isPrivate"
                            checked={formData.isPrivate}
                            onChange={handleChange}
                            disabled={isLoading}
                        />
                        Private document (only visible to you)
                    </label>
                </div>

                <button
                    className="px-6 py-3 bg-blue-500 text-white rounded-md cursor-pointer disabled:bg-gray-300 disabled:cursor-not-allowed"
                    type="submit"
                    disabled={isLoading || !auth.isAuthenticated}
                >
                    {isLoading
                        ? 'Submitting...'
                        : !auth.isAuthenticated
                            ? <> <FontAwesomeIcon icon={faBan} /> Unauthorized</>
                            : isEdit
                                ? <> <FontAwesomeIcon icon={faPenToSquare} /> Update</>
                                : <> <FontAwesomeIcon icon={faCirclePlus} /> Create</>
                    }
                </button>
            </form>
        </div>
    );
}

export { DocumentForm };

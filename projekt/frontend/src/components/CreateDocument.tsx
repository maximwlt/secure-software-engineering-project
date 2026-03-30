import { useAuth } from '../utils/useAuth';
import React, { useState } from 'react';
import { useNavigate } from 'react-router';
import { apiFetch } from '../utils/apiFetch';
import ErrorMessage from './ErrorMessage';
import Navbar from './Navbar';
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faBan, faCirclePlus} from "@fortawesome/free-solid-svg-icons";
import type {ApiErrorType} from "../types/ProblemDetail/ApiErrorType.ts";
import {isDetailError} from "../types/ProblemDetail/IsErrorTypeGuards.ts";

interface FormData {
    title: string;
    mdContent: string;
    isPrivate: boolean;
}

interface Errors {
    title?: string;
    content?: string;
    general?: string;
}

function CreateDocument() {
    const auth = useAuth();
    const navigate = useNavigate();

    const [formData, setFormData] = useState<FormData>({
        title: '',
        mdContent: '',
        isPrivate: false
    });

    const [errors, setErrors] = useState<Errors>({});
    const [isSubmitting, setIsSubmitting] = useState(false);

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

    const handleSubmit = async (e: React.SubmitEvent<HTMLFormElement>): Promise<void> => {
        e.preventDefault();

        if (!auth.isAuthenticated) {
            setErrors({ general: 'You have to be authorized to create a document' });
            return;
        }

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

        if (Object.keys(newErrors).length > 0) {
            setErrors(newErrors);
            return;
        }

        setIsSubmitting(true);
        setErrors({});

        try {
            const response = await apiFetch(auth, '/api/documents', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(formData)
            });

            if (!response.ok) {
                const errorData : ApiErrorType = await response.json();
                if (isDetailError(errorData)) {
                    setErrors({general: errorData.detail});
                    return;
                }
                // TODO: Handle validation errors (if backend returns them in a specific format)
                // if (isValidationError(errorData)) {}
            }

            const data = await response.json();
            navigate(data.noteId ? `/documents/${data.noteId}` : '/documents/public');

        } catch (error) {
            setErrors({
                general: error instanceof Error ? error.message : 'An error occurred.'
            });
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <>
            <Navbar />
            <div className="max-w-3xl mx-auto mt-8 px-4">
                <h1 className="text-2xl font-bold mb-6">Create new document</h1>

                <form className="w-full" onSubmit={handleSubmit}>
                    <div className="mb-6">
                        <label className="block mb-2 font-semibold">Title *</label>
                        <input
                            className="w-full px-3 py-3 text-base border border-gray-300 rounded-md"
                            type="text"
                            name="title"
                            value={formData.title}
                            onChange={handleChange}
                            placeholder="Insert document title"
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
                            className="w-full px-3 py-3 text-base border border-gray-300 rounded-md font-mono resize-y"
                            name="mdContent"
                            value={formData.mdContent}
                            onChange={handleChange}
                            rows={15}
                            placeholder="Insert document content. You can use Markdown syntax to format your text."
                        />
                        <ErrorMessage message={errors.content} type="field" />
                    </div>

                    <div className="mb-6">
                        <label className="inline-flex items-center gap-2 cursor-pointer font-semibold">
                            <input
                                className="w-4 h-4"
                                type="checkbox"
                                name="isPrivate"
                                checked={formData.isPrivate}
                                onChange={handleChange}
                            />
                            Private document (only visible to you)
                        </label>
                    </div>

                    <ErrorMessage message={errors.general} type="general" />

                    <button
                        className="px-6 py-3 bg-blue-500 text-white rounded-md cursor-pointer disabled:bg-gray-300 disabled:cursor-not-allowed"
                        type="submit"
                        disabled={isSubmitting || !auth.isAuthenticated}
                    >
                        {isSubmitting
                            ? 'Submitting...'
                            : !auth.isAuthenticated
                                ? <> <FontAwesomeIcon icon={faBan} /> Unauthorized</>
                                : <> <FontAwesomeIcon icon={faCirclePlus} /> Create</>
                        }
                    </button>
                </form>
            </div>
        </>
    );
}

export { CreateDocument };
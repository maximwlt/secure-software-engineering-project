import { useAuth } from '../utils/useAuth';
import React, { useState } from 'react';
import { useNavigate } from 'react-router';
import { apiFetch } from '../utils/apiFetch';
import ErrorMessage from './ErrorMessage';
import Navbar from './Navbar';
import '../styling/CreateDocuments.css';

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

    const handleSubmit = async (e: React.FormEvent): Promise<void> => {
        e.preventDefault();

        if (!auth.isAuthenticated) {
            setErrors({ general: 'Sie müssen angemeldet sein, um ein Dokument zu erstellen.' });
            return;
        }

        const newErrors: Errors = {};
        if (!formData.title.trim()) {
            newErrors.title = 'Titel ist erforderlich';
        } else if (formData.title.length > 200) {
            newErrors.title = 'Titel darf maximal 200 Zeichen haben';
        }

        if (!formData.mdContent.trim()) {
            newErrors.content = 'Inhalt ist erforderlich';
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
                const errorData = await response.json();
                throw new Error(errorData.message || 'Dokument konnte nicht erstellt werden');
            }

            const data = await response.json();
            navigate(data.noteId ? `/documents/${data.noteId}` : '/documents/public');

        } catch (error) {
            setErrors({
                general: error instanceof Error ? error.message : 'Ein Fehler ist aufgetreten'
            });
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <>
            <Navbar />
            <div className="create-document-container">
                <h1>Neues Dokument erstellen</h1>

                <form className="create-document-form" onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label className="form-label">Titel *</label>
                        <input
                            className="form-input"
                            type="text"
                            name="title"
                            value={formData.title}
                            onChange={handleChange}
                            placeholder="Dokumenttitel eingeben"
                        />
                        <ErrorMessage message={errors.title} type="field" />
                    </div>

                    <div className="form-group">
                        <label className="form-label">
                            Inhalt * (Markdown unterstützt)
                        </label>
                        <textarea
                            className="form-textarea"
                            name="mdContent"
                            value={formData.mdContent}
                            onChange={handleChange}
                            rows={15}
                            placeholder="Dokumentinhalt eingeben"
                        />
                        <ErrorMessage message={errors.content} type="field" />
                    </div>

                    <div className="form-group">
                        <label className="checkbox-label">
                            <input
                                type="checkbox"
                                name="isPrivate"
                                checked={formData.isPrivate}
                                onChange={handleChange}
                            />
                            Privat sichtbar
                        </label>
                    </div>

                    <ErrorMessage message={errors.general} type="general" />

                    <button
                        className="submit-button"
                        type="submit"
                        disabled={isSubmitting || !auth.isAuthenticated}
                    >
                        {isSubmitting
                            ? 'Wird erstellt...'
                            : !auth.isAuthenticated
                                ? 'Anmeldung erforderlich'
                                : 'Dokument erstellen'}
                    </button>
                </form>
            </div>
        </>
    );
}

export { CreateDocument };

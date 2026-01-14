import { useAuth } from '../utils/useAuth';
import React, { useState } from 'react';
import { useNavigate } from 'react-router';
import { apiFetch } from '../utils/apiFetch';
import ErrorMessage from './ErrorMessage';
import Navbar from "./Navbar.tsx";

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
            const checked = (e.target as HTMLInputElement).checked;
            setFormData(prev => ({
                ...prev,
                [name]: checked
            }));
        } else {
            setFormData(prev => ({
                ...prev,
                [name]: value
            }));
        }

        // Clear error when user types
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
            setErrors({
                general: 'Sie müssen angemeldet sein, um ein Dokument zu erstellen.'
            });
            return;
        }

        // Validation
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
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(formData)
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || 'Dokument konnte nicht erstellt werden');
            }

            const data = await response.json();

            // Redirect zum erstellten Dokument
            if (data.noteId) {
                navigate(`/documents/${data.noteId}`);
            } else {
                navigate('/documents/public');
            }

        } catch (error) {
            console.error("Submit error:", error);
            setErrors({
                general: error instanceof Error ? error.message : 'Ein Fehler ist aufgetreten'
            });
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <>
            <Navbar/>
            <div style={{maxWidth: '800px', margin: '2rem auto', padding: '0 1rem'}}>
                <h1>Neues Dokument erstellen</h1>

                <form onSubmit={handleSubmit}>
                    <div style={{marginBottom: '1.5rem'}}>
                        <label style={{display: 'block', marginBottom: '0.5rem', fontWeight: 600}}>
                            Titel *
                        </label>
                        <input
                            type="text"
                            name="title"
                            value={formData.title}
                            onChange={handleChange}
                            placeholder="Dokumenttitel eingeben"
                            style={{
                                width: '100%',
                                padding: '0.75rem',
                                fontSize: '1rem',
                                border: '1px solid #ddd',
                                borderRadius: '6px'
                            }}/>
                        <ErrorMessage message={errors.title} type="field"/>
                    </div>

                    <div style={{marginBottom: '1.5rem'}}>
                        <label style={{display: 'block', marginBottom: '0.5rem', fontWeight: 600}}>
                            Inhalt * (Markdown unterstützt)
                        </label>
                        <textarea
                            name="mdContent"
                            value={formData.mdContent}
                            onChange={handleChange}
                            placeholder="Dokumentinhalt eingeben"
                            rows={15}
                            style={{
                                width: '100%',
                                padding: '0.75rem',
                                fontSize: '1rem',
                                border: '1px solid #ddd',
                                borderRadius: '6px',
                                fontFamily: "'Courier New', monospace",
                                resize: 'vertical'
                            }}/>
                        <ErrorMessage message={errors.content} type="field"/>
                    </div>

                    <div style={{marginBottom: '1.5rem'}}>
                        <label style={{display: 'flex', alignItems: 'center', gap: '0.5rem', cursor: 'pointer'}}>
                            <input
                                type="checkbox"
                                name="isPrivate"
                                checked={formData.isPrivate}
                                onChange={handleChange}/>
                            <span style={{fontWeight: 600}}>Privat sichtbar</span>
                        </label>
                    </div>

                    <ErrorMessage message={errors.general} type="general"/>

                    <button
                        type="submit"
                        disabled={isSubmitting || !auth.isAuthenticated}
                        style={{
                            padding: '0.75rem 1.5rem',
                            backgroundColor: (isSubmitting || !auth.isAuthenticated) ? '#ccc' : '#2196f3',
                            color: 'white',
                            border: 'none',
                            borderRadius: '6px',
                            cursor: (isSubmitting || !auth.isAuthenticated) ? 'not-allowed' : 'pointer'
                        }}
                    >
                        {isSubmitting ? 'Wird erstellt...' :
                            !auth.isAuthenticated ? 'Anmeldung erforderlich' : 'Dokument erstellen'}
                    </button>
                </form>
            </div>
        </>
    );
}

export { CreateDocument };
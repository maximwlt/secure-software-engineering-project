import React, { useState } from 'react';
import ErrorMessage from "./ErrorMessage";
import {zxcvbn, zxcvbnOptions } from "@zxcvbn-ts/core";
import * as common from "@zxcvbn-ts/language-common";
import * as de from "@zxcvbn-ts/language-de";
import { useNavigate } from 'react-router';

zxcvbnOptions.setOptions({
    translations: de.translations,
    dictionary: {
        ...common.dictionary,
        ...de.dictionary
    }
})

// Types definieren
interface FormData {
    email: string;
    password: string;
}

interface Errors {
    email?: string;
    password?: string;
    general?: string;
}

async function submitRegister(
    formData: FormData,
    setIsSubmitting: (value: boolean) => void,
    setErrors: (value: Errors) => void,
    setIsSuccess: (value: boolean) => void
) {

    const newErrors: Errors = {};

    if (!formData.email) {
        newErrors.email = 'Email ist erforderlich';
    } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
        newErrors.email = 'Ungültige Email-Adresse';
    }

    const passwordStrength = zxcvbn(formData.password);
    if (!formData.password) {
        newErrors.password = 'Passwort ist erforderlich';
    } else if (passwordStrength.score < 3) {
        newErrors.password = 'Passwort ist zu schwach';
    }

    if (Object.keys(newErrors).length > 0) {
        setErrors(newErrors);
        return;
    }

    setIsSubmitting(true);
    setErrors({});

    try {
        const response = await fetch('/api/auth/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(formData)
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Registrierung fehlgeschlagen');
        }

        const data = await response.json();
        console.log('Registrierungsantwort: %o', data);

        // Success anzeigen
        setIsSuccess(true);

    } catch (error) {
        setErrors({
            general: error instanceof Error ? error.message : 'Ein Fehler ist aufgetreten'
        });
    } finally {
        setIsSubmitting(false);
    }
}

function RegisterPage() {
    const navigate = useNavigate();
    const [formData, setFormData] = useState<FormData>({
        email: '',
        password: ''
    });

    const [errors, setErrors] = useState<Errors>({});
    const [isSubmitting, setIsSubmitting] = useState<boolean>(false);
    const [isSuccess, setIsSuccess] = useState<boolean>(false);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>): void => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));

        if (errors[name as keyof Errors]) {
            setErrors(prev => ({
                ...prev,
                [name]: ''
            }));
        }
    };

    const handleSubmit = (): void => {
        submitRegister(formData, setIsSubmitting, setErrors, setIsSuccess).catch(
            error => {
                setErrors({
                    general: error instanceof Error ? error.message : 'Ein Fehler ist aufgetreten'
                });
            }
        )
    };

    // Success-Ansicht nach erfolgreicher Registrierung
    if (isSuccess) {
        return (
            <div style={{
                maxWidth: '600px',
                margin: '3rem auto',
                padding: '2rem',
                textAlign: 'center'
            }}>
                <div style={{
                    backgroundColor: '#e8f5e9',
                    border: '2px solid #4caf50',
                    borderRadius: '12px',
                    padding: '2rem',
                    marginBottom: '2rem'
                }}>
                    <h1 style={{ color: '#2e7d32', marginBottom: '1rem' }}>
                        Registrierung erfolgreich!
                    </h1>
                    <p style={{ fontSize: '1.1rem', color: '#333', lineHeight: '1.6' }}>
                        Wir haben dir eine Bestätigungs-E-Mail an <strong>{formData.email}</strong> gesendet.
                    </p>
                    <p style={{ fontSize: '1rem', color: '#666', marginTop: '1rem' }}>
                        Bitte überprüfe dein E-Mail-Postfach und klicke auf den Bestätigungslink,
                        um dein Konto zu aktivieren.
                    </p>
                </div>


                <button
                    onClick={() => navigate('/login')}
                    style={{
                        padding: '0.75rem 2rem',
                        fontSize: '1rem',
                        backgroundColor: '#2196f3',
                        color: 'white',
                        border: 'none',
                        borderRadius: '6px',
                        cursor: 'pointer',
                        fontWeight: 600
                    }}
                >
                    Zum Login
                </button>
            </div>
        );
    }

    // Registrierungs-Formular
    return (
        <div style={{ maxWidth: '500px', margin: '2rem auto', padding: '0 1rem' }}>
            <h1>Registrierung</h1>

            <div style={{ marginBottom: '1.5rem' }}>
                <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: 600 }}>
                    Email-Adresse:
                </label>
                <input
                    type="email"
                    name="email"
                    value={formData.email}
                    onChange={handleChange}
                    placeholder="E-Mail Adresse eingeben"
                    style={{
                        width: '100%',
                        padding: '0.75rem',
                        fontSize: '1rem',
                        border: '1px solid #ddd',
                        borderRadius: '6px'
                    }}
                />
                <ErrorMessage
                    message={errors.email}
                    type="field"
                />
            </div>

            <div style={{ marginBottom: '1.5rem' }}>
                <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: 600 }}>
                    Passwort:
                </label>
                <input
                    type="password"
                    name="password"
                    value={formData.password}
                    onChange={handleChange}
                    placeholder="Passwort eingeben"
                    style={{
                        width: '100%',
                        padding: '0.75rem',
                        fontSize: '1rem',
                        border: '1px solid #ddd',
                        borderRadius: '6px'
                    }}
                />
                <ErrorMessage
                    message={errors.password}
                    type="field"
                />
            </div>

            <ErrorMessage
                message={errors.general}
                type="general"
            />

            <button
                onClick={handleSubmit}
                disabled={isSubmitting}
                style={{
                    width: '100%',
                    padding: '0.75rem',
                    fontSize: '1rem',
                    backgroundColor: isSubmitting ? '#ccc' : '#2196f3',
                    color: 'white',
                    border: 'none',
                    borderRadius: '6px',
                    cursor: isSubmitting ? 'not-allowed' : 'pointer',
                    fontWeight: 600
                }}
            >
                {isSubmitting ? 'Lädt...' : 'Registrieren'}
            </button>

            <p style={{
                textAlign: 'center',
                marginTop: '1.5rem',
                color: '#666'
            }}>
                Bereits registriert?{' '}
                <span
                    onClick={() => navigate('/login')}
                    style={{
                        color: '#2196f3',
                        cursor: 'pointer',
                        textDecoration: 'underline'
                    }}
                >
                    Zum Login
                </span>
            </p>
        </div>
    );
}

export default RegisterPage;
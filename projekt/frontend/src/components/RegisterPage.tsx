import React, { useState } from 'react';
import ErrorMessage from "./ErrorMessage";
import {zxcvbn, zxcvbnOptions, type ZxcvbnResult} from "@zxcvbn-ts/core";
import * as common from "@zxcvbn-ts/language-common";
import * as de from "@zxcvbn-ts/language-de";
import { useNavigate } from 'react-router';
import "../styling/RegisterPage.css";
import Navbar from "./Navbar.tsx";
import type {ErrorType} from "../types/ErrorType.ts";
import ApiErrorMessage from "./ApiErrorMessage.tsx";

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
    setIsSuccess: (value: boolean) => void,
    setApiError: React.Dispatch<React.SetStateAction<Partial<ErrorType> | undefined>>
) {

    const newErrors: Errors = {};

    if (!formData.email) {
        newErrors.email = 'Email ist erforderlich';
    } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
        newErrors.email = 'Ungültige Email-Adresse';
    }

    const passwordStrength : ZxcvbnResult = zxcvbn(formData.password);
    if (!formData.password) {
        newErrors.password = 'Passwort ist erforderlich';
    }
    else {
        switch (passwordStrength.score) {
            case 0: newErrors.password = 'Passwort ist sehr schwach'; break;
            case 1: newErrors.password = 'Passwort ist schwach'; break;
            case 2: newErrors.password = 'Passwort ist akzeptabel'; break;
            case 3: newErrors.password = 'Passwort ist stark'; break;
            case 4: break;
            default: break;
        }
    }



    if (Object.keys(newErrors).length > 0) {
        setErrors(newErrors);
        return;
    }

    setIsSubmitting(true);
    setErrors({});
    setApiError(undefined)

    try {
        const response = await fetch('/api/auth/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(formData)
        });

        if (response.status === 429) {
            const errorData : ErrorType = await response.json();
            setApiError(errorData);
            return;
        }

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Registrierung fehlgeschlagen');
        }

        // const data = await response.json();
        // console.log('Registrierungsantwort: %o', data);

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
    const [formData, setFormData] = useState<FormData>({email: '', password: ''});
    const [errors, setErrors] = useState<Errors>({});
    const [isSubmitting, setIsSubmitting] = useState<boolean>(false);
    const [isSuccess, setIsSuccess] = useState<boolean>(false);
    const [passwordData, setPasswordData] = useState<ZxcvbnResult | null>(null);
    const [apiError, setApiError] = useState<Partial<ErrorType> | undefined>(undefined);

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
        if (name === 'password') {
            const strength : ZxcvbnResult = zxcvbn(value);
            setPasswordData(strength);
        }
    };

    const handleSubmit = (): void => {
        submitRegister(formData, setIsSubmitting, setErrors, setIsSuccess, setApiError).catch(
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
            <>
                <Navbar />
                <div className="register-success-wrapper">
                    <div className="register-success-box">
                        <h1>Registrierung erfolgreich!</h1>
                        <p>
                            Wir haben dir eine Bestätigungs-E-Mail an
                            <strong> {formData.email}</strong> gesendet.
                        </p>
                        <p className="hint">
                            Bitte bestätige deine E-Mail, um dein Konto zu aktivieren.
                        </p>
                    </div>

                    <button
                        className="primary-button"
                        onClick={() => navigate('/login')}
                    >
                        Zum Login
                    </button>
                </div>
            </>
        );
    }

    // Registrierungs-Formular
    return (
        <>
            <Navbar />
            <div className="register-wrapper">
                <h1>Registrierung</h1>

                <div className="form-group">
                    <label>Email-Adresse</label>
                    <input
                        type="email"
                        name="email"
                        value={formData.email}
                        onChange={handleChange}
                        placeholder="E-Mail Adresse eingeben"
                    />
                    <ErrorMessage message={errors.email} type="field" />
                </div>

                <div className="form-group">
                    <label>Passwort</label>
                    <input
                        type="password"
                        name="password"
                        value={formData.password}
                        onChange={handleChange}
                        placeholder="Passwort eingeben"
                    />
                    <progress max={4} value={passwordData?.score ?? 0} className="password-strength-bar"></progress>
                    {passwordData && (
                        <div className="password-feedback">
                            {passwordData.feedback.warning && (
                                <p className="warning"> ⚠️ {passwordData.feedback.warning}</p>
                            )}
                            {passwordData.feedback.suggestions.map((s, i) => (
                                <p key={i} className="suggestion">💡 {s}</p>
                            ))}
                        </div>
                    )}
                    <ErrorMessage message={errors.password} type="field" />
                </div>

                <ApiErrorMessage error={apiError} />
                <ErrorMessage message={errors.general} type="general" />

                <button
                    className={`primary-button ${isSubmitting ? 'disabled' : ''}`}
                    onClick={handleSubmit}
                    disabled={isSubmitting}
                >
                    {isSubmitting ? 'Lädt...' : 'Registrieren'}
                </button>

                <p className="login-hint">
                    Bereits registriert?{' '}
                    <span onClick={() => navigate('/login')}>
                        Zum Login
                    </span>
                </p>
            </div>
        </>
    );
}

export default RegisterPage;
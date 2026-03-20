import React, { useState } from 'react';
import ErrorMessage from "./ErrorMessage";
import { useAuth } from "../utils/useAuth.ts";
import {getCookie} from "../utils/cookies.ts";
import Navbar from "./Navbar.tsx";
import "../styling/DocumentDetailPage.css";
import {apiFetch} from "../utils/apiFetch.ts";
import {NavLink, redirect} from "react-router";
import type {ErrorType} from "../types/ErrorType.ts";
import ApiErrorMessage from "./ApiErrorMessage.tsx";


interface FormData {
    email: string;
    password: string;
}

interface Errors {
    email?: string;
    password?: string;
    general?: string;
}

function LoginPage() {
    const { login, logout, isAuthenticated } = useAuth();
    const [formData, setFormData] = useState<FormData>({
        email: '',
        password: ''
    });
    const auth = useAuth();

    const [deletePassword, setDeletePassword] = useState("");
    const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);

    const [errors, setErrors] = useState<Errors>({});
    const [apiError, setApiError] = useState<Partial<ErrorType> | undefined>(undefined);
    const [isSubmitting, setIsSubmitting] = useState<boolean>(false);
    const [cookieConsent, setCookieConsent] = useState<boolean>(false);

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
        if (!cookieConsent) {
            setErrors({ general: 'Bitte stimmen Sie der Verwendung von notwendigen Cookies zu.' });
            return;
        }

        setApiError(undefined);

        submitLogin(formData, setIsSubmitting, setErrors, setApiError, login).catch(
            error => {
                setErrors({
                    general: error instanceof Error ? error.message : 'Ein Fehler ist aufgetreten'
                });
            }
        )
    };

    const handleLogout = async (): Promise<void> => {
        setIsSubmitting(true);
        setErrors({});

        try {

            const csrf_token = getCookie("XSRF-TOKEN");

            const headers : HeadersInit = {
                'Content-Type': 'application/json',
            };
            if (csrf_token) {
                headers['X-XSRF-TOKEN'] = csrf_token;
            }

            const response = await fetch('/api/auth/rt/logout', {
                method: 'POST',
                headers: headers,
                credentials: 'include',
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || 'Logout fehlgeschlagen');
            }

            logout();

        } catch (error) {
            setErrors({
                general: error instanceof Error ? error.message : 'Ein Fehler ist aufgetreten'
            });
        } finally {
            setIsSubmitting(false);
        }
    };




    const handleDeleteAccount = async (): Promise<void> => {
        if (!deletePassword) {
            setErrors({ general: "Bitte Passwort eingeben." });
            return;
        }
        /*const confirmed = window.confirm(
            "Dein Konto wird gelöscht. Fortfahren?"
        );
        if (!confirmed) return;*/

        try {
            const res = await apiFetch(auth, '/api/auth/me', {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ password: deletePassword })
            })
            if (!res.ok) {
                const errorData = await res.json();
                throw new Error(errorData.message || 'Konto-Löschung fehlgeschlagen');
            }

            setErrors({})
            // Nach erfolgreicher Löschung ausloggen
            logout();
            redirect('/');
        } catch (error) {
            setErrors({
                general: error instanceof Error ? error.message : 'Ein Fehler ist aufgetreten'
            });
        }
    };

    // Wenn eingeloggt: Logout-View anzeigen
    if (isAuthenticated) {
        return (
            <>
                <Navbar/>
                <div className="register-wrapper">
                    <h1>Sie sind angemeldet</h1>

                    <ErrorMessage
                        message={errors.general}
                        type="general"
                    />
                    <button className="primary-button"
                        onClick={handleLogout}
                        disabled={isSubmitting}
                    >
                        {isSubmitting ? 'Wird abgemeldet...' : 'Abmelden'}
                    </button>

                    <button
                        className="delete-button"
                        onClick={() => setShowDeleteConfirm(!showDeleteConfirm)}
                    >
                        Nutzerkonto löschen
                    </button>

                    {showDeleteConfirm && (
                        <div className="delete-confirm-box">
                            <p>Bitte Passwort zur Bestätigung eingeben:</p>
                            <input
                                type="password"
                                value={deletePassword}
                                onChange={(e) => setDeletePassword(e.target.value)}
                                placeholder="Passwort"
                            />

                            <button
                                className="delete-button"
                                onClick={handleDeleteAccount}
                                disabled={isSubmitting}
                            >
                                {isSubmitting ? "Lösche..." : "Konto endgültig löschen"}
                            </button>

                        </div>
                    )}


                </div>
            </>
        );
    }

    // Wenn nicht eingeloggt: Login-Form anzeigen
    return (
        <>
            <Navbar/>
            <div className="register-wrapper">
                <h1>Login Page</h1>

                <div className="form-group">
                    <label>Email-Adresse </label>
                    <input
                        type="email"
                        name="email"
                        value={formData.email}
                        onChange={handleChange}
                        placeholder="E-Mail Adresse eingeben"/>
                    <ErrorMessage
                        message={errors.email}
                        type="field"/>
                </div>

                <div className="form-group">
                    <label>Passwort </label>
                    <input
                        type="password"
                        name="password"
                        value={formData.password}
                        onChange={handleChange}
                        placeholder="Passwort eingeben"/>
                    <ErrorMessage
                        message={errors.password}
                        type="field"/>
                </div>

                <div>
                    <label>
                        <input
                            type="checkbox"
                            checked={cookieConsent}
                            onChange={(e) => setCookieConsent(e.target.checked)}
                            required
                        />
                        {' '}Ich stimme der Verwendung von notwendigen Cookies zu.
                    </label>
                </div>

                <ApiErrorMessage error={apiError} />

                <ErrorMessage
                    message={errors.general}
                    type="general"/>

                <button className="primary-button" onClick={handleSubmit} disabled={isSubmitting}>
                    {isSubmitting ? 'Lädt...' : 'Login'}
                </button>

                <NavLink to="/forgot-password">Forgot password?</NavLink>
            </div>
        </>
    );
}

async function submitLogin(
    formData: FormData,
    setIsSubmitting: React.Dispatch<React.SetStateAction<boolean>>,
    setErrors: React.Dispatch<React.SetStateAction<Errors>>,
    setApiError: React.Dispatch<React.SetStateAction<Partial<ErrorType> | undefined>>,
    login: (token: string) => void
): Promise<void> {
    const newErrors: Errors = {};

    if (!formData.email) {
        newErrors.email = 'Email address is required.';
    } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
        newErrors.email = 'Invalid email address.';
    }

    if (!formData.password) {
        newErrors.password = 'Password is required.';
    }

    if (Object.keys(newErrors).length > 0) {
        setErrors(newErrors);
        return;
    }

    setIsSubmitting(true);
    setErrors({});

    try {
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(formData)
        });
        // Rate Limit CHECK (aus Proxy)
        if (response.status === 429) {
            const errorData : ErrorType = await response.json();
            setApiError(errorData);
            return;
        }

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Falsche E-Mail Adresse oder Passwort');
        }

        const data = await response.json();

        if (!data.accessToken) {
            throw new Error('Kein Zugriffstoken im Antwortkörper gefunden');
        }

        // Token im Context speichern (In-Memory)
        login(data.accessToken);

    } catch (error) {
        setErrors({
            general: error instanceof Error ? error.message : 'Ein Fehler ist aufgetreten'
        });
    } finally {
        setIsSubmitting(false);
    }
}

export default LoginPage;
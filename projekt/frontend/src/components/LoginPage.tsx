import React, { useState } from 'react';
import ErrorMessage from "./ErrorMessage";
import { useAuth } from "../shared/utils/useAuth.ts";
import Navbar from "./Navbar.tsx";
import "../shared/styling/DocumentDetailPage.css";
import {Navigate, NavLink} from "react-router";
import ApiErrorMessage from "../shared/components/ApiErrorMessage.tsx";
import type {ApiErrorType} from "../shared/types/ProblemDetail/ApiErrorType.ts";
import {isDetailError} from "../shared/types/ProblemDetail/IsErrorTypeGuards.ts";


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
    const { login, isAuthenticated } = useAuth();
    const [formData, setFormData] = useState<FormData>({
        email: '',
        password: ''
    });
    const [errors, setErrors] = useState<Errors>({});
    const [apiError, setApiError] = useState<ApiErrorType>();
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
            setErrors({ general: 'Please accept the cookie consent to proceed.' });
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


    if (isAuthenticated) {
        return <Navigate to="/users/profile" replace />;
    }

    // Wenn nicht eingeloggt: Login-Form anzeigen
    return (
        <>
            <Navbar/>
            <div className="auth-form-wrapper">
                <h1>Login Page</h1>

                <div className="form-group">
                    <label>Email-Address </label>
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
                    <label>Password </label>
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
                        {' '}I consent to the use of cookies for authentication purposes.
                    </label>
                </div>

                <ApiErrorMessage error={apiError} />

                <ErrorMessage
                    message={errors.general}
                    type="general"/>

                <button className="primary-button" onClick={handleSubmit} disabled={isSubmitting}>
                    {isSubmitting ? 'Loading...' : 'Login'}
                </button>

                <div className="flex justify-center mt-2">
                    <NavLink to="/forgot-password" className="bg-gray-200 hover:bg-gray-300 rounded-2xl px-3 py-1">Forgot password?</NavLink>
                </div>

            </div>
        </>
    );
}

async function submitLogin(
    formData: FormData,
    setIsSubmitting: React.Dispatch<React.SetStateAction<boolean>>,
    setErrors: React.Dispatch<React.SetStateAction<Errors>>,
    setApiError: React.Dispatch<React.SetStateAction<ApiErrorType | undefined>>,
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
            const errorData : ApiErrorType = await response.json();
            setApiError(errorData);
            return;
        }

        if (!response.ok) {
            const errorData : ApiErrorType  = await response.json();
            if (isDetailError(errorData)) {
                setErrors({ general: errorData.detail });
                throw new Error(errorData.detail);
            }
            throw new Error(errorData.title); // Fallback
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
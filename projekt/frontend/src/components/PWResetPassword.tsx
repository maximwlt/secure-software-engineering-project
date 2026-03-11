import React, {useEffect} from "react";
import type {ErrorType} from "../types/ErrorType.ts";
import type {MessageType} from "../types/MessageType.ts";
import Navbar from "./Navbar.tsx";
import ApiErrorMessage from "./ApiErrorMessage.tsx";
import ErrorMessage from "./ErrorMessage.tsx";
import type {FormErrorType} from "../types/FormErrorType.ts";
import {NavLink, useSearchParams} from "react-router";

type PageState = "validating" | "form" | "success" | "error";

export function PWResetPassword() {
    const [searchParams] = useSearchParams();
    const token : string | null = searchParams.get('token');

    const [pageState, setPageState] = React.useState<PageState>('validating');
    const [formData, setFormData] = React.useState({
        password: '',
        password_confirm: ''
    });

    const [errors, setErrors] = React.useState<FormErrorType>({});
    const [tokenError, setTokenError] = React.useState<Partial<ErrorType> | null>(null);
    const [isSubmitting, setIsSubmitting] = React.useState(false);
    const [responseMessage, setResponseMessage] = React.useState<string | null>(null);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const {name, value} = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    }


    useEffect(() => {
        const validateToken = async () => {

            try {
                const response: Response = await fetch(`/api/auth/reset-password?token=${encodeURIComponent(token || '')}`, {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                });

                if (!response.ok) {
                    const errorData: ErrorType = await response.json();
                    setTokenError(errorData);
                    setPageState("error");
                    return;
                }
                setPageState("form");
            } catch {
                setTokenError({title: "Token-Validierung fehlgeschlagen", detail: "Es gab ein Problem bei der Validierung des Tokens. Bitte versuchen Sie es erneut."});
                setPageState("error");
            }
        };

        validateToken();
    }, [token]);

    const handleSubmit = async () => {


        if (formData.password !== formData.password_confirm) {
            setErrors({general: 'Passwords do not match'});
            return;
        }

        if (!formData.password) {
            setErrors({general: 'Password cannot be empty'});
            return;
        }
        setIsSubmitting(true);
        setErrors({});


        try {
            const response: Response = await fetch('/api/auth/reset-password', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    token: token,
                    newPassword: formData.password
                })
            });

            if (response.status === 429) {
                const errorData: ErrorType = await response.json();
                setErrors({api: errorData});
                return;
            }

            if (!response.ok) {
                const errorData: ErrorType = await response.json();
                setErrors({api: errorData});
                return;
            }
            const data: MessageType = await response.json();
            setResponseMessage(data.message);
            setPageState("success");
        } catch (error) {
            setErrors({general: error instanceof Error ? error.message : 'Ein Fehler ist aufgetreten'});
        } finally {
            setIsSubmitting(false);
        }
    };


    if (pageState === "validating") {
        return (
            <>
                <Navbar/>
                <div className="register-wrapper">
                    <h1> Password Reset</h1>
                    <p>Validating token...</p>
                </div>
            </>
        );
    }

    if (pageState === "error") {
        return (
            <>
                <Navbar/>
                <div className="register-wrapper">
                    <h1>Password Reset</h1>
                    <ApiErrorMessage error={tokenError ?? undefined} />
                </div>
            </>
        );
    }

    if (pageState === "success") {
        return (
            <>
                <Navbar/>
                <div className="register-wrapper">
                    <h1>Password Reset</h1>
                    {responseMessage && <div className="success-message">{responseMessage}</div>}
                    <NavLink to="/login" className="primary-button">Go to Login</NavLink>
                </div>
            </>
        );
    }

    return (
        <>
            <Navbar/>
            <div className="register-wrapper">
                <h1>Set new password</h1>

                <div className="form-group">
                    <label>New password</label>
                    <input
                        type="password"
                        name="password"
                        value={formData.password}
                        onChange={handleChange}
                        placeholder="Enter new password"
                    />

                    <label>Confirm password</label>
                    <input
                        type="password"
                        name="password_confirm"
                        value={formData.password_confirm}
                        onChange={handleChange}
                        placeholder="Confirm new password"
                    />
                </div>

                <ApiErrorMessage error={errors.api} />
                <ErrorMessage message={errors.general} type="general"/>
                {responseMessage && <div className="success-message">{responseMessage}</div>}

                <button
                    className={`primary-button ${isSubmitting ? 'disabled' : ''}`}
                    onClick={handleSubmit}
                    disabled={isSubmitting}
                >
                    {isSubmitting ? 'Lädt...' : 'Send'}
                </button>

            </div>
        </>
    );
}
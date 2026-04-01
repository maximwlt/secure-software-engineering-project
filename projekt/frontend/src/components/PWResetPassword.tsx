import React, {useEffect} from "react";
import type {MessageType} from "../shared/types/MessageType.ts";
import Navbar from "./Navbar.tsx";
import ApiErrorMessage from "./ApiErrorMessage.tsx";
import ErrorMessage from "./ErrorMessage.tsx";
import type {FormErrorType} from "../shared/types/FormErrorType.ts";
import {useNavigate, useSearchParams} from "react-router";
import type {ApiErrorType} from "../shared/types/ProblemDetail/ApiErrorType.ts";
import type {DetailError} from "../shared/types/ProblemDetail/DetailError.ts";
import {SuccessWrapper} from "./SuccessWrapper.tsx";

type PageState = "validating" | "form" | "success" | "error";

export function PWResetPassword() {
    const [searchParams] = useSearchParams();
    const token : string | null = searchParams.get('token');

    const [pageState, setPageState] = React.useState<PageState>('validating');
    const [formData, setFormData] = React.useState({
        password: '',
        password_confirm: ''
    });

    const navigate = useNavigate();
    const [errors, setErrors] = React.useState<FormErrorType>({});
    const [tokenError, setTokenError] = React.useState<ApiErrorType | undefined>(undefined);
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
                    const errorData : ApiErrorType = await response.json();
                    setTokenError(errorData);
                    setPageState("error");
                    return;
                }
                setPageState("form");
            } catch {
                // setTokenError();
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
                const errorData : ApiErrorType = await response.json() as DetailError;
                setErrors({api: errorData});
                return;
            }

            if (!response.ok) {
                const errorData: ApiErrorType = await response.json();
                setErrors({api: errorData});
                return;
            }
            const data: MessageType = await response.json();
            setResponseMessage(data.message);
            setPageState("success");
        } catch (error) {
            setErrors({general: error instanceof Error ? error.message : 'An error occurred'});
        } finally {
            setIsSubmitting(false);
        }
    };


    if (pageState === "validating") {
        return (
            <>
                <Navbar/>
                <div className="auth-form-wrapper">
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
                <div className="auth-form-wrapper">
                    <h1>Password Reset</h1>
                    <ApiErrorMessage error={tokenError} />
                </div>
            </>
        );
    }

    if (pageState === "success") {
        return (
            <>
                <Navbar/>
                <SuccessWrapper buttonLabel="To Login" onButtonClick={() => navigate('/login')}>
                    <h1>Password Reset</h1>
                    {responseMessage && <div className="success-message">{responseMessage}</div>}
                </SuccessWrapper>
            </>
        );
    }

    return (
        <>
            <Navbar/>
            <div className="auth-form-wrapper">
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
                    {isSubmitting ? 'Loading...' : 'Send'}
                </button>

            </div>
        </>
    );
}
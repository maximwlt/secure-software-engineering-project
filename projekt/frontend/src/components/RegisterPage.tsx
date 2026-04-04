import React, { useState } from 'react';
import ErrorMessage from "./ErrorMessage";
import {zxcvbn, zxcvbnOptions, type ZxcvbnResult} from "@zxcvbn-ts/core";
import * as common from "@zxcvbn-ts/language-common";
import * as de from "@zxcvbn-ts/language-de";
import { useNavigate } from 'react-router';
import Navbar from "./Navbar.tsx";
import ApiErrorMessage from "../shared/components/ApiErrorMessage.tsx";
import {faLightbulb, faTriangleExclamation} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import LoadingBar from "../shared/components/LoadingBar.tsx";
import type {ApiErrorType} from "../shared/types/ProblemDetail/ApiErrorType.ts";
import type {DetailError} from "../shared/types/ProblemDetail/DetailError.ts";
import {SuccessWrapper} from "./SuccessWrapper.tsx";

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
    setApiError: React.Dispatch<React.SetStateAction<ApiErrorType | undefined>>
) {

    const newErrors: Errors = {};

    if (!formData.email) {
        newErrors.email = 'Email is required';
    } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
        newErrors.email = 'Invalid email address';
    }

    const passwordStrength : ZxcvbnResult = zxcvbn(formData.password);
    if (!formData.password) {
        newErrors.password = 'Password is required';
    }
    else {
        switch (passwordStrength.score) {
            case 0: newErrors.password = 'Password is too weak.'; break;
            case 1: newErrors.password = 'Password is weak.'; break;
            case 2: newErrors.password = 'Password is okay.'; break;
            case 3: newErrors.password = 'Password is good, but could be stronger.'; break;
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
            const errorData : ApiErrorType = await response.json() as DetailError;
            setApiError(errorData);
            return;
        }

        if (!response.ok) {
            const errorData : ApiErrorType = await response.json();
            setApiError(errorData);
            return;
        }

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
    const [apiError, setApiError] = useState<ApiErrorType | undefined>(undefined);
    const score = passwordData?.score ?? 0;

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
            (error : ApiErrorType) => {
                setErrors({general: error.title});
            }
        )
    };

    // Success-Ansicht nach erfolgreicher Registrierung
    if (isSuccess) {
        return (
            <>
                <Navbar />
                <SuccessWrapper buttonLabel="To Login" onButtonClick={() => navigate('/login')}>
                    <h1 className="text-2xl font-bold text-green-800 mb-4">
                        Registration successful!
                    </h1>
                    <p className="text-lg text-gray-700 leading-relaxed">
                        We have sent a confirmation email to <strong>{formData.email}</strong>.
                    </p>
                    <p className="text-base text-gray-500 mt-4">
                        Please confirm your email to activate your account.
                    </p>
                </SuccessWrapper>
            </>
        );
    }

    // Registrierungs-Formular
    return (
        <>
            <Navbar />
            <div className="auth-form-wrapper">
                <h1>Registration</h1>

                <div className="form-group">
                    <label>Email Address</label>
                    <input
                        type="email"
                        name="email"
                        value={formData.email}
                        onChange={handleChange}
                        placeholder="Enter Email Address"
                    />
                    <ErrorMessage message={errors.email} type="field" />
                </div>

                <div className="form-group">
                    <label>Password</label>
                    <input
                        type="password"
                        name="password"
                        value={formData.password}
                        onChange={handleChange}
                        placeholder="Enter Password"
                    />

                    <progress
                        max={4}
                        value={score}
                        className={`password-strength-bar strength-${score}`}
                    />

                    {passwordData && (
                        <div className="bg-gray-200 mt-1 text-sm">
                            {passwordData.feedback.warning && (
                                <p className="text-red-600 font-semibold"> <FontAwesomeIcon icon={faTriangleExclamation} /> {passwordData.feedback.warning}</p>
                            )}
                            {passwordData.feedback.suggestions.map((s, i) => (
                                <p key={i} className="text-yellow-600 ml-2"><FontAwesomeIcon icon={faLightbulb} /> {s}</p>
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
                    {isSubmitting ? <LoadingBar /> : 'Register'}
                </button>

                <p className="text-center mt-6 text-gray-500">
                    Already registered?{' '}
                    <span onClick={() => navigate('/login')} className="text-blue-500 cursor-pointer underline">
                        Login
                    </span>
                </p>
            </div>
        </>
    );
}

export default RegisterPage;
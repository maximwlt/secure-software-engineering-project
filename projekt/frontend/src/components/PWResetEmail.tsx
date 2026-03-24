import Navbar from "./Navbar.tsx";
import ErrorMessage from "./ErrorMessage.tsx";
import React from "react";
import type {MessageType} from "../types/MessageType.ts";
import type {ErrorType} from "../types/ErrorType.ts";
import ApiErrorMessage from "./ApiErrorMessage.tsx";
import type {FormErrorType} from "../types/FormErrorType.ts";



export function PWResetEmail() {


    const [formData, setFormData] = React.useState({
        email: ''
    });

    const [errors, setErrors] = React.useState<FormErrorType>({});
    const [isSubmitting, setIsSubmitting] = React.useState(false);
    const [responseMessage, setResponseMessage] = React.useState<string | null>(null);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const {name, value} = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    }


    const handleSubmit = async () => {
        setIsSubmitting(true);
        setErrors({});

        try {
            const response: Response = await fetch('/api/auth/forgot-password', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(formData)
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
        } catch (error) {
            setErrors({general: error instanceof Error ? error.message : 'Ein Fehler ist aufgetreten'});
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <>
            <Navbar/>
            <div className="auth-form-wrapper">
                <h1>Password Reset</h1>

                <div className="form-group">
                    <label>Email-Address</label>
                    <input
                        type="email"
                        name="email"
                        value={formData.email}
                        onChange={handleChange}
                        placeholder="Enter your email"
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
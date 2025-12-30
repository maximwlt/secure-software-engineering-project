import React, { useState } from 'react';
import ErrorMessage from "./ErrorMessage";


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
    const [formData, setFormData] = useState<FormData>({
        email: '',
        password: ''
    });

    const [errors, setErrors] = useState<Errors>({});
    const [isSubmitting, setIsSubmitting] = useState<boolean>(false);

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
        submitLogin(formData, setIsSubmitting, setErrors).catch(
            error => {
                setErrors({
                    general: error instanceof Error ? error.message : 'Ein Fehler ist aufgetreten'
                });
            }
        )
    };

    return (
        <div>
            <h1>Login Page</h1>

            <div>
                <label>Email-Adresse: </label>
                <input
                    type="email"
                    name="email"
                    value={formData.email}
                    onChange={handleChange}
                    placeholder="E-Mail Adresse eingeben"
                />
                <ErrorMessage
                    message={errors.email}
                    type="field"
                />
            </div>

            <div>
                <label>Passwort: </label>
                <input
                    type="password"
                    name="password"
                    value={formData.password}
                    onChange={handleChange}
                    placeholder="Password eingeben"
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

            <button onClick={handleSubmit} disabled={isSubmitting}>
                {isSubmitting ? 'Lädt...' : 'Login'}
            </button>
        </div>
    );
}

async function submitLogin(
    formData: FormData,
    setIsSubmitting: React.Dispatch<React.SetStateAction<boolean>>,
    setErrors: React.Dispatch<React.SetStateAction<Errors>>
): Promise<void> {
    // Validation
    const newErrors: Errors = {};

    if (!formData.email) {
        newErrors.email = 'Email ist erforderlich';
    } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
        newErrors.email = 'Ungültige Email-Adresse';
    }

    if (!formData.password) {
        newErrors.password = 'Passwort ist erforderlich';
    } else if (formData.password.length < 6) {
        newErrors.password = 'Passwort muss mindestens 6 Zeichen haben';
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
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(formData)
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Login fehlgeschlagen');
        }

        const data = await response.json();
        console.log('Erhaltene Daten:', data);

        if (!data.accessToken)  {
            throw new Error('Kein Zugriffstoken im Antwortkörper gefunden');
        }


        localStorage.setItem('jwt_token', data.accessToken);

        console.log('Login erfolgreich!');



    } catch (error) {
        setErrors({
            general: error instanceof Error ? error.message : 'Ein Fehler ist aufgetreten'
        });
    } finally {
        setIsSubmitting(false);
    }
}


export default LoginPage;

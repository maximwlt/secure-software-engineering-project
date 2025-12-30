import React, { useState } from 'react';
import ErrorMessage from "./ErrorMessage";
import {zxcvbn, zxcvbnOptions } from "@zxcvbn-ts/core";
import * as common from "@zxcvbn-ts/language-common";
import * as de from "@zxcvbn-ts/language-de";

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



async function submitRegister(formData: FormData, setIsSubmitting: (value: (((prevState: boolean) => boolean) | boolean)) => void, setErrors: (value: (((prevState: Errors) => Errors) | Errors)) => void) {

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
        console.log('Erhaltene Daten:', data);

        console.log('Registrierung erfolgreich! Bitte E-Mail bestätigen durch den Link in der E-Mail.');
    } catch (error) {
        setErrors({
            general: error instanceof Error ? error.message : 'Ein Fehler ist aufgetreten'
        });
    } finally {
        setIsSubmitting(false);
    }
}


function RegisterPage() {
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
        submitRegister(formData, setIsSubmitting, setErrors).catch(
            error => {
                setErrors({
                    general: error instanceof Error ? error.message : 'Ein Fehler ist aufgetreten'
                });
            }
        )
    };

    return (
        <div>
            <h1>Registrierung</h1>

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

            <button
                onClick={handleSubmit}
                disabled={isSubmitting}
            >
                {isSubmitting ? 'Lädt...' : 'Registrieren'}
            </button>
        </div>
    );
}

export default RegisterPage;

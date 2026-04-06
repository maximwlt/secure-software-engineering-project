import Navbar from "./Navbar.tsx";
import ErrorMessage from "./ErrorMessage.tsx";
import {useState} from "react";
import {apiFetch} from "../shared/utils/apiFetch.ts";
import type {ApiErrorType} from "../shared/types/ProblemDetail/ApiErrorType.ts";
import {isDetailError} from "../shared/types/ProblemDetail/IsErrorTypeGuards.ts";
import {Navigate, useNavigate} from "react-router";
import {getCookie} from "../shared/utils/cookies.ts";
import {useAuth} from "../shared/utils/useAuth.ts";
import {useUserProfile} from "../shared/hooks/useUserProfile.ts";


interface Errors {
    email?: string;
    password?: string;
    general?: string;
}


function Profile() {
    const { logout, isAuthenticated} = useAuth();
    const auth = useAuth();
    const navigate = useNavigate();

    const [deletePassword, setDeletePassword] = useState("");
    const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);

    const [isSubmitting, setIsSubmitting] = useState<boolean>(false);
    const [errors, setErrors] = useState<Errors>({});

    const { data, isLoading, error } = useUserProfile();

    // Es soll beim Laden der Komponente
    // GET /api/auth/me angezeigt werden, um die Profilinformationen des Benutzers zu erhalten.
    // public record UserProfileResponse(String id, String email) { }


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
                const errorData : ApiErrorType = await response.json();
                throw new Error(errorData.title);
            }

            logout();
            navigate('/');
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
            setErrors({ general: "Please enter password." });
            return;
        }

        try {
            const res = await apiFetch(auth, '/api/auth/me', {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ password: deletePassword })
            })
            if (!res.ok) {
                const errorData: ApiErrorType = await res.json();
                if (isDetailError(errorData)) {
                    setErrors({ general: errorData.detail });
                    throw new Error(errorData.detail);
                }
                throw new Error(errorData.title);
            }

            setErrors({})
            logout(); // After account deletion, log the user out
            navigate('/');
        } catch (error) {
            setErrors({
                general: error instanceof Error ? error.message : 'Ein Fehler ist aufgetreten'
            });
        }
    };

    if (!isAuthenticated) {
        return <Navigate to="/login" replace />;
    }

    /*if (apiError) {
        return <ApiErrorMessage error={apiError} />;
    }*/

    return (
        <>
        <Navbar/>
        <div className="auth-form-wrapper">
            <h1>You are currently logged in.</h1>

            {isLoading && <p>Loading profile...</p>}
            {error && <ErrorMessage message={error.title} type="general" />}
            {data && (
                <div>
                    <p><strong>Email:</strong> {data.email}</p>
                    <p><strong>Id:</strong> {data.id}</p>
                </div>
            )}
            <ErrorMessage
                message={errors.general}
                type="general"
            />
            <button className="primary-button"
                    onClick={handleLogout}
                    disabled={isSubmitting}
            >
                {isSubmitting ? 'Loading...' : 'Logout'}
            </button>

            <button
                className="delete-button"
                onClick={() => setShowDeleteConfirm(!showDeleteConfirm)}
            >
                Delete Account
            </button>

            {showDeleteConfirm && (
                <div className="delete-confirm-box">
                    <p>Please enter password to confirm account deletion:</p>
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
                        {isSubmitting ? "Deleting..." : "Confirm Delete"}
                    </button>

                </div>
            )}

            {/*apiError && <ApiErrorMessage error={apiError} />*/}

        </div>
    </>
    );
}
export { Profile };
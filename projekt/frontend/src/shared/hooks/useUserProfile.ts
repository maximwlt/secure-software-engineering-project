// src/shared/hooks/useUserProfile.ts
import { useEffect, useState } from "react";
import { apiFetch } from "../utils/apiFetch";
import { useAuth } from "../utils/useAuth";
import type { UserProfileInfo } from "../types/UserProfileInfo";
import type { ApiErrorType } from "../types/ProblemDetail/ApiErrorType";

interface UseUserProfileState {
    data: UserProfileInfo | null;
    isLoading: boolean;
    error: ApiErrorType | null;
}

export function useUserProfile() {
    const auth = useAuth();
    const [state, setState] = useState<UseUserProfileState>({
        data: null,
        isLoading: true,
        error: null,
    });

    useEffect(() => {
        const fetchUserProfile = async () => {
            try {
                setState((prev) => ({ ...prev, isLoading: true }));

                const response = await apiFetch(auth, "/api/auth/me", {
                    method: "GET",
                    headers: { "Content-Type": "application/json" },
                });

                if (!response.ok) {
                    const errorData: ApiErrorType = await response.json();
                    setState({
                        data: null,
                        isLoading: false,
                        error: errorData,
                    });
                    return;
                }

                const userData: UserProfileInfo = await response.json();
                setState({
                    data: userData,
                    isLoading: false,
                    error: null,
                });
            } catch (err : unknown) {
                setState({
                    data: null,
                    isLoading: false,
                    error: err as ApiErrorType
                });
            }
        };
        fetchUserProfile();
    }, [auth]);

    return state;
}

import  React, { useCallback, useEffect, useState } from "react";
import { getCookie } from "../shared/utils/cookies";
import { AuthContext } from "./AuthContext";
import LoadingBar from "../shared/components/LoadingBar.tsx";

let refreshPromise: Promise<string | null> | null = null;

function AuthProvider({ children }: { children: React.ReactNode }) {
    const [token, setToken] = useState<string | null>(null);
    const [isLoading, setIsLoading] = useState(true);

    const login = useCallback((newToken: string) => {
        setToken(newToken);
    }, []);

    const logout = useCallback(() => {
        setToken(null);
    }, []);

    const refreshAccessToken = useCallback(async (): Promise<string | null> => {
        if (!refreshPromise) {
            refreshPromise = (async () => {
                try {
                    const csrf = getCookie("XSRF-TOKEN");

                    // console.log('Refreshing access token with CSRF:', csrf ? 'EXISTS' : 'MISSING'); // DEBUG

                    const headers: HeadersInit = {
                        "Content-Type": "application/json",
                    };
                    if (csrf) headers["X-XSRF-TOKEN"] = csrf;

                    const res = await fetch("/api/auth/rt/refresh-token", {
                        method: "POST",
                        credentials: "include",
                        headers,
                    });

                    if (!res.ok) {
                        // console.log("Refresh token request failed with status:", res.status); // DEBUG

                        if (res.status === 401 || res.status === 403) {
                            // console.log("Refresh token is invalid or expired.");
                            logout();
                        }

                        return null;
                    }

                    const data = await res.json();
                    setToken(data.accessToken);
                    return data.accessToken;
                } finally {
                    refreshPromise = null;
                }
            })();
        }

        return refreshPromise;
    }, [logout]);

    // Initialer Silent Refresh beim App-Start
    useEffect(() => {
        refreshAccessToken().finally(() => setIsLoading(false));
    }, [refreshAccessToken]);

    // TODO: AuthContext.Provider ist deprecated. => Neuen Ansatz anschauen
    return (
        <AuthContext.Provider
            value={{
                token,
                login,
                logout,
                refreshAccessToken,
                isAuthenticated: token !== null,
            }}
        >
            {isLoading ? <LoadingBar /> : children}
        </AuthContext.Provider>
    );
}

export default AuthProvider

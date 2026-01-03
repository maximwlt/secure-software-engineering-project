import { getCookie } from "./cookies";
import type {AuthContextType} from "../components/AuthContext.tsx";

const CSRF_HEADER = "X-XSRF-TOKEN";
const CSRF_COOKIE = "XSRF-TOKEN";


export async function apiFetch(
    auth: AuthContextType,
    input: RequestInfo,
    init: RequestInit = {}
): Promise<Response> {

    const headers: Record<string, string> = {
        ...(init.headers as Record<string, string> || {}),
    };

    // Authorization Header
    if (auth.token) {
        headers["Authorization"] = `Bearer ${auth.token}`;
    }

    // CSRF Header
    /*
    const csrf = getCookie(CSRF_COOKIE);
    if (csrf) {
        headers[CSRF_HEADER] = csrf;
    }
    */



    console.log('apiFetch - First attempt with token:', auth.token ? 'EXISTS' : 'MISSING'); // DEBUG

    const response = await fetch(input, {
        ...init,
        headers,
        // credentials: "include",
    });

    if (response.status !== 401) {
        console.log('apiFetch - Response status:', response.status); // DEBUG
        return response;
    }


    console.log('apiFetch - Got 401, trying to refresh token...'); // DEBUG
    // Access Token refreshen
    const newToken = await auth.refreshAccessToken();
    if (!newToken) {
        console.error('apiFetch - Token refresh failed, user will be logged out');
        throw new Error("Session abgelaufen");
    }

    // Retry mit neuem Token + CSRF
    const retryHeaders: Record<string, string> = {
        ...(init.headers as Record<string, string> || {}),
        Authorization: `Bearer ${newToken}`,
    };


    const csrf_sec = getCookie(CSRF_COOKIE);
    if (csrf_sec) {
        retryHeaders[CSRF_HEADER] = csrf_sec;
    }


    return fetch(input, {
        ...init,
        headers: retryHeaders,
        credentials: "include",
    });
}

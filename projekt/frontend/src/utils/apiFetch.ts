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
    const csrf = getCookie(CSRF_COOKIE);
    if (csrf) {
        headers[CSRF_HEADER] = csrf;
    }


    const response = await fetch(input, {
        ...init,
        headers,
        credentials: "include",  // Cookies mitsenden (CSRF-Cookie und __Secure_Fgp)
    });

    if (response.status !== 401) {
        return response;
    }


    // Access Token refreshen
    const newToken = await auth.refreshAccessToken();
    if (!newToken) {
        throw new Error("You have to be authenticated to perform this action. Please log in.");
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

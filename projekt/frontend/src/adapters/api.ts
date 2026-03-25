import { getCookie } from "../utils/cookies";
import type {AuthContextType} from "../types/AuthContextType.ts";

const API_BASE = "/api";
const CSRF_HEADER = "X-XSRF-TOKEN";
const CSRF_COOKIE = "XSRF-TOKEN";

export interface ApiConfig {
    auth: AuthContextType;
}

class ApiAdapter {
    private auth: AuthContextType;

    constructor(auth: AuthContextType) {
        this.auth = auth;
    }

    private getHeaders(): HeadersInit {
        const headers: Record<string, string> = {
            "Content-Type": "application/json",
        };

        if (this.auth.token) {
            headers["Authorization"] = `Bearer ${this.auth.token}`;
        }

        const csrf = getCookie(CSRF_COOKIE);
        if (csrf) {
            headers[CSRF_HEADER] = csrf;
        }

        // Fingerprint Cookie will be sent automatically with credentials: "include", no need to add it to headers

        return headers;
    }

    async get<T>(endpoint: string): Promise<T> {
        const response = await fetch(`${API_BASE}${endpoint}`, {
            method: "GET",
            headers: this.getHeaders(),
            credentials: "include",
        });
        return this.handleResponse<T>(response);
    }

    async post<T>(endpoint: string, body?: unknown): Promise<T> {
        const response = await fetch(`${API_BASE}${endpoint}`, {
            method: "POST",
            headers: this.getHeaders(),
            body: body ? JSON.stringify(body) : undefined,
            credentials: "include",
        });
        return this.handleResponse<T>(response);
    }

    async put<T>(endpoint: string, body?: unknown): Promise<T> {
        const response = await fetch(`${API_BASE}${endpoint}`, {
            method: "PUT",
            headers: this.getHeaders(),
            body: body ? JSON.stringify(body) : undefined,
            credentials: "include",
        });
        return this.handleResponse<T>(response);
    }

    async delete<T>(endpoint: string): Promise<T> {
        const response = await fetch(`${API_BASE}${endpoint}`, {
            method: "DELETE",
            headers: this.getHeaders(),
            credentials: "include",
        });
        return this.handleResponse<T>(response);
    }

    private async handleResponse<T>(response: Response): Promise<T> {
        if (response.status === 401) {
            const newToken = await this.auth.refreshAccessToken();
            if (!newToken) {
                throw new Error("Authentication required.");
            }
        }

        if (!response.ok) {
            const error = await response.json().catch(() => ({}));
            throw new Error(error.title || "API request failed");
        }

        return response.json();
    }
}

export const createApiAdapter = (auth: AuthContextType): ApiAdapter => {
    return new ApiAdapter(auth);
};

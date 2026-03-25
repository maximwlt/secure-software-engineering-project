// Typen definieren
export interface AuthContextType {
    token: string | null;
    login: (newToken: string) => void;
    logout: () => void;
    refreshAccessToken: () => Promise<string | null>;
    isAuthenticated: boolean;
}
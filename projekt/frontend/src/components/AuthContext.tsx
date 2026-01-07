import {createContext} from 'react';


// Typen definieren
export interface AuthContextType {
    token: string | null;
    login: (newToken: string) => void;
    logout: () => void;
    refreshAccessToken: () => Promise<string | null>;
    isAuthenticated: boolean;
}

// Store JWT-Token In-Memory
export const AuthContext = createContext<AuthContextType | null>(null);




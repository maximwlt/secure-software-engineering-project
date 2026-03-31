import {createContext} from 'react';
import type {AuthContextType} from "../types/AuthContextType.ts";


/**
 * The AuthContext stores the authentication state of the user and provides methods for logging in and out.
 * It is storing a JWT In-Memory, which is lost when the page is refreshed.
 */
export const AuthContext = createContext<AuthContextType | null>(null);




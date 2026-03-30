// hooks/useAuth.ts
import { useContext } from 'react';
import {AuthContext} from '../components/AuthContext.tsx';
import type {AuthContextType} from "../types/AuthContextType.ts";

export function useAuth(): AuthContextType {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth muss innerhalb von AuthProvider verwendet werden');
    }
    return context;
}
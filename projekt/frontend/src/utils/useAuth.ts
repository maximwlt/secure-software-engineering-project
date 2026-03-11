// hooks/useAuth.ts
import { useContext } from 'react';
import {AuthContext, type AuthContextType} from '../components/AuthContext.tsx';

export function useAuth(): AuthContextType {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth muss innerhalb von AuthProvider verwendet werden');
    }
    return context;
}
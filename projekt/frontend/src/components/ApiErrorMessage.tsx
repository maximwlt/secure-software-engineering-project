import React from 'react';
import type { ErrorType } from '../types/ErrorType';
import '../styling/ErrorMessage.css';
import {faTriangleExclamation} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";

interface ApiErrorMessageProps {
    error?: Partial<ErrorType>;
}

const ApiErrorMessage: React.FC<ApiErrorMessageProps> = ({ error }) => {
    if (!error || !error.title) return null;

    return (
        <div className="error-message error-message--general">
            <FontAwesomeIcon icon={faTriangleExclamation} /> <strong>{error.title}</strong>
            {error.detail && <p>{error.detail}</p>}
            {error.status && <span>Status: {error.status}</span>}
        </div>
    );
};

export default ApiErrorMessage;

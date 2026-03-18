import React from 'react';
import '../styling/ErrorMessage.css';
import {faTriangleExclamation} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";

interface ErrorMessageProps {
    message?: string;
    type?: 'field' | 'general';
}

const ErrorMessage: React.FC<ErrorMessageProps> = ({ message, type = 'field' }) => {
    if (!message) return null;

    const className = `error-message error-message--${type}`;

    return (
        <div className={className}>
            {type === 'general' && <FontAwesomeIcon icon={faTriangleExclamation} />}
            {message}
        </div>
    );
};

export default ErrorMessage;

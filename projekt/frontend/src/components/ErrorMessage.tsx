import React from 'react';
import '../styling/ErrorMessage.css';

interface ErrorMessageProps {
    message?: string;
    type?: 'field' | 'general';
}

const ErrorMessage: React.FC<ErrorMessageProps> = ({ message, type = 'field' }) => {
    if (!message) return null;

    const className = `error-message error-message--${type}`;

    return (
        <div className={className}>
            {type === 'general' && '⚠️ '}
            {message}
        </div>
    );
};

export default ErrorMessage;

import React from 'react';

interface ErrorMessageProps {
    message?: string;
    type?: 'field' | 'general';
}

const ErrorMessage: React.FC<ErrorMessageProps> = ({ message, type = 'field' }) => {
    if (!message) return null;

    const styles = {
        field: {
            color: 'red',
            fontSize: '0.875rem',
            marginTop: '4px',
            marginBottom: '8px'
        },
        general: {
            color: 'red',
            backgroundColor: '#fee',
            padding: '10px',
            borderRadius: '4px',
            margin: '15px 0',
            border: '1px solid #f99'
        }
    };

    return (
        <div style={styles[type]}>
            {type === 'general' && '⚠️ '}
            {message}
        </div>
    );
};

export default ErrorMessage;
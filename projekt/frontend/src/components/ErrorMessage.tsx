import React from 'react';
import '../shared/styling/ErrorMessage.css';
import {faTriangleExclamation} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import type {ErrorMessageProps} from "../shared/types/ErrorMessageProps.ts";

/**
 * Displays an error message with optional with a warning icon
 *
 * @param message The error message to display. If empty, nothing will be rendered.
 * @param type Visual style variant. 'field' for inline below form fields, 'general' for prominent block with warning icon. Default is 'field'.
 * @example
 * <ErrorMessage message="This field is required." type="field" />
 * <ErrorMessage message="Failed to load data." type="general" />
 */
const ErrorMessage: React.FC<ErrorMessageProps> = ({ message, type = 'field' } : ErrorMessageProps) => {
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

import React from 'react';
import '../shared/styling/ErrorMessage.css';
import {faTriangleExclamation} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import type {ApiErrorType} from "../shared/types/ProblemDetail/ApiErrorType.ts";
import {isDetailError, isValidationError} from "../shared/types/ProblemDetail/IsErrorTypeGuards.ts";

interface Props {
    error: ApiErrorType | undefined;
}

const ApiErrorMessage: React.FC<Props> = ({ error }: Props ) => {
    if (!error) return null;

    return (
        <div className="error-message error-message--general">
            <FontAwesomeIcon icon={faTriangleExclamation} /> <strong>{error.title}</strong>
            {isDetailError(error) && <p>{error.detail}</p>}

            {isValidationError(error) && (
                <ul>
                    {error.errors.map((validationError, index) => (
                        <li key={index}>{validationError.message}</li>
                    ))}
                </ul>
            )}
            {error.status && <span>Status: {error.status}</span>}
        </div>
    );
};

export default ApiErrorMessage;

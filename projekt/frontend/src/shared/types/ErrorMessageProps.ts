/**
 * Interface for error message properties.
 */
export interface ErrorMessageProps {
    /** The error message to display. If empty, nothing will be rendered. */
    message?: string;
    /**
     * Visual style variant.
     * - `'field'`: inline below form fields
     * - `'general'`: prominent block with warning icon
     * @default 'field'
     */
    type?: 'field' | 'general';
}
/**
 * The ProblemDetailBaseType interface represents the base structure of a problem detail response
 * after the RFC 7807 standard. It includes the common fields that are present in all problem detail responses, such as instance, status, and title.
 * This interface can be extended by other specific problem detail types, such as DetailError and ValidationError, to include additional fields relevant to those specific error cases.
 */
export interface ProblemDetailBaseType {
    instance: string;
    status: number;
    title: string;
}
import type {PublicDocument} from "./PublicDocument.ts";

export interface OverviewPageProps {
    documents: PublicDocument[];
    onCardClick: (noteId: string) => void;
}
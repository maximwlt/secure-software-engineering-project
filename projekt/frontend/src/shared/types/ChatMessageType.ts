export interface ChatMessage {
    senderId: string;
    recipientId: string;
    senderName: string;
    content: string;
    type: "CHAT" | "JOIN" | "LEAVE";
    sentAt?: string;
}
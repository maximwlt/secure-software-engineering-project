import { useEffect, useRef } from "react";
import {Client, type IMessage} from "@stomp/stompjs";
import type {ChatMessage} from "../types/ChatMessageType.ts";

export function useWebSocket(
    userId: string,
    topic: string,
    onMessage: (msg: ChatMessage) => void
) {
    const clientRef = useRef<Client | null>(null);

    useEffect(() => {
        const client = new Client({
            brokerURL: "ws://localhost:8090/ws",
            reconnectDelay: 5000,
            onConnect: () => {
                client.subscribe(`/user/${userId}/queue/messages`, (msg: IMessage) => {
                   const parsed : ChatMessage= JSON.parse(msg.body)
                   onMessage(parsed);
                });
                if (topic) {
                    client.subscribe(topic, (msg: IMessage) => {
                        const parsed : ChatMessage = JSON.parse(msg.body)
                        onMessage(parsed);
                    });
                }
            },
            onStompError: (frame) => console.error("STOMP Fehler:", frame),
            onWebSocketError: () => console.log("Websocket Error!"),
            onDisconnect: () => console.log("Disconnected.")
        });

        client.activate();
        clientRef.current = client;

        return () => { client.deactivate(); };
    }, [onMessage, topic, userId]);

    const sendPrivateMessage = (recipientId: string, senderName : string, content: string) => {
        clientRef.current?.publish({
            destination: "/app/chat/private",
            body: JSON.stringify({
                senderId: userId,
                recipientId: recipientId,
                senderName: senderName,
                content: content,
                type: "CHAT",
            }),
        });
    };

    return { sendPrivateMessage };
}
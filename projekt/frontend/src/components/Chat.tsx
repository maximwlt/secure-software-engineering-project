/*import { useState } from "react";
import type {ChatMessage} from "../shared/types/ChatMessageType.ts";
import {useWebSocket} from "../shared/hooks/useWebSocket.ts";*/

export function Chat() {
    /*
    const [messages, setMessages] = useState<ChatMessage[]>([]);
    const [content, setContent] = useState("");


    const { sendPrivateMessage } = useWebSocket("/topic/public", (msg: ChatMessage) => {
        setMessages((prev) => [...prev, msg]);
    });

    const handleSend = () => {
        sendPrivateMessage("/app/chat", {
            sender: "Max",
            content: content,
            type: "CHAT",
        });
    };

    return (
        <>
            <input type="text"
                   name="content"
                   value={content}
                   onChange={(e) => setContent(e.target.value)}
                   placeholder={"Type a message..."}
            />

            <div>
                <button onClick={handleSend}>Send</button>
                <ul>
                    {messages.map((m, i) => (
                        <li key={i}>{m.sender}: {m.content}</li>
                    ))}
                </ul>
            </div>
        </>
    );

     */
}
import { useState, useEffect } from "react";
import type { ChatMessage } from "../shared/types/ChatMessageType";
import { useWebSocket } from "../shared/hooks/useWebSocket";
import { useAuth } from "../shared/utils/useAuth.ts";
import type { UserProfileInfo } from "../shared/types/UserProfileInfo.ts";
import { apiFetch } from "../shared/utils/apiFetch.ts";
import type { ApiErrorType } from "../shared/types/ProblemDetail/ApiErrorType.ts";

export function PrivateChat() {
    const auth = useAuth();
    const [messages, setMessages] = useState<ChatMessage[]>([]);
    const [content, setContent] = useState("");
    const [recipientId, setRecipientId] = useState("");
    const [isConnected, setIsConnected] = useState(false);
    const [connectionStep, setConnectionStep] = useState<"select" | "chat">("select");
    const [userData, setUserData] = useState<UserProfileInfo | null>(null);
    const [error, setError] = useState<ApiErrorType | null>(null);
    const [isLoading, setIsLoading] = useState(true);

    // Fetch user profile in useEffect
    useEffect(() => {
        const fetchUserData = async () => {
            try {
                setIsLoading(true);
                setError(null);

                const response = await apiFetch(auth, "/api/auth/me", {
                    method: "GET",
                    headers: { "Content-Type": "application/json" },
                });

                if (!response.ok) {
                    const errorData: ApiErrorType = await response.json();
                    setError(errorData);
                    return;
                }

                const data: UserProfileInfo = await response.json();
                setUserData(data);
            } catch (err) {
                console.error("Error fetching user data:", err);
                // Bei generischen Errors (z.B. Network Error) kannst du ein DetailError erstellen
                setError({
                    detail: err instanceof Error ? err.message : "Failed to fetch user profile",
                    status: 500,
                    title: "Error"
                } as ApiErrorType); // Cast only if necessary
            } finally {
                setIsLoading(false);
            }
        };

        fetchUserData();
    }, [auth]);

    const { sendPrivateMessage } = useWebSocket(
        userData?.email || "",
        "",
        (msg: ChatMessage) => {
            setMessages((prev) => [...prev, msg]);
        }
    );

    const handleConnect = () => {
        if (!recipientId.trim()) {
            alert("Bitte geben Sie die UUID des anderen Nutzers ein");
            return;
        }
        setConnectionStep("chat");
        setIsConnected(true);
        setMessages([]);
    };

    const handleSend = () => {
        if (!content.trim()) return;

        sendPrivateMessage(
            recipientId,
            userData?.email || "Anonymer Nutzer",
            content
        );

        setContent("");
    };

    const handleDisconnect = () => {
        setConnectionStep("select");
        setIsConnected(false);
        setRecipientId("");
        setMessages([]);
    };

    if (isLoading) {
        return <div>Laden...</div>;
    }

    if (error) {
        return <div className="text-red-500">Fehler beim Laden des Benutzers</div>;
    }

    if (!userData) {
        return <div className="text-red-500">Benutzerdaten konnten nicht geladen werden</div>;
    }

    return (
        <div className="max-w-2xl mx-auto p-4 border rounded-lg">
            <h2 className="text-xl font-bold mb-4">Private Chat</h2>

            {connectionStep === "select" ? (
                <div className="space-y-4">
                    <div>
                        <label className="block font-semibold mb-2">
                            Mit User verbinden (UUID eingeben):
                        </label>
                        <input
                            type="text"
                            value={recipientId}
                            onChange={(e) => setRecipientId(e.target.value)}
                            placeholder="z.B. 550e8400-e29b-41d4-a716-446655440000"
                            className="w-full px-3 py-2 border rounded-md"
                        />
                    </div>
                    <button
                        onClick={handleConnect}
                        className="w-full px-4 py-2 bg-blue-500 text-white rounded-md hover:bg-blue-600"
                    >
                        Verbinden
                    </button>
                </div>
            ) : (
                <div className="space-y-4">
                    <div className="text-sm text-gray-600">
                        Verbunden mit: <code className="bg-gray-100 px-2 py-1">{recipientId}</code>
                    </div>

                    <div className="border rounded-md p-3 h-64 overflow-y-auto bg-gray-50">
                        {messages.length === 0 ? (
                            <p className="text-gray-500 text-center mt-20">Keine Nachrichten</p>
                        ) : (
                            <ul className="space-y-2">
                                {messages.map((m, i) => (
                                    <li
                                        key={i}
                                        className={`p-2 rounded ${
                                            m.senderId === userData.id
                                                ? "bg-blue-100 text-right"
                                                : "bg-gray-200"
                                        }`}
                                    >
                                        <strong>{m.senderName}:</strong> {m.content}
                                    </li>
                                ))}
                            </ul>
                        )}
                    </div>

                    <div className="flex gap-2">
                        <input
                            type="text"
                            value={content}
                            onChange={(e) => setContent(e.target.value)}
                            onKeyDown={(e) => e.key === "Enter" && handleSend()}
                            placeholder="Nachricht eingeben..."
                            className="flex-1 px-3 py-2 border rounded-md"
                            disabled={!isConnected}
                        />
                        <button
                            onClick={handleSend}
                            disabled={!isConnected || !content.trim()}
                            className="px-4 py-2 bg-green-500 text-white rounded-md hover:bg-green-600 disabled:bg-gray-400"
                        >
                            Senden
                        </button>
                    </div>

                    <button
                        onClick={handleDisconnect}
                        className="w-full px-4 py-2 bg-red-500 text-white rounded-md hover:bg-red-600"
                    >
                        Trennen
                    </button>
                </div>
            )}
        </div>
    );
}

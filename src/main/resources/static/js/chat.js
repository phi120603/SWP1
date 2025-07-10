// Updated JavaScript Messenger Chat to call backend API
class MessengerChat {
    constructor() {
        this.currentThread = null;
        this.threads = []; // Fetch from backend later
        this.apiBase = '/api';
        this.init();
    }

    async init() {
        await this.loadThreads();
        this.renderThreadList();
        this.bindEvents();
        this.updateSendButton();
    }

    async loadThreads() {
        try {
            const res = await fetch(`${this.apiBase}/session`);
            const data = await res.json();
            this.currentSessionId = data.session;

            const historyRes = await fetch(`${this.apiBase}/history/${this.currentSessionId}`);
            const messages = await historyRes.json();

            this.threads = [
                {
                    id: this.currentSessionId,
                    name: "Manager",
                    avatar: "/placeholder.svg?height=48&width=48",
                    lastMessage: messages.length > 0 ? messages[messages.length - 1].content : "",
                    timestamp: "now",
                    unread: 0,
                    online: true,
                    messages: messages.map((msg) => ({
                        id: msg.id,
                        text: msg.content,
                        sender: msg.mine ? "user" : "other",
                        timestamp: new Date(msg.createdAt),
                        avatar: msg.mine ? null : "/placeholder.svg?height=24&width=24",
                    })),
                },
            ];

            this.currentThread = this.threads[0];
        } catch (e) {
            console.error("Failed to load thread or messages", e);
        }
    }

    // Keep the rest of the class mostly the same
    // Modify sendMessage to POST to backend

    async sendMessage() {
        const messageInput = document.getElementById("messageInput");
        const messageText = messageInput.value.trim();

        if (!messageText || !this.currentThread) return;

        const payload = {
            content: messageText,
            sessionId: this.currentThread.id,
        };

        try {
            const res = await fetch(`${this.apiBase}/message`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload),
            });

            if (res.ok) {
                const saved = await res.json();

                const newMessage = {
                    id: saved.id,
                    text: saved.content,
                    sender: "user",
                    timestamp: new Date(saved.createdAt),
                };

                this.currentThread.messages.push(newMessage);
                this.currentThread.lastMessage = newMessage.text;
                this.currentThread.timestamp = "now";
                messageInput.value = "";
                this.updateSendButton();
                this.renderMessages(this.currentThread.messages);
                this.renderThreadList();
            }
        } catch (e) {
            console.error("Failed to send message", e);
        }
    }

    // ... Keep other methods the same ...
}

document.addEventListener("DOMContentLoaded", () => {
    new MessengerChat();
});

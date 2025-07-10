class MessengerChat {
    constructor() {
        this.currentThread = null;
        this.threads = [];
        this.apiBase = '/api';

        // Base64 avatar dự phòng (ảnh hình tròn chữ "M")
        this.defaultAvatar = "data:image/svg+xml;base64,PHN2ZyBoZWlnaHQ9IjQ4IiB3aWR0aD0iNDgiIHZpZXdCb3g9IjAgMCAxMDAgMTAwIiBmaWxsPSIjY2NjY2NjIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciPgogICAgPHJlY3Qgd2lkdGg9IjEwMCIgaGVpZ2h0PSIxMDAiIHJ4PSIyMCIgZmlsbD0iI2RkZGRkZCIgLz4KICAgIDx0ZXh0IHg9IjUwJSIgeT0iNTAlIiBmb250LXNpemU9IjQ1IiBmb250LWZhbWlseT0iQXJpYWwiIHRleHQtYW5jaG9yPSJtaWRkbGUiIGZpbGw9IiM3NzdjODAiPk08L3RleHQ+Cjwvc3ZnPg==";

        this.elements = {
            messageInput: document.getElementById("messageInput"),
            sendBtn: document.getElementById("sendBtn"),
            messagesContainer: document.getElementById("messagesContainer"),
            threadList: document.getElementById("thread-list"),
            emptyState: document.getElementById("emptyState"),
            chatContent: document.getElementById("chatContent"),
            peerName: document.getElementById("peerName"),
        };
        this.init();
    }

    async init() {
        await this.loadThreads();
        this.renderThreadList();
        this.bindEvents();
        this.updateUI();
    }

    async loadThreads() {
        try {
            const res = await fetch(`${this.apiBase}/session/dev`);
            const data = await res.json();
            this.currentSessionId = data.session;

            const historyRes = await fetch(`${this.apiBase}/history/${this.currentSessionId}`);
            const messages = await historyRes.json();

            const formattedMessages = messages.map((msg) => ({
                id: msg.id,
                text: msg.content,
                sender: msg.mine === true || msg.mine === 1 ? "user" : "other",  // <-- sửa chỗ này
                timestamp: new Date(msg.createdAt),
                avatar: (msg.mine === true || msg.mine === 1) ? null : "/placeholder.svg",
            }));


            this.threads = [{
                id: this.currentSessionId,
                name: "Manager",
                avatar: this.defaultAvatar,
                lastMessage: formattedMessages.at(-1)?.text || "",
                timestamp: "now",
                unread: 0,
                online: true,
                messages: formattedMessages,
            }];

            this.currentThread = this.threads[0];
        } catch (e) {
            console.error("❌ Failed to load threads or messages:", e);
        }
    }

    bindEvents() {
        this.elements.sendBtn.addEventListener("click", () => this.sendMessage());
        this.elements.messageInput.addEventListener("keypress", (e) => {
            if (e.key === "Enter") {
                e.preventDefault();
                this.sendMessage();
            }
        });
    }

    async sendMessage() {
        const text = this.elements.messageInput.value.trim();
        if (!text || !this.currentThread) return;

        const payload = {
            content: text,
            sessionId: this.currentThread.id,
        };

        console.log("📤 Sending payload:", payload);

        try {
            const res = await fetch(`${this.apiBase}/message`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload),
            });

            if (!res.ok) {
                console.error("❌ Message send failed. Status:", res.status);
                return;
            }

            let saved;
            try {
                saved = await res.json();
            } catch (jsonErr) {
                console.error("❌ Failed to parse JSON from server:", jsonErr);
                return;
            }

            if (!saved || !saved.content) {
                console.warn("⚠️ Server returned invalid message:", saved);
                return;
            }

            const newMessage = {
                id: saved.id,
                text: saved.content,
                sender: "user",
                timestamp: new Date(saved.createdAt || Date.now()),
            };

            this.currentThread.messages.push(newMessage);
            this.currentThread.lastMessage = newMessage.text;
            this.elements.messageInput.value = "";
            this.renderMessages(this.currentThread.messages);
            this.renderThreadList();

        } catch (e) {
            console.error("❌ Error sending message:", e);
        }
    }

    renderMessages(messages) {
        const container = this.elements.messagesContainer;
        container.innerHTML = "";

        messages.forEach(msg => {
            const group = document.createElement("div");
            group.className = `message-group ${msg.sender === "user" ? "sent" : "received"}`;

            if (msg.sender !== "user") {
                const avatar = document.createElement("img");
                avatar.className = "message-avatar";
                avatar.src = msg.avatar || this.defaultAvatar;
                avatar.onerror = () => avatar.src = this.defaultAvatar;
                group.appendChild(avatar);
            }

            const bubble = document.createElement("div");
            bubble.className = `message-bubble ${msg.sender === "user" ? "sent" : "received"}`;
            bubble.textContent = msg.text;
            group.appendChild(bubble);

            container.appendChild(group);
        });

        container.scrollTop = container.scrollHeight;
    }

    renderThreadList() {
        const ul = this.elements.threadList;
        ul.innerHTML = "";

        this.threads.forEach(thread => {
            const li = document.createElement("li");
            li.className = "thread-item";
            if (this.currentThread?.id === thread.id) {
                li.classList.add("active");
            }

            li.innerHTML = `
                <img src="${thread.avatar}" class="avatar" onerror="this.src='${this.defaultAvatar}'">
                <div class="thread-info">
                    <strong>${thread.name}</strong>
                    <p>${thread.lastMessage}</p>
                </div>
            `;

            li.addEventListener("click", () => {
                this.currentThread = thread;
                this.renderThreadList();
                this.updateUI();
                this.renderMessages(thread.messages);
            });

            ul.appendChild(li);
        });
    }

    updateUI() {
        if (this.currentThread) {
            this.elements.emptyState.style.display = "none";
            this.elements.chatContent.style.display = "flex";
            this.elements.peerName.textContent = this.currentThread.name;
            this.renderMessages(this.currentThread.messages);
        } else {
            this.elements.emptyState.style.display = "flex";
            this.elements.chatContent.style.display = "none";
        }
    }

    updateSendButton() {
        const input = this.elements.messageInput;
        this.elements.sendBtn.disabled = input.value.trim() === "";
        input.addEventListener("input", () => {
            this.elements.sendBtn.disabled = input.value.trim() === "";
        });
    }
}

document.addEventListener("DOMContentLoaded", () => {
    new MessengerChat();
});

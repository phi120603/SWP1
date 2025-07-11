// == File: chat.js - Đã cập nhật ==
class MessengerChat {
    constructor() {
        this.currentThread = null;
        this.threads = [];
        this.apiBase = '/api';
        this.stompClient = null;
        this.senderId = null; // ✅ Gán từ session backend

        this.defaultAvatar = "data:image/svg+xml;base64,...";

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
        await this.loadSessionInfo();
        await this.loadThreads();
        this.renderThreadList();
        this.bindEvents();
        this.updateUI();
        this.connectWebSocket(this.currentThread.id);
    }

    async loadSessionInfo() {
        try {
            const res = await fetch(`${this.apiBase}/session/dev`);
            const data = await res.json();
            this.currentSessionId = data.session;
            this.senderId = data.senderId;
        } catch (e) {
            console.error("❌ Failed to load session info", e);
        }
    }

    generateRoomId(userA, userB) {
        return userA < userB ? `room-${userA}-${userB}` : `room-${userB}-${userA}`;
    }

    async loadThreads() {
        try {
            const peerId = "user-6"; // Manager ID
            const roomId = this.generateRoomId(this.senderId, peerId);

            const historyRes = await fetch(`${this.apiBase}/messages?peerId=${peerId}&currentUserId=${this.senderId}`);
            const messages = await historyRes.json();

            const formattedMessages = messages.map((msg) => ({
                id: msg.id,
                text: msg.content,
                sender: msg.senderId === this.senderId ? "user" : "other",
                timestamp: new Date(msg.createdAt),
                avatar: msg.senderId === this.senderId ? null : "/placeholder.svg",
            }));

            // ✅ Luôn tạo thread với manager, kể cả khi chưa có tin nhắn nào
            const thread = {
                id: roomId,
                name: "Quản lý",
                avatar: this.defaultAvatar,
                lastMessage: formattedMessages.at(-1)?.text || "Bắt đầu trò chuyện với quản lý",
                timestamp: formattedMessages.at(-1)?.timestamp || "now",
                unread: 0,
                online: true,
                messages: formattedMessages.length > 0 ? formattedMessages : [],
            };

            this.threads = [thread];
            this.currentThread = thread;

        } catch (e) {
            console.error("❌ Failed to load messages:", e);

            // ✅ Trường hợp lỗi gọi API vẫn tạo thread
            const fallbackRoomId = this.generateRoomId(this.senderId, "user-6");
            this.threads = [{
                id: fallbackRoomId,
                name: "Quản lý",
                avatar: this.defaultAvatar,
                lastMessage: "Bắt đầu trò chuyện với quản lý",
                timestamp: "now",
                unread: 0,
                online: true,
                messages: [],
            }];
            this.currentThread = this.threads[0];
        }
    }


    connectWebSocket(roomId) {
        const socket = new SockJS('/ws');
        this.stompClient = Stomp.over(socket);
        const _this = this;

        this.stompClient.connect({}, function () {
            console.log("✅ Connected to WebSocket");

            _this.stompClient.subscribe(`/topic/room/${roomId}`, function (message) {
                if (!message.body) return;
                const msg = JSON.parse(message.body);

                const newMsg = {
                    id: msg.id,
                    text: msg.content,
                    sender: msg.senderId === _this.senderId ? "user" : "other",
                    timestamp: new Date(msg.createdAt),
                    avatar: msg.senderId === _this.senderId ? null : "/placeholder.svg"
                };

                if (_this.currentThread?.id === roomId) {
                    _this.currentThread.messages.push(newMsg);
                    _this.renderMessages(_this.currentThread.messages);
                }
            });
        });
    }

    sendViaWebSocket(payload) {
        if (this.stompClient && this.stompClient.connected) {
            this.stompClient.send("/app/chat", {}, JSON.stringify(payload));
        } else {
            console.warn("❌ WebSocket not connected");
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

    sendMessage() {
        const text = this.elements.messageInput.value.trim();
        if (!text || !this.currentThread || !this.senderId) return;

        const payload = {
            content: text,
            roomId: this.currentThread.id,
            senderId: this.senderId
        };

        this.sendViaWebSocket(payload);

        const newMessage = {
            id: Date.now(),
            text: text,
            sender: "user",
            timestamp: new Date(),
        };

        this.currentThread.messages.push(newMessage);
        this.currentThread.lastMessage = newMessage.text;
        this.elements.messageInput.value = "";
        this.renderMessages(this.currentThread.messages);
        this.renderThreadList();
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
            if (this.currentThread?.id === thread.id) li.classList.add("active");

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

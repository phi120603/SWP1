class MessengerChat {
    constructor() {
        this.apiBase = '/api';
        this.stompClient = null;
        this.senderId = null;
        this.currentThread = null;
        this.threads = [];

        this.managerId = "user-6"; // ID cố định của Manager
        this.defaultAvatar = "/placeholder.svg";

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
        await this.loadSessionInfo(); // Lấy senderId
        await this.loadThreads();     // Gọi sau khi có senderId
        this.renderThreadList();
        this.bindEvents();
        this.updateUI();

        // Kết nối WebSocket với đúng roomId
        this.connectWebSocket(this.currentThread?.id);
    }

    async loadSessionInfo() {
        try {
            const res = await fetch(`${this.apiBase}/current-user`);
            const data = await res.json();
            this.senderId = data.id;
        } catch (e) {
            alert("Phiên đăng nhập đã hết. Vui lòng đăng nhập lại.");
            window.location.href = "/api/login";
        }
    }

    generateRoomId(userA, userB) {
        return userA < userB ? `room-${userA}-${userB}` : `room-${userB}-${userA}`;
    }

    async loadThreads() {
        try {
            const roomId = this.generateRoomId(this.senderId, this.managerId);
            const response = await fetch(`${this.apiBase}/messages?peerId=${this.managerId}&currentUserId=${this.senderId}`);
            const messages = await response.json();

            const formatted = messages.map(m => ({
                id: m.id,
                text: m.content,
                sender: m.senderId === this.senderId ? "user" : "other",
                timestamp: new Date(m.createdAt),
                avatar: m.senderId === this.senderId ? null : this.defaultAvatar
            }));

            const thread = {
                id: roomId,
                name: "Quản lý",
                avatar: this.defaultAvatar,
                lastMessage: formatted.at(-1)?.text || "Bắt đầu trò chuyện với quản lý",
                timestamp: formatted.at(-1)?.timestamp || new Date(),
                messages: formatted
            };

            this.threads = [thread];
            this.currentThread = thread;

        } catch (e) {
            console.error("❌ Lỗi khi load tin nhắn:", e);
            const roomId = this.generateRoomId(this.senderId, this.managerId);
            const emptyThread = {
                id: roomId,
                name: "Quản lý",
                avatar: this.defaultAvatar,
                lastMessage: "Bắt đầu trò chuyện với quản lý",
                timestamp: new Date(),
                messages: []
            };
            this.threads = [emptyThread];
            this.currentThread = emptyThread;
        }
    }

    connectWebSocket(roomId) {
        const socket = new SockJS('/ws');
        this.stompClient = Stomp.over(socket);
        this.stompClient.connect({}, () => {
            console.log("✅ Kết nối WebSocket thành công");
            this.stompClient.subscribe(`/topic/room/${roomId}`, (message) => {
                if (!message.body) return;
                const msg = JSON.parse(message.body);
                const newMsg = {
                    id: msg.id,
                    text: msg.content,
                    sender: msg.senderId === this.senderId ? "user" : "other",
                    timestamp: new Date(msg.createdAt),
                    avatar: msg.senderId === this.senderId ? null : this.defaultAvatar
                };

                if (this.currentThread?.id === roomId) {
                    this.currentThread.messages.push(newMsg);
                    this.renderMessages(this.currentThread.messages);
                }
            });
        });
    }

    sendViaWebSocket(payload) {
        if (this.stompClient?.connected) {
            this.stompClient.send("/app/chat", {}, JSON.stringify(payload));
        } else {
            console.warn("❌ WebSocket chưa kết nối");
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
            senderId: this.senderId,
            receiverId: this.managerId
        };

        this.sendViaWebSocket(payload);

        const newMsg = {
            id: Date.now(),
            text: text,
            sender: "user",
            timestamp: new Date()
        };

        this.currentThread.messages.push(newMsg);
        this.currentThread.lastMessage = newMsg.text;

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
}

document.addEventListener("DOMContentLoaded", () => {
    new MessengerChat();
});

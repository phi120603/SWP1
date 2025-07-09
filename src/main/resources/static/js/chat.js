// chat.js
let stomp, sid;
const log  = document.getElementById("messages");   // đúng ID
const input= document.getElementById("msg");
const sendBtn = document.getElementById("send");

/* 1. Tạo / lấy session */
fetch("/api/session", {method:"POST"})
    .then(r => r.json())
    .then(({session}) => { sid = session; connect(); loadHistory(); });

/* 2. Kết nối WebSocket */
function connect(){
    const sock = new SockJS("/ws");          // khớp WebSocketConfig
    stomp = Stomp.over(sock);
    stomp.connect({}, () =>{
        stomp.subscribe(`/topic/${sid}`, e => append(JSON.parse(e.body)));
    });
}

/* 3. Lịch sử */
function loadHistory(){
    fetch(`/api/history/${sid}`)
        .then(r => r.json())
        .then(arr => arr.forEach(append));
}

/* 4. Gửi tin */
sendBtn.onclick = () =>{
    const text = input.value.trim();
    if(!text) return;
    stomp.send(`/app/send/${sid}`, {}, text);
    input.value = "";
};

/* 5. Hiển thị tin */
function append(m){
    const bubble = document.createElement("div");
    bubble.className = m.mine ? "bubble mine" : "bubble other";
    bubble.textContent = m.content;
    log.appendChild(bubble);
    log.scrollTop = log.scrollHeight;
}

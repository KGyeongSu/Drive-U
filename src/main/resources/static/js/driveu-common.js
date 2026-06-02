function scrollTopSmooth() {
    window.scrollTo({top: 0, behavior: "smooth"});
}

const siteHeader = document.querySelector(".site-header");
const navButtons = document.querySelectorAll(".nav-button");
const megaMenu = document.querySelector(".mega-menu");

navButtons.forEach(button => {
    button.addEventListener("click", event => {
        event.stopPropagation();
        siteHeader.classList.toggle("menu-open");
    });
});

if (megaMenu) {
    megaMenu.addEventListener("click", event => event.stopPropagation());
}

document.addEventListener("click", () => {
    if (siteHeader) {
        siteHeader.classList.remove("menu-open");
    }
});

document.addEventListener("keydown", event => {
    if (event.key === "Escape" && siteHeader) {
        siteHeader.classList.remove("menu-open");
    }
});

function goTermsNext(button) {
    const agreeYn = document.getElementById("agreeYn");

    if (!agreeYn || !agreeYn.checked) {
        alert("약관에 동의해야 다음 단계로 이동할 수 있습니다.");
        return;
    }

    location.href = button.dataset.next;
}

function goPayment(button) {
    const checkedMethod = document.querySelector('input[name="paymentMethod"]:checked');

    if (!checkedMethod) {
        alert("지불방법을 선택해 주세요.");
        return;
    }

    alert("결제가 완료되었습니다.");
    location.href = button.dataset.next;
}

document.querySelectorAll(".time-item").forEach(item => {
    item.addEventListener("click", () => {
        document.querySelectorAll(".time-item").forEach(button => button.classList.remove("selected"));
        item.classList.add("selected");
    });
});
// 챗봇
// 챗봇 토글
function toggleChat() {
    var chatWindow = document.getElementById('chatbotWindow');
    // chatWindow가 페이지에 있는지 확인
    if (chatWindow) {
        chatWindow.style.display = (chatWindow.style.display === 'none' || chatWindow.style.display === '') ? 'block' : 'none';
    } else {
        console.warn("챗봇 창을 찾을 수 없습니다.");
    }
}

function sendQuestion(message) {

    console.log("서버로 보낼 질문:", message);
    var chatBody = document.getElementById('chatBody');

    // 사용자 메시지 생성
    let userMsg = document.createElement('div');
    userMsg.className = 'message user';
    userMsg.innerText = message;
    chatBody.appendChild(userMsg);

    scrollChatBottom();

    // 서버 요청
    fetch('/api/chat', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ question: message })
    })
        .then(response => response.json())
        .then(data => {
            // 봇 메시지 생성
            let botMsg = document.createElement('div');
            botMsg.className = 'message bot';
            botMsg.innerText = data.answer;
            chatBody.appendChild(botMsg);

            // 버튼 추가
            if (data.buttons && data.buttons.length > 0) {
                let gridContainer = document.createElement('div');
                gridContainer.className = 'button-grid';
                data.buttons.forEach(btn => {
                    let button = document.createElement('button');
                    button.innerText = btn.title;
                    button.onclick = () => location.href = btn.url;
                    gridContainer.appendChild(button);
                });
                chatBody.appendChild(gridContainer);
            }

            setTimeout(() => { scrollChatBottom(); }, 100);
        })
        .catch(error => {

            console.error('Error:', error);
            let errorMsg = document.createElement('div');
            errorMsg.className = 'message bot';
            errorMsg.innerText = '죄송합니다. 서버 통신 중 오류가 발생했습니다.';

            chatBody.appendChild(errorMsg);
            scrollChatBottom();

        });
}

// 입력창 전송 함수
function sendManualQuestion() {
    var input = document.getElementById('chatInput');
    if (input.value.trim() !== "") {
        sendQuestion(input.value);
        input.value = "";
    }
}

// 엔터키 지원
document.getElementById('chatInput')?.addEventListener('keypress', function (e) {
    if (e.key === 'Enter') {
        sendManualQuestion();
    }
});

// 챗봇창 drag & drop
let isDragging = false;
let offsetX, offsetY;

const chatbot = document.getElementById('chatbotWindow');
const header = chatbot?.querySelector('.chatbot-header');

if (header) {

    header.addEventListener('mousedown', (e) => {
        isDragging = true;
        // 현재 위치를 고정하기 위해 초기 좌표 계산
        const rect = chatbot.getBoundingClientRect();
        offsetX = e.clientX - rect.left;
        offsetY = e.clientY - rect.top;

        chatbot.style.cursor = 'grabbing';
        document.body.style.userSelect = 'none'; // 드래그 중 텍스트 선택 방지

    });

    document.addEventListener('mousemove', (e) => {

        if (!isDragging) return;

        if (isDragging) {


            // 마우스 위치에 따라 챗봇 창 위치 업데이트
            chatbot.style.left = (e.clientX - offsetX) + 'px';
            chatbot.style.top = (e.clientY - offsetY) + 'px';
            chatbot.style.transform = 'none';

        }
    });

    document.addEventListener('mouseup', () => {

        isDragging = false;
        chatbot.style.cursor = 'default';

    });

}

// 채팅 치면 해당 부분 보여주는 함수
function scrollChatBottom () {

    const chatBody = document.getElementById('chatBody');

    chatBody.scrollTop = chatBody.scrollHeight;

}
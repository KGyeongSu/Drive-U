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

const centerData = {
    daejeon: [
        {name: "대전 운전면허시험장", address: "대전광역시 동구 산서로1660번길 90"},
        {name: "대전 교육장", address: "대전광역시 중구 중앙로121번길 20"}
    ],
    seoul: [
        {name: "강남 운전면허시험장", address: "서울특별시 강남구 테헤란로114길 23"},
        {name: "도봉 운전면허시험장", address: "서울특별시 노원구 동일로 1449"}
    ],
    chungbuk: [
        {name: "청주 운전면허시험장", address: "충청북도 청주시 상당구 가덕면 교육원로 131-20"},
        {name: "충주 출장장", address: "충청북도 충주시 예시로 100"}
    ],
    busan: [
        {name: "부산남부 운전면허시험장", address: "부산광역시 남구 용호로 16"},
        {name: "부산북부 운전면허시험장", address: "부산광역시 사상구 사상로367번길 35"}
    ]
};

const regionSelect = document.getElementById("regionSelect");
const centerList = document.getElementById("centerList");
const mapTitle = document.getElementById("mapTitle");
const mapAddress = document.getElementById("mapAddress");

if (regionSelect && centerList && mapTitle && mapAddress) {
    renderCenters(regionSelect.value);
    regionSelect.addEventListener("change", () => renderCenters(regionSelect.value));
}

function renderCenters(region) {
    centerList.innerHTML = "";

    centerData[region].forEach((center, index) => {
        const button = document.createElement("button");
        button.type = "button";
        button.className = "center-button" + (index === 0 ? " active" : "");
        button.innerHTML = `<strong>${center.name}</strong><br><small>${center.address}</small>`;

        button.addEventListener("click", () => {
            document.querySelectorAll(".center-button").forEach(btn => btn.classList.remove("active"));
            button.classList.add("active");
            mapTitle.textContent = center.name;
            mapAddress.textContent = center.address;
        });

        centerList.appendChild(button);

        if (index === 0) {
            mapTitle.textContent = center.name;
            mapAddress.textContent = center.address;
        }
    });
}

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

// alert
document.addEventListener("DOMContentLoaded", function() {

    // 성공 메시지 처리
    if (window.successMsg && window.successMsg.trim() !== '') {

        Swal.fire({
            icon: 'success',
            title: '알림',
            text: window.successMsg,
            confirmButtonColor: '#3085d6'
        });

    }

    // 에러 메시지 처리
    if (window.errorMsg && window.errorMsg.trim() !== '') {

        Swal.fire({
            icon: 'error',
            title: '오류',
            text: window.errorMsg,
            confirmButtonColor: '#d33'
        });

    }
});

// 파일 업로드
let fileListArray = [];

// 폼 제출 시 실행할 함수
function syncFilesBeforeSubmit() {

    const fileInput = document.getElementById('fileInput');
    const dataTransfer = new DataTransfer();

    // JS 배열에 있는 파일들을 DataTransfer에 담기
    fileListArray.forEach(file => dataTransfer.items.add(file));

    // 브라우저의 공식 input에 파일들 넣어주기
    fileInput.files = dataTransfer.files;

    return true;

}

function handleFileChange(input) {

    const files = Array.from(input.files);

    // 3개 제한 로직
    if (fileListArray.length + files.length > 3) {

        alert("파일은 최대 3개까지만 업로드할 수 있습니다.");
        input.value = '';
        return;

    }

    files.forEach(file => {

        fileListArray.push(file);

    });

    renderFileList();
    input.value = '';

}

function removeFile(event, index) {

    event.stopPropagation();
    event.preventDefault();

    fileListArray.splice(index, 1);
    renderFileList();

}

function renderFileList() {

    const fileListDiv = document.getElementById('fileList');
    fileListDiv.innerHTML = '';

    if (fileListArray.length === 0) {

        fileListDiv.innerHTML = '<span style="color: #999; font-size: 0.9em;">파일을 클릭하여 선택하세요 (최대 3개)</span>';
        return;

    }

    fileListArray.forEach((file, index) => {

        const fileItem = document.createElement('div');
        fileItem.style.display = 'flex';
        fileItem.style.justifyContent = 'space-between';
        fileItem.style.padding = '5px 0';
        fileItem.innerHTML = `
            <span>✅ ${file.name}</span>
            <span onclick="removeFile(event, ${index})" 
                  style="color: red; cursor: pointer; font-weight: bold; padding: 0 10px;">X</span>
        `;
        fileListDiv.appendChild(fileItem);
    });
}
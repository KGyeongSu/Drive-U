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
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({question: message})
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

            setTimeout(() => {
                scrollChatBottom();
            }, 100);
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
function scrollChatBottom() {

    const chatBody = document.getElementById('chatBody');

    chatBody.scrollTop = chatBody.scrollHeight;

}

// alert
document.addEventListener("DOMContentLoaded", function () {

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
let initialFileCount = 0;
let initialTitle = "";
let initialContent = "";
let deleteIdsArray = [];

// 파일 등록
// 폼 제출 시 실행할 함수
function syncFilesBeforeSubmit() {

    const fileInput = document.getElementById('fileInput');
    const dataTransfer = new DataTransfer();
    // 수정 시 삭제 대상
    const form = document.getElementById('noticeForm');

    deleteIdsArray.forEach(id => {

        const hiddenInput = document.createElement('input');
        hiddenInput.type = 'hidden';
        hiddenInput.name = 'deleteIds';
        hiddenInput.value = id;
        form.appendChild(hiddenInput);

    });

    // JS 배열에 있는 새로 추가된 파일들을 DataTransfer에 담기
    fileListArray.filter(file => !file.isExisting)
        .forEach(file => dataTransfer.items.add(file));

    // 브라우저의 공식 input에 파일들 넣어주기
    fileInput.files = dataTransfer.files;

    return true;

}

// 폼 제출 프로세스 제어
function validateAndSubmit() {

    if (!validateForm()) return false;
    return syncFilesBeforeSubmit();

}

// 필수 입력 체크 함수
function validateForm() {
    const title = document.getElementById("title").value.trim();
    const content = document.getElementById("content").value.trim();

    if (!title) {
        Swal.fire({icon: 'warning', title: '알림', text: '제목을 입력해 주세요.'});
        return false;
    }
    if (!content) {
        Swal.fire({icon: 'warning', title: '알림', text: '내용을 입력해 주세요.'});
        return false;
    }
    return true;
}

// 파일 수정
document.addEventListener("DOMContentLoaded", function () {
    const titleInput = document.getElementById("title");
    const contentTextarea = document.getElementById("content");
    const fileInput = document.getElementById("fileInput");
    const modifyBtn = document.getElementById("modifyBtn");

    if (titleInput && contentTextarea) {
        initialTitle = titleInput.value;
        initialContent = contentTextarea.value;

        titleInput.addEventListener("input", checkChange);
        contentTextarea.addEventListener("input", checkChange);
    }

    if (modifyBtn) {

        if (typeof hasAnswer !== 'undefined' && hasAnswer) {
            modifyBtn.disabled = true;
        } else {
            modifyBtn.disabled = true; // 변경 전까지는 비활성화
        }

    }

    if (fileInput) {
        fileInput.addEventListener("change", handleFileChange);
    }

    // 문서 준비되면 기존 파일 복원 로직 실행
    loadExistingFiles();

});

// 페이지 로드 시 기존 파일 정보를 진짜 File 객체로 복원하는 함수
console.log("js에서 확인한 파일:", filesFromThymeleaf);

async function loadExistingFiles() {

    const existingFiles = (typeof filesFromThymeleaf !== 'undefined') ? filesFromThymeleaf : [];
    console.log("로드할 파일 목록:", existingFiles);

    if (!existingFiles ||existingFiles.length === 0) return;

    fileListArray = [];

    existingFiles.forEach(f => {
        const fileObj = {
            name: f.fileName,
            isExisting: true,
            fileId: f.id
        };

        fileListArray.push(fileObj);

    });

    initialFileCount = fileListArray.length;

    renderFileList();

}

const isAnswered = (typeof hasAnswer !== 'undefined') ? hasAnswer : false;

function checkChange() {
    const titleInput = document.getElementById("title");
    const contentTextarea = document.getElementById("content");
    const modifyBtn = document.getElementById("modifyBtn");

    if (!modifyBtn || !titleInput || !contentTextarea) return;

    if (isAnswered) {
        modifyBtn.disabled = true;
        return;
    }

    // 텍스트 변경 감지
    const isTextChanged = (titleInput.value !== initialTitle || contentTextarea.value !== initialContent);
    // 파일 변경 감지
    const hasNewFile = fileListArray.some(file => !file.isExisting);
    const isExistingFileDeleted = (fileListArray.filter(file => file.isExisting).length !== initialFileCount);
    const isFileChanged = hasNewFile || isExistingFileDeleted;

    if (isTextChanged || isFileChanged) {
        modifyBtn.disabled = false;
    } else {
        modifyBtn.disabled = true;
    }
}

// 새 파일 선택 시 처리 함수
function handleFileChange(targetOrEvent) {
    const input = targetOrEvent.target ? targetOrEvent.target : targetOrEvent;
    const files = Array.from(input.files);

    if (fileListArray.length + files.length > 3) {
        Swal.fire({
            icon: 'warning',
            title: '업로드 제한',
            text: '파일은 최대 3개까지만 업로드할 수 있습니다.',
            confirmButtonColor: '#3085d6'
        });
        input.value = '';
        return;
    }

    files.forEach(file => {
        file.isExisting = false;
        fileListArray.push(file);
    });

    renderFileList();
    input.value = '';
}

function renderFileList() {
    const fileListDiv = document.getElementById('fileList');
    if (!fileListDiv) return;

    fileListDiv.innerHTML = '';

    if (fileListArray.length === 0) {
        fileListDiv.innerHTML = '<span style="color: #999; font-size: 0.9em;">파일을 클릭하여 선택하세요</span>';
        checkChange();
        return;
    }

    fileListArray.forEach((file, index) => {
        const fileItem = document.createElement('div');
        fileItem.style.display = 'flex';
        fileItem.style.justifyContent = 'space-between';
        fileItem.style.alignItems = 'center';
        fileItem.style.padding = '6px 4px';
        fileItem.style.borderBottom = index === fileListArray.length - 1 ? 'none' : '1px dashed #eee';

        const icon = file.isExisting ? '✅' : '🆕';
        const iconColor = file.isExisting ? '#28a745' : '#007bff';

        fileItem.innerHTML = `
            <span style="font-size: 0.95em; color: #333; font-weight: 500;">
                <span style="color: ${iconColor}; margin-right: 5px;">${icon}</span> ${file.name}
            </span>
            <span onclick="removeFile(event, ${index})" 
                  style="color: red; cursor: pointer; font-weight: bold; padding: 0 10px; font-size: 1.1em;">X</span>
        `;
        fileListDiv.appendChild(fileItem);
    });

    checkChange();
}

// 파일 삭제 함수
function removeFile(event, index) {

    event.stopPropagation();
    event.preventDefault();

    const file = fileListArray[index];

    if (file.isExisting) {

        deleteIdsArray.push(file.fileId);

    }

    fileListArray.splice(index, 1);
    renderFileList();

}

// 범용 삭제 확인 함수
function confirmDelete(deleteUrl, formId) {
    Swal.fire({
        title: '정말 삭제하시겠습니까?',
        text: "삭제된 데이터는 복구할 수 없습니다.",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#d33',
        cancelButtonColor: '#3085d6',
        confirmButtonText: '삭제',
        cancelButtonText: '취소'
    }).then((result) => {
        if (result.isConfirmed) {
            const form = document.getElementById(formId);
            if (!form) {
                console.error("해당 ID의 폼을 찾을 수 없습니다: " + formId);
                return;
            }
            form.action = deleteUrl;
            form.method = "post";
            form.submit();
        }
    });
}
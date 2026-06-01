let isIdChecked = false;
let isEmailChecked = false;

// 1. 아이디 입력 시 상태 리셋
if (document.getElementById("idInput")) {
    document.getElementById("idInput").addEventListener("input", function() {
        isIdChecked = false;
        document.getElementById("idMessage").innerText = "";
    });
}

// 이메일(아이디 또는 도메인) 입력 시 중복 확인 상태 리셋
if (document.getElementById("email-id")) {
    document.getElementById("email-id").addEventListener("input", function() {
        isEmailChecked = false;
        if(document.getElementById("emailMessage")) document.getElementById("emailMessage").innerText = "";
    });
}
if (document.getElementById("email-domain")) {
    document.getElementById("email-domain").addEventListener("input", function() {
        isEmailChecked = false;
        if(document.getElementById("emailMessage")) document.getElementById("emailMessage").innerText = "";
    });
}

// 아이디 중복 체크 함수
function checkId() {
    const idInput = document.getElementById("idInput").value;
    const msg = document.getElementById("idMessage");

    if (!idInput) {
        alert("아이디를 입력해주세요.");
        return;
    }

    fetch(`/login/checkId?id=${idInput}`)
        .then(response => response.json())
        .then(isDuplicate => {
            if (isDuplicate) {
                msg.innerText = "이미 사용 중인 아이디입니다.";
                msg.style.color = "red";
                isIdChecked = false;
            } else {
                msg.innerText = "사용 가능한 아이디입니다.";
                msg.style.color = "blue";
                isIdChecked = true;
            }
        });
}

// 이메일 중복 체크 함수
function checkEmail() {
    combineEmail();

    const realEmail = document.getElementById('real-email').value;
    const msg = document.getElementById("emailMessage");

    if (!realEmail || !realEmail.includes('@') || realEmail.endsWith('@')) {
        alert("올바른 이메일 형식을 입력해주세요.");
        return;
    }

    fetch(`/login/checkEmail?email=${encodeURIComponent(realEmail)}`)
        .then(response => response.text())
        .then(result => {
            if (!msg) return;

            if (result === "DUPLICATE") {
                msg.innerText = "이미 존재하는 이메일입니다.";
                msg.style.color = "red";
                isEmailChecked = false;
            } else if (result === "AVAILABLE") {
                msg.innerText = "사용 가능한 이메일입니다.";
                msg.style.color = "blue";
                isEmailChecked = true;
            }
        })
        .catch(error => {
            console.error("이메일 체크 중 오류:", error);
        });
}

// 이메일 도메인 셀렉트 박스 변경 함수
function handleEmailSelect() {
    const selectBox = document.getElementById('email-select');
    const domainInput = document.getElementById('email-domain');

    if (selectBox.value === 'direct') {
        domainInput.value = '';
        domainInput.readOnly = false;
        domainInput.placeholder = '직접 입력';
        domainInput.focus();
    } else {
        domainInput.value = selectBox.value;
        domainInput.readOnly = true;
    }

    isEmailChecked = false;
    if(document.getElementById("emailMessage")) document.getElementById("emailMessage").innerText = "";

    combineEmail();
}

// 이메일 결합 함수
function combineEmail() {
    const emailId = document.getElementById('email-id').value.trim();
    const emailDomain = document.getElementById('email-domain').value.trim();
    const realEmailInput = document.getElementById('real-email');

    if (realEmailInput) {
        if (emailId && emailDomain) {
            realEmailInput.value = emailId + '@' + emailDomain;
        } else {
            realEmailInput.value = '';
        }
    }
}

// 서브밋 전 이메일 준비 함수
function prepareEmailSubmit() {
    const emailId = document.getElementById('email-id').value.trim();
    const emailDomain = document.getElementById('email-domain').value.trim();
    const realEmailInput = document.getElementById('real-email');

    if (!emailId || !emailDomain) {
        if (realEmailInput) realEmailInput.value = '';
        return true;
    }

    if (realEmailInput) realEmailInput.value = emailId + '@' + emailDomain;
    return true;
}

// 페이지 로드 시 실행
window.addEventListener('DOMContentLoaded', () => {
    const realEmailInput = document.getElementById('real-email');
    const realEmail = realEmailInput ? realEmailInput.value : '';

    if (realEmail && realEmail.includes('@')) {
        const splitEmail = realEmail.split('@');
        const emailId = splitEmail[0];
        const emailDomain = splitEmail[1];

        const idInput = document.getElementById('email-id');
        const domainInput = document.getElementById('email-domain');
        const selectBox = document.getElementById('email-select');

        idInput.value = emailId;
        domainInput.value = emailDomain;

        const optionExists = Array.from(selectBox.options).some(option => option.value === emailDomain);

        if (optionExists) {
            selectBox.value = emailDomain;
        } else {
            selectBox.value = 'direct';
        }

        idInput.readOnly = true;
        domainInput.readOnly = true;
        selectBox.disabled = true;

        const hasThymeLeafEmailError = document.querySelector(".form-group p[style*='color: #dc3545']") !== null;

        if (hasThymeLeafEmailError) {
            isEmailChecked = false; // 중복 계정이므로 가입 불가 잠금
        } else {
            isEmailChecked = true;  // 에러가 없다면 소셜 유저는 기본 true 인정
        }

        combineEmail();
    }
});

// 최종 서브밋 유효성 검사 함수
function onSignUpSubmit(event) {
    const isSocial = document.getElementById('idInput') == null;

    // 1. 일반 회원 가입인데 아이디 중복체크를 안 했다면 차단
    if (!isSocial && !isIdChecked) {
        alert("아이디 중복 확인을 완료해 주세요.");
        event.preventDefault();
        return false;
    }

    // 2. 이메일 중복체크 검증
    if (!isEmailChecked) {
        // 소셜 유저인데 중복 확인이 안 된 경우
        if (isSocial) {
            alert("이미 가입된 일반 계정이 존재하는 이메일입니다. 소셜 가입을 진행할 수 없습니다.");
        } else {
            alert("이메일 중복 확인을 완료해 주세요.");
        }
        event.preventDefault();
        return false;
    }

    prepareEmailSubmit();
    return true;
}
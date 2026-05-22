let isIdChecked = false;

if (document.getElementById("idInput")) {
    document.getElementById("idInput").addEventListener("input", function() {
        isIdChecked = false;
        document.getElementById("idMessage").innerText = "";
    });
}

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
    combineEmail();
}

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

        combineEmail();
    }
});
function toggleAddressEdit(isEdit) {
    const displayArea = document.getElementById('address-display-area');
    const editArea = document.getElementById('address-edit-area');

    if (isEdit) {
        displayArea.style.display = 'none';
        editArea.style.display = 'block';

        document.getElementById('input-address').focus();
    } else {
        displayArea.style.display = 'block';
        editArea.style.display = 'none';
    }
}

// 주소 전송 함수
function submitAddress() {
    const newAddress = document.getElementById('input-address').value;

    if (!newAddress || !newAddress.trim()) {
        alert('주소를 입력해 주세요!');
        return;
    }

    const formData = new FormData();
    formData.append('address', newAddress.trim());

    fetch('/drive-u/updateAddress', {
        method: 'POST',
        body: formData
    })
        .then(response => response.text())
        .then(data => {
            if (data === 'SUCCESS') {
                alert('주소가 성공적으로 저장되었습니다!');

                // 1. 화면의 현재 주소 글자 실시간 변경
                const currentAddressElement = document.getElementById('current-address');
                if (currentAddressElement) {
                    currentAddressElement.innerText = newAddress.trim();
                }
                
                toggleAddressEdit(false);
            } else {
                alert('수정에 실패했습니다: ' + data);
            }
        })
        .catch(error => {
            console.error('통신 에러 발생:', error);
            alert('서버 통신 중 에러가 발생했습니다.');
        });
}
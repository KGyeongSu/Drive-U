// pagination
async function loadPage(url, page) {

    const urlParams = new URLSearchParams(window.location.search);

    urlParams.set('page', page);

    const response = await fetch(`${url}?${urlParams.toString()}`);
    const html = await response.text();

    document.getElementById('ajax-update-area').innerHTML = html;

}

// 유효성 처리
document.addEventListener("DOMContentLoaded", function() {

    const titleInput = document.getElementById("title");
    const contentTextarea = document.getElementById("content");
    const modifyBtn = document.getElementById("modifyBtn");

    // 수정 버튼이 존재할 경우에만 변경 감지 로직 실행
    if (modifyBtn && titleInput && contentTextarea) {

        // 초기값 저장 (데이터가 있을 경우에만)
        const initialTitle = titleInput.value;
        const initialContent = contentTextarea.value;

        // 수정 버튼 비활성화 (초기 상태)
        modifyBtn.disabled = true;

        // 내용 변경 시 버튼 활성화
        const checkChange = () => {

            if (titleInput.value !== initialTitle || contentTextarea.value !== initialContent) {

                modifyBtn.disabled = false;

            } else {

                modifyBtn.disabled = true;

            }
        };

        titleInput.addEventListener("input", checkChange);
        contentTextarea.addEventListener("input", checkChange);
    }
});

// 필수 입력 체크 함수
function validateForm() {

    const title = document.getElementById("title").value.trim();
    const content = document.getElementById("content").value.trim();

    if (!title) {

        Swal.fire({ icon: 'warning', title: '알림', text: '제목을 입력해 주세요.' });
        return false;

    }
    if (!content) {

        Swal.fire({ icon: 'warning', title: '알림', text: '내용을 입력해 주세요.' });
        return false;

    }
    return true;
}

// 문의사항 글 삭제 시 alert 문 확인
function confirmDelete(button) {

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

            const form = button.closest('form');

            form.action = "/drive-u/userInfo/questionRemove";
            form.method = "post";
            form.submit();

        }
    });
}

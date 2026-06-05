document.querySelectorAll(".admin-tab").forEach((tab) => {
    tab.addEventListener("click", () => {
        const targetId = tab.dataset.target;

        document.querySelectorAll(".admin-tab").forEach((button) => {
            button.classList.remove("active");
        });

        document.querySelectorAll(".admin-panel").forEach((panel) => {
            panel.classList.remove("active");
        });

        tab.classList.add("active");
        document.getElementById(targetId)?.classList.add("active");
    });
});

// 페이지 로드 시 검색 결과 스크롤 및 검색어 삭제 감지
document.addEventListener("DOMContentLoaded", function() {
    // 검색 후 자동 스크롤
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.has('keyword') || urlParams.has('type') || urlParams.has('status')) {
        const searchArea = document.getElementById('searchResult');
        if (searchArea) {
            searchArea.scrollIntoView({ behavior: 'smooth' });
        }
    }

    // 검색어 입력창 감지
    const keywordInput = document.querySelector('input[name="keyword"]');
    if (keywordInput) {
        keywordInput.addEventListener('input', function(e) {
            // 값이 완전히 지워졌을 때만 이동
            if (e.target.value.trim() === "") {
                window.location.href = "/drive-u/admin";

            }
        });
    }
});
document.addEventListener("DOMContentLoaded", function () {
    initAdminTabs();
    initAdminSearch();
    initExamPassManagement();
});

/* =========================================================
   관리자 탭
========================================================= */
function initAdminTabs() {
    const adminTabs = document.querySelectorAll(".admin-tab");

    if (adminTabs.length === 0) {
        return;
    }

    adminTabs.forEach((tab) => {
        tab.addEventListener("click", function () {
            const targetId = tab.dataset.target;

            document.querySelectorAll(".admin-tab").forEach((button) => {
                button.classList.remove("active");
            });

            document.querySelectorAll(".admin-panel").forEach((panel) => {
                panel.classList.remove("active");
            });

            tab.classList.add("active");

            if (targetId) {
                document
                    .getElementById(targetId)
                    ?.classList.add("active");
            }
        });
    });
}

/* =========================================================
   검색 결과 스크롤 및 검색어 삭제 감지
========================================================= */
function initAdminSearch() {
    const urlParams =
        new URLSearchParams(window.location.search);

    const hasSearchCondition =
        urlParams.has("keyword")
        || urlParams.has("type")
        || urlParams.has("status");

    if (hasSearchCondition) {
        const searchArea =
            document.getElementById("searchResult");

        if (searchArea) {
            searchArea.scrollIntoView({
                behavior: "smooth"
            });
        }
    }

    const keywordInput =
        document.querySelector('input[name="keyword"]');

    if (!keywordInput) {
        return;
    }

    keywordInput.addEventListener("input", function (event) {
        if (event.target.value.trim() === "") {
            window.location.href = "/drive-u/admin";
        }
    });
}

/* =========================================================
   시험 합격·불합격 처리
========================================================= */
function initExamPassManagement() {
    /*
     * 합격처리 페이지에서만 존재하는 요소들
     */
    const passCenter =
        document.getElementById("passCenter");

    const passTime =
        document.getElementById("passTime");

    const passDate =
        document.getElementById("passDate");

    const candidateBody =
        document.getElementById("candidateBody");

    const candidatePagination =
        document.getElementById("candidatePagination");

    const loadCandidatesBtn =
        document.getElementById("loadCandidatesBtn");

    const passTab =
        document.querySelector(
            '.admin-tab[data-target="passPanel"]'
        );

    /*
     * 학습영상·CBT 등 다른 관리자 페이지에서는
     * 합격처리 요소가 없으므로 여기서 종료
     */
    if (
        !passCenter
        || !passTime
        || !passDate
        || !candidateBody
        || !candidatePagination
        || !loadCandidatesBtn
    ) {
        return;
    }

    let allCandidates = [];
    let currentPage = 0;

    const PAGE_SIZE = 5;

    /* -----------------------------------------------------
       전체 응시자 명단 조회
    ----------------------------------------------------- */
    async function loadCandidates() {
        try {
            const response = await fetch(
                "/drive-u/admin/examPass/candidates"
            );

            if (!response.ok) {
                throw new Error(
                    `응시자 조회 실패: ${response.status}`
                );
            }

            allCandidates = await response.json();

            buildFilterOptions();
            renderCandidates();
        } catch (error) {
            console.error(error);
            alert("응시자 명단을 불러오지 못했습니다.");
        }
    }

    /* -----------------------------------------------------
       시험장·시간 필터 선택지 생성
    ----------------------------------------------------- */
    function buildFilterOptions() {
        const centers = [
            ...new Set(
                allCandidates
                    .map((candidate) => candidate.centerName)
                    .filter(Boolean)
            )
        ];

        const times = [
            ...new Set(
                allCandidates
                    .map((candidate) => candidate.examTime)
                    .filter(Boolean)
            )
        ].sort();

        passCenter.innerHTML =
            '<option value="">전체</option>'
            + centers
                .map((center) => {
                    return `
                        <option value="${escapeHtml(center)}">
                            ${escapeHtml(center)}
                        </option>
                    `;
                })
                .join("");

        passTime.innerHTML =
            '<option value="">전체</option>'
            + times
                .map((time) => {
                    const displayTime =
                        String(time).substring(0, 5);

                    return `
                        <option value="${escapeHtml(time)}">
                            ${escapeHtml(displayTime)}
                        </option>
                    `;
                })
                .join("");
    }

    /* -----------------------------------------------------
       필터링 및 응시자 테이블 출력
    ----------------------------------------------------- */
    function renderCandidates() {
        const center = passCenter.value;
        const time = passTime.value;
        const date = passDate.value;

        const filteredCandidates =
            allCandidates.filter((candidate) => {
                const centerMatches =
                    center === ""
                    || candidate.centerName === center;

                const timeMatches =
                    time === ""
                    || candidate.examTime === time;

                const dateMatches =
                    date === ""
                    || candidate.examDate === date;

                return (
                    centerMatches
                    && timeMatches
                    && dateMatches
                );
            });

        if (filteredCandidates.length === 0) {
            candidateBody.innerHTML = `
                <tr>
                    <td colspan="9">
                        해당 조건의 응시자가 없습니다.
                    </td>
                </tr>
            `;

            renderPagination(0);
            return;
        }

        const totalPages = Math.ceil(
            filteredCandidates.length / PAGE_SIZE
        );

        if (currentPage >= totalPages) {
            currentPage = totalPages - 1;
        }

        if (currentPage < 0) {
            currentPage = 0;
        }

        const startIndex =
            currentPage * PAGE_SIZE;

        const pageItems =
            filteredCandidates.slice(
                startIndex,
                startIndex + PAGE_SIZE
            );

        candidateBody.innerHTML =
            pageItems
                .map((candidate) => {
                    const isPassed =
                        candidate.examPassId != null;

                    const isFailed =
                        candidate.examFailId != null;

                    let scoreCell = "";

                    if (isPassed) {
                        scoreCell = `
                            <span class="passed-badge">
                                합격
                            </span>
                        `;
                    } else if (isFailed) {
                        scoreCell = `
                            <span class="failed-badge">
                                불합격
                            </span>
                        `;
                    } else {
                        scoreCell = `
                            <input
                                type="number"
                                class="score-input"
                                min="0"
                                max="100"
                                placeholder="점수">
                        `;
                    }

                    let actionCell = "";

                    if (isPassed) {
                        actionCell = `
                            <button
                                type="button"
                                class="cancel-btn">
                                합격취소
                            </button>
                        `;
                    } else if (isFailed) {
                        actionCell = `
                            <button
                                type="button"
                                class="cancel-fail-btn">
                                불합격취소
                            </button>
                        `;
                    } else {
                        actionCell = `
                            <button
                                type="button"
                                class="pass-btn">
                                합격처리
                            </button>
                        `;
                    }

                    const examTime =
                        candidate.examTime
                            ? String(candidate.examTime)
                                .substring(0, 5)
                            : "";

                    return `
                        <tr
                            data-app-id="${candidate.applicationId}"
                            data-exam-pass-id="${candidate.examPassId ?? ""}"
                            data-exam-fail-id="${candidate.examFailId ?? ""}">

                            <td>
                                ${escapeHtml(candidate.applicationId)}
                            </td>

                            <td>
                                ${escapeHtml(candidate.examType)}
                            </td>

                            <td>
                                ${escapeHtml(candidate.licenseType)}
                            </td>

                            <td>
                                ${escapeHtml(candidate.centerName)}
                            </td>

                            <td>
                                ${escapeHtml(candidate.examDate)}
                                ${escapeHtml(examTime)}
                            </td>

                            <td>
                                ${escapeHtml(candidate.contactName)}
                            </td>

                            <td>
                                ${escapeHtml(candidate.contactPhone)}
                            </td>

                            <td>
                                ${scoreCell}
                            </td>

                            <td>
                                ${actionCell}
                            </td>
                        </tr>
                    `;
                })
                .join("");

        renderPagination(totalPages);
    }

    /* -----------------------------------------------------
       페이지네이션 출력
    ----------------------------------------------------- */
    function renderPagination(totalPages) {
        if (totalPages <= 1) {
            candidatePagination.innerHTML = "";
            return;
        }

        let html = "";

        html += `
            <button
                type="button"
                class="page-nav-btn"
                data-page="0"
                ${currentPage === 0 ? "disabled" : ""}>
                &laquo;
            </button>
        `;

        html += `
            <button
                type="button"
                class="page-nav-btn"
                data-page="${currentPage - 1}"
                ${currentPage === 0 ? "disabled" : ""}>
                이전
            </button>
        `;

        for (let page = 0; page < totalPages; page++) {
            html += `
                <button
                    type="button"
                    class="page-num-btn
                        ${page === currentPage ? "active" : ""}"
                    data-page="${page}">
                    ${page + 1}
                </button>
            `;
        }

        html += `
            <button
                type="button"
                class="page-nav-btn"
                data-page="${currentPage + 1}"
                ${currentPage === totalPages - 1
            ? "disabled"
            : ""}>
                다음
            </button>
        `;

        html += `
            <button
                type="button"
                class="page-nav-btn"
                data-page="${totalPages - 1}"
                ${currentPage === totalPages - 1
            ? "disabled"
            : ""}>
                &raquo;
            </button>
        `;

        candidatePagination.innerHTML = html;
    }

    /* -----------------------------------------------------
       필터 변경
    ----------------------------------------------------- */
    function onFilterChange() {
        currentPage = 0;
        renderCandidates();
    }

    passCenter.addEventListener(
        "change",
        onFilterChange
    );

    passTime.addEventListener(
        "change",
        onFilterChange
    );

    passDate.addEventListener(
        "change",
        onFilterChange
    );

    /* -----------------------------------------------------
       페이지 번호 클릭
    ----------------------------------------------------- */
    candidatePagination.addEventListener(
        "click",
        function (event) {
            const button =
                event.target.closest("button[data-page]");

            if (!button || button.disabled) {
                return;
            }

            const selectedPage =
                Number(button.dataset.page);

            if (Number.isNaN(selectedPage)) {
                return;
            }

            currentPage = selectedPage;
            renderCandidates();
        }
    );

    /* -----------------------------------------------------
       합격처리
    ----------------------------------------------------- */
    candidateBody.addEventListener(
        "click",
        async function (event) {
            const passButton =
                event.target.closest(".pass-btn");

            if (!passButton) {
                return;
            }

            const row =
                passButton.closest("tr");

            if (!row) {
                return;
            }

            const applicationId =
                Number(row.dataset.appId);

            const scoreInput =
                row.querySelector(".score-input");

            if (!scoreInput) {
                return;
            }

            const score =
                Number(scoreInput.value);

            if (
                scoreInput.value === ""
                || Number.isNaN(score)
                || score < 0
                || score > 100
            ) {
                alert(
                    "점수를 0~100 사이로 입력하세요."
                );
                return;
            }

            try {
                const response = await fetch(
                    "/drive-u/admin/examPass",
                    {
                        method: "POST",
                        headers: {
                            "Content-Type":
                                "application/json"
                        },
                        body: JSON.stringify({
                            applicationId:
                            applicationId,
                            score: score
                        })
                    }
                );

                const data = await readJsonSafely(response);

                alert(
                    data.message
                    || "처리가 완료되었습니다."
                );

                if (response.ok) {
                    await loadCandidates();
                }
            } catch (error) {
                console.error(error);
                alert(
                    "합격처리 중 오류가 발생했습니다."
                );
            }
        }
    );

    /* -----------------------------------------------------
       불합격 취소
    ----------------------------------------------------- */
    candidateBody.addEventListener(
        "click",
        async function (event) {
            const cancelFailButton =
                event.target.closest(
                    ".cancel-fail-btn"
                );

            if (!cancelFailButton) {
                return;
            }

            const row =
                cancelFailButton.closest("tr");

            if (!row) {
                return;
            }

            const examFailId =
                row.dataset.examFailId;

            if (!examFailId) {
                alert(
                    "불합격 처리 정보를 찾을 수 없습니다."
                );
                return;
            }

            const confirmed =
                confirm("이 불합격을 취소할까요?");

            if (!confirmed) {
                return;
            }

            try {
                const response = await fetch(
                    `/drive-u/admin/examPass/fail/${examFailId}`,
                    {
                        method: "DELETE"
                    }
                );

                const data =
                    await readJsonSafely(response);

                alert(
                    data.message
                    || "처리가 완료되었습니다."
                );

                if (response.ok) {
                    await loadCandidates();
                }
            } catch (error) {
                console.error(error);
                alert(
                    "불합격취소 중 오류가 발생했습니다."
                );
            }
        }
    );

    /* -----------------------------------------------------
       응시자 조회 버튼
    ----------------------------------------------------- */
    loadCandidatesBtn.addEventListener(
        "click",
        function () {
            passCenter.value = "";
            passTime.value = "";
            passDate.value = "";

            currentPage = 0;
            loadCandidates();
        }
    );

    /* -----------------------------------------------------
       합격처리 탭 클릭 시 최초 1회 조회
    ----------------------------------------------------- */
    if (passTab) {
        passTab.addEventListener(
            "click",
            function () {
                if (allCandidates.length === 0) {
                    loadCandidates();
                }
            },
            {
                once: true
            }
        );
    }

    /*
     * 페이지에 들어왔을 때 합격처리 패널이
     * 이미 활성화되어 있다면 바로 조회
     */
    const passPanel =
        document.getElementById("passPanel");

    if (
        passPanel
        && passPanel.classList.contains("active")
        && allCandidates.length === 0
    ) {
        loadCandidates();
    }
}

/* =========================================================
   JSON 안전 파싱
========================================================= */
async function readJsonSafely(response) {
    const contentType =
        response.headers.get("content-type") || "";

    if (!contentType.includes("application/json")) {
        return {
            message: response.ok
                ? "처리가 완료되었습니다."
                : "서버 처리 중 오류가 발생했습니다."
        };
    }

    return response.json();
}

/* =========================================================
   동적 HTML 출력값 이스케이프
========================================================= */
function escapeHtml(value) {
    if (value === null || value === undefined) {
        return "";
    }

    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}
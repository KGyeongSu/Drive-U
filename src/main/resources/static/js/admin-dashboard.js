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
            searchArea.scrollIntoView({behavior: 'smooth'});
        }
    }

    // 검색어 입력창 감지
    const keywordInput = document.querySelector('input[name="keyword"]');
    if (keywordInput) {
        keywordInput.addEventListener('input', function (e) {
            // 값이 완전히 지워졌을 때만 이동
            if (e.target.value.trim() === "") {
                window.location.href = "/drive-u/admin";

            }
        });
    }
});

// ===== 합격처리 =====
let allCandidates = [];   // 서버에서 받은 COMPLETED 전체 명단 (필터의 원본)
let currentPage = 0;      // 현재 페이지 (0부터 시작)
const PAGE_SIZE = 5;     // 한 페이지에 보여줄 행 수

// 1. 전체 응시자 명단 조회
async function loadCandidates() {
    try {
        const res = await fetch("/drive-u/admin/examPass/candidates");
        if (!res.ok) throw new Error("조회 실패");
        allCandidates = await res.json();

        buildFilterOptions();   // 필터 select 채우기
        renderCandidates();     // 테이블 그리기
    } catch (e) {
        console.error(e);
        alert("응시자 명단을 불러오지 못했습니다.");
    }
}
// 2. 명단에서 시험장·시간대 추출해 필터 옵션 생성 (중복 제거)
function buildFilterOptions() {
    const centerSel = document.getElementById("passCenter");
    const timeSel   = document.getElementById("passTime");

    // Set 으로 중복 제거
    const centers = [...new Set(allCandidates.map(c => c.centerName))];
    const times   = [...new Set(allCandidates.map(c => c.examTime))].sort();

    // "전체" 기본 옵션 + 추출값
    centerSel.innerHTML = `<option value="">전체</option>`
        + centers.map(c => `<option value="${c}">${c}</option>`).join("");

    timeSel.innerHTML = `<option value="">전체</option>`
        + times.map(t => `<option value="${t}">${t.substring(0,5)}</option>`).join("");
}
// 3. 현재 필터로 명단 거르고 테이블 그리기
function renderCandidates() {
    const center = document.getElementById("passCenter").value;
    const time   = document.getElementById("passTime").value;
    const date   = document.getElementById('passDate').value;
    const tbody  = document.getElementById("candidateBody");

    // 필터: 값이 "" (전체)면 통과, 아니면 일치하는 것만
    const filtered = allCandidates.filter(c =>
        (center === "" || c.centerName === center) &&
        (time   === "" || c.examTime === time) &&
        (date   === "" || c.examDate === date)
    );

    if (filtered.length === 0) {
        tbody.innerHTML = `<tr><td colspan="9">해당 조건의 응시자가 없습니다.</td></tr>`;
        renderPagination(0);   // 결과 없으면 페이지버튼도 비움
        return;
    }

    // ── 페이지 슬라이스 ──
    const totalPages = Math.ceil(filtered.length / PAGE_SIZE);
    // 필터가 바뀌어 페이지 수가 줄면 currentPage가 범위를 벗어날 수 있음 → 보정
    if (currentPage >= totalPages) currentPage = totalPages - 1;
    if (currentPage < 0) currentPage = 0;

    const start = currentPage * PAGE_SIZE;
    const pageItems = filtered.slice(start, start + PAGE_SIZE);

    tbody.innerHTML = pageItems.map(c => {
        // 상태 우선순위: 합격 > 불합격 > 미처리
        const isPassed = c.examPassId != null;
        const isFailed = c.examFailId != null;

        // 점수칸: 합격이면 배지, 불합격이면 '불합격' 표시, 미처리면 입력칸
        let scoreCell;
        if (isPassed) {
            scoreCell = `<span class="passed-badge">합격</span>`;
        } else if (isFailed) {
            scoreCell = `<span class="failed-badge">불합격</span>`;
        } else {
            scoreCell = `<input type="number" class="score-input" min="0" max="100" placeholder="점수">`;
        }

        // 처리칸: 합격이면 합격취소, 불합격이면 불합격취소, 미처리면 합격처리
        let actionCell;
        if (isPassed) {
            actionCell = `<button type="button" class="cancel-btn">합격취소</button>`;
        } else if (isFailed) {
            actionCell = `<button type="button" class="cancel-fail-btn">불합격취소</button>`;
        } else {
            actionCell = `<button type="button" class="pass-btn">합격처리</button>`;
        }

        return `
    <tr data-app-id="${c.applicationId}"
        data-exam-pass-id="${c.examPassId ?? ''}"
        data-exam-fail-id="${c.examFailId ?? ''}">
        <td>${c.applicationId}</td>
        <td>${c.examType}</td>
        <td>${c.licenseType}</td>
        <td>${c.centerName}</td>
        <td>${c.examDate} ${c.examTime.substring(0,5)}</td>
        <td>${c.contactName}</td>
        <td>${c.contactPhone}</td>
        <td>${scoreCell}</td>
        <td>${actionCell}</td>
    </tr>`;
    }).join("");
    renderPagination(totalPages);
}
// 페이지 버튼 그리기 (팀원 현황 탭과 같은 클래스 사용 → 모양 통일)
function renderPagination(totalPages) {
    const box = document.getElementById("candidatePagination");

    // 페이지가 1개 이하면 버튼 숨김
    if (totalPages <= 1) {
        box.innerHTML = "";
        return;
    }

    let html = "";

    // « 처음 / 이전 (첫 페이지면 비활성)
    html += `<button class="page-nav-btn" data-page="0" ${currentPage === 0 ? "disabled" : ""}>&laquo;</button>`;
    html += `<button class="page-nav-btn" data-page="${currentPage - 1}" ${currentPage === 0 ? "disabled" : ""}>이전</button>`;

    // 숫자 버튼 (현재 페이지엔 active)
    for (let i = 0; i < totalPages; i++) {
        html += `<button class="page-num-btn ${i === currentPage ? "active" : ""}" data-page="${i}">${i + 1}</button>`;
    }

    // 다음 / 끝 » (마지막 페이지면 비활성)
    html += `<button class="page-nav-btn" data-page="${currentPage + 1}" ${currentPage === totalPages - 1 ? "disabled" : ""}>다음</button>`;
    html += `<button class="page-nav-btn" data-page="${totalPages - 1}" ${currentPage === totalPages - 1 ? "disabled" : ""}>&raquo;</button>`;

    box.innerHTML = html;
}
// 4. 필터 select 변경 → 즉시 다시 렌더
// 필터 바뀌면 1페이지부터 다시
function onFilterChange() {
    currentPage = 0;
    renderCandidates();
}
document.getElementById("passCenter").addEventListener("change", onFilterChange);
document.getElementById("passTime").addEventListener("change", onFilterChange);
document.getElementById('passDate').addEventListener("change", onFilterChange);

// 페이지 버튼 클릭 (이벤트 위임)
document.getElementById("candidatePagination").addEventListener("click", (e) => {
    const btn = e.target.closest("button");
    if (!btn || btn.disabled) return;
    currentPage = Number(btn.dataset.page);
    renderCandidates();
});

// 5. 합격처리 버튼 (이벤트 위임)
document.getElementById("candidateBody").addEventListener("click", async (e) => {
    if (!e.target.classList.contains("pass-btn")) return;

    const row   = e.target.closest("tr");
    const appId = Number(row.dataset.appId);
    const input = row.querySelector(".score-input");
    const score = Number(input.value);

    if (input.value === "" || score < 0 || score > 100) {
        alert("점수를 0~100 사이로 입력하세요.");
        return;
    }

    try {
        const res = await fetch("/drive-u/admin/examPass", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ applicationId: appId, score: score })
        });
        const data = await res.json();

        alert(data.message);              // "합격 처리됐습니다." / "불합격 (기준 점수 미달)"
        // 합격이든 불합격이든 DB에 기록됨(exam_pass / exam_fail) → 명단 다시 그리면 상태 복원
        loadCandidates();
    } catch (err) {
        console.error(err);
        alert("합격처리 중 오류가 발생했습니다.");
    }
});
// 6. 탭이 합격처리로 바뀔 때 최초 1회 로딩
document.querySelector('.admin-tab[data-target="passPanel"]')
    ?.addEventListener("click", () => {
        if (allCandidates.length === 0) loadCandidates();
    }, { once: true });
// 7-1. 불합격취소 버튼 (합격취소의 미러)
document.getElementById("candidateBody").addEventListener("click", async (e) => {
    if (!e.target.classList.contains("cancel-fail-btn")) return;

    const row        = e.target.closest("tr");
    const examFailId = row.dataset.examFailId;

    if (!confirm("이 불합격을 취소할까요?")) return;

    try {
        const res = await fetch(`/drive-u/admin/examPass/fail/${examFailId}`, {
            method: "DELETE"
        });
        const data = await res.json();

        alert(data.message);          // "불합격이 취소됐습니다." / 예외 메시지

        if (res.ok) {
            loadCandidates();         // 명단 다시 로딩 → 취소된 행이 미처리로 되돌아감
        }
    } catch (err) {
        console.error(err);
        alert("불합격취소 중 오류가 발생했습니다.");
    }
});
// 8. '응시자 조회' = 전체 새로고침
document.getElementById("loadCandidatesBtn").addEventListener("click", () => {
    document.getElementById("passCenter").value = "";
    document.getElementById("passTime").value = "";
    document.getElementById("passDate").value = "";
    loadCandidates();
});
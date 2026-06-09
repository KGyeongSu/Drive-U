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
        return;
    }

    tbody.innerHTML = filtered.map(c => {
        // 합격행이 있으면(examPassId 존재) 합격 상태로 렌더
        const isPassed = c.examPassId != null;

        return `
    <tr data-app-id="${c.applicationId}" data-exam-pass-id="${c.examPassId ?? ''}">
        <td>${c.applicationId}</td>
        <td>${c.examType}</td>
        <td>${c.licenseType}</td>
        <td>${c.centerName}</td>
        <td>${c.examDate} ${c.examTime.substring(0,5)}</td>
        <td>${c.contactName}</td>
        <td>${c.contactPhone}</td>
        <td>
            ${isPassed
            ? `<span class="passed-badge">합격</span>`
            : `<input type="number" class="score-input" min="0" max="100" placeholder="점수">`}
        </td>
        <td>
            ${isPassed
            ? `<button type="button" class="cancel-btn">합격취소</button>`
            : `<button type="button" class="pass-btn">합격처리</button>`}
        </td>
    </tr>`;
    }).join("");
}
// 4. 필터 select 변경 → 즉시 다시 렌더링
document.getElementById("passCenter").addEventListener("change", renderCandidates);
document.getElementById("passTime").addEventListener("change", renderCandidates);
document.getElementById('passDate').addEventListener("change", renderCandidates);

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

        alert(data.message);              // "합격 처리됐습니다." / "불합격 (기준 점수 미달입니다)"
        if (data.passed) {
            loadCandidates();
        } else {
            // 불합격 → DB엔 안 남음. 화면에서만 이번 회차 결과 표시.
            row.classList.add("failed-row");      // 불합격 표시용 클래스
            e.target.textContent = "불합격";

            // 점수칸을 다시 건드리면 불합격 표시 해제 → 재입력 후 합격처리 가능
            input.addEventListener("input", () => {
                row.classList.remove("failed-row");
                e.target.textContent = "합격처리";
            }, { once: true });
        }
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
// 7. 합격취소 버튼
document.getElementById("candidateBody").addEventListener("click", async (e) => {
    if (!e.target.classList.contains("cancel-btn")) return;

    const row        = e.target.closest("tr");
    const examPassId = row.dataset.examPassId;

    if (!confirm("이 합격을 취소할까요?")) return;

    try {
        const res = await fetch(`/drive-u/admin/examPass/${examPassId}`, {
            method: "DELETE"
        });
        const data = await res.json();

        alert(data.message);          // "합격이 취소됐습니다." / 예외 메시지

        if (res.ok) {
            loadCandidates();         // 명단 다시 로딩 → 취소된 행이 미합격으로 되돌아감
        }
    } catch (err) {
        console.error(err);
        alert("합격취소 중 오류가 발생했습니다.");
    }
});
// 8. '응시자 조회' = 전체 새로고침
document.getElementById("loadCandidatesBtn").addEventListener("click", () => {
    document.getElementById("passCenter").value = "";
    document.getElementById("passTime").value = "";
    document.getElementById("passDate").value = "";
    loadCandidates();
});
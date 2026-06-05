//// 관리자 시험 일정 등록 화면 전용 JS
//// 1) 지역 → 시험장 cascading (wApply2.js 패턴 동일)
//// 2) 시험구분(학과) 선택 시 면허종별 비활성 (Service가 학과면 licenseType=null로 버림)

const examTypeSel = document.getElementById('examTypeSelect');
const licenseTypeSel = document.getElementById('licenseTypeSelect');
const regionSel = document.getElementById('regionSelect');
const centerSel = document.getElementById('centerSelect');
const timeChipBox = document.getElementById('timeChips');
const hiddenBox = document.getElementById('timeHiddenBox');
const examDateInput = document.getElementById('examDateInput');
const guide = document.getElementById('licenseGuide');

//// 지역 선택 → 해당 region 시험장만 노출 (전체 centers 중 필터)
function filterCenters() {
    const selectedRegion = regionSel.value;
    for (const option of centerSel.options) {
        option.hidden = option.dataset.region !== selectedRegion;
    }
    centerSel.value = ''; //지역바꾸면 시험장은 다시 reset
}

//// 시험구분에 따라 면허종별 select 처리
//// - WRITTEN(학과): 비활성 (관리자는 종별 무관, 문제 동일 / 신청자가 wApply에서 본인 종별 선택)
//// - FUNCTION/DRIVE: 해당 시험 종별 option만 노출
function filterLicenseTypes() {
    const examType = examTypeSel.value;

    if (examType === 'WRITTEN') {
        // 종별 입력 막음. 빈 값으로 보내야 Service의 resolveLicenseType()이 null 처리
        licenseTypeSel.disabled = true;
        licenseTypeSel.value = '';
        guide.textContent = '학과시험은 선택 불필요';
        return;
    }

    guide.textContent = '면허종별 선택';
    licenseTypeSel.disabled = false;

    for (const option of licenseTypeSel.options) {
        option.hidden = option.dataset.exam !== examType;
    }
    licenseTypeSel.value = '';  // 시험구분 바꾸면 종별도 reset
}

examTypeSel.addEventListener('change', filterLicenseTypes);
regionSel.addEventListener('change', filterCenters);

//// 초기 1회 적용 (정의 끝난 뒤 호출)
filterCenters();
filterLicenseTypes();


//// 칩 클릭 → 선택/해제 토글
timeChipBox.addEventListener('click', (e) => {
    const chip = e.target.closest('.time-chip');
    if (!chip) return;              // 칩 아닌 곳 클릭은 무시
    if (chip.disabled) return;      // 이미 등록된(비활성) 칩은 무시

    chip.classList.toggle('selected');
    rebuildHiddenInputs();
});

//// 현재 selected 칩들 기준으로 hidden input 다시 그림
function rebuildHiddenInputs() {
    hiddenBox.innerHTML = '';       // 매번 싹 비우고
    const selected = timeChipBox.querySelectorAll('.time-chip.selected');
    selected.forEach(chip => {
        const input = document.createElement('input');
        input.type = 'hidden';
        input.name = 'examTimes';            // ★ DTO의 List examTimes와 매칭
        input.value = chip.dataset.time;     // "09:00"
        hiddenBox.appendChild(input);
    });
}

//// 4개 입력(시험구분/종별/시험장/날짜) 기준으로 이미 등록된 시간 조회
async function refreshDisabledChips() {
    const examType = examTypeSel.value;
    const testCenterId = centerSel.value;
    const examDate = examDateInput.value;

    // 날짜·시험장 안 정해졌으면 조회 의미 없음 → 전부 활성 상태로 초기화
    if (!examDate || !testCenterId) {
        resetChips();
        return;
    }

    // 쿼리스트링 조립
    const params = new URLSearchParams({
        examType: examType,
        testCenterId: testCenterId,
        examDate: examDate
    });
    // 학과(WRITTEN)는 종별 안 보냄
    if (examType !== 'WRITTEN') {
        params.append('licenseType', licenseTypeSel.value);
    }

    try {
        const res = await fetch(`/drive-u/admin/examSchedule/registeredTimes?${params}`);
        if (!res.ok) return;
        const taken = await res.json();   // ["09:00:00","10:00:00"] 형태

        applyDisabled(taken);
    } catch (err) {
        console.error('등록 시간 조회 실패', err);
    }
}

//// 조회된 시간 목록과 칩 매칭 → 등록된 칩만 disabled
function applyDisabled(takenTimes) {
    // "09:00:00" 형태로 올 수 있으니 앞 5자리(HH:mm)만 비교
    const takenSet = new Set(takenTimes.map(t => t.substring(0, 5)));

    timeChipBox.querySelectorAll('.time-chip').forEach(chip => {
        const isTaken = takenSet.has(chip.dataset.time);
        chip.disabled = isTaken;
        chip.classList.toggle('taken', isTaken);
        if (isTaken) {
            chip.classList.remove('selected');   // 막힌 칩은 선택 해제
        }
    });
    rebuildHiddenInputs();   // 선택 해제된 게 있을 수 있으니 hidden 다시 그림
}

//// 전부 활성 + 선택/비활성 초기화
function resetChips() {
    timeChipBox.querySelectorAll('.time-chip').forEach(chip => {
        chip.disabled = false;
        chip.classList.remove('taken', 'selected');
    });
    rebuildHiddenInputs();
}

//// 4개 입력 중 뭐든 바뀌면 다시 조회
[examTypeSel, licenseTypeSel, centerSel, examDateInput].forEach(el => {
    el.addEventListener('change', refreshDisabledChips);
});


//// ===== 저장 전 1차 검증 (빈 칸 콕 집어 알림) =====
const scheduleForm = document.getElementById('scheduleForm');

scheduleForm.addEventListener('submit', (e) => {
    const examType = examTypeSel.value;

    if (!examType) {
        e.preventDefault();
        Swal.fire({ icon: 'warning', title: '시험구분을 선택해주세요' });
        return;
    }
    if (!centerSel.value) {
        e.preventDefault();
        Swal.fire({ icon: 'warning', title: '시험장을 선택해주세요' });
        return;
    }
    if (!examDateInput.value) {
        e.preventDefault();
        Swal.fire({ icon: 'warning', title: '시험일을 지정해주세요' });
        return;
    }
    if (hiddenBox.querySelectorAll('input[name="examTimes"]').length === 0) {
        e.preventDefault();
        Swal.fire({ icon: 'warning', title: '시험시간을 1개 이상 선택해주세요' });
        return;
    }
    // 종별은 기능/도로일 때만, 맨 마지막에
    if (examType !== 'WRITTEN' && !licenseTypeSel.value) {
        e.preventDefault();
        Swal.fire({ icon: 'warning', title: '면허종별을 선택해주세요' });
        return;
    }
});

let currentYear, currentMonth, currentCenterId;

const modalCenterSel = document.getElementById("modalCenterSelect");

// 모달 열기 (시험장 선택 없이 바로 열림)
document.getElementById("openCalendarBtn").addEventListener("click", () => {
    const now = new Date();
    currentYear = now.getFullYear();
    currentMonth = now.getMonth() + 1;   // getMonth는 0~11이라 +1

    modalCenterSel.value = "";           // 열 때마다 시험장 초기화
    currentCenterId = "";

    showView("viewMonthly");
    document.getElementById("calendarModal").style.display = "flex";
    loadMonthly();                       // 시험장 없이 한 번 그림 → 빈 달력
});

// 모달 안 시험장 바꾸면 그 시험장 달력으로 다시 그림
modalCenterSel.addEventListener("change", () => {
    currentCenterId = modalCenterSel.value;
    loadMonthly();
});

// 모달 안 화면 전환
function showView(id) {
    ["viewMonthly", "viewWeekly"].forEach(v => {
        document.getElementById(v).style.display = (v === id) ? "block" : "none";
    });
    modalCenterSel.disabled = (id === "viewWeekly");
}

function closeCalendarModal() {
    document.getElementById("calendarModal").style.display = "none";
    closeSlotModal();
}

// 월 이동
document.getElementById("prevMonth").addEventListener("click", () => {
    currentMonth--;
    if (currentMonth < 1) { currentMonth = 12; currentYear--; }
    loadMonthly();
});
document.getElementById("nextMonth").addEventListener("click", () => {
    currentMonth++;
    if (currentMonth > 12) { currentMonth = 1; currentYear++; }
    loadMonthly();
});

// 월간 데이터 불러와서 달력 그리기
async function loadMonthly() {
    document.getElementById("monthLabel").textContent = `${currentYear}년 ${currentMonth}월`;

    // 시험장 안 골랐으면 fetch 안 하고 빈 달력만
    if (!currentCenterId) {
        document.getElementById("monthlyGrid").innerHTML = buildMonthlyGrid({});
        return;
    }

    const url = `/drive-u/admin/examSchedule/monthly?testCenterId=${currentCenterId}&year=${currentYear}&month=${currentMonth}`;
    const res = await fetch(url);
    const counts = await res.json();   // { "2026-05-12": 3, ... }

    document.getElementById("monthlyGrid").innerHTML = buildMonthlyGrid(counts);
}

// 월간 달력 HTML 만들기
function buildMonthlyGrid(counts) {
    const firstDay = new Date(currentYear, currentMonth - 1, 1);
    const leadingBlanks = (firstDay.getDay() + 6) % 7;  // 월요일 시작 보정
    const lengthOfMonth = new Date(currentYear, currentMonth, 0).getDate();

    let html = '<div class="cal-grid">';
    ["월","화","수","목","금","토","일"].forEach(d => html += `<div class="cal-dow">${d}</div>`);
    html += '</div><div class="cal-grid">';

    for (let i = 0; i < leadingBlanks; i++) html += '<div class="cal-cell blank"></div>';

    for (let day = 1; day <= lengthOfMonth; day++) {
        const date = `${currentYear}-${String(currentMonth).padStart(2,'0')}-${String(day).padStart(2,'0')}`;
        const cnt = counts[date] || 0;
        const dow = new Date(currentYear, currentMonth - 1, day).getDay(); // 0=일
        const sunday = (dow === 0) ? ' sunday' : '';
        const monday = mondayOf(currentYear, currentMonth, day);

        html += `<div class="cal-cell day-cell${sunday}" data-monday="${monday}">
                    <div class="cal-date">${day}</div>
                    <div class="cal-count ${cnt > 0 ? 'has' : 'empty'}">${cnt > 0 ? '일정 ' + cnt + '건' : '없음'}</div>
                 </div>`;
    }
    html += '</div>';
    return html;
}

// 그 날이 속한 주의 월요일 (yyyy-MM-dd)
function mondayOf(y, m, day) {
    const d = new Date(y, m - 1, day);
    const diff = (d.getDay() + 6) % 7;       // 월요일까지 며칠 뒤로
    d.setDate(d.getDate() - diff);
    const mm = String(d.getMonth() + 1).padStart(2, '0');
    const dd = String(d.getDate()).padStart(2, '0');
    return `${d.getFullYear()}-${mm}-${dd}`;
}

// ===== 2단계: 주간 =====
let currentWeekStart;

const EXAM_TIMES = ["09:00", "10:00", "11:00", "14:00", "15:00", "16:00"]; // ExamConstants.EXAM_TIMES와 맞춰야 함
const WEEK_DOW = ["월", "화", "수", "목", "금", "토"];
const EXAM_TYPE_LABEL = { WRITTEN: "학과", FUNCTION: "기능", DRIVE: "도로주행" };
const LICENSE_BY_EXAM = { FUNCTION: [], DRIVE: [] };
licenseTypeSel.querySelectorAll('option[data-exam]').forEach(opt => {
    const exam = opt.dataset.exam;
    if (LICENSE_BY_EXAM[exam]) LICENSE_BY_EXAM[exam].push(opt.value);
});

// 날짜 칸 클릭 -> 그 주 열기 (이벤트 위임: monthlyGrid에 한 번만)
document.getElementById("monthlyGrid").addEventListener("click", (e) => {
    const cell = e.target.closest(".day-cell:not(.sunday)");
    if (!cell) return;
    openWeek(cell.dataset.monday);
});

// 월간으로 돌아가기
document.getElementById("backToMonthly").addEventListener("click", () => {
    showView("viewMonthly");
});

// 주간 데이터 불러와서 그리기
async function openWeek(weekStart) {
    currentWeekStart = weekStart;

    const url = `/drive-u/admin/examSchedule/weekly?testCenterId=${currentCenterId}&weekStart=${weekStart}`;
    const res = await fetch(url);
    const schedules = await res.json();

    document.getElementById("weeklyGrid").innerHTML = buildWeeklyGrid(schedules, weekStart);
    showView("viewWeekly");
}

// 주간 시간표 HTML 만들기
function buildWeeklyGrid(schedules, weekStart) {
    // (시간|요일인덱스) -> 일정들
    const map = {};
    schedules.forEach(s => {
        const time = s.examTime.substring(0, 5);     // "09:00:00" -> "09:00"
        const dow = dayIndex(s.examDate, weekStart);  // 0=월 ... 5=토
        const key = `${time}|${dow}`;
        (map[key] = map[key] || []).push(s);
    });

    let html = '<table class="week-table"><thead><tr><th>시간</th>';
    WEEK_DOW.forEach((d, i) => {
        const date = addDays(weekStart, i);     // 월요일 + i일
        html += `<th>${date}<br>(${d})</th>`;
    });
    html += '</tr></thead><tbody>';

    EXAM_TIMES.forEach(time => {
        html += `<tr><td class="time-col">${time}</td>`;
        for (let dow = 0; dow < 6; dow++) {
            const list = map[`${time}|${dow}`] || [];
            const date = addDaysFull(weekStart, dow);   // "2026-06-01" (조회용 풀 날짜)
            html += `<td class="slot" data-date="${date}" data-time="${time}">${renderSlot(list)}</td>`;
        }
        html += '</tr>';
    });

    html += '</tbody></table>';
    return html;
}

// 한 슬롯 내용
function renderSlot(list) {
    if (list.length === 0) return '<span class="empty-slot">+</span>';
    return list.map(s => {
        const label = EXAM_TYPE_LABEL[s.examType];
        const lt = s.licenseType ? ` ${s.licenseType}` : "";
        return `<div class="exam-block type-${s.examType}">${label}${lt} (${s.maxCount})</div>`;
    }).join("");
}

// 날짜 문자열 -> 요일 인덱스 (월=0 ... 토=5)
function dayIndex(examDate, weekStart) {
    const d1 = new Date(examDate);
    const d2 = new Date(weekStart);
    return Math.round((d1 - d2) / 86400000);
}

// ===== 3단계: 슬롯 클릭 → 우측 패널 =====
let currentSlot = null;   // 지금 보고 있는 슬롯 {date, time} 기억 (등록/삭제 후 재조회용)

// 주간 표의 슬롯 클릭 (이벤트 위임: weeklyGrid에 한 번만)
document.getElementById("weeklyGrid").addEventListener("click", (e) => {
    const slot = e.target.closest(".slot");
    if (!slot) return;
    currentSlot = { date: slot.dataset.date, time: slot.dataset.time };
    openSlotModal();
    loadSlotPanel();
});
// 슬롯 모달 열기/닫기 (주간 모달 위에 겹쳐 뜸)
function openSlotModal() {
    document.getElementById("slotBox").style.display = "block";
}
function closeSlotModal() {
    document.getElementById("slotBox").style.display = "none";
    currentSlot = null;
}

// 현재 슬롯 일정 조회해서 우측 패널 그리기
async function loadSlotPanel() {
    if (!currentSlot) return;

    const params = new URLSearchParams({
        testCenterId: currentCenterId,
        examDate: currentSlot.date,
        examTime: currentSlot.time
    });

    const res = await fetch(`/drive-u/admin/examSchedule/slot?${params}`);
    const list = await res.json();   // ExamScheduleViewDTO 배열

    document.getElementById("slotPanel").innerHTML = buildSlotPanel(list);
}

// 패널 HTML
function buildSlotPanel(list) {
    const head = `<div class="slot-head">${currentSlot.date} ${currentSlot.time}</div>`;

    let listHtml = '<div class="slot-list">';
    if (list.length === 0) {
        listHtml += '<p class="slot-empty-guide">등록된 일정이 없습니다.</p>';
    } else {
        list.forEach(s => {
            const label = EXAM_TYPE_LABEL[s.examType];
            const lt = s.licenseType ? ` ${s.licenseType}` : "";
            listHtml += `
                <div class="slot-item type-${s.examType}">
                    <span>${label}${lt} (정원 ${s.maxCount})</span>
                    <button type="button" class="slot-del-btn" data-id="${s.scheduleId}">삭제</button>
                </div>`;
        });
    }
    listHtml += '</div>';

    return head + listHtml + buildSlotForm();
}

// 패널 하단 새 등록 폼 (시험구분/종별/정원)
function buildSlotForm() {
    // 시험구분 옵션
    const typeOpts = Object.entries(EXAM_TYPE_LABEL)
        .map(([v, label]) => `<option value="${v}">${label}</option>`).join("");

    return `
        <div class="slot-form">
            <strong>새 일정 등록</strong>
            <select id="slotExamType">
                <option value="">시험구분</option>
                ${typeOpts}
            </select>
            <select id="slotLicenseType" disabled>
                <option value="">면허종별</option>
            </select>
            <input type="number" id="slotMaxCount" min="1" value="30" placeholder="정원">
            <button type="button" id="slotAddBtn">등록</button>
        </div>`;
}

// weekStart("2026-06-01")에 days일 더해서 "06-01" 형태로
function addDays(weekStart, days) {
    const d = new Date(weekStart);
    d.setDate(d.getDate() + days);
    const mm = String(d.getMonth() + 1).padStart(2, '0');
    const dd = String(d.getDate()).padStart(2, '0');
    return `${mm}-${dd}`;
}
// weekStart에 days일 더해서 "2026-06-01" 형태 (조회용 - 연도 포함)
function addDaysFull(weekStart, days) {
    const d = new Date(weekStart);
    d.setDate(d.getDate() + days);
    const mm = String(d.getMonth() + 1).padStart(2, '0');
    const dd = String(d.getDate()).padStart(2, '0');
    return `${d.getFullYear()}-${mm}-${dd}`;
}

const slotPanel = document.getElementById("slotPanel");

// 패널 안 클릭 위임 (등록 버튼 / 삭제 버튼)
slotPanel.addEventListener("click", (e) => {
    if (e.target.id === "slotAddBtn") addSlotSchedule();

    const delBtn = e.target.closest(".slot-del-btn");
    if (delBtn) deleteSlotSchedule(delBtn.dataset.id);
});

// 패널 안 시험구분 바뀌면 종별 select 채우기
slotPanel.addEventListener("change", (e) => {
    if (e.target.id !== "slotExamType") return;

    const examType = e.target.value;
    const licenseSel = document.getElementById("slotLicenseType");

    if (examType === "WRITTEN" || examType === "") {
        licenseSel.disabled = true;
        licenseSel.innerHTML = '<option value="">선택 불필요</option>';
        return;
    }
    licenseSel.disabled = false;
    const opts = (LICENSE_BY_EXAM[examType] || [])
        .map(v => `<option value="${v}">${v}</option>`).join("");
    licenseSel.innerHTML = '<option value="">면허종별</option>' + opts;
});

// 등록
async function addSlotSchedule() {
    const examType = document.getElementById("slotExamType").value;
    const licenseType = document.getElementById("slotLicenseType").value;
    const maxCount = document.getElementById("slotMaxCount").value;

    if (!examType) { Swal.fire({ icon: 'warning', title: '시험구분을 선택하세요' }); return; }
    if (examType !== "WRITTEN" && !licenseType) {
        Swal.fire({ icon: 'warning', title: '면허종별을 선택하세요' }); return;
    }

    const body = {
        examType: examType,
        licenseType: examType === "WRITTEN" ? null : licenseType,
        testCenterId: currentCenterId,
        examDate: currentSlot.date,
        examTimes: [currentSlot.time],   // ★ 단건이라도 List로 (서버 register 재활용)
        maxCount: Number(maxCount)
    };

    const res = await fetch('/drive-u/admin/examSchedule/slot', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
    });

    if (!res.ok) {
        Swal.fire({ icon: 'error', title: '등록 실패' });
        return;
    }

    const result = await res.json();

    if (result.count === 0) {
        // 한 건 요청했는데 0건 등록 = 이미 있는 일정
        Swal.fire({ icon: 'info', title: '이미 등록된 일정입니다' });
        return;   // 중복이니 새로고침 불필요
    }

    // 정상 등록됨
    await refreshAfterChange();
}

// 삭제
async function deleteSlotSchedule(scheduleId) {
    const ok = await Swal.fire({
        icon: 'warning', title: '삭제할까요?', showCancelButton: true,
        confirmButtonText: '삭제', cancelButtonText: '취소'
    });
    if (!ok.isConfirmed) return;

    const res = await fetch(`/drive-u/admin/examSchedule/slot/${scheduleId}`, { method: 'DELETE' });

    if (res.ok) {
        await refreshAfterChange();
    } else {
        Swal.fire({ icon: 'error', title: '삭제 실패' });
    }
}

// 등록/삭제 후: 패널 + 주간 표 둘 다 다시 그림
async function refreshAfterChange() {
    await openWeek(currentWeekStart);      // 주간 표 갱신 (블록 수/정원 반영)
    await loadSlotPanel();                 // 우측 패널 갱신
}
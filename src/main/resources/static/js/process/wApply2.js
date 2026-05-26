//// 지역선택시 시험장 연동.
const regionSel = document.getElementById('regionSelect');
const centerSel = document.getElementById('centerSelect');

//// 사용자 선택 상태 (전역)
let selectedScheduleId = null;

function filterCenters() {
    const selectedRegion = regionSel.value;
    for (const option of centerSel.options) {
        option.hidden = option.dataset.region !== selectedRegion;
    }
    const firstVisible = Array.from(centerSel.options).find(opt => !opt.hidden);
    if (firstVisible) {
        centerSel.value = firstVisible.value;
    }
    if (typeof resetSelection === 'function') resetSelection();
}


//// 달력 내용채우기.(날짜data 불러오기)
const prevBtn = document.getElementById('prevMonthBtn');
const nextBtn = document.getElementById('nextMonthBtn');
const calendarTitle = document.getElementById('calendarTitle');
const calendarBody = document.getElementById('calendarBody');

let currentDate = new Date();   // 처음엔 오늘

function renderCalendar() {
    const year = currentDate.getFullYear();
    const month = currentDate.getMonth();   // 0~11

    // 제목 갱신 (month + 1로 표시)
    calendarTitle.textContent = `${year}년 ${month + 1}월`;

    // 1일이 무슨 요일? (0=일, 6=토)
    const firstDay = new Date(year, month, 1).getDay();

    // 이 달 마지막 날 (다음 달 0일 트릭)
    const lastDate = new Date(year, month + 1, 0).getDate();

    // 오늘 (시/분/초 0으로 맞춤)
    const today = new Date();
    today.setHours(0, 0,0, 0);

    // 셀 배열 만들기 — 앞쪽 빈칸 + 1~말일
    const cells = [];
    for (let i = 0; i < firstDay; i++) {
        cells.push(null);
    }
    for (let d = 1; d <= lastDate; d++) {
        cells.push(d);
    }

    // 7개씩 끊어서 <tr> 만들기
    let html = '';
    for (let i = 0; i < cells.length; i += 7) {
        const week = cells.slice(i, i + 7);
        // 마지막 주가 7칸 안 되면 빈칸으로 채움
        while (week.length < 7) week.push(null);

        html += '<tr>';
        for (const day of week) {
            if(day === null) {
                html += `<td></td>`;
            }else{
                const cellDate = new Date(year, month, day);
                const isPast = cellDate < today;
                const cls = isPast ? ' class="past-day"' : '';
                html += `<td${cls}>${day}</td>`;
            }
        }
        html += '</tr>';
    }
    calendarBody.innerHTML = html;
}

// 이전/다음 버튼
prevBtn.addEventListener('click', () => {
    currentDate.setMonth(currentDate.getMonth() - 1);
    renderCalendar();
});

nextBtn.addEventListener('click', () => {
    currentDate.setMonth(currentDate.getMonth() + 1);
    renderCalendar();
});

// 초기 렌더
renderCalendar();


//// 날짜 클릭 처리
let selectedDate = null;

calendarBody.addEventListener('click', (e) => {
    if (e.target.tagName !== 'TD') return;
    if (e.target.classList.contains('past-day')) return;
    const dayText = e.target.textContent.trim();
    if (!dayText) return;

    const prev = calendarBody.querySelector('td.selected-day');
    if (prev) prev.classList.remove('selected-day');

    e.target.classList.add('selected-day');

    const year = currentDate.getFullYear();
    const month = String(currentDate.getMonth() + 1).padStart(2, '0');
    const day = String(dayText).padStart(2, '0');
    selectedDate = `${year}-${month}-${day}`;

    loadSchedules();
});



//// AJAX 호출 (날짜·시험장 기준 일정 조회)
const EXAM_TYPE = "WRITTEN";

async function loadSchedules() {
    if (!selectedDate) return;
    const testCenterId = centerSel.value;
    if (!testCenterId) return;

    const url = `/drive-u/process/wApply2/schedules`
        + `?examType=${EXAM_TYPE}`
        + `&testCenterId=${testCenterId}`
        + `&examDate=${selectedDate}`;

    const res = await fetch(url);
    const schedules = await res.json();

    renderTimeButtons(schedules);
}

function renderTimeButtons(schedules) {
    const buttons = document.querySelectorAll('.time-item');

    // 오늘 + 현재시각 + 1시간
    const now = new Date();
    const cutoff = new Date(now.getTime() + 60 * 60 * 1000);
    const isToday = selectedDate === toDateString(now);

    buttons.forEach(btn => {
        const btnTime = btn.dataset.time;

        // 초기화
        btn.classList.remove('selected', 'full', 'no-schedule', 'too-soon');
        btn.disabled = false;
        const oldInfo = btn.querySelector('.time-info');
        if (oldInfo) oldInfo.remove();

        // 오늘 + 1시간 안쪽 시간이면 차단
        if (isToday){
            const [hh, mm] = btnTime.split(':').map(Number);
            const btnDate = new Date();
            btnDate.setHours(hh, mm, 0, 0);
            if(btnDate < cutoff){
                btn.disabled = true;
                btn.classList.add('too-soon');
                appendInfo(btn, '신청마감');
                return;
            }
        }

        const matched = schedules.find(s => s.examTime.substring(0, 5) === btnTime);

        // 일정 없음 → 비활성 + 회색 (텍스트 없이)
        if (!matched) {
            btn.disabled = true;
            btn.classList.add('no-schedule');
            appendInfo(btn, '미운영');
            return;
        }

        const remaining = matched.maxCount - matched.currentCount;

        // 마감 → 비활성 + 빨간색
        if (remaining <= 0) {
            btn.disabled = true;
            btn.classList.add('full');
            appendInfo(btn, '정원마감');
            return;
        }

        // 활성 → 잔여석 표시
        btn.dataset.scheduleId = matched.scheduleId;
        appendInfo(btn, `잔여 ${remaining}`);
    });
}
//
function toDateString(d){
    const y = d.getFullYear();
    const m = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${y}-${m}-${day}`;
}

//// select변경시 시간 초기화
function appendInfo(btn, text) {
    const span = document.createElement('span');
    span.className = 'time-info';
    span.textContent = ` (${text})`;
    btn.appendChild(span);
}

function resetSelection() {
    // 날짜 선택 해제
    selectedDate = null;
    selectedScheduleId = null;

    const prevDay = calendarBody.querySelector('td.selected-day');
    if (prevDay) prevDay.classList.remove('selected-day');

    // 시간버튼 전부 초기화
    document.querySelectorAll('.time-item').forEach(btn => {
        btn.classList.remove('selected', 'full', 'no-schedule');
        btn.disabled = false;
        const oldInfo = btn.querySelector('.time-info');
        if (oldInfo) oldInfo.remove();
    });
}

//// 시간버튼 클릭 처리
document.querySelectorAll('.time-item').forEach(btn => {
    btn.addEventListener('click', () => {
        if (btn.disabled) return;
        document.querySelectorAll('.time-item.selected')
            .forEach(b => b.classList.remove('selected'));
        btn.classList.add('selected');
        selectedScheduleId = btn.dataset.scheduleId;
    });
});

regionSel.addEventListener('change', filterCenters);
// 시험장 변경 시 초기화
centerSel.addEventListener('change', resetSelection);
// 초기 필터 적용 (모든 변수·함수 정의된 뒤 호출)
filterCenters();


//// 시간버튼 클릭 처리
document.querySelectorAll('.time-item').forEach(btn => {
    btn.addEventListener('click', () => {
        if (btn.disabled) return;
        document.querySelectorAll('.time-item.selected')
            .forEach(b => b.classList.remove('selected'));
        btn.classList.add('selected');
        selectedScheduleId = btn.dataset.scheduleId;
        document.getElementById('examScheduleIdInput').value = selectedScheduleId;  // ← 추가
    });
});

//// 폼 제출 시 일정 선택 여부 검증
document.getElementById('applyForm').addEventListener('submit', (e) => {
    if (!selectedScheduleId) {
        e.preventDefault();
        alert('시험 일정을 선택해주세요.');
        return false;
    }
});
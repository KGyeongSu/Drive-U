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
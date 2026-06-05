``// 1. 지도 클릭 시 발동하는 함수 (SVG 내 onclick에서 호출됨)
async function selectRegion(regionName) {

    const mapSvg = document.querySelector('.korea-map');
    const allRegions = document.querySelectorAll('.region, .region-group');
    const svgWrapper = document.querySelector('.svg-wrapper');
    let target = document.getElementById(regionName) || document.getElementById(regionName.toLowerCase());

    if (target) {

        // 블러 처리 & 3D 팝업강조 액티브
        mapSvg.classList.remove('is-selected');
        allRegions.forEach(el => {

            el.classList.remove('active-region')
            el.style.transform = 'translate(0px, 0px)';

        });

        mapSvg.classList.add('is-selected');
        target.classList.add('active-region');

        document.getElementById('academy-list').style.display = 'none';

        let cityGroup = '';
        switch (regionName) {

            case 'seoul':
                cityGroup = "수도권";
                break;
            case 'gangwon':
                cityGroup ="강원";
                break;
            case 'chungbuk':
                cityGroup = "충북";
                break;
            case 'daejeonChung':
                cityGroup = "대전•충남";
                break;
            case 'jeonbuk':
                cityGroup = "전북";
                break;
            case 'jeonnam':
                cityGroup = "광주•전남";
                break;
            case 'jeju':
                cityGroup = "제주";
                break;
            case 'gyeongnam':
                cityGroup = "부산•울산•경남";
                break;
            case 'gyeongbuk':
                cityGroup = "대구•경북";
                break;

        }

        try {

            // 클릭된 행정구역 상대 중심점
            const rect = target.getBoundingClientRect();

            const parentRect = svgWrapper.getBoundingClientRect(); // 지도 감싸는 박스 기준
            const originalX = (rect.left + rect.width / 2) - parentRect.left;
            const originalY = (rect.top + rect.height / 2) - parentRect.top;

            // 클릭 시 해당 구역 화면 중심 위치
            const finalCenterX = parentRect.width / 2;
            const finalCenterY = parentRect.height / 2;

            // 이동 거리 계산
            const moveX = finalCenterX - originalX;
            const moveY = finalCenterY - originalY;

            target.parentNode.appendChild(target);

            target.style.transition = 'transform 0.5s ease-in-out';
            target.style.transform = `translate(${moveX}px, ${moveY}px)`;

            // 행정구역내 시험장 가져오기
            setTimeout(async () => {
                // 이동 완료된 구역의 현재 화면상 절대 좌표를 구합니다.
                const rect = target.getBoundingClientRect();
                let centerX = rect.left + rect.width / 2;
                let centerY = rect.top + rect.height / 2;

                if (cityGroup === "대구•경북") {

                    centerX -= 40;
                    centerY += 40;

                } else if (cityGroup === "충북") {

                    centerX -= 5;
                    centerY -= 20;

                } else if (cityGroup === "광주•전남") {

                    centerX += 50;
                    centerY -= 10;

                } else if (cityGroup === "수도권") {

                    centerX += 60;
                    centerY -= 10;

                }

                const response = await fetch(`/api/predict/centerMap?cityGroup=${encodeURIComponent(cityGroup)}`);
                const centers = await response.json();

                // 이제 정확한 중심 좌표(centerX, centerY)를 넘겨줍니다.
                renderDynamicMindmap(centers, centerX, centerY);
            });


        } catch (error) {

            console.error("시험장 목록 가져오기 실패", error);

        }

    }
}

function renderDynamicMindmap(centers, passedX, passedY) {
    const mindmap = document.getElementById('mindmap-container');
    const linesContainer = document.getElementById('mindmap-lines-container');
    const nodesContainer = document.getElementById('mindmap-nodes-container');

    if (!mindmap || !linesContainer || !nodesContainer) return;

    mindmap.dataset.clickScrollX = window.scrollX; // 클릭한 순간의 가로 스크롤 저장
    mindmap.dataset.clickScrollY = window.scrollY; // 클릭한 순간의 세로 스크롤 저장
    mindmap.style.transform = 'none';

    linesContainer.innerHTML = '';
    nodesContainer.innerHTML = '';
    mindmap.classList.remove('is-active');
    mindmap.style.display = 'block';

    const totalNodes = centers.length;
    const radius = totalNodes >= 5 ? 140 : 100;

    centers.forEach((center, index) => {
        const angle = (360 / totalNodes) * index;
        const angleRad = (angle * Math.PI) / 180;

        // 원형 배치 좌표
        const targetX = passedX + radius * Math.cos(angleRad);
        const targetY = passedY + radius * Math.sin(angleRad);

        // 1. 선 생성
        const line = document.createElement('div');
        line.className = 'mindmap-line';
        line.style.left = `${passedX}px`;
        line.style.top = `${passedY}px`;
        line.style.transform = `rotate(${angle}deg)`;
        line.style.setProperty('--line-length', `${radius}px`);
        linesContainer.appendChild(line);

        // 2. 노드 생성
        const button = document.createElement('button');
        button.className = 'mindmap-node';
        button.innerText = center.testCenterName;

        // 초기 중심 위치 (선들의 시작점)
        button.style.left = `${passedX}px`;
        button.style.top = `${passedY}px`;

        button.onclick = () => showCenterPredict(center);
        nodesContainer.appendChild(button);

        // 애니메이션 수행
        setTimeout(() => {
            button.style.left = `${targetX}px`;
            button.style.top = `${targetY}px`;
        }, 14);
    });

    setTimeout(() => mindmap.classList.add('is-active'), 14);
}

// 면허 시험 & 면허 발급 대기 예측 함수
async function showCenterPredict (center) {

    const regionTitle = document.getElementById('region-title');
    const regionDesc = document.getElementById('region-desc');
    const infoContainer = document.getElementById('academy-list');

    if (infoContainer) {

        infoContainer.style.display = 'block';

    }

    //상단 헤더 텍스트 변경
    if (regionTitle) regionTitle.innerText = center.testCenterName;
    if (regionDesc) regionDesc.innerText = "선택하신 시험장의 3년 통계 현황입니다.";

    // 현재 시간 체크 및 영업외 상태
    const now = new Date();
    const currentHour = now.getHours();

    let isClosed = false;
    let timeStatusText = "";

    if (currentHour < 9) {

        isClosed = true;
        timeStatusText = "운영전";

    } else if (currentHour >= 18) {

        isClosed = true;
        timeStatusText = "운영종료";

    }

    // 기본 변수 세팅
    let examTimeDisplay = "";
    let issueTimeDisplay = "";

    // 각 카드별
    let examColorKey = "GRAY";
    let issueColorKey = "GRAY";
    let finalColorKey = "GRAY";

    // 주말 마감 여부
    let isWeekend = false;

    // 대기인수
    let examWaitCount = '0';
    let issueWaitCount = '0';

    // 영업시간일 때만 데이터 예측
    if (!isClosed) {

        const month = now.getMonth() + 1;
        const day = now.getDate();
        const hour = currentHour;


        try {

            // 발급 & 시험 데이터 호출
            const response = await fetch(`/api/predict/centerStatus?testCenterName=${encodeURIComponent(center.testCenterName)}&month=${month}&day=${day}&hour=${hour}`);
            const data = await response.json();

            // json 문자열 데이터 > 객체 변환
            const examData = data.exam;
            const issueData = data.issue;

            if (examData.expectedMinutes === -1) {

                // 주말
                isWeekend = true;
                timeStatusText = "운영마감";

                examTimeDisplay = `<span style="color: #ef4444; font-weight: bold;">운영마감 (주말 휴무)</span>`;
                issueTimeDisplay = `<span style="color: #ef4444; font-weight: bold;">운영마감 (주말 휴무)</span>`;

                examColorKey = "GRAY";
                issueColorKey = "GRAY";
                finalColorKey = "GRAY";

            } else {

                // 평일
                // 대기인원
                examWaitCount = examData.expectedWaitCount;
                issueWaitCount = issueData.expectedWaitCount;

                // 대기시간
                examTimeDisplay = `${examData.expectedMinutes} 분`;
                issueTimeDisplay = `${issueData.expectedMinutes} 분`;

                // 혼잡도
                examColorKey = examData.statusColor;
                issueColorKey = issueData.statusColor;

                finalColorKey = (examColorKey === 'RED' || issueColorKey === 'RED') ? 'RED'
                    : (examColorKey === 'YELLOW' || issueColorKey === 'YELLOW') ? 'YELLOW' : 'GREEN';

            }

        } catch (error) {

            console.error("실시간 예측 데이터 로드 실패:", error);
            examTimeDisplay = "오류";
            issueTimeDisplay = "오류";

        }

    } else {

        // 주중 평일 영업시간 외 (9시 전, 18시 후)
        examTimeDisplay = `-`;
        issueTimeDisplay = `-`;
        examColorKey = "GREEN";
        issueColorKey = "GREEN";
        finalColorKey = "GREEN";

    }

    // UI 렌더링용 컬러 및 텍스트 맵
    const colorMap = { 'RED': '#e11d48', 'YELLOW': '#eab308', 'GREEN': '#22c55e', 'GRAY': '#9ca3af' };
    const textMap = { 'RED': '혼잡', 'YELLOW': '보통', 'GREEN': '원활', 'GRAY': '마감' };

    if (infoContainer) {
        infoContainer.innerHTML = `
            <div class="center-info-container">

                <div class="info-status-card" style="${isWeekend ? 'border-left: 5px solid #9ca3af;' : ''}">
                    <div class="card-title-area">
                        <span>🪪</span>
                        <div class="card-title-text">면허 발급 및 대기 통계 정보</div>
                    </div>
                    <div class="card-content-text">
                        • 평균 대기인원: <strong>${isClosed || isWeekend ? '0' : issueWaitCount} 명</strong><br>
                        • 평균 대기시간: <strong style="color: ${isClosed ? '#000' : colorMap[issueColorKey]};">${issueTimeDisplay}</strong><br>
                        ${isClosed && !isWeekend ? `<br><span class="status-badge" style="background-color: #e11d48; color: white;">${timeStatusText}</span>` : ''}
                    </div>
                </div>

                <div class="info-status-card" style="${isWeekend ? 'border-left: 5px solid #9ca3af;' : ''}">
                    <div class="card-title-area">
                        <span>🚗</span>
                        <div class="card-title-text">면허 시험 대기 통계 정보</div>
                    </div>
                    <div class="card-content-text">
                         • 평균 대기인원: <strong>${isClosed || isWeekend ? '0' : examWaitCount} 명</strong><br>
                         • 평균 대기시간: <strong style="color: ${isClosed ? '#000' : colorMap[examColorKey]};">${examTimeDisplay}</strong><br>
                         ${isClosed && !isWeekend ? `<br><span class="status-badge" style="background-color: #e11d48; color: white;">${timeStatusText}</span>` : ''}
                    </div>
                </div>

                <div class="info-status-card" style="${isWeekend ? 'border-left: 5px solid #9ca3af;' : ''}">
                    <div class="card-title-area">
                        <span>📍</span>
                        <div class="card-title-text">통계 기반 혼잡도</div>
                    </div>
                    <div class="card-content-text">
                        • 예상 혼잡도: <span style="font-weight:bold; color: ${colorMap[finalColorKey]};">${isClosed || isWeekend ? timeStatusText : textMap[finalColorKey]}</span>
                    </div>
                </div>

            </div>
        `;
    }

}

// 빈 공간(바다) 클릭 시 해제
document.querySelector('.korea-map').addEventListener('click', function(e) {
    if (e.target === this) {

        this.classList.remove('is-selected');

        document.querySelectorAll('.active-region').forEach(el => {
            el.style.transition = 'transform 0.5s ease-in-out';
            el.style.transform = 'translate(0px, 0px)';
            el.classList.remove('active-region');
        });

        const mindmap = document.getElementById('mindmap-container');
        mindmap.style.display = 'none';
        mindmap.style.transform = 'none';

        document.getElementById('academy-list').style.display = 'none';

        // 초기상태로 복구
        const regionTitle = document.getElementById('region-title');
        const regionDesc = document.getElementById('region-desc');
        const academyList = document.getElementById('academy-list');
        if (regionTitle) regionTitle.innerText = "지역을 선택하세요.";
        if (regionDesc) regionDesc.innerText = "원하는 시험장을 클릭하세요.";
        if (academyList) {
            academyList.style.display = 'block';
            academyList.innerHTML = `
                <div class="empty-msg-wrapper">
                    <div class="empty-msg">해당 시험장 현황이 표시됩니다.</div>
                </div>
            `;
        }

    }
});

// 스크롤 시 마인드맵 고정
window.addEventListener('scroll', function() {
    const mindmap = document.getElementById('mindmap-container');

    if (mindmap && mindmap.style.display === 'block' && mindmap.dataset.clickScrollY !== undefined) {
        // 처음 클릭했을 때보다 스크롤이 얼마나 움직였는지 차이 계산
        const deltaX = window.scrollX - parseFloat(mindmap.dataset.clickScrollX);
        const deltaY = window.scrollY - parseFloat(mindmap.dataset.clickScrollY);

        mindmap.style.transform = `translate(${-deltaX}px, ${-deltaY}px)`;
    }
});

// driveU 소식 탭 메뉴
function switchNewsTab(type, element) {
    const tabs = document.querySelectorAll('.news-tab');
    tabs.forEach(tab => tab.classList.remove('active'));
    element.classList.add('active');

    // 타입별 사용 데이터 선택
    const dataList = (type === 'notice') ? window.noticeList : window.lawList;
    const container = document.getElementById('news-items-container');
    const moreBtn = document.querySelector('.news-more-btn');

    // 초기화
    container.innerHTML = '';

    // 데이터가 있는경우 반복문으로 리스트 생성
    if (dataList && dataList.length > 0) {

        dataList.forEach(item => {

            const itemDiv = document.createElement('div');
            itemDiv.className = 'news-item';
            itemDiv.onclick = function() {
                updateNewsDetail(type, item.id, this);
            };

            itemDiv.innerHTML = `
            
                <span class="news-item-title">${item.title}</span>
                <span class="news-date">${item.date}</span>
            
            `;

            moreBtn.style.display = 'block';
            container.appendChild(itemDiv);

        });

        const firstItem = container.querySelector('.news-item');

        if (firstItem) {
            updateNewsDetail(type, dataList[0].id, firstItem);
        }

        moreBtn.disabled = false;

    } else {

        container.innerHTML = `<div class="news-item">등록된 정보가 없습니다.</div>`;

        document.getElementById('detail-category').innerText = '미리보기';
        document.getElementById('detail-title').innerText = '등록된 내용이 없습니다.';
        document.getElementById('detail-desc').innerText = '';

        moreBtn.disabled = true;

    }

}

// 탭 이동
function updateNewsDetail (type, id, element) {

    const dataList = (type === 'notice') ? window.noticeList : window.lawList;

    const items = document.querySelectorAll('.news-item');
    items.forEach(item => item.classList.remove('active'));

    document.querySelectorAll('.news-item').forEach(el => el.classList.remove('active'));

    if (element) {
        element.classList.add('active');
    }

    // i : 각 글 하나하나, id 같은지 선별해서
    const item = dataList.find(i => i.id === id);
    if (!item) return;

    // 화면 갱신
    document.getElementById('detail-title').innerText = item.title;
    document.getElementById('detail-desc').innerText = item.content;

    // 전체보기 url 변경
    const moreBtn = document.querySelector('.news-more-btn');
    const url = (type === 'notice') ? `/drive-u/userInfo/noticeHome/noticeDetail?id=${id}` : `/drive-u/userInfo/lawsHome/lawsDetail?id=${id}`;
    moreBtn.setAttribute('onclick', `location.href='${url}'`);

}

document.addEventListener("DOMContentLoaded", function() {

    // 처음 로딩 시 공지사항 첫 번째 선택
    const firstNotice = document.querySelector('.news-item');
    if (firstNotice) firstNotice.classList.add('active');

});
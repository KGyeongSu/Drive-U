function scrollTopSmooth() {
    window.scrollTo({top: 0, behavior: "smooth"});
}

const siteHeader = document.querySelector(".site-header");
const navButtons = document.querySelectorAll(".nav-button");
const megaMenu = document.querySelector(".mega-menu");

navButtons.forEach(button => {
    button.addEventListener("click", event => {
        event.stopPropagation();
        siteHeader.classList.toggle("menu-open");
    });
});

if (megaMenu) {
    megaMenu.addEventListener("click", event => event.stopPropagation());
}

document.addEventListener("click", () => {
    if (siteHeader) {
        siteHeader.classList.remove("menu-open");
    }
});

document.addEventListener("keydown", event => {
    if (event.key === "Escape" && siteHeader) {
        siteHeader.classList.remove("menu-open");
    }
});

function goTermsNext(button) {
    const agreeYn = document.getElementById("agreeYn");

    if (!agreeYn || !agreeYn.checked) {
        alert("약관에 동의해야 다음 단계로 이동할 수 있습니다.");
        return;
    }

    location.href = button.dataset.next;
}

function goPayment(button) {
    const checkedMethod = document.querySelector('input[name="paymentMethod"]:checked');

    if (!checkedMethod) {
        alert("지불방법을 선택해 주세요.");
        return;
    }

    alert("결제가 완료되었습니다.");
    location.href = button.dataset.next;
}

document.querySelectorAll(".time-item").forEach(item => {
    item.addEventListener("click", () => {
        document.querySelectorAll(".time-item").forEach(button => button.classList.remove("selected"));
        item.classList.add("selected");
    });
});

const centerData = {
    daejeon: [
        {name: "대전 운전면허시험장", address: "대전광역시 동구 산서로1660번길 90"},
        {name: "대전 교육장", address: "대전광역시 중구 중앙로121번길 20"}
    ],
    seoul: [
        {name: "강남 운전면허시험장", address: "서울특별시 강남구 테헤란로114길 23"},
        {name: "도봉 운전면허시험장", address: "서울특별시 노원구 동일로 1449"}
    ],
    chungbuk: [
        {name: "청주 운전면허시험장", address: "충청북도 청주시 상당구 가덕면 교육원로 131-20"},
        {name: "충주 출장장", address: "충청북도 충주시 예시로 100"}
    ],
    busan: [
        {name: "부산남부 운전면허시험장", address: "부산광역시 남구 용호로 16"},
        {name: "부산북부 운전면허시험장", address: "부산광역시 사상구 사상로367번길 35"}
    ]
};

const regionSelect = document.getElementById("regionSelect");
const centerList = document.getElementById("centerList");
const mapTitle = document.getElementById("mapTitle");
const mapAddress = document.getElementById("mapAddress");

if (regionSelect && centerList && mapTitle && mapAddress) {
    renderCenters(regionSelect.value);
    regionSelect.addEventListener("change", () => renderCenters(regionSelect.value));
}

function renderCenters(region) {
    centerList.innerHTML = "";

    centerData[region].forEach((center, index) => {
        const button = document.createElement("button");
        button.type = "button";
        button.className = "center-button" + (index === 0 ? " active" : "");
        button.innerHTML = `<strong>${center.name}</strong><br><small>${center.address}</small>`;

        button.addEventListener("click", () => {
            document.querySelectorAll(".center-button").forEach(btn => btn.classList.remove("active"));
            button.classList.add("active");
            mapTitle.textContent = center.name;
            mapAddress.textContent = center.address;
        });

        centerList.appendChild(button);

        if (index === 0) {
            mapTitle.textContent = center.name;
            mapAddress.textContent = center.address;
        }
    });
}
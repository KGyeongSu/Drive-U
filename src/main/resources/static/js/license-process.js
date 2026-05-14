// License process checklist interaction
document.querySelectorAll(".step-check").forEach((check) => {
    check.addEventListener("change", () => {
        const item = check.closest(".process-item");

        if (item) {
            item.classList.toggle("completed", check.checked);
        }

        updateLicenseProcessStatus();
    });
});

function updateLicenseProcessStatus() {
    const checkedCount = document.querySelectorAll(".step-check:checked").length;
    const nextStepText = document.getElementById("nextStepText");
    const nextPrepareText = document.getElementById("nextPrepareText");

    const stepData = [
        { title: "응시 전 교통안전교육", prepare: "신분증" },
        { title: "신체검사", prepare: "신분증" },
        { title: "학과시험", prepare: "응시원서, 신분증, 사진" },
        { title: "기능시험", prepare: "응시원서, 신분증" },
        { title: "연습면허 발급", prepare: "응시원서, 신분증" },
        { title: "도로주행시험", prepare: "응시원서, 신분증" },
        { title: "운전면허증 발급", prepare: "응시원서, 신분증" }
    ];

    if (!nextStepText || !nextPrepareText) {
        return;
    }

    if (checkedCount < stepData.length) {
        nextStepText.textContent = stepData[checkedCount].title;
        nextPrepareText.textContent = stepData[checkedCount].prepare;
    } else {
        nextStepText.textContent = "면허취득 완료";
        nextPrepareText.textContent = "모든 절차 완료";
    }
}

document.addEventListener("DOMContentLoaded", function () {
    let questions = [];
    let currentIndex = 0;
    let userAnswers = {};
    let remainSeconds = 40 * 60;
    let timerId = null;

    const questionCounter = document.getElementById("questionCounter");
    const timer = document.getElementById("timer");
    const loadingBox = document.getElementById("loadingBox");
    const questionBox = document.getElementById("questionBox");
    const questionText = document.getElementById("questionText");
    const questionImageBox = document.getElementById("questionImageBox");
    const questionImage = document.getElementById("questionImage");
    const answerList = document.getElementById("answerList");

    const prevBtn = document.getElementById("prevBtn");
    const nextBtn = document.getElementById("nextBtn");
    const submitBtn = document.getElementById("submitBtn");

    const resultBox = document.getElementById("resultBox");
    const resultTitle = document.getElementById("resultTitle");
    const resultScore = document.getElementById("resultScore");
    const resultPassText = document.getElementById("resultPassText");
    const resultTotalCount = document.getElementById("resultTotalCount");
    const resultCorrectCount = document.getElementById("resultCorrectCount");
    const resultWrongCount = document.getElementById("resultWrongCount");
    const resultUnansweredCount = document.getElementById("resultUnansweredCount");
    const questionResultList = document.getElementById("questionResultList");

    const retryBtn = document.getElementById("retryBtn");
    const goApplyBtn = document.getElementById("goApplyBtn");

    loadQuestions();

    function loadQuestions() {
        fetch("/api/cbt/questions/random?count=40")
            .then(response => {
                if (!response.ok) {
                    return response.text().then(message => {
                        throw new Error(message || "문제 API 호출 실패");
                    });
                }
                return response.json();
            })
            .then(data => {
                questions = data;

                if (!questions || questions.length === 0) {
                    loadingBox.textContent = "등록된 CBT 문제가 없습니다. DB에 문제 데이터를 먼저 넣어야 합니다.";
                    return;
                }

                loadingBox.style.display = "none";
                questionBox.style.display = "block";

                renderQuestion();
                startTimer();
            })
            .catch(error => {
                console.error(error);
                loadingBox.textContent = error.message || "문제를 불러오지 못했습니다.";
            });
    }

    function renderQuestion() {
        const question = questions[currentIndex];

        questionCounter.textContent = `문제 ${currentIndex + 1} / ${questions.length}`;

        if (question.questionType === "MULTIPLE") {
            questionText.textContent = question.questionText;
        } else {
            questionText.textContent = question.questionText;
        }

        renderQuestionImage(question);
        renderChoices(question);
        updateButtons();
    }

    function renderQuestionImage(question) {
        if (question.imageUrl) {
            questionImageBox.style.display = "block";
            questionImage.src = question.imageUrl;
        } else {
            questionImageBox.style.display = "none";
            questionImage.src = "";
        }
    }

    function renderChoices(question) {
        answerList.innerHTML = "";

        const savedAnswer = userAnswers[question.questionId] || [];
        const maxSelectCount = question.questionType === "MULTIPLE" ? 2 : 1;

        question.choices.forEach(choice => {
            const label = document.createElement("label");
            label.style.display = "block";

            const input = document.createElement("input");
            input.type = "checkbox";
            input.name = "answer_" + question.questionId;
            input.value = choice.choiceNo;

            if (Array.isArray(savedAnswer) && savedAnswer.includes(choice.choiceNo)) {
                input.checked = true;
            }

            input.addEventListener("change", function () {
                const questionId = question.questionId;
                const selectedNo = Number(this.value);

                if (!Array.isArray(userAnswers[questionId])) {
                    userAnswers[questionId] = [];
                }

                if (this.checked) {
                    if (userAnswers[questionId].length >= maxSelectCount) {
                        this.checked = false;

                        alert(maxSelectCount === 1
                            ? "이 문제는 1개만 선택할 수 있습니다."
                            : "이 문제는 2개까지만 선택할 수 있습니다."
                        );

                        return;
                    }

                    if (!userAnswers[questionId].includes(selectedNo)) {
                        userAnswers[questionId].push(selectedNo);
                    }
                } else {
                    userAnswers[questionId] = userAnswers[questionId]
                        .filter(no => no !== selectedNo);
                }

                userAnswers[questionId].sort((a, b) => a - b);
                console.log("현재 답안:", userAnswers);
            });

            label.appendChild(input);
            label.append(` ${choice.choiceNo}. ${choice.choiceText}`);

            if (choice.choiceImageUrl) {
                const img = document.createElement("img");
                img.src = choice.choiceImageUrl;
                img.alt = "보기 이미지";
                img.style.maxWidth = "100%";
                img.style.display = "block";
                img.style.margin = "10px 0";
                img.style.borderRadius = "12px";
                label.appendChild(img);
            }

            answerList.appendChild(label);
        });
    }

    function updateButtons() {
        prevBtn.disabled = currentIndex === 0;

        if (currentIndex === questions.length - 1) {
            nextBtn.style.display = "none";
            submitBtn.style.display = "inline-block";
        } else {
            nextBtn.style.display = "inline-block";
            submitBtn.style.display = "none";
        }
    }

    prevBtn.addEventListener("click", function () {
        if (currentIndex > 0) {
            currentIndex--;
            renderQuestion();
        }
    });

    nextBtn.addEventListener("click", function () {
        if (currentIndex < questions.length - 1) {
            currentIndex++;
            renderQuestion();
        }
    });

    submitBtn.addEventListener("click", function () {
        const answeredCount = Object.keys(userAnswers)
            .filter(questionId => userAnswers[questionId].length > 0)
            .length;

        const unansweredCount = questions.length - answeredCount;

        const submitAnswers = questions.map(question => {
            return {
                questionId: question.questionId,
                selectedChoiceNos: userAnswers[question.questionId] || []
            };
        });

        console.log("제출 답안:", submitAnswers);

        fetch("/api/cbt/submit", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                answers: submitAnswers
            })
        })
            .then(response => {
                if (!response.ok) {
                    return response.text().then(message => {
                        throw new Error(message || "제출 실패");
                    });
                }
                return response.json();
            })
            .then(result => {
                console.log("채점 결과:", result);
                showResult(result, unansweredCount);
            })
            .catch(error => {
                console.error(error);
                alert(error.message);
            });
    });

    function showResult(result, unansweredCount) {
        if (timerId) {
            clearInterval(timerId);
        }

        const passText = result.passYn === "Y" ? "합격" : "불합격";
        const realWrongCount = Math.max(result.wrongCount - unansweredCount, 0);

        questionBox.style.display = "none";
        loadingBox.style.display = "none";
        resultBox.style.display = "block";

        questionCounter.textContent = "채점 완료";
        timer.textContent = "시험 종료";

        resultTitle.textContent = "CBT 모의고사 결과";
        resultScore.textContent = `${result.score}점`;
        resultPassText.textContent = passText;

        resultTotalCount.textContent = result.totalCount;
        resultCorrectCount.textContent = result.correctCount;
        resultWrongCount.textContent = realWrongCount;
        resultUnansweredCount.textContent = unansweredCount;

        renderQuestionResults(result.questionResults || []);
    }

    function startTimer() {
        timerId = setInterval(function () {
            remainSeconds--;

            const min = Math.floor(remainSeconds / 60);
            const sec = remainSeconds % 60;

            timer.textContent = `남은 시간 ${String(min).padStart(2, "0")}:${String(sec).padStart(2, "0")}`;

            if (remainSeconds <= 0) {
                clearInterval(timerId);
                alert("시험 시간이 종료되었습니다.");
                submitBtn.click();
            }
        }, 1000);
    }

    function renderQuestionResults(questionResults) {
        questionResultList.innerHTML = "";

        questionResults.forEach((item, index) => {
            const selected = item.selectedChoiceNos || [];
            const correct = item.correctChoiceNos || [];

            let statusClass = "wrong";
            let statusText = "오답";

            if (selected.length === 0) {
                statusClass = "unanswered";
                statusText = "미풀이";
            } else if (item.correctYn === "Y") {
                statusClass = "correct";
                statusText = "정답";
            }

            const card = document.createElement("div");
            card.className = `question-result-card ${statusClass}`;

            card.innerHTML = `
            <span class="result-label ${statusClass}">${statusText}</span>
            <h4>문제 ${index + 1}. ${escapeHtml(item.questionText || "")}</h4>
            <p><strong>내 답:</strong> ${selected.length > 0 ? selected.join(", ") : "선택 안 함"}</p>
            <p><strong>정답:</strong> ${correct.join(", ")}</p>
            <p><strong>해설:</strong> ${escapeHtml(item.explanation || "해설이 없습니다.")}</p>
        `;

            questionResultList.appendChild(card);
        });
    }
    function escapeHtml(value) {
        return String(value)
            .replaceAll("&", "&amp;")
            .replaceAll("<", "&lt;")
            .replaceAll(">", "&gt;")
            .replaceAll('"', "&quot;")
            .replaceAll("'", "&#039;");
    }

    if (retryBtn) {
        retryBtn.addEventListener("click", function () {
            location.reload();
        });
    }

    if (goApplyBtn) {
        goApplyBtn.addEventListener("click", function () {
            location.href = "/drive-u/process/wApply1";
        });
    }

});
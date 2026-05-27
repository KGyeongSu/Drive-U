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

    loadQuestions();

    function loadQuestions() {
        fetch("/api/cbt/questions/random?count=40")
            .then(response => {
                if (!response.ok) {
                    throw new Error("문제 API 호출 실패");
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
                loadingBox.textContent = "문제를 불러오지 못했습니다. API 또는 DB 상태를 확인하세요.";
            });
    }

    function renderQuestion() {
        const question = questions[currentIndex];

        questionCounter.textContent = `문제 ${currentIndex + 1} / ${questions.length}`;
        if (question.questionType === "MULTIPLE") {
            questionText.textContent = question.questionText + " (2개 선택)";
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

        // 단일정답은 1개, 복수정답은 2개까지만 선택
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

                        if (maxSelectCount === 1) {
                            alert("이 문제는 1개만 선택할 수 있습니다.");
                        } else {
                            alert("이 문제는 2개까지만 선택할 수 있습니다.");
                        }

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
        nextBtn.style.display = currentIndex === questions.length - 1 ? "none" : "inline-block";
        submitBtn.style.display = currentIndex === questions.length - 1 ? "inline-block" : "none";
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

        if (answeredCount < questions.length) {
            const submitConfirm = confirm(`아직 ${questions.length - answeredCount}문제를 풀지 않았습니다. 그래도 제출할까요?`);
            if (!submitConfirm) {
                return;
            }
        }

        const submitAnswers = questions.map(question => {
            return {
                questionId: question.questionId,
                selectedChoiceNos: userAnswers[question.questionId] || []
            };
        });

        console.log("제출 답안:", submitAnswers);
        alert("제출 데이터가 콘솔에 출력되었습니다.");
    });

    function startTimer() {
        timerId = setInterval(function () {
            remainSeconds--;

            const min = Math.floor(remainSeconds / 60);
            const sec = remainSeconds % 60;

            timer.textContent = `남은 시간 ${String(min).padStart(2, "0")}:${String(sec).padStart(2, "0")}`;

            if (remainSeconds <= 0) {
                clearInterval(timerId);
                alert("시험 시간이 종료되었습니다.");
                console.log("시간 종료 답안:", userAnswers);
            }
        }, 1000);
    }
});

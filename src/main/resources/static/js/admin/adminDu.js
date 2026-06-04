
    document.addEventListener('DOMContentLoaded', function () {
    const quizFormContainer = document.getElementById('quizFormContainer');
    const addQuizBtn = document.getElementById('addQuizBtn');
    const quizTemplate = document.getElementById('quizTemplate');

    if (!quizFormContainer || !addQuizBtn || !quizTemplate) {
    return;
}

    function refreshQuizIndexes() {
    const quizBoxes = quizFormContainer.querySelectorAll('[data-quiz-box]');

    quizBoxes.forEach(function (box, quizIndex) {
    const quizNo = quizIndex + 1;

    const title = box.querySelector('[data-quiz-title]');
    if (title) {
    title.textContent = '퀴즈 ' + quizNo;
}

    const quizOrderInput = box.querySelector('[data-name="quizOrder"]');
    if (quizOrderInput) {
    quizOrderInput.name = 'quizzes[' + quizIndex + '].quizOrder';
    quizOrderInput.value = quizNo;
}

    const questionInput = box.querySelector('[data-name="questionText"]');
    if (questionInput) {
    questionInput.name = 'quizzes[' + quizIndex + '].questionText';
}

    const explanationTextarea = box.querySelector('[data-name="explanation"]');
    if (explanationTextarea) {
    explanationTextarea.name = 'quizzes[' + quizIndex + '].explanation';
}

    const correctSelect = box.querySelector('[data-name="correctChoiceOrder"]');
    if (correctSelect) {
    correctSelect.name = 'quizzes[' + quizIndex + '].correctChoiceOrder';
}

    const choiceInputs = box.querySelectorAll('[data-choice-index]');
    choiceInputs.forEach(function (choiceInput) {
    const choiceIndex = choiceInput.dataset.choiceIndex;
    choiceInput.name = 'quizzes[' + quizIndex + '].choices[' + choiceIndex + ']';
});
});
}

    function addQuizBox() {
    const clone = quizTemplate.content.cloneNode(true);
    quizFormContainer.appendChild(clone);
    refreshQuizIndexes();
}

    addQuizBtn.addEventListener('click', function () {
    addQuizBox();
});

    quizFormContainer.addEventListener('click', function (event) {
    const removeBtn = event.target.closest('[data-remove-quiz]');

    if (!removeBtn) {
    return;
}

    const quizBoxes = quizFormContainer.querySelectorAll('[data-quiz-box]');

    if (quizBoxes.length <= 1) {
    alert('퀴즈는 최소 1개 이상 등록해야 합니다.');
    return;
}

    removeBtn.closest('[data-quiz-box]').remove();
    refreshQuizIndexes();
});

    // 기본 퀴즈 3개 생성
    addQuizBox();
    addQuizBox();
    addQuizBox();
});

const DU_TEST_MODE = false;

let player;
let maxWatchedSec = 0;
let checkTimer = null;
let videoCompleted = false;

function forceQuizPassForTest() {
    const message = document.getElementById('quizMessage');
    const nextBtn = document.getElementById('nextChapterBtn');
    const completeBtn = document.getElementById('completeBtn');

    if (message) {
        message.textContent = '테스트 통과 처리: 다음 챕터로 이동할 수 있습니다.';
        message.className = 'quiz-message correct';
    }

    if (nextBtn) {
        nextBtn.classList.remove('disabled');
    }

    if (completeBtn) {
        completeBtn.classList.remove('disabled');
    }
}

function onYouTubeIframeAPIReady() {
    const iframe = document.getElementById('duPlayer');

    if (!iframe) {
        return;
    }

    player = new YT.Player('duPlayer', {
        events: {
            'onStateChange': onPlayerStateChange
        }
    });
}

function onPlayerStateChange(event) {
    if (event.data === YT.PlayerState.PLAYING) {
        startWatchCheck();
    }

    if (event.data === YT.PlayerState.PAUSED) {
        stopWatchCheck();
    }

    if (event.data === YT.PlayerState.ENDED) {
        videoCompleted = true;
        stopWatchCheck();
        showQuiz();
    }
}

function startWatchCheck() {
    if (checkTimer !== null) {
        return;
    }

    checkTimer = setInterval(function () {
        if (!player || typeof player.getCurrentTime !== 'function') {
            return;
        }

        const currentSec = player.getCurrentTime();
        const duration = player.getDuration();

        // 앞으로 스킵하면 마지막 정상 시청 위치로 되돌림
        if (currentSec > maxWatchedSec + 2 && !videoCompleted) {
            player.seekTo(maxWatchedSec, true);
            return;
        }

        // 정상 재생 중이면 최대 시청 위치 갱신
        if (currentSec > maxWatchedSec) {
            maxWatchedSec = currentSec;
            updateProgressBar(duration);
        }
    }, 500);
}

function stopWatchCheck() {
    if (checkTimer !== null) {
        clearInterval(checkTimer);
        checkTimer = null;
    }
}

function updateProgressBar(duration) {
    if (!duration || duration <= 0) {
        return;
    }

    const rate = Math.min((maxWatchedSec / duration) * 100, 100);
    const progressBar = document.getElementById('duProgressBar');

    if (progressBar) {
        progressBar.style.width = rate + '%';
    }
}

function showQuiz() {
    const quizBox = document.getElementById('quizBox');

    if (quizBox) {
        quizBox.style.display = 'block';
    }

}

function forceCompleteForTest() {
    videoCompleted = true;
    maxWatchedSec = 99999;

    const progressBar = document.getElementById('duProgressBar');

    if (progressBar) {
        progressBar.style.width = '100%';
    }

    showQuiz();
}

function resetChapter() {
    videoCompleted = false;
    maxWatchedSec = 0;

    const progressBar = document.getElementById('duProgressBar');
    const quizBox = document.getElementById('quizBox');

    if (progressBar) {
        progressBar.style.width = '0%';
    }

    if (quizBox) {
        quizBox.style.display = 'none';
    }

    if (player && typeof player.seekTo === 'function') {
        player.seekTo(0, true);
        player.playVideo();
    }
}

document.addEventListener('DOMContentLoaded', function () {
    const quizBox = document.getElementById('quizBox');
    const quizItems = document.querySelectorAll('.quiz-item');
    const quizOptions = document.querySelectorAll('.quiz-option');

    const prevQuizBtn = document.getElementById('prevQuizBtn');
    const nextQuizBtn = document.getElementById('nextQuizBtn');
    const submitQuizBtn = document.getElementById('submitQuizBtn');

    const quizCurrentNo = document.getElementById('quizCurrentNo');
    const message = document.getElementById('quizMessage');

    const nextChapterBtn = document.getElementById('nextChapterBtn');
    const retryBtn = document.getElementById('retryBtn');
    const chapterIdInput = document.getElementById('currentChapterId');

    if (!quizBox || quizItems.length === 0) {
        return;
    }

    const duCompleted = String(quizBox.dataset.completed).toLowerCase() === 'true';

    let currentQuizIndex = 0;

    // quizId별 선택한 choiceId 저장
    const selectedAnswerMap = {};

    function getCurrentQuizItem() {
        return quizItems[currentQuizIndex];
    }

    function isCurrentQuizSelected() {
        const currentItem = getCurrentQuizItem();

        if (!currentItem) {
            return false;
        }

        return currentItem.querySelector('.quiz-option.selected') !== null;
    }

    function isLastQuiz() {
        return currentQuizIndex === quizItems.length - 1;
    }

    function updateNavButtons() {
        const selected = isCurrentQuizSelected();
        const last = isLastQuiz();

        if (prevQuizBtn) {
            prevQuizBtn.disabled = currentQuizIndex === 0;
        }

        if (nextQuizBtn) {
            nextQuizBtn.style.display = last ? 'none' : 'inline-flex';

            if (duCompleted) {
                nextQuizBtn.disabled = last;
            } else {
                nextQuizBtn.disabled = !selected;
            }
        }

        if (submitQuizBtn) {
            if (duCompleted) {
                submitQuizBtn.style.display = 'none';
                submitQuizBtn.disabled = true;
            } else {
                submitQuizBtn.style.display = last ? 'inline-flex' : 'none';
                submitQuizBtn.disabled = !selected;
            }
        }
    }
    function showQuiz(index) {
        quizItems.forEach(function (item, itemIndex) {
            item.style.display = itemIndex === index ? 'block' : 'none';
        });

        if (quizCurrentNo) {
            quizCurrentNo.textContent = index + 1;
        }

        updateNavButtons();
    }

    function getSelectedAnswers() {
        const answers = [];

        quizItems.forEach(function (item) {
            const selectedOption = item.querySelector('.quiz-option.selected');

            if (!selectedOption) {
                return;
            }

            answers.push({
                quizId: Number(selectedOption.dataset.quizId),
                choiceId: Number(selectedOption.dataset.choiceId)
            });
        });

        return answers;
    }

    function markSelectedOption(button) {
        const currentItem = button.closest('.quiz-item');

        if (!currentItem) {
            return;
        }

        currentItem.querySelectorAll('.quiz-option').forEach(function (option) {
            option.classList.remove('selected');
        });

        button.classList.add('selected');

        selectedAnswerMap[button.dataset.quizId] = button.dataset.choiceId;

        if (message) {
            message.textContent = '';
            message.className = 'quiz-message';
        }

        updateNavButtons();
    }

    quizOptions.forEach(function (button) {
        button.addEventListener('click', function () {
            if (button.disabled) {
                return;
            }

            markSelectedOption(button);
        });
    });

    if (prevQuizBtn) {
        prevQuizBtn.addEventListener('click', function () {
            if (currentQuizIndex <= 0) {
                return;
            }

            currentQuizIndex--;
            showQuiz(currentQuizIndex);
        });
    }

    if (nextQuizBtn) {
        nextQuizBtn.addEventListener('click', function () {
            if (!duCompleted && !isCurrentQuizSelected()) {
                if (message) {
                    message.textContent = '답안을 선택한 후 다음 문제로 이동할 수 있습니다.';
                    message.className = 'quiz-message wrong';
                }
                return;
            }

            if (currentQuizIndex < quizItems.length - 1) {
                currentQuizIndex++;
                showQuiz(currentQuizIndex);
            }
        });
    }

    if (submitQuizBtn) {
        submitQuizBtn.addEventListener('click', function () {
            const answers = getSelectedAnswers();

            if (answers.length !== quizItems.length) {
                if (message) {
                    message.textContent = '모든 문제의 답안을 선택해 주세요.';
                    message.className = 'quiz-message wrong';
                }
                return;
            }

            const chapterId = chapterIdInput ? Number(chapterIdInput.value) : 0;

            submitQuizBtn.disabled = true;

            fetch('/drive-u/du/quiz/submit', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    chapterId: chapterId,
                    answers: answers
                })
            })
                .then(function (response) {
                    const contentType = response.headers.get('content-type');

                    if (response.redirected) {
                        throw new Error('로그인 또는 권한 문제로 다른 페이지로 이동되었습니다.');
                    }

                    if (!contentType || !contentType.includes('application/json')) {
                        return response.text().then(function () {
                            throw new Error('서버가 JSON이 아닌 응답을 반환했습니다.');
                        });
                    }

                    return response.json();
                })
                .then(function (result) {
                    if (message) {
                        message.textContent = result.message;
                    }

                    if (result.passed) {
                        if (message) {
                            message.className = 'quiz-message correct';
                        }

                        quizOptions.forEach(function (option) {
                            option.disabled = true;
                        });

                        if (nextChapterBtn) {
                            nextChapterBtn.classList.remove('disabled');
                        }

                        if (retryBtn) {
                            retryBtn.classList.add('disabled');
                        }

                        if (prevQuizBtn) {
                            prevQuizBtn.disabled = true;
                        }

                        if (nextQuizBtn) {
                            nextQuizBtn.disabled = true;
                        }

                        if (submitQuizBtn) {
                            submitQuizBtn.style.display = 'none';
                        }
                    } else {
                        if (message) {
                            message.className = 'quiz-message wrong';
                        }

                        if (retryBtn) {
                            retryBtn.classList.remove('disabled');
                        }

                        if (nextChapterBtn) {
                            nextChapterBtn.classList.add('disabled');
                        }

                        if (submitQuizBtn) {
                            submitQuizBtn.disabled = false;
                        }
                    }
                })
                .catch(function (error) {
                    if (message) {
                        message.textContent = error.message;
                        message.className = 'quiz-message wrong';
                    }

                    if (submitQuizBtn) {
                        submitQuizBtn.disabled = false;
                    }
                });
        });
    }

    if (retryBtn) {
        retryBtn.addEventListener('click', function () {
            if (retryBtn.classList.contains('disabled')) {
                return;
            }

            location.reload();
        });
    }

    showQuiz(currentQuizIndex);
});
function showExplanations() {
    const explanations = document.querySelectorAll('.quiz-explanation');

    explanations.forEach(function (explanation) {
        explanation.style.display = 'block';
    });
}

function markCorrectChoices() {
    const options = document.querySelectorAll('.quiz-option');

    options.forEach(function (option) {
        if (option.dataset.answer === 'Y') {
            option.classList.add('correct-choice');
        }

        if (option.classList.contains('selected') && option.dataset.answer === 'N') {
            option.classList.add('wrong-choice');
        }
    });
}
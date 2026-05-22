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
    if (DU_TEST_MODE) {
        setTimeout(function () {
            forceCompleteForTest();
        }, 500);
    }
    const options = document.querySelectorAll('.quiz-option');
    const message = document.getElementById('quizMessage');
    const nextBtn = document.getElementById('nextChapterBtn');
    const completeBtn = document.getElementById('completeBtn');
    const retryBtn = document.getElementById('retryChapterBtn');

    const selectedAnswers = {};

    options.forEach(function (button) {
        button.addEventListener('click', function (event) {
            event.preventDefault();

            if (!videoCompleted) {
                message.textContent = '영상을 끝까지 시청한 후 퀴즈를 풀 수 있습니다.';
                message.className = 'quiz-message wrong';
                return;
            }

            const quizId = button.dataset.quizId;
            const choiceId = button.dataset.choiceId;

            selectedAnswers[quizId] = choiceId;

            const sameQuizButtons = document.querySelectorAll(
                `.quiz-option[data-quiz-id="${quizId}"]`
            );

            sameQuizButtons.forEach(function (option) {
                option.classList.remove('selected');
            });

            button.classList.add('selected');

            const quizItems = document.querySelectorAll('.quiz-item');
            const totalQuizCount = quizItems.length;
            const selectedQuizCount = Object.keys(selectedAnswers).length;

            if (selectedQuizCount < totalQuizCount) {
                message.textContent = `${selectedQuizCount}/${totalQuizCount}문제를 선택했습니다.`;
                message.className = 'quiz-message';
                return;
            }

            submitQuizAnswers();
        });
    });

    function submitQuizAnswers() {
        const chapterId = document.getElementById('currentChapterId').value;
        const memberSeq = document.getElementById('currentMemberSeq').value;

        const answers = Object.keys(selectedAnswers).map(function (quizId) {
            return {
                quizId: Number(quizId),
                choiceId: Number(selectedAnswers[quizId])
            };
        });

        fetch('/drive-u/du/quiz/submit', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                chapterId: Number(chapterId),
                memberSeq: Number(memberSeq),
                answers: answers
            })
        })
            .then(function (response) {
                if (!response.ok) {
                    throw new Error('퀴즈 제출 중 오류가 발생했습니다.');
                }

                return response.json();
            })
            .then(function (result) {
                console.log('passed:', result.passed);
                console.log('nextBtn:', nextBtn);
                console.log('next href:', nextBtn ? nextBtn.href : 'nextBtn 없음');
                console.log('next class:', nextBtn ? nextBtn.className : 'nextBtn 없음');

                message.textContent = result.message;
                message.className = result.passed
                    ? 'quiz-message correct'
                    : 'quiz-message wrong';


                showExplanations();
                markCorrectChoices();

                if (result.passed) {
                    if (nextBtn) {
                        nextBtn.classList.remove('disabled');
                    }

                    if (completeBtn) {
                        completeBtn.classList.remove('disabled');
                    }

                    if (retryBtn) {
                        retryBtn.classList.add('disabled');
                    }

                    options.forEach(function (option) {
                        option.disabled = true;
                    });
                } else {
                    message.textContent = result.message || '오답입니다. 해설을 확인한 뒤 이 챕터를 다시 시청하세요.';
                    message.className = 'quiz-message wrong';

                    showExplanations();
                    markCorrectChoices();

                    if (nextBtn) {
                        nextBtn.classList.add('disabled');
                    }

                    if (completeBtn) {
                        completeBtn.classList.add('disabled');
                    }

                    if (retryBtn) {
                        retryBtn.classList.remove('disabled');
                    }

                    options.forEach(function (option) {
                        option.disabled = true;
                    });
                }
            })
            .catch(function (error) {
                message.textContent = error.message;
                message.className = 'quiz-message wrong';
            });
    }
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
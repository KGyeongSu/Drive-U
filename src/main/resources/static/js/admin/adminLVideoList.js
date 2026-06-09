document.addEventListener('DOMContentLoaded', function () {
    const editModal = document.getElementById('editVideoModal');
    const deleteModal = document.getElementById('deleteVideoModal');

    const editVideoForm = document.getElementById('editVideoForm');
    const deleteVideoForm = document.getElementById('deleteVideoForm');

    const editCategory = document.getElementById('editCategory');
    const editCourseOrder = document.getElementById('editCourseOrder');
    const editTitle = document.getElementById('editTitle');
    const editVideoUrl = document.getElementById('editVideoUrl');
    const editDescription = document.getElementById('editDescription');
    const editUseYn = document.getElementById('editUseYn');

    const editModalDeleteBtn =
        document.getElementById('editModalDeleteBtn');

    const deleteVideoTitle =
        document.getElementById('deleteVideoTitle');

    function openModal(modal) {
        if (!modal) {
            return;
        }

        modal.classList.add('open');
        modal.setAttribute('aria-hidden', 'false');
        document.body.classList.add('modal-open');
    }

    function closeModal(modal) {
        if (!modal) {
            return;
        }
        const focusedElement = document.activeElement;

        if (focusedElement && modal.contains(focusedElement)) {
            focusedElement.blur();
        }

        modal.classList.remove('open');
        modal.setAttribute('aria-hidden', 'true');

        /*
         * 다른 모달이 열려 있지 않을 때만
         * body의 modal-open 제거
         */
        const openedModal =
            document.querySelector('.admin-modal.open');

        if (!openedModal) {
            document.body.classList.remove('modal-open');
        }
    }

    /*
     * 목록의 수정 버튼, 닫기 버튼 처리
     */
    document.addEventListener('click', function (event) {
        const editButton =
            event.target.closest('[data-edit-video]');

        if (editButton) {
            const courseId = editButton.dataset.courseId;
            const title = editButton.dataset.title || '';

            editCategory.value =
                editButton.dataset.category || '';

            editCourseOrder.value =
                editButton.dataset.courseOrder || '';

            editTitle.value = title;

            editVideoUrl.value =
                editButton.dataset.videoUrl || '';

            editDescription.value =
                editButton.dataset.description || '';

            editUseYn.value =
                editButton.dataset.useYn || 'Y';

            editVideoForm.action =
                '/drive-u/admin/video/lVideo/'
                + courseId
                + '/modify';

            /*
             * 수정 모달 내부 삭제 버튼에
             * 현재 영상 정보 저장
             */
            if (editModalDeleteBtn) {
                editModalDeleteBtn.dataset.courseId = courseId;
                editModalDeleteBtn.dataset.title = title;
            }

            openModal(editModal);
            return;
        }

        if (event.target.closest('[data-close-edit-modal]')) {
            closeModal(editModal);
            return;
        }

        if (event.target.closest('[data-close-delete-modal]')) {
            closeModal(deleteModal);
        }
    });

    /*
     * 수정 모달 내부의 삭제 버튼
     */
    if (editModalDeleteBtn) {
        editModalDeleteBtn.addEventListener('click', function () {
            const courseId =
                editModalDeleteBtn.dataset.courseId;

            const title =
                editModalDeleteBtn.dataset.title
                || '선택한 영상';

            if (!courseId) {
                return;
            }

            if (deleteVideoTitle) {
                deleteVideoTitle.textContent = title;
            }

            deleteVideoForm.action =
                '/drive-u/admin/video/lVideo/'
                + courseId
                + '/delete';

            closeModal(editModal);
            openModal(deleteModal);
        });
    }

    /*
     * ESC로 모달 닫기
     */
    document.addEventListener('keydown', function (event) {
        if (event.key !== 'Escape') {
            return;
        }

        closeModal(editModal);
        closeModal(deleteModal);
    });
});

document.addEventListener('DOMContentLoaded', function () {
    const sortSelect =
        document.getElementById('videoSortSelect');

    const sortForm =
        document.getElementById('sortForm');

    if (!sortSelect || !sortForm) {
        return;
    }

    sortSelect.addEventListener('change', function () {
        sortForm.submit();
    });
});
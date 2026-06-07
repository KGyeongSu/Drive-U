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

    const deleteVideoTitle = document.getElementById('deleteVideoTitle');

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

        modal.classList.remove('open');
        modal.setAttribute('aria-hidden', 'true');
        document.body.classList.remove('modal-open');
    }

    document.addEventListener('click', function (event) {
        const editButton = event.target.closest('[data-edit-video]');

        if (editButton) {
            const courseId = editButton.dataset.courseId;

            editCategory.value = editButton.dataset.category || '';
            editCourseOrder.value = editButton.dataset.courseOrder || '';
            editTitle.value = editButton.dataset.title || '';
            editVideoUrl.value = editButton.dataset.videoUrl || '';
            editDescription.value = editButton.dataset.description || '';
            editUseYn.value = editButton.dataset.useYn || 'Y';

            editVideoForm.action =
                '/drive-u/admin/video/lVideo/' + courseId + '/modify';

            openModal(editModal);
            return;
        }

        const deleteButton = event.target.closest('[data-delete-video]');

        if (deleteButton) {
            const courseId = deleteButton.dataset.courseId;
            const title = deleteButton.dataset.title || '선택한 영상';

            deleteVideoTitle.textContent = title;

            deleteVideoForm.action =
                '/drive-u/admin/video/lVideo/' + courseId + '/delete';

            openModal(deleteModal);
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
document.querySelectorAll(".admin-tab").forEach((tab) => {
    tab.addEventListener("click", () => {
        const targetId = tab.dataset.target;

        document.querySelectorAll(".admin-tab").forEach((button) => {
            button.classList.remove("active");
        });

        document.querySelectorAll(".admin-panel").forEach((panel) => {
            panel.classList.remove("active");
        });

        tab.classList.add("active");
        document.getElementById(targetId)?.classList.add("active");
    });
});

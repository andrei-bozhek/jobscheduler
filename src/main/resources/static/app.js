const createTaskForm = document.querySelector("#createTaskForm");
const refreshButton = document.querySelector("#refreshButton");
const statusFilter = document.querySelector("#statusFilter");
const taskList = document.querySelector("#taskList");

refreshButton.addEventListener("click", loadTasks);
statusFilter.addEventListener("change", loadTasks);
createTaskForm.addEventListener("submit", createTask);

loadTasks();

async function loadTasks() {
    const status = statusFilter.value;
    const url = status ? `/tasks?status=${status}` : "/tasks";

    const response = await fetch(url);
    const tasks = await response.json();

    renderTasks(tasks);
}

function renderTasks(tasks) {
    if (tasks.length === 0) {
        taskList.innerHTML = `<p class="muted">No tasks found.</p>`;
        return;
    }

    taskList.innerHTML = tasks.map(task => `
        <article class="taskItem">
            <div class="taskMeta">
                <strong>${task.type}</strong>
                <span class="status ${statusClass(task.status)}">${task.status}</span>
            </div>
            <div class="taskId">${task.id}</div>
            <div class="muted">Run at: ${task.runAt}</div>
            <div class="muted">Attempt: ${task.attempt} / ${task.maxAttempts}</div>
        </article>
    `).join("");
}

function statusClass(status) {
    if (status === "PENDING") {
        return "statusPending";
    }
    if (status === "RUNNING") {
        return "statusRunning";
    }
    if (status === "DONE") {
        return "statusDone";
    }
    if (status === "FAILED") {
        return "statusFailed";
    }
    if (status === "CANCELED") {
        return "statusCanceled";
    }
    return "";
}

async function createTask(event) {
    event.preventDefault();

    const formData = new FormData(createTaskForm);

    const request = {
        type: formData.get("type"),
        payload: {
            message: formData.get("message")
        },
        runAt: toOffsetDateTime(formData.get("runAt")),
        maxAttempts: Number(formData.get("maxAttempts"))
    };

    const response = await fetch("/tasks", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(request)
    });

    if (!response.ok) {
        console.error("Failed to create task", await response.text());
        return;
    }

    await loadTasks();
}

function toOffsetDateTime(localDateTime) {
    return `${localDateTime}:00+09:00`;
}
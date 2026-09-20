"use strict";

const translations = {
    en: {
        newTask: "New task", taskStatus: "Task status", upNext: "Up next", sourceCode: "Source code",
        calendar: "Calendar", language: "Language", demoHeading: "Watch a task come to life",
        demoText: "Schedule a task for 30 seconds from now and follow its execution.", runIn30: "Run in 30 seconds",
        retry: "Retry", previousMonth: "Previous month", nextMonth: "Next month", today: "Today", refresh: "Refresh",
        scheduleTask: "Schedule a task", close: "Close", title: "Title", titlePlaceholder: "What would you like to schedule?",
        notes: "Notes", optional: "optional", notesPlaceholder: "Add a little context…", dateTime: "Date & time",
        echoExplanation: "The echo handler writes your message to the server log. The result appears in the task’s run history.",
        dismiss: "Cancel", schedule: "Schedule task", cancelHeading: "Cancel this task?",
        cancelExplanation: "A pending task will be canceled. Its record stays in the calendar.", keepTask: "Keep task", cancelTask: "Cancel task",
        ALL: "All tasks", PENDING: "Pending", RUNNING: "Running", DONE: "Completed", FAILED: "Failed", CANCELED: "Canceled",
        STARTED: "Started", SUCCESS: "Succeeded", connected: "API connected", connecting: "Connecting…", disconnected: "API unavailable",
        offline: "Cannot reach the task API. Check that Spring Boot is running, then retry.",
        stale: "Connection lost. Showing the last received tasks; updates will resume automatically.",
        sync: "Statuses refresh automatically", lastSync: "Last updated {time}", noSync: "Waiting for the task API",
        calendarHint: "Select a day to explore its tasks", noUpcoming: "Nothing scheduled yet.", selectedDay: "Selected day",
        taskCount: "{count} tasks", oneTask: "1 task", noTasks: "A little room in your day", noTasksText: "Schedule something here, or try the 30-second demo.",
        noMatching: "No matching tasks", noMatchingText: "Choose another status to see more tasks on this day.",
        noPastText: "No tasks were scheduled for this day.", loading: "Loading tasks…", loadingText: "Your calendar will appear once the API responds.",
        offlineTitle: "Let’s connect your calendar", offlineText: "Start the application to load tasks and schedule your first run.",
        more: "+{count} more", backToDay: "Back to day", scheduledFor: "Scheduled for", handler: "Handler", attempts: "Attempts",
        nextAttempt: "Next attempt", history: "Run history", noRuns: "No runs yet. The worker will pick up this task when it is due.",
        canceledRuns: "Canceled before execution.", noHistory: "No run records returned by the API.", loadingHistory: "Loading run history…",
        historyError: "Could not load run history.", attempt: "Attempt {number}", startedAt: "Started {time}", finishedAt: "Finished {time}",
        countdown: "Starts in {time}", waiting: "Due · waiting for the worker", runningNow: "The worker is executing this task",
        quickTitle: "Quick demo", created: "Task scheduled", canceled: "Task canceled", completed: "Task completed — view its run history",
        saving: "Scheduling…", canceling: "Canceling…", titleRequired: "Enter a title.", futureRequired: "Choose a date and time in the future.",
        requestFailed: "The request failed. Please try again.", uncertain: "Could not confirm the request. Refresh the calendar before trying again.",
        conflict: "This task is no longer pending and cannot be canceled.", timezone: "Times shown in {zone}", untitled: "Untitled task",
        taskDetails: "Selected day and task details", payload: "Task payload", invalidDate: "Unknown date"
    },
    ja: {
        newTask: "新しいタスク", taskStatus: "ステータス", upNext: "次の実行", sourceCode: "ソースコード",
        calendar: "カレンダー", language: "言語", demoHeading: "タスクの実行を見てみよう",
        demoText: "30秒後にタスクを実行して、結果を確認できます。", runIn30: "30秒後に実行",
        retry: "再試行", previousMonth: "前の月", nextMonth: "次の月", today: "今日", refresh: "更新",
        scheduleTask: "実行予約", close: "閉じる", title: "タイトル", titlePlaceholder: "何を実行しますか？",
        notes: "メモ", optional: "任意", notesPlaceholder: "タスクの説明を入力…", dateTime: "実行日時",
        echoExplanation: "echoハンドラーがメッセージをサーバーログに出力します。結果は実行履歴で確認できます。",
        dismiss: "閉じる", schedule: "タスクを予約", cancelHeading: "タスクを取り消しますか？",
        cancelExplanation: "待機中のタスクを取り消します。記録はカレンダーに残ります。", keepTask: "戻る", cancelTask: "実行を取り消す",
        ALL: "すべて", PENDING: "待機中", RUNNING: "実行中", DONE: "完了", FAILED: "失敗", CANCELED: "取消済み",
        STARTED: "開始", SUCCESS: "成功", connected: "API接続済み", connecting: "接続中…", disconnected: "API未接続",
        offline: "タスクAPIに接続できません。Spring Bootが起動しているか確認し、再試行してください。",
        stale: "接続が切れました。最後に取得したタスクを表示しています。接続後に自動更新します。",
        sync: "ステータスは自動更新されます", lastSync: "最終更新 {time}", noSync: "APIへの接続待ち",
        calendarHint: "日付を選択してタスクを表示", noUpcoming: "予約されたタスクはありません。", selectedDay: "選択した日",
        taskCount: "{count}件のタスク", oneTask: "1件のタスク", noTasks: "予定のない一日", noTasksText: "タスクを予約するか、30秒のデモを試してみましょう。",
        noMatching: "該当するタスクはありません", noMatchingText: "別のステータスを選択してください。",
        noPastText: "この日に予約されたタスクはありません。", loading: "読み込み中…", loadingText: "APIからタスクを取得しています。",
        offlineTitle: "カレンダーに接続しましょう", offlineText: "アプリを起動すると、タスクの表示と予約ができます。",
        more: "他{count}件", backToDay: "この日のタスク", scheduledFor: "予約日時", handler: "ハンドラー", attempts: "実行回数",
        nextAttempt: "次回の試行", history: "実行履歴", noRuns: "まだ実行されていません。予約時刻になるとワーカーが実行します。",
        canceledRuns: "実行前に取り消されました。", noHistory: "実行記録はありません。", loadingHistory: "実行履歴を取得中…",
        historyError: "実行履歴を取得できませんでした。", attempt: "試行 {number}", startedAt: "開始 {time}", finishedAt: "終了 {time}",
        countdown: "あと{time}で実行", waiting: "実行時刻 · ワーカーを待機中", runningNow: "ワーカーがタスクを実行中です",
        quickTitle: "クイックデモ", created: "タスクを予約しました", canceled: "タスクを取り消しました", completed: "タスクが完了しました。実行履歴を確認できます",
        saving: "予約中…", canceling: "取消中…", titleRequired: "タイトルを入力してください。", futureRequired: "未来の日時を選択してください。",
        requestFailed: "リクエストに失敗しました。再試行してください。", uncertain: "処理結果を確認できませんでした。再試行する前にカレンダーを更新してください。",
        conflict: "タスクは待機中ではないため、取り消せません。", timezone: "表示のタイムゾーン：{zone}", untitled: "無題のタスク",
        taskDetails: "選択した日とタスクの詳細", payload: "タスクのペイロード", invalidDate: "不明な日時"
    },
    ru: {
        newTask: "Новая задача", taskStatus: "Статус задач", upNext: "Следующий запуск", sourceCode: "Исходный код",
        calendar: "Календарь", language: "Язык", demoHeading: "Посмотри, как выполняется задача",
        demoText: "Запланируй запуск через 30 секунд и проследи за результатом.", runIn30: "Запустить через 30 секунд",
        retry: "Повторить", previousMonth: "Предыдущий месяц", nextMonth: "Следующий месяц", today: "Сегодня", refresh: "Обновить",
        scheduleTask: "Планирование", close: "Закрыть", title: "Название", titlePlaceholder: "Что запланировать?",
        notes: "Заметки", optional: "необязательно", notesPlaceholder: "Добавь описание…", dateTime: "Дата и время",
        echoExplanation: "Обработчик echo запишет сообщение в лог сервера. Результат появится в истории запусков задачи.",
        dismiss: "Закрыть", schedule: "Запланировать", cancelHeading: "Отменить задачу?",
        cancelExplanation: "Задача в ожидании будет отменена. Её запись останется в календаре.", keepTask: "Оставить", cancelTask: "Отменить задачу",
        ALL: "Все задачи", PENDING: "Ожидание", RUNNING: "Выполняется", DONE: "Выполнено", FAILED: "Ошибка", CANCELED: "Отменено",
        STARTED: "Начат", SUCCESS: "Успешно", connected: "API подключён", connecting: "Подключение…", disconnected: "API недоступен",
        offline: "Не удалось подключиться к API. Проверь, что Spring Boot запущен, и повтори попытку.",
        stale: "Соединение потеряно. Показаны последние полученные задачи; обновление возобновится автоматически.",
        sync: "Статусы обновляются автоматически", lastSync: "Обновлено в {time}", noSync: "Ожидание подключения к API",
        calendarHint: "Выбери день, чтобы посмотреть задачи", noUpcoming: "Пока ничего не запланировано.", selectedDay: "Выбранный день",
        taskCount: "Задач: {count}", oneTask: "1 задача", noTasks: "Здесь пока свободно", noTasksText: "Добавь задачу или попробуй запуск через 30 секунд.",
        noMatching: "Нет подходящих задач", noMatchingText: "Выбери другой статус, чтобы увидеть задачи этого дня.",
        noPastText: "На этот день задач не было запланировано.", loading: "Загрузка задач…", loadingText: "Календарь появится после ответа API.",
        offlineTitle: "Подключим календарь", offlineText: "Запусти приложение, чтобы загрузить задачи и запланировать первый запуск.",
        more: "+ ещё {count}", backToDay: "К задачам дня", scheduledFor: "Запланировано", handler: "Обработчик", attempts: "Попытки",
        nextAttempt: "Следующая попытка", history: "История запусков", noRuns: "Запусков ещё нет. В назначенное время воркер возьмёт задачу в работу.",
        canceledRuns: "Отменена до выполнения.", noHistory: "API не вернул записей о запусках.", loadingHistory: "Загрузка истории…",
        historyError: "Не удалось загрузить историю запусков.", attempt: "Попытка {number}", startedAt: "Начало: {time}", finishedAt: "Завершение: {time}",
        countdown: "Запуск через {time}", waiting: "Время наступило · ожидание воркера", runningNow: "Воркер выполняет задачу",
        quickTitle: "Быстрое демо", created: "Задача запланирована", canceled: "Задача отменена", completed: "Задача выполнена — результат в истории запусков",
        saving: "Сохранение…", canceling: "Отмена…", titleRequired: "Введи название.", futureRequired: "Выбери дату и время в будущем.",
        requestFailed: "Запрос не выполнен. Попробуй ещё раз.", uncertain: "Результат запроса неизвестен. Обнови календарь перед повторной попыткой.",
        conflict: "Задача уже вышла из ожидания, отменить её нельзя.", timezone: "Время в часовом поясе {zone}", untitled: "Задача без названия",
        taskDetails: "Выбранный день и сведения о задаче", payload: "Данные задачи", invalidDate: "Дата неизвестна"
    }
};

const $ = (selector) => document.querySelector(selector);
const statuses = ["ALL", "PENDING", "RUNNING", "DONE", "FAILED", "CANCELED"];
const locales = {en: "en-GB", ja: "ja-JP", ru: "ru-RU"};
const zone = Intl.DateTimeFormat().resolvedOptions().timeZone;
const pad = (value) => String(value).padStart(2, "0");
const dayKey = (date) => `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`;
const fromDay = (key) => { const [y, m, d] = key.split("-").map(Number); return new Date(y, m - 1, d, 12); };
const monthOf = (date) => new Date(date.getFullYear(), date.getMonth(), 1, 12);
const localInput = (date) => `${dayKey(date)}T${pad(date.getHours())}:${pad(date.getMinutes())}`;
const validDate = (value) => typeof value === "string" && Number.isFinite(Date.parse(value));
const escapeHtml = (value) => String(value ?? "").replace(/[&<>"']/g, (c) => ({"&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;"}[c]));
const icon = (name) => `<svg class="icon" aria-hidden="true"><use href="#i-${name}"/></svg>`;
let language = "en";
try { const saved = localStorage.getItem("calendar-language"); if (translations[saved]) language = saved; } catch { /* Storage is optional. */ }
const state = {
    language, month: monthOf(new Date()), day: dayKey(new Date()), selectedId: null, filter: "ALL",
    tasks: [], loaded: false, connected: false, connectionAttempted: false, lastSync: null,
    creating: false, canceling: false, cancelId: null
};
const history = new Map();
const historyRequests = new Map();
let refreshRequest = null;
let pollTimer;
let toastTimer;
const t = (key, params = {}) => Object.entries(params).reduce((text, [name, value]) => text.replaceAll(`{${name}}`, String(value)), translations[state.language][key] ?? key);
const format = (date, options) => Number.isFinite(new Date(date).getTime()) ? new Intl.DateTimeFormat(locales[state.language], options).format(new Date(date)) : t("invalidDate");
const time = (date, seconds = false) => format(date, {hour: "2-digit", minute: "2-digit", ...(seconds ? {second: "2-digit"} : {}), hour12: false});
const fullDate = (date) => format(date, {day: "numeric", month: "short", year: "numeric"});
const scheduledAt = (task) => validDate(task.payload?.scheduledAt) ? task.payload.scheduledAt : task.runAt;
const taskTitle = (task) => [task.payload?.message, task.payload?.title].find((value) => typeof value === "string" && value.trim()) || t("untitled");
const statusClass = (status) => statuses.includes(status) ? status.toLowerCase() : "all";
const badge = (status) => `<span class="status-badge ${statusClass(status)}"><span class="status-dot ${statusClass(status)}"></span>${escapeHtml(t(status))}</span>`;
const selectedTask = () => state.tasks.find((task) => task.id === state.selectedId);
const matchesFilter = (task) => state.filter === "ALL" || task.status === state.filter;
const tasksOnDay = (key) => state.tasks.filter((task) => matchesFilter(task) && dayKey(new Date(scheduledAt(task))) === key).sort((a, b) => new Date(scheduledAt(a)) - new Date(scheduledAt(b)));

// Keep keyboard focus during polling when the focused control still exists.
function replaceContent(element, html) {
    const active = element.contains(document.activeElement) ? document.activeElement : null;
    const focusData = active?.dataset;
    element.innerHTML = html;
    if (focusData?.action) {
        [...element.querySelectorAll("[data-action]")].find((node) => ["action", "id", "day", "filter"].every((key) => node.dataset[key] === focusData[key]))?.focus({preventScroll: true});
    }
}

class ApiError extends Error {
    constructor(message, status = 0) { super(message); this.status = status; }
}

async function request(path, options = {}) {
    const controller = new AbortController();
    const timeout = setTimeout(() => controller.abort(), 8000);
    try {
        const response = await fetch(path, {cache: "no-store", ...options, signal: controller.signal});
        if (!response.ok) {
            const problem = await response.json().catch(() => ({}));
            throw new ApiError(problem.detail || problem.message || t("requestFailed"), response.status);
        }
        return response.status === 204 ? null : await response.json();
    } catch (error) {
        if (error instanceof ApiError) throw error;
        throw new ApiError(t(options.method ? "uncertain" : "requestFailed"));
    } finally { clearTimeout(timeout); }
}

async function fetchTasks() {
    const tasks = new Map();
    for (let offset = 0; ; offset += 100) {
        const page = await request(`/tasks?limit=100&offset=${offset}`);
        if (!Array.isArray(page)) throw new ApiError(t("requestFailed"));
        page.forEach((task) => tasks.set(task.id, task));
        if (page.length < 100) break;
    }
    return [...tasks.values()];
}

function refreshTasks() {
    if (refreshRequest) return refreshRequest;
    if (state.creating || state.canceling) return Promise.resolve();
    refreshRequest = (async () => {
        try {
            const tasks = await fetchTasks();
            const previous = selectedTask();
            const completed = previous && previous.status !== "DONE" && tasks.some((task) => task.id === previous.id && task.status === "DONE");
            const changed = !state.loaded || !state.connected || JSON.stringify(tasks) !== JSON.stringify(state.tasks);
            state.tasks = tasks;
            state.loaded = true;
            state.connected = true;
            state.lastSync = new Date();
            if (state.selectedId && !selectedTask()) state.selectedId = null;
            if (changed) render();
            if (completed) showToast(t("completed"));
        } catch {
            const changed = state.connected || !state.connectionAttempted;
            state.connected = false;
            if (changed) { state.connectionAttempted = true; render(); }
        } finally {
            state.connectionAttempted = true;
            renderConnection();
            refreshRequest = null;
        }
    })();
    return refreshRequest;
}

async function loadHistory(id, force = false) {
    if (!id || historyRequests.has(id)) return;
    const previous = history.get(id);
    if (!force && previous && Date.now() - previous.fetchedAt < 2000) return;
    historyRequests.set(id, true);
    try {
        const runs = await request(`/tasks/${encodeURIComponent(id)}/runs`);
        if (!Array.isArray(runs)) throw new ApiError(t("historyError"));
        const changed = !previous || previous.error || JSON.stringify(previous.runs) !== JSON.stringify(runs);
        history.set(id, {runs, fetchedAt: Date.now(), error: false});
        if (changed && state.selectedId === id) renderDetail();
    } catch {
        history.set(id, {runs: previous?.runs || [], fetchedAt: Date.now(), error: true});
        if (state.selectedId === id) renderDetail();
    } finally { historyRequests.delete(id); }
}

async function poll() {
    clearTimeout(pollTimer);
    if (!document.hidden) {
        await refreshTasks();
        if (state.connected) await loadHistory(state.selectedId);
    }
    pollTimer = setTimeout(poll, state.connected ? 1000 : 5000);
}

function renderConnection() {
    const connection = $("#connection");
    const key = state.connected ? "connected" : state.connectionAttempted ? "disconnected" : "connecting";
    connection.className = `connection ${key}`;
    connection.textContent = t(key);
    $("#apiError").hidden = state.connected || !state.connectionAttempted;
    $("#apiErrorText").textContent = t(state.loaded ? "stale" : "offline");
    $("#syncNote").textContent = state.lastSync ? `${t("sync")} · ${t("lastSync", {time: time(state.lastSync, true)})}` : t("noSync");
    $("#newTaskSidebar").disabled = !state.connected;
    $("#quickRun").disabled = !state.connected || state.creating;
    $("#quickRun span").textContent = t(state.creating ? "saving" : "runIn30");
}

function renderFilters() {
    replaceContent($("#statusFilters"), statuses.map((status) => `<button class="filter" data-action="filter" data-filter="${status}" aria-pressed="${state.filter === status}"><span class="status-dot ${statusClass(status)}"></span><span>${t(status)}</span><span class="filter-count">${state.loaded ? state.tasks.filter((task) => status === "ALL" || task.status === status).length : "—"}</span></button>`).join(""));
    const next = state.tasks.filter((task) => task.status === "PENDING").sort((a, b) => new Date(a.runAt) - new Date(b.runAt))[0];
    replaceContent($("#nextTask"), next ? `<button class="next-task-button" data-action="task" data-id="${escapeHtml(next.id)}"><span class="next-date">${icon("clock")}${escapeHtml(format(next.runAt, {day: "numeric", month: "short"}))} · ${time(next.runAt)}</span><strong>${escapeHtml(taskTitle(next))}</strong><small>${t("PENDING")}</small></button>` : `<p>${t(state.loaded ? "noUpcoming" : "noSync")}</p>`);
}

function renderCalendar() {
    $("#monthHeading").textContent = format(state.month, {month: "long", year: "numeric"});
    $("#weekdays").innerHTML = Array.from({length: 7}, (_, i) => `<span>${format(new Date(2024, 0, 1 + i), {weekday: "short"})}</span>`).join("");
    const first = new Date(state.month);
    const leadingDays = (first.getDay() + 6) % 7;
    const daysInMonth = new Date(first.getFullYear(), first.getMonth() + 1, 0).getDate();
    const cells = Math.ceil((leadingDays + daysInMonth) / 7) * 7;
    first.setDate(1 - leadingDays);
    const today = dayKey(new Date());
    let html = "";
    for (let i = 0; i < cells; i++) {
        const date = new Date(first);
        date.setDate(first.getDate() + i);
        const key = dayKey(date);
        const tasks = tasksOnDay(key);
        const label = `${fullDate(date)}, ${t(tasks.length === 1 ? "oneTask" : "taskCount", {count: tasks.length})}`;
        const classes = ["day-cell", i % 7 > 4 && "weekend", date.getMonth() !== state.month.getMonth() && "outside", key === state.day && "selected", key === today && "is-today"].filter(Boolean).join(" ");
        html += `<div class="${classes}" data-day-cell="${key}"><div class="day-header">${key >= today && state.connected ? `<button class="day-add" data-action="create" data-day="${key}" aria-label="${escapeHtml(t("newTask") + ": " + fullDate(date))}">${icon("plus")}</button>` : ""}<button class="day-number" data-action="day" data-day="${key}" aria-label="${escapeHtml(label)}" aria-pressed="${state.day === key}" ${key === today ? 'aria-current="date"' : ""}>${date.getDate()}</button></div><div class="day-events">${tasks.slice(0, 2).map((task) => `<button class="event-chip ${statusClass(task.status)}" data-action="task" data-id="${escapeHtml(task.id)}" title="${escapeHtml(taskTitle(task) + " · " + t(task.status))}"><span class="event-time">${time(scheduledAt(task))}</span><span class="event-name">${escapeHtml(taskTitle(task))}</span><span class="sr-only"> · ${t(task.status)}</span></button>`).join("")}${tasks.length > 2 ? `<button class="event-overflow" data-action="day" data-day="${key}">${t("more", {count: tasks.length - 2})}</button>` : ""}</div><div class="mobile-dots" aria-hidden="true">${tasks.slice(0, 6).map((task) => `<span class="status-dot ${statusClass(task.status)}"></span>`).join("")}</div></div>`;
    }
    replaceContent($("#calendarGrid"), html);
    $("#calendarGrid").setAttribute("aria-label", t("calendar"));
    $("#calendarHint").textContent = t("calendarHint");
    $("#timezone").textContent = zone;
}

function renderDay() {
    const tasks = tasksOnDay(state.day);
    const canCreate = state.connected && state.day >= dayKey(new Date());
    let body;
    if (tasks.length) {
        body = `<div class="day-task-list">${tasks.map((task) => `<button class="day-task" data-action="task" data-id="${escapeHtml(task.id)}"><time datetime="${escapeHtml(scheduledAt(task))}">${time(scheduledAt(task))}</time><span class="day-task-body"><strong>${escapeHtml(taskTitle(task))}</strong>${badge(task.status)}</span></button>`).join("")}</div>`;
    } else {
        let heading = "noTasks", description = canCreate ? "noTasksText" : "noPastText";
        if (!state.loaded) { heading = state.connectionAttempted ? "offlineTitle" : "loading"; description = state.connectionAttempted ? "offlineText" : "loadingText"; }
        else if (state.filter !== "ALL") { heading = "noMatching"; description = "noMatchingText"; }
        body = `<div class="empty-state"><div class="empty-icon">${icon("calendar")}</div><h3>${t(heading)}</h3><p>${t(description)}</p>${canCreate ? `<button class="button button-outline" data-action="create" data-day="${state.day}">${icon("plus")}${t("newTask")}</button>` : ""}</div>`;
    }
    return `<div class="panel-topline"><p class="eyebrow">${t("selectedDay")}</p>${canCreate ? `<button class="icon-button" data-action="create" data-day="${state.day}" aria-label="${t("newTask")}">${icon("plus")}</button>` : ""}</div><h2 class="panel-date">${format(fromDay(state.day), {weekday: "short", day: "numeric", month: "long"})}</h2><p class="panel-caption">${state.loaded ? t(tasks.length === 1 ? "oneTask" : "taskCount", {count: tasks.length}) : "—"}</p>${body}`;
}

function renderHistory(task) {
    const data = history.get(task.id);
    let html = "";
    if (data?.error) html += `<p class="field-error">${t("historyError")} <button class="text-button" data-action="history">${t("retry")}</button></p>`;
    if (!data) return `<p class="history-empty">${t("loadingHistory")}</p>`;
    if (!data.runs.length) return html + (data.error ? "" : `<p class="history-empty">${t(task.status === "CANCELED" ? "canceledRuns" : ["PENDING", "RUNNING"].includes(task.status) ? "noRuns" : "noHistory")}</p>`);
    return html + `<ol class="attempt-list">${[...data.runs].sort((a, b) => b.attempt - a.attempt).map((run) => {
        const status = run.status === "SUCCESS" ? "done" : run.status === "FAILED" ? "failed" : "running";
        return `<li class="attempt"><span class="attempt-symbol ${status}">${icon(status === "done" ? "check" : status === "failed" ? "close" : "clock")}</span><div class="attempt-body"><strong>${t("attempt", {number: escapeHtml(run.attempt)})} · ${escapeHtml(t(run.status))}</strong><p>${t("startedAt", {time: escapeHtml(fullDate(run.startedAt) + " · " + time(run.startedAt, true))})}${run.finishedAt ? `<br>${t("finishedAt", {time: escapeHtml(fullDate(run.finishedAt) + " · " + time(run.finishedAt, true))})}` : ""}</p>${run.error ? `<p class="attempt-error">${escapeHtml(run.error)}</p>` : ""}</div></li>`;
    }).join("")}</ol>`;
}

function renderDetail() {
    const task = selectedTask();
    const panel = $("#detailPanel");
    panel.setAttribute("aria-label", t("taskDetails"));
    if (!task) { replaceContent(panel, renderDay()); return; }
    const notes = typeof task.payload?.description === "string" ? task.payload.description : "";
    const original = scheduledAt(task);
    const isRetry = task.status === "PENDING" && new Date(original).getTime() !== new Date(task.runAt).getTime();
    const payloadOpen = panel.querySelector("details")?.open;
    replaceContent(panel, `<button class="text-button" data-action="back">${icon("left")}${t("backToDay")}</button><h2 class="task-detail-title">${escapeHtml(taskTitle(task))}</h2>${badge(task.status)}${notes ? `<p class="task-notes">${escapeHtml(notes)}</p>` : ""}<dl class="detail-list"><div><dt>${t("scheduledFor")}</dt><dd>${fullDate(original)}<small>${time(original, true)}</small></dd></div><div><dt>${t("handler")}</dt><dd>${escapeHtml(task.type)}</dd></div><div><dt>${t("attempts")}</dt><dd>${escapeHtml(task.attempt)} / ${escapeHtml(task.maxAttempts)}</dd></div>${isRetry ? `<div><dt>${t("nextAttempt")}</dt><dd>${fullDate(task.runAt)}<small>${time(task.runAt, true)}</small></dd></div>` : ""}</dl>${["PENDING", "RUNNING"].includes(task.status) ? `<div class="countdown">${icon("clock")}<strong id="countdown"></strong></div>` : ""}${task.error ? `<p class="field-error">${escapeHtml(task.error)}</p>` : ""}<section class="history-section"><h3>${t("history")}</h3>${renderHistory(task)}</section><details class="payload-details" ${payloadOpen ? "open" : ""}><summary>${t("payload")}</summary><pre>${escapeHtml(JSON.stringify(task.payload, null, 2))}</pre></details><p class="task-id">ID ${escapeHtml(task.id)}</p>${task.status === "PENDING" ? `<button class="button button-danger cancel-task-button" data-action="cancel" ${!state.connected || state.canceling ? "disabled" : ""}>${t("cancelTask")}</button>` : ""}`);
    updateCountdown();
}

function updateCountdown() {
    const task = selectedTask(), target = $("#countdown");
    if (!task || !target) return;
    const seconds = Math.ceil((new Date(task.runAt).getTime() - Date.now()) / 1000);
    let remaining = `${Math.floor(seconds / 60)}:${pad(seconds % 60)}`;
    if (seconds >= 3600) remaining = `${Math.floor(seconds / 3600)}:${pad(Math.floor(seconds / 60) % 60)}:${pad(seconds % 60)}`;
    target.textContent = task.status === "RUNNING" ? t("runningNow") : seconds <= 0 ? t("waiting") : t("countdown", {time: remaining});
}

function render() { renderConnection(); renderFilters(); renderCalendar(); renderDetail(); }

function applyLanguage() {
    document.documentElement.lang = state.language;
    $("#language").value = state.language;
    $("#language").setAttribute("aria-label", t("language"));
    document.querySelectorAll("[data-i18n]").forEach((node) => { node.textContent = t(node.dataset.i18n); });
    document.querySelectorAll("[data-i18n-label]").forEach((node) => { node.setAttribute("aria-label", t(node.dataset.i18nLabel)); });
    document.querySelectorAll("[data-i18n-placeholder]").forEach((node) => { node.placeholder = t(node.dataset.i18nPlaceholder); });
    $("#pageDate").textContent = format(new Date(), {weekday: "long", day: "numeric", month: "long", year: "numeric"});
    $("#formTimezone").textContent = t("timezone", {zone});
    render();
}

function showToast(message, error = false) {
    clearTimeout(toastTimer);
    $("#toast").textContent = message;
    $("#toast").className = `toast${error ? " error" : ""}`;
    $("#toast").hidden = false;
    toastTimer = setTimeout(() => { $("#toast").hidden = true; }, error ? 8000 : 4500);
}

function showError(selector, message) { const node = $(selector); node.textContent = message; node.hidden = !message; }

function chooseDay(key) {
    state.day = key;
    state.month = monthOf(fromDay(key));
    state.selectedId = null;
    renderCalendar();
    renderDetail();
}

function chooseTask(id) {
    const task = state.tasks.find((item) => item.id === id);
    if (!task) return;
    state.selectedId = id;
    state.day = dayKey(new Date(scheduledAt(task)));
    state.month = monthOf(fromDay(state.day));
    renderCalendar();
    renderDetail();
    void loadHistory(id, true);
    revealDetail();
}

function revealDetail() {
    if (window.matchMedia("(max-width: 1000px)").matches) $("#detailPanel").scrollIntoView({behavior: "auto", block: "start"});
}

function openCreate(key = state.day) {
    if (!state.connected || state.creating) return;
    $("#createTaskForm").reset();
    showError("#formError", "");
    const earliest = new Date(Math.ceil((Date.now() + 60000) / 60000) * 60000);
    let date = fromDay(key);
    date.setHours(9, 0, 0, 0);
    if (date < earliest) date = new Date(Math.ceil((Date.now() + 300000) / 60000) * 60000);
    $("#runAtInput").min = localInput(earliest);
    $("#runAtInput").value = localInput(date);
    $("#createDialog").showModal();
    $("#taskTitle").focus();
}

function setCreating(busy) {
    state.creating = busy;
    $("#createTaskForm").querySelectorAll("input, textarea, button").forEach((node) => { node.disabled = busy; });
    $("#submitTask").textContent = t(busy ? "saving" : "schedule");
    renderConnection();
}

async function createTask(title, notes, runAt) {
    // scheduledAt preserves the calendar position if retry backoff moves runAt.
    const payload = {message: title, scheduledAt: runAt, ...(notes ? {description: notes} : {})};
    if (refreshRequest) await refreshRequest;
    const task = await request("/tasks", {method: "POST", headers: {"Content-Type": "application/json"}, body: JSON.stringify({type: "echo", payload, runAt, maxAttempts: 3})});
    state.tasks = [task, ...state.tasks.filter((item) => item.id !== task.id)];
    state.filter = "ALL";
    state.selectedId = task.id;
    state.day = dayKey(new Date(scheduledAt(task)));
    state.month = monthOf(fromDay(state.day));
    render();
    void loadHistory(task.id, true);
    showToast(t("created"));
    return task;
}

$("#createTaskForm").addEventListener("submit", async (event) => {
    event.preventDefault();
    if (state.creating) return;
    const data = new FormData(event.currentTarget);
    const title = String(data.get("title")).trim(), notes = String(data.get("notes")).trim();
    const date = new Date(String(data.get("runAt")));
    if (!title) { showError("#formError", t("titleRequired")); $("#taskTitle").focus(); return; }
    if (!Number.isFinite(date.getTime()) || date.getTime() <= Date.now() || localInput(date) !== data.get("runAt")) { showError("#formError", t("futureRequired")); $("#runAtInput").focus(); return; }
    showError("#formError", "");
    setCreating(true);
    try { await createTask(title, notes, date.toISOString()); $("#createDialog").close(); revealDetail(); }
    catch (error) { showError("#formError", error.message); }
    finally { setCreating(false); void refreshTasks(); }
});

async function quickRun() {
    if (state.creating || !state.connected) return;
    setCreating(true);
    const date = new Date(Date.now() + 30000);
    try { await createTask(`${t("quickTitle")} · ${time(date, true)}`, "", date.toISOString()); revealDetail(); }
    catch (error) { showToast(error.message, true); }
    finally { setCreating(false); void refreshTasks(); }
}

function openCancel() {
    if (selectedTask()?.status !== "PENDING" || !state.connected) return;
    state.cancelId = state.selectedId;
    showError("#cancelError", "");
    $("#cancelDialog").showModal();
    $("#cancelDialog [data-action=close-cancel]").focus();
}

$("#cancelTaskForm").addEventListener("submit", async (event) => {
    event.preventDefault();
    if (state.canceling || !state.cancelId) return;
    state.canceling = true;
    $("#cancelTaskForm").querySelectorAll("button").forEach((node) => { node.disabled = true; });
    $("#confirmCancel").textContent = t("canceling");
    showError("#cancelError", "");
    try {
        if (refreshRequest) await refreshRequest;
        await request(`/tasks/${encodeURIComponent(state.cancelId)}/cancel`, {method: "POST"});
        const task = state.tasks.find((item) => item.id === state.cancelId);
        if (task) task.status = "CANCELED";
        render();
        $("#cancelDialog").close();
        showToast(t("canceled"));
    } catch (error) { showError("#cancelError", error.status === 409 ? t("conflict") : error.message); }
    finally {
        state.canceling = false;
        $("#cancelTaskForm").querySelectorAll("button").forEach((node) => { node.disabled = false; });
        $("#confirmCancel").textContent = t("cancelTask");
        await refreshTasks();
    }
});

document.addEventListener("click", (event) => {
    const button = event.target.closest("[data-action]");
    if (!button) {
        const cell = event.target.closest("[data-day-cell]");
        if (cell) chooseDay(cell.dataset.dayCell);
        return;
    }
    if (button.disabled) return;
    const {action, day, id, filter} = button.dataset;
    switch (action) {
        case "create": openCreate(day || state.day); break;
        case "close-create": if (!state.creating) $("#createDialog").close(); break;
        case "close-cancel": if (!state.canceling) $("#cancelDialog").close(); break;
        case "quick": void quickRun(); break;
        case "prev": case "next": {
            state.month = new Date(state.month.getFullYear(), state.month.getMonth() + (action === "prev" ? -1 : 1), 1, 12);
            state.day = dayKey(state.month);
            state.selectedId = null;
            renderCalendar(); renderDetail(); break;
        }
        case "today": chooseDay(dayKey(new Date())); break;
        case "day": chooseDay(day); break;
        case "task": chooseTask(id); break;
        case "back": state.selectedId = null; renderDetail(); break;
        case "filter": state.filter = filter; state.selectedId = null; render(); break;
        case "cancel": openCancel(); break;
        case "history": void loadHistory(state.selectedId, true); break;
        case "refresh": void refreshTasks().then(() => loadHistory(state.selectedId, true)); break;
    }
});

$("#language").addEventListener("change", (event) => {
    state.language = event.target.value;
    try { localStorage.setItem("calendar-language", state.language); } catch { /* Storage is optional. */ }
    applyLanguage();
});
$("#createDialog").addEventListener("cancel", (event) => { if (state.creating) event.preventDefault(); });
$("#cancelDialog").addEventListener("cancel", (event) => { if (state.canceling) event.preventDefault(); });
document.addEventListener("visibilitychange", () => { if (!document.hidden) void poll(); });
window.addEventListener("online", () => { void refreshTasks(); });
setInterval(updateCountdown, 1000);
applyLanguage();
void poll();

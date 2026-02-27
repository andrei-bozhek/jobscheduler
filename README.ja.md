# Job Scheduler

Spring Boot + PostgreSQL で作った *小さなタスクスケジューラ*です。  
ポートフォリオとして、以下を意識して実装しました：

- API / Service / Repository の層構成
- Flyway による DB マイグレーション
- バリデーションと API 境界
- 競合安全なタスク取得（DB ロック / リース）
- リトライとバックオフ、実行履歴（attempts）の記録

---

## 技術スタック
- Java 21
- Spring Boot（Web / Validation / JDBC / Actuator）
- PostgreSQL 16
- Flyway

---

## ローカル実行

### 1) PostgreSQL 起動（Docker）

```bash
docker compose up -d
```

`docker-compose.yml` のデフォルト:
- DB: `job_scheduler`
- user: `job_user`
- password: `job_pass`
- host port: `5433` → container `5432`

> アプリは `localhost:5433` の Postgres に接続する想定です。  
> `application.yaml` が異なる場合は適宜調整してください。

### 2) アプリ起動

```bash
./gradlew bootRun
```

起動時に Flyway がマイグレーションを自動適用します。

### 3) (任意) ヘルスチェック

Actuator を有効にしている場合:
```bash
curl http://localhost:8080/actuator/health
```

---

## API

Base path: `/tasks`

### タスク作成
`POST /tasks`

Request（CreateTaskRequest）:
```json
{
  "type": "echo",
  "payload": { "message": "hello" },
  "runAt": "2026-02-20T10:20:00+09:00",
  "maxAttempts": 3
}
```

バリデーション（DTO/Controller レベル）:
- `type`：必須（空不可）
- `payload`：必須（JSON）
- `runAt`：必須
- `maxAttempts`：任意。指定する場合は `1..5`

Response（TaskResponse）:
```json
{
  "id": "2c7a9a1f-3ac8-4d2d-8f5f-6f2d6a5c7e2a",
  "type": "echo",
  "payload": "{\"message\":\"hello\"}",
  "status": "PENDING",
  "runAt": "2026-02-20T10:20:00+09:00",
  "attempt": 0,
  "maxAttempts": 3,
  "error": null
}
```

### 取得

`GET /tasks/{id}`  
TaskResponse を返します。

### 一覧

`GET /tasks?status=PENDING&limit=20&offset=0`

- `status`（任意）: `PENDING | RUNNING | DONE | FAILED | CANCELED`
- `limit`（デフォルト 20）: `1..100`
- `offset`（デフォルト 0）: `0以上`

### 実行履歴（attempts）

`GET /tasks/{id}/runs`  
TaskAttemptResponse の配列を返します。

### キャンセル

`POST /tasks/{id}/cancel`  
`PENDING` のみ可能。`204 No Content` を返します。

---

## DB スキーマ（概要）

### tasks
- `id`（UUID, PK）
- `type`（TEXT, NOT NULL）
- `payload`（JSONB, NOT NULL）
- `status`（TEXT, NOT NULL）: `PENDING/RUNNING/DONE/FAILED/CANCELED`
- `run_at`（TIMESTAMPTZ, NOT NULL）
- `attempt`（INT, default 0）
- `max_attempts`（INT, default 3）
- `error`（TEXT）
- `locked_by`（TEXT）
- `locked_until`（TIMESTAMPTZ）
- `created_at` / `updated_at`（TIMESTAMPTZ）

Index:
- `(status, run_at)`
- `(locked_until)`

Trigger:
- UPDATE 時に `updated_at` を自動更新します。

### task_attempts
- `id`（BIGSERIAL, PK）
- `task_id`（UUID, FK → tasks.id）
- `attempt`（INT）
- `started_at` / `finished_at`
- `status`（TEXT）
- `error`（TEXT）

Index:
- `(task_id, attempt)`

---

## ステータス
- `PENDING`   — 実行待ち
- `RUNNING`   — 実行中（ワーカーが取得済み）
- `DONE`      — 成功
- `FAILED`    — 失敗（リトライ上限到達）
- `CANCELED`  — キャンセル

---

## 競合制御（タスク取得）
複数ワーカー／複数インスタンスでも二重実行を避けるために、
DB の仕組み（行ロック）や `locked_by` / `locked_until` のリース情報を使って
タスク取得を競合安全にします。

---

## リトライ / バックオフ
失敗時:
- 残り回数があれば `PENDING` に戻して再実行
- 次の `runAt` は `(attempt * 5秒)` で後ろにずらす
- 各 attempt は `task_attempts` に記録

---

## 改善案
- actuator health status
- OpenAPI/Swagger UI の追加
- integration tests（Testcontainers）
- メトリクス追加（status別件数、成功率、処理時間など）
- RUNNING のリース切れ再キュー（ワーカー死亡対策）
- `type` ごとの handler

---
name: BillX Stack Setup
description: How the BillX POS full-stack is configured and runs in Replit
---

## Runtime
- Node.js 20 installed via `installLanguagePackages({ language: "nodejs", packages: [] })`
- Backend: Spring Boot on port 8080, started via `bash /home/runner/workspace/start.sh` (MySQL first)
- Frontend: Vite/React on port 5000, started via `cd Frontend && npm run dev`

## Workflows
- "Start application" → webview, port 5000, frontend
- "Backend API" → console, port 8080, backend

## Frontend proxy
- `/api` and `/auth` are proxied to `http://localhost:8080` in vite.config.ts
- No CORS issues since requests look same-origin from the browser

**Why:** Replit requires port 5000 for webview; backend must stay on 8080 for existing APIs.

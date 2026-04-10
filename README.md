# 📝 Markdown Notes

> A secure note-sharing application with HTML-rendered Markdown, featuring Policy-Based Access Control (PBAC) and OWASP-aligned security architecture.

---

## Overview

This project is a fullstack web application that allows users to create, manage, and share notes written in Markdown — rendered as HTML. The focus lies on applying modern security principles, including Policy-Based Access Control via Open Policy Agent and OWASP best practices throughout the entire stack.

---

## Features

- 📄 **Markdown Notes** – Create and edit notes with full Markdown support, rendered as HTML
- 🔐 **Policy-Based Access Control (PBAC)** – Fine-grained authorization powered by Open Policy Agent
- 🔗 **Note Sharing** – Share notes with other users with configurable access permissions
- 🛡️ **OWASP-Aligned Security** – Secure authentication flow with JWT + Refresh Token rotation, HttpOnly Cookies, Fingerprinting, and CSRF protection
- ✉️ **Email Verification** – Account registration includes email confirmation
- 🔑 **Password Reset** – Secure password reset via email link
- 💬 **Real-Time Chat** *(Coming Soon)* – Secure WebSocket-based messaging between users, following [OWASP WebSocket Security](https://cheatsheetseries.owasp.org/cheatsheets/WebSocket_Security_Cheat_Sheet.html)
- 👥 **Team Structure** *(Coming Soon)* – Organize users into teams with team-based resource access control via Open Policy Agent

---

## Technologies

![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![React](https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![Nginx](https://img.shields.io/badge/Nginx-009639?style=for-the-badge&logo=nginx&logoColor=white)
![Open Policy Agent](https://img.shields.io/badge/Open_Policy_Agent-7D3C98?style=for-the-badge&logo=openpolicyagent&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)

---

## Getting Started

### Prerequisites

Docker & Docker Compose installed

### 1. Configure environment variables

Copy the example file and fill in your values:

```bash
cd projekt
cp .env.example .env
```

```text
JWT_SECRET=your-secret-here
REFRESH_TOKEN_HMAC_SECRET=your-secret-here
POSTGRES_USER=your-user
POSTGRES_DB=your-db
POSTGRES_PASSWORD=your-password
MAIL_HOST=mailhog
MAIL_PORT=1025
```

> ⚠️ **Note:** The local database configuration is required for running backend integration and unit tests.

### 2. Start the development environment

```bash
docker compose -f docker-compose.dev.yml up --build
```

### 3. Access the application

| Service  | URL                   |
|----------|-----------------------|
| Backend  | http://localhost:8080 |
| Frontend | http://localhost:8090 |
| Mailhog  | http://localhost:8025 |

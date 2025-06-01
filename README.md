<h1 align="center">SchemaSync</h1>

<p align="center"><strong>Lightweight Database Schema Version Control Tool</strong></p>

<p align="center">
  <a href="https://github.com/jeevansingh901/schemasync/actions/workflows/build.yml">
    <img src="https://github.com/jeevansingh901/schemasync/actions/workflows/build.yml/badge.svg" alt="Build Status"/>
  </a>
  <a href="https://github.com/jeevansingh901/schemasync/blob/main/LICENSE">
    <img src="https://img.shields.io/github/license/jeevansingh901/schemasync" alt="License"/>
  </a>
  <a href="https://github.com/jeevansingh901/schemasync/releases">
    <img src="https://img.shields.io/github/v/release/jeevansingh901/schemasync" alt="Latest Release"/>
  </a>
  <a href="https://github.com/jeevansingh901/schemasync/stargazers">
    <img src="https://img.shields.io/github/stars/jeevansingh901/schemasync?style=social" alt="GitHub Stars"/>
  </a>
</p>

---

## 📖 Overview

**SchemaSync** is a lightweight, developer-friendly database schema version control tool inspired by Flyway and Liquibase. Built in Java, it enables seamless management and tracking of SQL migrations using versioned scripts.

> ⚠️ **Note:** Checksum validation is not yet implemented but is planned for future releases.

---

## 📸 SchemaSync Diagram

```text
┌────────────────────────────┐
│        Your App            │
│  ┌──────────────────────┐  │
│  │  MigrationTracker    │◄─┼── Calls from Java/CLI
│  └────────┬─────────────┘  │
│           │                │
│    ┌──────▼───────────┐    │
│    │ schema_version DB│    │
│    └──────────────────┘    │
└────────────────────────────┘

---
🚀 Features
✅ Schema version tracking

✅ Apply & rollback SQL migrations

✅ Idempotent migration execution

✅ Track applied migrations with timestamps

✅ In-memory & file-based SQLite support

✅ PostgreSQL compatibility via H2 test mode

❌ Checksum validation (coming soon)
---

🛠️ Getting Started

---
Prerequisites
Java 17 or higher
Gradle
----
📂 Folder Structure

schemasync/
├── src/
│   ├── main/java/com/schemasync/db/MigrationTracker.java
│   └── ...
├── test/java/com/schemasync/SchemaTrackerTest.java
├── migrations/               # SQL files e.g., V1__init.sql
├── schema_version.db         # SQLite tracking file
├── build.gradle
├── Dockerfile
└── README.md
---

⚙️ Installation & Usage
1. Clone the repository
git clone https://github.com/jeevansingh901/schemasync.git
cd schemasync
---
2. Build with Gradle
./gradlew build
---
3. Run Migration
./gradlew run --args="migrate"
---
4. Run Tests
./gradlew test
---
5. Migration Naming Convention
V1__init.sql  
V2__add_users_table.sql  

---
6. Run with Docker
docker build -t schemasync .
docker run --rm schemasync
---
📜 License
This project is licensed under the MIT License.
---
🙌 Contributing
Pull requests are welcome! For major changes, please open an issue first to discuss what you would like to change.
---
⭐️ Show your support
If you find this project useful, consider giving it a ⭐️ on GitHub!

---

✅ Now all badges will work properly if:
- You’ve pushed `build.yml`, a `LICENSE`, and at least one release.
- Repo is public at: [https://github.com/jeevansingh901/schemasync](https://github.com/jeevansingh901/schemasync)

Let me know if you'd like help setting up the `build.yml`, Dockerfile, or SQL migration 

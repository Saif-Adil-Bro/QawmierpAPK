# QawmiERP V2.0 — Native Android APK (`QawmierpAPK`)

Official native Android client (**Kotlin + Jetpack Compose + Material 3 + Retrofit + Hilt + Room**) for **QawmiERP V2.0** (`/api/mobile/*`).

## Phase 1 (MVP) Features Implemented
- **Authentication & Server Config**: Email / Student ID / Roll / Phone login, Madrasa registration, automatic `401` token refresh (`TokenAuthenticator`), `is_default_password` warning, and dynamic Server URL + `x-api-key` configuration.
- **Role-Based Unified Navigation**:
  - `super_admin` / management -> **Admin Dashboard** (KPIs, Attendance, Income/Expense, Universal Search)
  - `teacher` / `ustad` -> **Teacher Portal** (Assigned subjects, routines, classes)
  - `student` / `parent` -> **Student / Guardian Portal** (Multi-child switcher, attendance, Hifz/Kitab logs, fees, notices)
- **Offline-First Support**: EncryptedSharedPreferences for tokens/session, Room DB portal cache, and `WorkManager` (`AttendanceSyncWorker`) for offline bulk attendance sync.
- **Bilingual UI**: Bangla (`bn`) default with runtime English (`en`) toggle.
- **GitHub Actions CI/CD**: Automated unit tests, lint, and Debug APK build via `.github/workflows/android-ci.yml`.

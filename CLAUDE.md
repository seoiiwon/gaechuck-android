# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**gaechuck** is an Android app for Gyeongsang National University (경상국립대학교) students. It provides: lost items board (분실물), rental items (대여), business partnerships (제휴), student council notices (총학생회 공지), university notices (학교 공지), cafeteria menus (식당 메뉴), bus route info, club directory (동아리), and student account features (signup/login/mypage).

## Build & Run

### Prerequisites

`local.properties` must contain the server base URL:
```
sdk.dir=...
BASE_URL=http://<host>:<port>/
```
`BASE_URL` is injected via `BuildConfig.BASE_URL` at compile time (`app/build.gradle.kts`).

### Common Gradle commands (run from project root)

```bash
./gradlew assembleDebug        # build debug APK
./gradlew assembleRelease      # build release APK (minified)
./gradlew installDebug         # build + install on connected device
./gradlew test                 # run unit tests
./gradlew connectedAndroidTest # run instrumented tests
./gradlew lint                 # run lint checks
```

Use Android Studio's built-in run/debug for most development. Min SDK 24, target/compile SDK 35, Java 17 / JVM target 17.

## Architecture

**MVVM + Repository**, single-module Android app (`app/`).

### Layer structure

```
api/           → Retrofit singleton (ApiConnection), ApiService interface,
                 AuthManager (JWT token storage in SharedPreferences),
                 TokenAuthenticator (auto 401 → token refresh)
data/
  model/       → domain model data classes
  request/     → Retrofit @Body request data classes
  response/    → Retrofit response data classes
repository/    → one Repository per feature; calls ApiConnection.getRetrofitService
ui/
  <feature>/   → Activity + Main/Detail Fragments + ViewModel + adapter subpackage
  util/        → shared dialog fragments, decorations
```

### Feature modules under `ui/`

| Package | Feature |
|---------|---------|
| `splash` | Launcher activity; decides next screen (onboarding vs. main) |
| `onboarding` | First-run intro slides (ViewPager2), gated by `AppPreferences` |
| `auth` | Login/signup flow (`AuthActivity`, `auth/signup/*` steps: email → verify → info → complete) |
| `login` | Admin login (JWT) |
| `main` | Home dashboard (`MainActivity` at package root) + `ViewPagerAdapter` |
| `mypage` | Logged-in user profile view/edit |
| `bus` | Bus route |
| `business` | Business partnerships (제휴) |
| `club` | Club directory (동아리): list/gallery, detail, favorites, apply |
| `lose` | Lost & found (분실물) |
| `menu` | Cafeteria menu (식당 메뉴) |
| `noticecouncil` | Student council notices |
| `noticeuniv` | University notices |
| `rent` | Rental items (대여) |
| `setting` | App settings |
| `termsofuse` | Terms of use display |

### App entry flow

`SplashActivity` → `OnboardingActivity` (first run only, tracked by `AppPreferences.isOnboardingShown()`) → `MainActivity` (dashboard). `MainActivity` lives directly under the `gaechuck` root package (not `ui/main`) and is the hub other features are launched from; it is not itself nav-graph based. Login is optional for browsing — `MainActivity` routes to `MyPageActivity` or `LoginActivity` depending on `AuthManager.getToken()`.

### Navigation pattern

CRUD-style list features (`bus`, `business`, `club`, `lose`, `menu`, `noticecouncil`, `noticeuniv`, `rent`) each use a single **Activity** that hosts a `NavHostFragment`. Fragments (`MainFragment` → `DetailFragment`) are navigated via Jetpack **NavController**. The Activity owns the toolbar and handles back-press/etc-menu logic, delegating data operations to a **ViewModel** backed by a **Repository**. `auth`/`mypage`/`setting`/`splash`/`onboarding`/`termsofuse` are simpler flows using plain Activities/Fragments without a nav graph.

### API / Auth flow

- `Gaechuck.kt` (Application class) initialises `AuthManager` and `ApiConnection.create()` at startup.
- `ApiConnection` builds an OkHttp client with:
  - An auth interceptor that attaches `Authorization: Bearer <token>` to requests listed in `authRequiredPaths`.
  - `TokenAuthenticator` which intercepts 401 responses and transparently re-issues the access token using the stored refresh token (synchronised, max 3 retries).
- Tokens are stored in `SharedPreferences` via `AuthManager`.
- Admin-only write operations (post/patch/delete) require a logged-in session; public read endpoints do not.
- `AppPreferences` (also initialised in `Gaechuck.kt`) is a separate `SharedPreferences`-backed singleton for local app state (currently: onboarding-shown flag) — not to be confused with `AuthManager`, which holds auth tokens.

### Image upload pattern

Repositories convert `Uri` lists to `MultipartBody.Part` — handling both `content://` (local picker) and `http://` (existing server URLs by re-downloading to cache). The `data` part is a JSON `RequestBody` created with Gson.

## Key conventions

- **ViewBinding** is enabled; use `binding.*` rather than `findViewById` in new code.
- **Coroutines**: ViewModels launch coroutines via `viewModelScope`; repositories are `suspend` functions. Use `Dispatchers.IO` for network/file I/O, switch to `Dispatchers.Main` before posting to LiveData.
- **State**: UI state is `MutableLiveData` (list, detail, post/patch/delete result, login status). `StateFlow` is used for image selection state in some ViewModels.
- **ViewModel factories**: Each ViewModel that takes constructor args defines an inner `Factory`/`ViewModelFactory` class.
- **PR template**: Link to the resolved issue and list what was done (`.github/PULL_REQUEST_TEMPLETE.md`).

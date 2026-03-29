# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Ampflower is an Android music player client for [Ampache](https://ampache.org/) media servers. It allows users to browse, search, and play audio from self-hosted Ampache instances. Requires Android 12+ (API 31).

## Build Commands

```bash
# Build
./gradlew assembleDebug
./gradlew assembleRelease

# Test
./gradlew test                    # Unit tests (local JVM)
./gradlew connectedAndroidTest    # Instrumented tests (requires device/emulator)
./gradlew testDebug --tests FunctionTest  # Single test class

# Other
./gradlew build                   # Full build (includes lint)
./gradlew clean
```

## Project Structure

Two Gradle modules:
- **`app/`** — Main application (minSdk 31, compileSdk 35)
- **`utils/`** — Android library with 3 custom sliding-panel UI components (minSdk 23)

All application code is Java under `app/src/main/java/ar/com/strellis/ampflower/`.

## Architecture

MVVM with repository pattern and multi-layer data access:

```
Fragments/Activities → ViewModels → Repositories → DataSources (network/db/memory)
```

### Key packages

| Package | Purpose |
|---|---|
| `data/dao/` | Room DAOs for Song, Album, Artist, Playlist (+ remote pagination keys) |
| `data/datasource/db/` | Database interactors wrapping DAO access |
| `data/datasource/network/` | Retrofit paging sources and remote mediators (Paging 3) |
| `data/datasource/memory/` | In-memory cache interactors |
| `data/repository/` | Aggregates sources: `SongsRepository`, `AlbumsRepositoryRx`, `ArtistsRepositoryRx`, `PlaylistsRepositoryRx`, `FavoritesRepository` |
| `data/model/` | Domain models, Room entities, API response wrappers, remote keys |
| `service/` | `MediaPlayerService` (foreground, Media3/ExoPlayer), `AmpacheService` (Retrofit interface) |
| `broadcastreceiver/` | Hardware media buttons, app audio actions, network state |
| `ui/` | Fragments organized by feature (home, albums, artists, playlists, favorites, settings, etc.) |
| `viewmodel/` | One ViewModel per feature area |
| `eventbus/` | EventBus events: `AmpacheSessionExpiredEvent`, `RenewLoginEvent`, `PlayerPositionEvent` |

### Core patterns

- **Async**: RxJava3 throughout (repositories return `Observable`/`Single`/`Completable`); EventBus for cross-component events
- **Pagination**: Paging 3 with `RemoteMediator` (network + Room); `*PagingSourceRx` classes for direct network paging
- **Database**: Room v22, destructive migration on version change (dev mode), with junction tables for many-to-many relationships (AlbumSong, ArtistSong, PlaylistSong)
- **Media playback**: `MediaPlayerService` manages ExoPlayer (Media3 1.9.2), `MediaSession`, notifications via `PlayerNotificationManager`, and audio focus
- **Image loading**: Glide (primary) and Picasso; `glide-transformations` for rounded corners etc.

### Authentication flow

Ampache uses a time-based SHA-256 authentication handshake. Session tokens expire and trigger `AmpacheSessionExpiredEvent` → `RenewLoginEvent` → re-auth via `AmpacheService`. See `AmpacheUtil.java` and `LoginCallback.java`.

## Testing Notes

`FunctionTest.java` (instrumented) hits a real Ampache server at `192.168.1.5` — this is a local dev server and will only pass in the original developer's environment. Tests authenticate with the Ampache API and exercise all major endpoints in alphabetical order (`@FixMethodOrder(NAME_ASCENDING)`).

Room schema is at `app/schemas/` (auto-exported) — check this when changing database entities.
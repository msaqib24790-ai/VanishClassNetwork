# Vanish Class Network

An individual Android assignment prototype for short-lived class messaging.

## Project
- Package: `com.vanish.classnetwork`
- Language/UI: Kotlin, Jetpack Compose, Material 3
- Local persistence: Room
- Student identity: Preferences DataStore
- Connectivity: Android `ConnectivityManager` network callbacks

## Build and run
Open the project in Android Studio, then run:

```powershell
.\gradlew.bat testDebugUnitTest assembleDebug --no-configuration-cache
.\gradlew.bat installDebug
adb shell am start -n com.vanish.classnetwork/.MainActivity
```

The final test/build command completed successfully in the development environment (`BUILD SUCCESSFUL`).

## Individual implementation scope
- Welcome, identity, class join, chat, message details, connection dashboard, offline queue, settings, privacy, protocol, about and help screens.
- Student identity persistence using DataStore.
- Local message model and lifecycle states.
- Room-backed local message storage and repository.
- Local 30-second expiry behaviour and cleanup.
- Offline WAITING queue display.
- Duplicate local message ID protection using a unique class/message index and insert-ignore handling.
- Internet connectivity indicator using Android network callbacks.

## Verified manual behaviour
The developer reported successful emulator checks for identity persistence, queue visibility, 30-second local expiry, expiry after force-stop/relaunch, background expiry, online/offline indicator changes using Airplane Mode, and duplicate visibility checks. The configured unit-test task and debug build also completed successfully.

## Important limitations
This is the individual app stage, not the completed shared class messaging system:
- No approved class relay endpoint is configured.
- No actual cross-device message transmission or relay acknowledgements are implemented.
- `Online/Offline` indicates device internet availability, not relay reachability.
- Queue retry through a relay is not implemented.
- Current local expiry is prototype behaviour. Final expiry must be based on the relay's authoritative `sent_at`/`expires_at`.
- Server memory-only retention, server-side deletion, authentication and end-to-end delivery must be implemented and verified in the later class relay integration.

Do not present local emulator tests as proof of server-side privacy or cross-device delivery.

## Suggested next integration tests
1. Connect two devices to the approved class relay.
2. Verify protocol version and agreed message fields (`id`, `sender`, `text`, `sent_at`, `expires_at`).
3. Verify relay-defined 30-second expiry and deletion from relay memory.
4. Verify acknowledgements, reconnect behaviour and duplicate suppression.
5. Queue a message offline, reconnect after its expiry, and confirm it is dropped rather than transmitted.
6. Verify that message content is not written to relay logs or persistent server storage.

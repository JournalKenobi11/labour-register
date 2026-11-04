# Labour Register — Native Android Application

An offline, privacy-first, lightweight digital register designed for recording and managing labourers' details directly on Android devices.

---

## Features

- **100% Offline**: No accounts, login, backend servers, advertisements, or network permissions required.
- **Fast Data Entry (Add Labour)**:
  - 4 fields: **Labourer Name**, **Village**, **Daily Charge (₹)**, and **Mobile Number**.
  - All four fields are optional individually; validation prevents saving entirely empty records.
  - Automatically records creation and update timestamps.
  - Quick-entry workflow: automatically clears fields on save so multiple records can be added consecutively without leaving the screen.
- **Tabular Register View**:
  - Displays all saved records in a structured ledger with clean column dividers.
  - Shows live count of all saved labourers at the top.
  - Real-time search across **Name**, **Village**, and **Mobile Number** as you type.
  - Compact responsive layout: all 4 columns fit on screen in a single view with zero horizontal swiping required.
  - Multi-line word wrapping for names (e.g., surname wraps to next line).
- **In-Place Double-Tap Cell Editing**:
  - Double-tapping any table cell immediately opens an editor pre-populated with that field's value.
  - Automatically switches to the appropriate keyboard:
    - **Name**: Standard QWERTY text keyboard
    - **Village**: Standard QWERTY text keyboard
    - **Daily Charge**: Numeric decimal keyboard
    - **Mobile Number**: Numeric phone keypad
  - Changes persist immediately in the local SQLite database.
- **CSV Export**:
  - Exports every record using Android's Storage Access Framework (`ACTION_CREATE_DOCUMENT`).
  - RFC 4180 compliant: properly escapes commas, quotes, and newlines.
  - Includes UTF-8 BOM (`0xEF, 0xBB, 0xBF`) for seamless opening in Microsoft Excel.
  - Exports all records even when a search query is active.

---

## Tech Stack & Architecture

- **Language**: Kotlin 1.9.23
- **UI Toolkit**: Jetpack Compose with Material 3 (Compose BOM 2024.04.01)
- **Architecture**: Single Activity, MVVM (Model-View-ViewModel) pattern
- **Database**: Room Database 2.6.1 with SQLite (Integer paise storage for INR currency)
- **Concurrency & State**: Kotlin Coroutines & `StateFlow`
- **Build System**: Gradle 8.7 with Android Gradle Plugin 8.3.2 and KSP 1.9.23-1.0.20
- **SDK Target**: Min SDK 26 (Android 8.0), Target SDK 34 (Android 14)

---

## Project Structure

```
app/
 └── src/
      ├── main/
      │    ├── AndroidManifest.xml
      │    ├── java/com/labourregister/app/
      │    │    ├── LabourApplication.kt
      │    │    ├── MainActivity.kt
      │    │    ├── data/
      │    │    │    ├── LabourerEntity.kt
      │    │    │    ├── LabourerDao.kt
      │    │    │    ├── LabourDatabase.kt
      │    │    │    └── LabourRepository.kt
      │    │    ├── export/
      │    │    │    └── CsvExporter.kt
      │    │    ├── ui/
      │    │    │    ├── components/
      │    │    │    │    ├── EditCellDialog.kt
      │    │    │    │    ├── LabourInputField.kt
      │    │    │    │    └── LabourTable.kt
      │    │    │    ├── navigation/
      │    │    │    │    ├── AppBottomBar.kt
      │    │    │    │    └── Screen.kt
      │    │    │    ├── screens/
      │    │    │    │    ├── AddLabourScreen.kt
      │    │    │    │    └── LabourRegisterScreen.kt
      │    │    │    └── theme/
      │    │    │         ├── Color.kt
      │    │    │         ├── Theme.kt
      │    │    │         └── Type.kt
      │    │    └── viewmodel/
      │    │         └── LabourViewModel.kt
      │    └── res/
      └── test/
           └── java/com/labourregister/app/
                ├── CsvExporterTest.kt
                └── LabourerEntityTest.kt
```

---

## Building and Installing

### 1. Build the Debug APK
```bash
./gradlew assembleDebug
```
The output APK is generated at:
`app/build/outputs/apk/debug/app-debug.apk`

### 2. Run Unit Tests
```bash
./gradlew testDebugUnitTest
```

### 3. Install on Connected Device via ADB
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```
Or stream directly to `/data/local/tmp`:
```bash
adb push app/build/outputs/apk/debug/app-debug.apk /data/local/tmp/labour-register.apk
adb shell pm install -r -t /data/local/tmp/labour-register.apk
```
Launch the app:
```bash
adb shell am start -n com.labourregister.app.debug/com.labourregister.app.MainActivity
```

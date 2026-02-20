# Tasks - Desktop To-Do App (JavaFX)

A desktop to-do application built with JavaFX, and Gradle.

This project is a modular Java app (`org.example.todo`) with a styled JavaFX UI and CSS, task persistence using JSON, and unit tests for core task and repository behavior.

## Implemented Features

- Add tasks (blank input is ignored)
- Mark tasks complete/incomplete
- Delete tasks
- Clear all completed tasks in one click
- Live progress/status counters:
  - `X of Y completed`
  - Active count
  - Completed count
- Persistent storage across runs
- Error alerts when load/save fails

## Storage

Tasks are stored locally as JSON at:

`~/.todo-desktop-java/tasks.json`

The app creates the directory automatically if it does not exist.

## Tech Stack

- Java (toolchain target: 23)
- JavaFX (`javafx.controls`, version `23.0.2`)
- Jackson Databind (`2.20.0`) for JSON persistence
- Gradle Wrapper (`9.2.1`)
- JUnit 5 (`5.12.1`)
- `org.beryx.jlink` plugin for runtime image and installer packaging

## Project Structure

- `/app/src/main/java/org/example/Main.java`: JavaFX application UI and interactions
- `/app/src/main/java/org/example/TaskService.java`: task domain operations
- `/app/src/main/java/org/example/TaskRepository.java`: JSON load/save
- `/app/src/main/resources/styles/app.css`: application styling
- `/app/src/test/java/org/example/*`: unit tests

## Run the App

From the repository root:

```bash
./gradlew :app:run
```

## Run Tests

From the repository root:

```bash
./gradlew test
```

## Build/Package

Create a modular runtime image:

```bash
./gradlew :app:jlink
```

Create an installer image/package with `jpackage`:

```bash
./gradlew :app:jpackage
```

Current `jpackage` config is set to:

- app name: `Tasks`
- version: `1.0.0`
- vendor: `Arpit`
- installer type: `dmg` (macOS)
- icon: `app/src/main/resources/icons/Tasks.icns`

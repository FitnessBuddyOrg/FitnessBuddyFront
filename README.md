# FitnessBuddy 🏋️‍♂️

FitnessBuddy is a comprehensive fitness application designed to help users track their workouts, monitor their progress, and stay motivated. The app includes features such as exercise tracking, routine management, user authentication, and detailed statistics.

## Table of Contents 📑

- [Features](#features)
- [Installation](#installation)
- [Usage](#usage)
- [Architecture](#architecture)
- [Dependencies](#dependencies)
- [Contributing](#contributing)
- [License](#license)

## Features ✨

- **User Authentication** 🔒: Secure login and registration using email and Google OAuth.
- **Exercise Tracking** 🏃‍♂️: Add, edit, and view exercises with detailed instructions and video links.
- **Routine Management** 📋: Create and manage workout routines, including adding exercises to routines.
- **Statistics** 📊: View detailed statistics on app usage and completed routines.
- **Profile Management** 👤: Update user profile information and profile picture.
- **Notifications** 🔔: Receive notifications for routine reminders and app usage.

## Installation 🛠️

### Prerequisites 📋

- Android Studio
- Kotlin 1.8
- Java 1.8
- Gradle 8.0+

### Steps 🚀

1. Clone the repository

2. Open the project in Android Studio.

3. Sync the project with Gradle files.

4. Build and run the project on an emulator or physical device.

## Usage 📱

### User Authentication 🔑

- **Login**: Users can log in using their email and password, Google account or Github account.
- **Register**: New users can register by providing their email, password.

### Exercise Management 🏋️

- **Add Exercise**: Users can add new exercises with details such as name, instructions, video link, and category.
- **Edit Exercise**: Users can edit existing exercises.
- **View Exercise**: Users can view detailed information about an exercise.

### Routine Management 📅

- **Create Routine**: Users can create new workout routines.
- **Add Exercises to Routine**: Users can add exercises to their routines.
- **View Routine**: Users can view and manage their routines.
- **Edit Routine**: Users can edit existing routines.
- **Delete Routine**: Users can delete routines.
- **Start Routine**: Users can start a routine and track their progress.
- **Complete Routine**: Users can mark a routine as completed.
- **Routine Notifications**: Users can receive notifications when they are on a routine.

### Statistics 📈

- **App Usage**: View statistics on app opens and time spent on the app.
- **Completed Routines**: View statistics on completed workout routines.
- **Admin Statistics**: Admin users can view detailed statistics on all app usage and completed routines.

### Profile Management 🧑‍💼

- **Update Profile**: Users can update their profile information and profile picture.

## Architecture 🏗️

The project follows the MVVM (Model-View-ViewModel) architecture pattern, ensuring a clear separation of concerns and making the codebase more maintainable.

### Key Components 🔑

- **ViewModel**: Manages UI-related data and handles business logic.
- **Repository**: Manages data operations and provides a clean API for data access.
- **Retrofit**: Used for network operations and API calls.
- **Room**: Used for local database operations.

## Dependencies 📦

- **AndroidX**: Core libraries for Android development.
- **Compose**: Jetpack Compose for building UI.
- **Hilt**: Dependency injection.
- **Retrofit**: HTTP client for API calls.
- **Room**: Persistence library for local database.
- **MPAndroidChart**: Charting library for displaying statistics.
- **Coil**: Image loading library.
- **UCrop**: Image cropping library.

## Contributing 🤝

Contributions are welcome! Please follow these steps to contribute:

1. Fork the repository.
2. Create a new branch (`git checkout -b feature/your-feature`).
3. Commit your changes (`git commit -m 'Add some feature'`).
4. Push to the branch (`git push origin feature/your-feature`).
5. Open a pull request.
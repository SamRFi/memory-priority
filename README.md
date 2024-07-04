# Memory Priority

Memory Priority is a simple JavaFX application designed to help you memorize and prioritize sets of key-value pairs. Each memory set can be ordered by priority and rehearsed as needed. Upon rehearsal, the rehearse timestamp updates, and other memory sets within the same priority level take precedence.

## Features

- **Priority-Based Memorization**: Organize memory sets by priority to ensure the most important sets are rehearsed first.
- **Rehearsal Tracking**: Automatically updates the timestamp upon rehearsal, reordering sets within the same priority level.
- **Review Functionality**: The "Review All Memory Sets" button selects the most imminent memory set for rehearsal based on priority level and the last rehearsal date. Continuously click this button to review all memory sets in the correct order according to their defined priorities.
- **Offline Functionality**: Operates fully offline with data stored in simple text files, offering great flexibility and control over your memorization data.

## Data Management

Memory Priority stores data in text files, where each file corresponds to a user profile. Within these profile files, memory sets are saved with their relevant data. As long as the file format is respected, you can freely adjust the content to suit your needs.

### Data File Structure

- Each profile is represented by a separate text file.
- Memory sets within a profile are stored in the profile’s text file.
- Files must follow a specific pattern for parsing.

This structure ensures your data is easily manageable and highly flexible.

The Data files are located in the data directory, which you can find in the root of the project.

## Installation

To run the Memory Priority application, follow these steps:

### Prerequisites

Ensure you have the following software installed on your system:

1. **Java Development Kit (JDK)**
   - Download and install JDK 17 from the official Oracle website or OpenJDK.
   - Ensure the `JAVA_HOME` environment variable is set to the JDK installation directory.

2. **Gradle**
   - Download and install Gradle from the official Gradle website.
   - Alternatively, use the Gradle wrapper included in the project (`./gradlew`), which does not require a separate Gradle installation.

3. **Git**
   - Download and install Git from the official Git website.

### Steps to Run the Application

1. **Clone the Repository**
   Open your terminal or command prompt and run the following command:
   ```bash
   git clone https://github.com/SamRFi/memory-priority.git
   ```

2. **Navigate to the Project Directory**
   Change to the project directory:
   ```bash
   cd memory-priority
   ```

3. **Build the Project**
   If you are using the Gradle wrapper, run:
   ```bash
   ./gradlew build
   ```
   If you have Gradle installed on your system, you can alternatively run:
   ```bash
   gradle build
   ```

4. **Run the Application**
   To start the application, use the following command:
   ```bash
   ./gradlew run
   ```

### Additional Configuration

- **JavaFX SDK**: Ensure the JavaFX SDK is properly configured in the `build.gradle` file. The dependencies should be specified as follows:
  ```groovy
  javafx {
      version = "18"
      modules = [ 'javafx.controls', 'javafx.fxml']
  }
  ```

### Troubleshooting

If you encounter any issues during the installation or running of the application, ensure the following:

- Your `JAVA_HOME` environment variable is set to the JDK installation directory.
- The `gradlew` script has execute permissions. You can set this by running:
  ```bash
  chmod +x gradlew
  ```
- Check for any errors or messages in the terminal and resolve dependencies accordingly.

## Known Bugs
- If you add duplicate key-value pairs, identical keys, or identical values to a memory set, the program will crash when you attempt to rehearse that set.

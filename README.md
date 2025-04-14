# Course Evaluation System

A comprehensive course management system for educational institutions with role-based access control.

## Features

- **Multi-Role Support**
  - Student: Course enrollment, timetable management, auto-generate timetable
  - Instructor: Course management, student evaluation
  - Admin: User management, course management, system settings

- **Course Management**
  - Add/Edit/Delete courses
  - Set course capacity and schedules
  - Track enrollments
  - Automatic timetable generation

- **User Management**
  - Role-based user accounts
  - Password management
  - Department and specialization tracking

- **Timetable Features**
  - Visual timetable display
  - Auto-generate conflict-free timetables
  - Export timetable functionality

## Getting Started

1. **Login**
   - Use your credentials to log in
   - Select appropriate role (Student/Instructor/Admin)
   - Press Enter or click Login to proceed

2. **Default Accounts**
   - Admin: username=admin1, password=admin123
   - Student: username=student1, password=password123
   - Instructor: username=instructor1, password=password456

3. **Course Management**
   - Students can enroll in available courses
   - View course details and schedules
   - Generate automatic timetables based on preferences

## System Requirements

- Java 11 or higher
- Maven for building the project
- Minimum 4GB RAM recommended

## Building and Running

```bash
# Build the project
mvn clean install

# Run the application
mvn exec:java -Dexec.mainClass="com.courseevaluation.main.CourseEvaluationSystem"
```

## Project Structure

```
src/
├── main/
│   └── java/
│       └── com/
│           └── courseevaluation/
│               ├── data/         # Data management classes
│               ├── gui/          # User interface components
│               ├── models/       # Core domain models
│               ├── utils/        # Utility classes
│               └── main/         # Main application class
└── test/                        # Test classes
```

## Data Files

- `data/users.csv`: User account information
- `data/courses.csv`: Course details and schedules
- `data/enrollments.csv`: Course enrollment records 
# Course Evaluation System

A Java GUI-based application for managing course enrollments and evaluations in an educational institution.

## Features

### Student Features
- View enrolled courses
- Search courses by name/code
- Add new courses to enrollment
- View course status and details

### Instructor Features
- View courses they teach
- View list of enrolled students per course
- Track enrollment status

### Admin Features
- Manage users (students/instructors)
- Manage courses
- View and manage enrollments
- System-wide statistics

## Project Structure

```
course-evaluation-system/
│
├── gui/                 // GUI components
│   ├── LoginPage.java
│   ├── StudentHome.java
│   ├── InstructorHome.java
│   └── AdminHome.java
│
├── users/              // User-related classes
│   ├── User.java
│   ├── Student.java
│   ├── Instructor.java
│   └── Admin.java
│
├── data/               // Data management
│   ├── UserDatabase.java
│   ├── CourseDatabase.java
│   └── EnrollmentDatabase.java
│
├── models/             // Data models
│   ├── Course.java
│   └── Enrollment.java
│
├── utils/              // Utility classes
│   ├── FileUtil.java
│   ├── Validator.java
│   └── DateTimeUtil.java
│
└── main/               // Application entry
    └── CourseEvaluationSystem.java
```

## Data Files

The system uses the following data files:

1. `users.txt` - Stores user information
   ```
   role,username,password,name,department
   ```

2. `courses.txt` - Stores course information
   ```
   courseCode,title,instructor,credits,schedule,enrolled/max,status
   ```

3. `student_classes.txt` - Stores enrollment information
   ```
   studentId,courseCode,enrollmentDate,status
   ```

## Getting Started

### Prerequisites
- Java JDK 11 or higher
- Maven (for building)

### Building the Project
```bash
mvn clean install
```

### Running the Application
```bash
java -jar target/course-evaluation-system-1.0.jar
```

## Default Credentials

For testing purposes, the following default accounts are available:

1. Student:
   - Username: 2022AAPS1234H
   - Password: password123

2. Instructor:
   - Username: shashwat
   - Password: password456

3. Admin:
   - Username: admin
   - Password: password789

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details. 
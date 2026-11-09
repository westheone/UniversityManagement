# University Management System

Requires **JDK 17 or newer**. No external libraries or build tools are needed.

## Run

On Windows, double-click `Run.bat`. Alternatively, from this folder:

```text
javac -d build src/*.java
java -cp build UniversityApp
```

## Sample logins

| Last name | Student ID | Level | Starting GPA |
|---|---|---|---|
| Morgan | 1001 | UG | 2.67 |
| Lee | 1002 | PhD | 3.60 |

Last names ignore capitalization and surrounding spaces. IDs must match exactly.
Create an MS student to demonstrate middle-level permissions. New IDs start at 1003.

## Behavior

- All signup fields are required; profile information and student level remain fixed.
- The catalog contains 10 UG entries, 3 MS entries (including the project), and 3 PhD entries (including the dissertation).
- Students see entries at their own level and below.
- Register or retake a selected eligible entry; a confirmation lists name, ID, and all current registrations.
- My Record displays ungraded current courses, completed attempts, and credit-weighted GPA.
- Only ungraded attempts can be dropped. Only F/Fail results permit retakes.
- A graded retake replaces the previous F in GPA. Until then, the F counts. Previous completed attempts stay visible.
- Projects and dissertations use Pass/Fail and never count toward GPA.
- Students cannot assign grades. Sample grades are initialized in `UniversitySystem`; the model's `assignGrade` method supports testing final-grade transitions.
- Logout preserves session data. Closing the app discards it; startup reloads samples.
- Login is a classroom record lookup, not password-based authentication.

## OOP design

`Student` is an abstract parent with private fields and controlled registration operations.
`UndergraduateStudent`, `MSStudent`, and `PhDStudent` each extend `Student` directly.
The GUI filters the catalog by calling the overridden `canRegisterFor` through a `Student` reference, demonstrating polymorphism.
`Course` describes a catalog entry; `Enrollment` represents one attempt.
`UniversitySystem` owns catalog data, student records, sequential IDs, and lookup.
`UniversityApp` handles Swing navigation. All UI work runs on Swing's event dispatch thread.

## Checks

Run `Test.bat`, or compile and run `java -cp build UniversitySystemTest`.
Checks cover eligibility, GPA weighting and replacement, repeated failures, Pass/Fail retakes,
duplicate blocking, dropping, grade validation, signup validation, IDs, lookup, and startup reset.

Manual demonstration: log in as Morgan, retake CS301, drop it, then log in as Lee and retake CS590.
Create an MS student, register a course, log out and back in, and confirm the registration remains.
Restart to restore sample data. GUI testing does not require entering final grades.

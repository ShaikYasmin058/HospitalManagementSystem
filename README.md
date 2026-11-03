# CarePoint -- Hospital Management System

A role-based Hospital Management System built with **Java, Spring Boot,
Thymeleaf, and a relational database**. CarePoint is being developed as
a practical, portfolio-ready web application for managing hospital
administration, patients, doctors, appointments, and medical
information.

> **Project status:** The admin dashboard and the core patient, doctor,
> and appointment workflows have been developed and tested during the
> current project work. Some advanced modules described below are
> planned or still need end-to-end verification. This README separates
> current functionality from planned enhancements so it does not claim
> unfinished features as complete.

------------------------------------------------------------------------

## Table of Contents

1.  [Project Overview](#1-project-overview)
2.  [Technology Stack](#2-technology-stack)
3.  [User Roles](#3-user-roles)
4.  [Modules and How They Work](#4-modules-and-how-they-work)
5.  [Dashboard Design](#5-dashboard-design)
6.  [Main Workflows](#6-main-workflows)
7.  [Project Structure](#7-project-structure)
8.  [Database and Data Safety](#8-database-and-data-safety)
9.  [Run the Project](#9-run-the-project)
10. [Testing Checklist](#10-testing-checklist)
11. [Planned Enhancements](#11-planned-enhancements)
12. [Future UI Direction](#12-future-ui-direction)

------------------------------------------------------------------------

## 1. Project Overview

CarePoint provides a central application for common hospital operations.

The system is intended to help authorized users:

-   Maintain patient information.
-   Maintain doctor profiles and availability.
-   Create and manage appointments.
-   View dashboard statistics and appointment analytics.
-   Access role-appropriate information.
-   Maintain medical records as the medical-record workflow is completed
    and verified.

The application uses a server-rendered web interface. Users interact
with pages in a browser; Spring Boot controllers receive requests,
services apply application logic, repositories communicate with the
database, and Thymeleaf renders the response.

## 2. Technology Stack

  -----------------------------------------------------------------------
  Layer                               Technologies
  ----------------------------------- -----------------------------------
  Backend                             Java, Spring Boot, Spring MVC

  Persistence                         Spring Data JPA, Hibernate

  Frontend                            HTML, CSS, JavaScript, Thymeleaf

  UI framework                        Bootstrap (where used by the
                                      templates)

  Charts                              JavaScript chart rendering in the
                                      dashboard

  Database                            H2 is used in the current
                                      development setup; MySQL can be
                                      used when configured

  Build                               Maven

  IDE                                 Visual Studio Code
  -----------------------------------------------------------------------

### Request flow

``` text
Browser
   |
   v
Thymeleaf page / HTML form
   |
   v
Spring MVC Controller
   |
   v
Service layer (HospitalService)
   |
   v
Spring Data JPA Repository
   |
   v
Database
```

A controller handles the web route, the service layer performs the
operation, and the repository reads or writes records. The controller
then returns a Thymeleaf template or redirects to another page.

## 3. User Roles

CarePoint is designed around four roles. The exact pages and permissions
for each role should be checked against the current security
configuration before deployment.

### 3.1 Admin

The administrator oversees the system.

Expected responsibilities:

-   View the main dashboard.
-   View patient, doctor, staff, and appointment totals.
-   Manage patient and doctor records.
-   View appointment activity and status summaries.
-   Access administrative reports and system-management pages as those
    modules are completed.

### 3.2 Doctor

The doctor role is intended for clinical work.

Expected responsibilities:

-   Sign in using a doctor account.
-   View appointments assigned to that doctor.
-   Review relevant patient information.
-   Record consultation details, diagnosis, and prescriptions when the
    clinical workflow is implemented.
-   Review medical history according to access permissions.

### 3.3 Receptionist

The receptionist role is intended for front-desk operations.

Expected responsibilities:

-   Register patients.
-   Find and update patient details.
-   Book appointments.
-   Check appointment information and doctor availability.
-   Assist with appointment check-in and status updates when supported.

### 3.4 Patient

The patient role is intended for self-service.

Expected responsibilities:

-   Register and sign in.
-   View their own profile.
-   Request or view appointments.
-   Review appointment history.
-   Cancel an eligible appointment.
-   View permitted medical records and prescriptions when that portal
    functionality is completed.

**Security note:** Each role must only be able to access the records and
actions it is authorized to use. Role-specific access should be tested
before the application is used with real patient information.

## 4. Modules and How They Work

### 4.1 Authentication and Accounts

**Purpose:** Identify the user and associate the session with an account
and role.

Typical flow:

1.  The user opens the login page.
2.  The user enters their credentials.
3.  The application checks the account.
4.  The user's role determines the appropriate access and destination.
5.  The user signs out when finished.

The project contains account and role concepts, including `ADMIN`,
`DOCTOR`, `RECEPTIONIST`, and `PATIENT`. Registration and login flows
exist in the project; verify password encoding, session handling,
logout, and authorization rules before production use.

### 4.2 Admin Dashboard

**Purpose:** Give administrators a quick overview of hospital activity.

The current dashboard work includes:

-   Total patients.
-   Total doctors.
-   Total staff (based on staff records in the current dashboard
    implementation).
-   Total appointments.
-   Today's appointments.
-   Scheduled appointments.
-   Completed appointments.
-   Cancelled appointments.
-   Daily appointment chart data.
-   Monthly appointment chart data.
-   Recent appointments.
-   Patient-name and doctor-name lookup maps for appointment display.

**How it works:**

1.  The dashboard controller requests data through `HospitalService`.
2.  The service retrieves patient, doctor, and appointment records.
3.  The controller calculates totals and status counts.
4.  Appointment dates are grouped for daily and monthly chart data.
5.  Patient and doctor names are associated with appointment IDs.
6.  The controller places the values in the model.
7.  Thymeleaf renders the dashboard and JavaScript draws the charts.

The `/dashboard` route is handled by `AdminController`. The
`DashboardController` handles `/` so the two controllers do not compete
for the same `/dashboard` route.

### 4.3 Patient Management

**Purpose:** Maintain patient information.

The patient model includes fields such as:

-   Patient ID.
-   Account ID (when linked to a login account).
-   Name.
-   Age.
-   Gender.
-   Phone.
-   Disease or presenting condition.

Typical workflow:

1.  An authorized user opens the patient registration page.
2.  The user enters patient information.
3.  The form submits to a controller.
4.  The service validates and saves the patient through the patient
    repository.
5.  The patient appears in the patient list.
6.  An authorized user can view or update the record; deletion should be
    restricted and handled carefully.

Patient CRUD pages and patient retrieval methods are part of the current
project. Test each operation and its validation before marking the
module fully complete.

### 4.4 Doctor Management

**Purpose:** Maintain doctor profiles and availability.

The doctor model includes fields such as:

-   Doctor ID.
-   Account ID (when linked to a login account).
-   Name.
-   Specialization.
-   Phone.
-   Email.
-   Photo.
-   Availability status.

Typical workflow:

1.  An authorized user opens the doctor registration page.
2.  The user enters the doctor's profile and contact information.
3.  The application saves the record.
4.  The doctor appears in the doctor list.
5.  Authorized users can view, edit, or remove a doctor record.
6.  Appointment booking can use the doctor ID and availability
    information.

Doctor CRUD and doctor lookup methods are present in the service layer.
Confirm how availability is updated and ensure doctors with existing
appointments are handled safely before deleting them.

### 4.5 Appointment Management

**Purpose:** Schedule and track patient visits with doctors.

The appointment model includes:

-   Appointment ID.
-   Patient ID.
-   Doctor ID.
-   Appointment date/time.
-   Status.

Statuses used by the dashboard include:

-   `Scheduled`
-   `Completed`
-   `Cancelled`

Typical workflow:

1.  An authorized user opens the booking form.
2.  The user selects a patient and doctor.
3.  The user chooses a date and time.
4.  The application checks whether the doctor is already booked for that
    time.
5.  The appointment is saved.
6.  The appointment appears in the appointment list and dashboard.
7.  The status can be updated or the appointment can be cancelled
    through the supported workflow.

The project contains appointment retrieval, saving, doctor-booking
checks, and cancellation methods. The current dashboard also calculates
today's and status-based appointment totals.

**Important:** Confirm that the booking check prevents overlapping
appointments, not just identical date/time strings. Also validate past
dates, required fields, and status transitions.

### 4.6 Medical Records

**Purpose:** Keep clinical information associated with a patient and
consultation.

The project contains a `MedicalRecord` model/repository/service workflow
and methods for retrieving records by doctor or patient and saving a
record.

A complete clinical workflow should:

1.  Allow an authorized doctor to open an eligible patient consultation.
2.  Display only the patient information needed for care.
3.  Record diagnosis, notes, treatment, and prescription details as
    supported by the model.
4.  Save the record with the correct patient and doctor association.
5.  Allow authorized users to review the record later.

The exact fields and screens should be checked in the current
`MedicalRecord` model and templates. Do not assume every prescription or
medical-history screen is complete until it has been tested.

### 4.7 Staff Management

**Purpose:** Maintain non-doctor staff accounts, such as receptionists.

The project has a `StaffRepository` and the dashboard displays a staff
count. The admin account repository also contains role information,
including the `RECEPTIONIST` role.

A complete staff module should support:

-   Creating staff accounts.
-   Assigning an appropriate role.
-   Viewing and updating staff details.
-   Disabling or deactivating access when staff leave.
-   Restricting staff actions based on role.

**Counting note:** The current dashboard implementations have used
different sources for the staff total. Confirm whether the application's
intended definition of "staff" is receptionist records, all staff
records, or another category, then use one consistent source throughout
the project.

### 4.8 Billing and Payments --- Planned

A full billing module can support:

-   Creating an invoice for a consultation or service.
-   Adding consultation, procedure, pharmacy, or room charges.
-   Calculating subtotal, tax, discount, and final amount.
-   Recording payment method and payment status.
-   Printing or downloading an invoice.
-   Viewing payment history.

This is a recommended enhancement, not confirmed as complete in the
current project.

### 4.9 Reports --- Planned / To Verify

Useful reports include:

-   Patient registration report.
-   Doctor and specialization report.
-   Appointment report by date range.
-   Appointment status report.
-   Revenue report after billing is implemented.

Reports should respect user roles and should not expose private medical
information unnecessarily.

### 4.10 Notifications --- Planned

Possible notifications:

-   Appointment booking confirmation.
-   Appointment reminder.
-   Appointment cancellation notice.
-   Doctor schedule changes.

Email or SMS integration requires a configured provider and should not
be described as active until tested.

## 5. Dashboard Design

The intended desktop dashboard design is inspired by the supplied
hospital-software reference image.

### 5.1 Layout

-   **Left sidebar:** Main navigation for Dashboard, Patients, Doctors,
    Appointments, Staff, Medical Records, Billing, and Reports.
-   **Top bar:** CarePoint branding, quick navigation, and signed-in
    user information.
-   **Main content:** Wide dashboard with grouped module cards and
    analytics.
-   **Responsive layout:** Desktop-first presentation that adapts to
    tablet and mobile widths.

### 5.2 Dashboard sections

**Administration**

-   Dashboard overview.
-   Register patient.
-   Patient list.
-   Appointment list.
-   Doctor and staff management.

**Clinical**

-   Doctor appointments.
-   Patient clinical details.
-   Medical records.
-   Diagnosis and prescriptions.

**Accounting**

-   Billing.
-   Payments.
-   Invoices.
-   Financial reports.

These sections describe the desired navigation and visual organization.
A shortcut should only be displayed as an active feature when its
corresponding page and backend workflow exist.

### 5.3 Charts

The current dashboard work prepares daily and monthly appointment chart
data and appointment-status counts.

Recommended visualizations:

-   Daily appointments: bar or line chart.
-   Monthly appointments: bar chart.
-   Appointment status: doughnut chart.
-   Revenue trends: chart after billing is implemented.

Charts must use actual database values. Avoid hard-coded sample numbers
in the live dashboard.

## 6. Main Workflows

### 6.1 Register a patient

``` text
Open patient registration
        |
Enter patient details
        |
Submit form
        |
Controller receives request
        |
HospitalService saves patient
        |
PatientRepository stores record
        |
Show success / patient list
```

### 6.2 Book an appointment

``` text
Open appointment form
        |
Select patient and doctor
        |
Choose appointment date and time
        |
Validate input and doctor availability
        |
Save appointment
        |
Show appointment in list and dashboard
```

### 6.3 Doctor consultation (target workflow)

``` text
Doctor signs in
        |
View assigned appointments
        |
Open patient consultation
        |
Review permitted patient history
        |
Enter diagnosis and treatment
        |
Save medical record
        |
Patient record is updated
```

### 6.4 Patient self-service (target workflow)

``` text
Patient signs in
        |
View personal dashboard
        |
Request / view appointment
        |
Review appointment status
        |
View permitted records and prescriptions
```

## 7. Project Structure

The exact project may contain additional files, but the main Spring Boot
structure follows this pattern:

``` text
HospitalManagementSystem/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/hospital/management/
│   │   │       ├── controller/
│   │   │       ├── model/
│   │   │       ├── repository/
│   │   │       ├── service/
│   │   │       └── HospitalManagementApplication.java
│   │   └── resources/
│   │       ├── templates/
│   │       │   ├── dashboard.html
│   │       │   ├── layout.html
│   │       │   └── other page templates
│   │       ├── static/
│   │       │   ├── css/
│   │       │   ├── js/
│   │       │   └── images/
│   │       └── application.properties
│   └── test/
├── pom.xml
└── README.md
```

### Main code responsibilities

  -----------------------------------------------------------------------
  Package                             Responsibility
  ----------------------------------- -----------------------------------
  `controller`                        Handles browser routes, form
                                      submissions, and model attributes

  `model`                             Defines entities such as Patient,
                                      Doctor, Appointment, and Admin

  `repository`                        Provides database access through
                                      Spring Data JPA

  `service`                           Contains business logic and
                                      coordinates repositories

  `templates`                         Thymeleaf HTML pages

  `static`                            CSS, JavaScript, and images

  `application.properties`            Application and database
                                      configuration
  -----------------------------------------------------------------------

### Important classes already discussed

-   `AdminController` -- admin dashboard route and dashboard data.
-   `DashboardController` -- root (`/`) dashboard route; `/dashboard` is
    handled by `AdminController`.
-   `HospitalService` -- patient, doctor, appointment, account, and
    medical-record operations.
-   `Patient`, `Doctor`, `Appointment`, `Admin` -- domain models.
-   `AdminRepository` -- admin account persistence.

## 8. Database and Data Safety

The current development setup uses H2. The database may be file-based,
depending on the active configuration.

Before changing database settings:

1.  Check `application.properties`.
2.  Confirm the active JDBC URL and database mode.
3.  Back up the database before schema or migration changes.
4.  Never delete the H2 database files just to fix a startup or code
    issue.
5.  Do not use a reset operation against a database containing records
    you want to keep.

For production, use a properly configured database such as MySQL,
migrations, regular backups, and access controls.

**Patient privacy:** Use fictional data during development. Do not
upload real patient details, credentials, or medical records to a public
repository.

## 9. Run the Project

### Prerequisites

-   Java JDK compatible with the project's Maven compiler configuration.
-   Maven.
-   Visual Studio Code or another Java IDE.
-   Project dependencies available through Maven.

### Open the project

Open the project folder in VS Code:

``` cmd
code .
```

Open the integrated terminal and move to the project root if needed:

``` cmd
cd "C:\Users\HP\Desktop\Java project\HospitalManagementSystem"
```

### Compile

If Maven is available on PATH:

``` cmd
mvn -DskipTests compile
```

### Run

``` cmd
mvn spring-boot:run
```

The project has also been run in the current development environment
using the installed VS Code Maven launcher directly, because the local
`mvn.cmd` launcher was not working. If the normal Maven commands do not
work, use the known working launcher command from the project's
development notes rather than deleting or reinstalling project files.

### Open in browser

``` text
http://localhost:8080/
http://localhost:8080/dashboard
```

The `/dashboard` route is served by `AdminController`. The root `/`
route is served by `DashboardController`.

Stop the running application from its terminal with `Ctrl + C`.

## 10. Testing Checklist

Use this checklist after changes. Test with development data and avoid
destructive actions on records you need.

### Dashboard

-   [ ] `/dashboard` opens without an HTTP error.
-   [ ] Patient, doctor, staff, and appointment totals display.
-   [ ] Today's appointment count is correct.
-   [ ] Scheduled, completed, and cancelled counts are correct.
-   [ ] Daily and monthly charts display database-derived values.
-   [ ] Recent appointments show the correct patient and doctor names.

### Navigation

-   [ ] Patient registration shortcut opens the patient form.
-   [ ] Doctor registration shortcut opens the doctor form.
-   [ ] Appointment shortcut opens the booking form.
-   [ ] Appointment list opens.
-   [ ] Sidebar links lead to the intended pages.
-   [ ] Login and logout work.

### Patient management

-   [ ] Create a patient.
-   [ ] View the patient list.
-   [ ] Open a patient detail page.
-   [ ] Edit a patient.
-   [ ] Confirm deletion behavior only with a disposable test record.

### Doctor management

-   [ ] Create a doctor.
-   [ ] View doctor list and profile.
-   [ ] Edit doctor information.
-   [ ] Check availability behavior.
-   [ ] Confirm safe handling of doctors with appointments.

### Appointment management

-   [ ] Book an appointment with valid details.
-   [ ] Verify the patient and doctor associations.
-   [ ] Check doctor availability and conflicting bookings.
-   [ ] Verify appointment date and status.
-   [ ] Test cancellation only with a disposable test appointment.

### Role and privacy checks

-   [ ] Admin can access only intended admin functions.
-   [ ] Doctor can access only permitted doctor and patient information.
-   [ ] Receptionist can access only permitted front-desk functions.
-   [ ] Patient can access only their own information.
-   [ ] Unauthenticated users cannot access protected pages.

## 11. Planned Enhancements

Recommended implementation order:

1.  Finish and polish the shared desktop layout and navigation.
2.  Complete role-specific dashboards for doctor, receptionist, and
    patient.
3.  Verify all patient, doctor, and appointment CRUD flows and
    validations.
4.  Complete medical records and prescription workflow.
5.  Add billing, payments, and printable invoices.
6.  Add date-filtered reports.
7.  Add email/SMS notifications if a provider is configured.
8.  Strengthen authorization, password handling, validation, logging,
    and automated tests.
9.  Prepare production database configuration, backups, and deployment
    documentation.

## 12. Future UI Direction

The desired CarePoint desktop interface should have:

-   A persistent left navigation sidebar.
-   A clean top header with logo and user menu.
-   Clearly grouped Administration, Clinical, and Accounting sections.
-   Consistent module shortcut cards with icons.
-   Summary statistic cards.
-   Readable daily/monthly appointment charts.
-   A status doughnut chart.
-   A recent appointments table.
-   Responsive behavior on smaller screens.

The reference image is a design inspiration. CarePoint should keep its
own branding, real project modules, and actual database-driven
information.

------------------------------------------------------------------------

## Project Summary

**CarePoint -- Hospital Management System** is a Java Spring Boot web
application designed to organize hospital administration and clinical
workflows. Its current development includes patient, doctor,
appointment, account, and dashboard functionality, with dashboard
statistics and appointment analytics. Medical records and staff-related
service support are present in the codebase and should be validated
through the relevant screens and role permissions. Billing, advanced
reports, notifications, and fully developed role-specific portals are
planned enhancements.

The goal is to build a maintainable, secure, easy-to-use hospital
management application with clear separation between controllers,
services, repositories, database entities, and the web interface.





database connection:
--------------------------------------------------------------------------------------

http://localhost:8080/h2-console/login.jsp?jsessionid=9194b35b29ef5acebac865f31a1f2c77
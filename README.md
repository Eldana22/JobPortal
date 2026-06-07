JobPortal - Android Job Search Application
 Overview

JobPortal is a comprehensive Android application that connects job seekers with employers. It provides a complete job marketplace platform with role-based access control for Job Seekers, Employers, and Administrators.
 Features
Authentication
- User Registration (Job Seeker / Employer)
- Secure Login with email/password
- Role-based navigation and permissions

Job Seeker Features
- Browse all available jobs
- Search jobs by title or company
- Save jobs to favorites
- Apply for jobs with cover letter and CV upload
- View saved jobs list
- Change password
- Help section with admin contact

Employer Features
- Post new job listings
- View only their own job postings
- Edit job details
- Delete job postings
- Close jobs (marks as "CLOSED" with visual badge)
- Change password

Admin Features
- Dashboard with system statistics
- View all users (Seekers & Employers)
- View all job postings
- View all job applications
- Delete any user or job
- Promote users to admin
- Demote admin users
- Change password

General Features
- Material Design UI
- Search/filter jobs
- Responsive layout
- Offline data storage with Room database

Tech Stack

| Technology | Purpose |
|------------|---------|
| Android Studio | IDE |
| Java | Programming language |
| Room Database | Local data persistence |
| RecyclerView | Scrollable job listings |
| CardView | Styled job cards |
| Material Design Components | UI components |
| ViewPager2 | Tab navigation in Admin |
| Activity Result API | File picker for CV upload |

 Minimum Requirements

- Android 7.0 (API Level 24) or higher
- 4GB RAM (8GB recommended)
- 500MB free storage space

Database Schema (7 Tables)

| Table | Purpose |
|-------|---------|
| users | User accounts (Seeker/Employer/Admin) |
| jobs | Job postings |
| applications | Job applications |
| categories | Job categories |
| saved_jobs | User's saved jobs |
| reviews | Job ratings and comments |
| skills | Job skills requirements |

Installation Guide

Prerequisites
- Android Studio Panda
- Android device with API 24+ or emulator

### Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/Eldana22/JobPortal.git

# User Stories: Smart Clinic Management System

This document outlines the user stories, role-based permissions, and acceptance criteria for the Smart Clinic Management System.

---

## 👥 User Roles & Permissions Matrix

| Role | Permissions | Restrictions |
| :--- | :--- | :--- |
| **Admin** | Manage users, view all appointments, register doctors, query doctor statistics. | Cannot issue prescriptions or alter patient medical files directly. |
| **Doctor** | View assigned appointments, update appointment status (Complete/Cancel), create prescriptions (NoSQL). | Cannot modify system-wide user credentials or other doctors' schedules. |
| **Patient**| Self-registration, view own profile, book appointments, view own prescriptions. | Cannot view other patients' information, stats, or register admin/doctor accounts. |

---

## 1. 🧑‍⚕️ Doctor User Stories

### Story 1: View Daily Schedule
* **As a** Doctor
* **I want to** view a list of my scheduled consultations for the day
* **So that** I can prepare for patient appointments and manage my time efficiently.
* **Acceptance Criteria**:
  - The Doctor Dashboard displays appointments sorted by date and time.
  - Each appointment includes the Patient's Name and the Reason for Visit.
  - Displays a status badge (e.g., `SCHEDULED`, `COMPLETED`).

### Story 2: Issue Digital Prescriptions
* **As a** Doctor
* **I want to** write a digital prescription containing symptoms, diagnosis, and medication details (saved in NoSQL MongoDB)
* **So that** patients can retrieve their treatment details instantly.
* **Acceptance Criteria**:
  - The Doctor can click a "Prescribe" button on active appointments.
  - A form allows inputting comma-separated symptoms, diagnosis, medication name, frequency, and duration.
  - Saving the prescription successfully inserts a document in MongoDB and transitions the appointment status to `COMPLETED`.

---

## 2. 🤒 Patient User Stories

### Story 1: Schedule Consultations
* **As a** Patient
* **I want to** book an appointment online with a doctor on a specific date and time
* **So that** I can schedule my clinic visit without calling the front desk.
* **Acceptance Criteria**:
  - A form allows selecting a specialist from a dropdown, choosing a preferred date/time, and entering a reason for visit.
  - Submitting the form saves the appointment in MySQL with status `SCHEDULED`.
  - The new appointment appears immediately on the patient's schedule view.

### Story 2: Access Medical & Prescription History
* **As a** Patient
* **I want to** view a history of all prescriptions issued to me by my doctors
* **So that** I can double-check medication instructions at home.
* **Acceptance Criteria**:
  - A dedicated "My Prescriptions" list displays all prescriptions containing the doctor's name, diagnosis, and instructions.
  - Patients can click "View" to open a detailed prescription slip modal.

---

## 3. ⚙️ Admin User Stories

### Story 1: Doctor and Patient Profile Management
* **As an** Admin
* **I want to** list all active doctors and scheduled appointments in the system
* **So that** I can oversee daily clinic operations and resource availability.
* **Acceptance Criteria**:
  - The Admin Dashboard features overview sections for active doctors and appointments.
  - Admin can view all entries without restrictions.

### Story 2: Monitor Clinic Performance
* **As an** Admin
* **I want to** view aggregate statistics showing the number of appointments handled by each doctor
* **So that** I can balance resource workloads and audit clinic performance.
* **Acceptance Criteria**:
  - The Admin panel invokes the `GetDoctorAppointmentStats` SQL stored procedure.
  - Displays visual summary cards showing each doctor's name, specialization, and total count of appointments.

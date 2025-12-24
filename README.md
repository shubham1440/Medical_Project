# 🏥 Gopal Hospital - MedicareX 
> A modern, web-based Medical Management System designed to bridge the gap between patients and healthcare providers.

[![Build Status](https://img.shields.io/badge/Maintained%3F-yes-green.svg)](https://github.com/shubham1440/Medical_Project)
[![Platform](https://img.shields.io/badge/platform-Web-blue.svg)](https://github.com/shubham1440/Medical_Project)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

---

## 📌 Project Overview
**MedicareX** is a comprehensive digital solution developed for **Gopal Hospital**. It replaces traditional paper-based systems with a streamlined digital workflow for managing patient registrations, doctor consultations, and hospital administration.

### 🎯 Objective
* To provide 24/7 online appointment booking for patients.
* To organize medical records and patient history for doctors.
* To automate billing and staff management for hospital administrators.

---

## 📸 Visual Tour

### 🌐 Landing Page & Access
*The gateway for patients to explore hospital services and access their accounts.*

| Home Page | Login & Sign-up |
| :---: | :---: |
| ![Home Page](screenshots/home.png) | ![Login Page](screenshots/login.png) |

### 👤 Patient & Provider Panels
*Seamless coordination between the person seeking care and the medical professional.*

| Patient Appointment Dashboard | Doctor Confirmation & Management |
| :---: | :---: |
| ![Patient Dashboard](screenshots/patient.png) | ![Doctor Panel](screenshots/doctor_dashboard1.png) |
| *Patients can book and view history.* | *Doctors confirm appointments & issue prescriptions.* |

## ✨ Core Features

### 👤 Patient Portal
* **One-Click Appointment:** Book visits based on symptoms or specialty.
* **Medical Vault:** View past prescriptions and lab reports online.
* **Profile Management:** Update personal and emergency contact details.

### 👨‍⚕️ Doctor Portal
* **Live Queue:** See the list of scheduled patients for the day.
* **Patient History:** Access longitudinal health records during consultation.
* **Digital Prescription:** Generate and print prescriptions instantly.

### 🛡️ Admin Dashboard
* **Staff Management:** Manage doctor shifts and nursing staff.
* **Inventory Control:** Monitor medicine stocks and hospital supplies.
* **Financial Reports:** Track daily/monthly revenue and billing status.

---

## 🛠️ Technical Stack

| Layer | Technology |
| :--- | :--- |
| **Frontend** | HTML5, CSS3, JavaScript, Bootstrap |
| **Backend** | PHP (Core) |
| **Database** | MySQL |
| **Server** | Apache (XAMPP / WAMP) |

---

## 📂 Project Structure

```text
medicarex/
├── assets/           # Images, logos, and UI icons
├── css/              # Custom stylesheets (Bootstrap & Custom)
├── js/               # Client-side validation & AJAX scripts
├── includes/         # DB connection & reusable components (header/footer)
├── patient/          # Patient-specific modules & dashboards
├── doctor/           # Doctor-specific modules & dashboards
├── admin/            # Administrative management tools
└── index.php         # Landing page for Gopal Hospital

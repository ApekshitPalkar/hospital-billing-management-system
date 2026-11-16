# Project Walkthrough (Short Interview Notes)

## 30-second explanation

“I developed an educational Java AWT hospital billing application. It collects fictional patient and billing details, validates them, calculates sample charges using `BigDecimal`, and saves patient and bill records in MariaDB through JDBC. I designed separate patient and bill tables with a foreign-key relationship, used prepared statements and a transaction for persistence, and added a screen to retrieve recent saved bills.”

## Architecture

1. **AWT UI (`ui`)** — collects input, handles button and keyboard events, and displays receipts and saved-bill summaries.
2. **Billing service (`service`)** — validates dates, age, contact number, days stayed, charges, discount and demonstration tax; calculates amounts.
3. **Bill model (`model`)** — immutable object carrying validated billing details.
4. **Database layer (`db`)** — reads local credentials, opens JDBC connections, and saves/retrieves records.
5. **MariaDB schema (`database/schema.sql`)** — contains `patients` and `bills`; each bill references a patient.

## Why `BigDecimal`?

Money should not rely on binary floating-point arithmetic. `BigDecimal` provides decimal arithmetic and explicit rounding to two decimal places (`HALF_UP`) for this educational calculator.

## Why prepared statements?

Prepared statements pass values separately from SQL structure. They help avoid building SQL by concatenating user input and reduce SQL injection risk for these database operations.

## Why a transaction?

Saving a bill can involve updating or inserting a patient row and inserting a bill row. A transaction makes those related operations succeed or roll back together if a database operation fails.

## What I personally brought to the project

The original project came from the student's academic work and ideas. A distinctive original usability requirement was Enter-key navigation between form fields. The student also supplied project requirements, configured the Fedora/MariaDB environment, created and loaded the schema, created a restricted application user, ran the application with fictional sample data, checked the expected bill calculation, and independently queried MariaDB to verify one saved patient and one saved bill. AI assistance was used for parts of the redesign, code organisation, implementation, and documentation; it should not be presented as entirely independently authored code.

## Important limitations to be transparent about

- The project is a desktop educational demo, not a production hospital system.
- Sample ward/procedure rates and the tax percentage are illustrative, not statutory or real hospital rates.
- Days stayed is manually entered; the system validates it but does not derive billable days from admission/discharge dates.
- It does not implement login, role permissions, encrypted patient fields, audit logs, backup/restore, or compliance certification.
- Use fictional data only.

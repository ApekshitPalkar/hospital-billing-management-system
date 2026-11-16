# Hospital Billing Management System (Educational Project)

A Java AWT desktop application that calculates **sample** hospital charges and saves fictional patient and billing records to a local MariaDB database through JDBC.

> **Educational-use only:** This is a student software project, not hospital-grade software. Do not use real patient data. It is not a real medical record system, does not calculate statutory GST, has not undergone clinical, financial, security, or regulatory audits, and must not be used for real billing or healthcare operations. Ward, procedure, and tax values are demonstration values only.

## Features

- Java AWT desktop interface with patient details and billing fields.
- Keyboard navigation: pressing **Enter** moves focus through the form, preserving the original usability idea.
- Validation of required fields, age, 10-digit contact number, dates, length of stay, monetary amounts, discount, and demonstration tax percentage.
- Strict `DD-MM-YYYY` date parsing; discharge date cannot precede admission date.
- `BigDecimal` monetary arithmetic with two-decimal rounding.
- Separate model, service, UI, and database/repository classes.
- JDBC persistence using prepared statements and a database transaction.
- Relational `patients` and `bills` tables linked by a foreign key.
- View the most recent saved bills from the desktop application.

## Technology

- Java 17 or later (the original development machine used OpenJDK 25)
- Java AWT
- JDBC
- MariaDB Server 11.x (manual end-to-end test used MariaDB 11.8.8)
- MariaDB Connector/J 4.0.0
- Apache Maven 3.9.x

## Project structure

```text
Hospital-Billing-Management-System/
├── .github/workflows/java-ci.yml
├── database/
│   └── schema.sql
├── docs/
│   └── TEST_PLAN.md
├── scripts/
│   └── run-validation-tests.sh
├── src/
│   ├── main/java/com/apekshit/hospital/
│   │   ├── HospitalBillingApp.java
│   │   ├── db/
│   │   ├── model/
│   │   ├── service/
│   │   └── ui/
│   └── test/java/com/apekshit/hospital/service/
│       └── BillingServiceValidationTest.java
├── database.properties.example
├── .gitignore
└── pom.xml
```

## Requirements

Install:

- JDK 17 or later
- Apache Maven 3.9 or later
- MariaDB Server 11.x

Check the tools:

```bash
java -version
javac -version
mvn -version
mariadb --version
systemctl is-active mariadb
```

On Fedora, start MariaDB and enable it at boot if the server package is already installed:

```bash
sudo systemctl enable --now mariadb
```

## Database setup

Run these commands from the project root (the folder containing `pom.xml`):

```bash
sudo mariadb < database/schema.sql
```

The schema creates a database named `hospital_billing_db` and two tables:

- `patients`: one record per unique patient code.
- `bills`: one bill per transaction, linked to a patient through a foreign key.

Create a dedicated local application account. Replace the password placeholder with a unique local password before executing the SQL:

```bash
sudo mariadb
```

```sql
CREATE USER 'hospital_app'@'localhost' IDENTIFIED BY 'REPLACE_WITH_A_UNIQUE_LOCAL_PASSWORD';
GRANT SELECT, INSERT, UPDATE ON hospital_billing_db.* TO 'hospital_app'@'localhost';
SHOW GRANTS FOR 'hospital_app'@'localhost';
EXIT;
```

Do not use the database administrator account from the application.

## Configure the Java connection

From the project root:

```bash
cp database.properties.example database.properties
nano database.properties
```

Set the local credentials:

```properties
db.url=jdbc:mariadb://127.0.0.1:3306/hospital_billing_db?sslMode=disable
db.user=hospital_app
db.password=YOUR_LOCAL_PASSWORD
```

Protect the local file:

```bash
chmod 600 database.properties
```

`database.properties` is ignored by Git. Never commit it, paste its contents into an issue, or share the database password. The URL above disables TLS only for a local loopback development connection; do not reuse that setting for a remote database. Environment variables can also be used instead:

```bash
export HOSPITAL_DB_URL='jdbc:mariadb://127.0.0.1:3306/hospital_billing_db?sslMode=disable'
export HOSPITAL_DB_USER='hospital_app'
export HOSPITAL_DB_PASSWORD='YOUR_LOCAL_PASSWORD'
```

## Build and run

From the project root:

```bash
mvn clean compile
mvn exec:java
```

Maven downloads MariaDB Connector/J automatically. A separate manual JDBC JAR download is not required.

### Import into Eclipse

1. Open Eclipse.
2. Select **File → Import → Maven → Existing Maven Projects**.
3. Choose the extracted project folder (the one containing `pom.xml`).
4. Finish and allow Maven to resolve dependencies.
5. Open `HospitalBillingApp.java` and run it as a Java application, or run the project using Maven's `exec:java` goal.

## Run the validation checks

The project includes a dependency-free validation test harness for the billing service. From the project root:

```bash
./scripts/run-validation-tests.sh
```

It compiles the application sources against Java 17 APIs and runs 22 checks covering valid calculations, rounding, strict dates, date order, positive whole-number stay duration, invalid numeric values, discount boundaries, tax boundaries, contact numbers, age, and invalid selections.

The same checks are configured in the GitHub Actions workflow under `.github/workflows/java-ci.yml`.

## Manual test workflow

Use fictional data only. A successful sample test previously entered through the GUI produced:

- Ward: General Ward, 2 days at INR 500/day
- Doctor charge: INR 1,500
- Medicines: INR 500
- Tests: INR 750
- Other charges: INR 250
- Discount: INR 100
- Demonstration tax rate: 5%
- Expected total: **INR 4,095.00**

The project owner manually verified that:

1. The AWT application launched on Fedora.
2. The sample bill calculated to INR 4,095.00.
3. One patient row and one bill row were stored in MariaDB.
4. SQL `SELECT` queries retrieved the stored records.
5. The application's saved-bills window displayed the saved bill.

See [`docs/TEST_PLAN.md`](docs/TEST_PLAN.md) for the distinction between automated validation checks and manual GUI/database tests, including checks still to be done.

## How the database integration works

1. The AWT interface collects the entered values.
2. `BillingService` validates the inputs and calculates a `Bill` using `BigDecimal`.
3. `BillRepository` uses JDBC `PreparedStatement` objects to save or update the patient and insert a bill within a transaction.
4. MariaDB stores the data in `patients` and `bills`.
5. `SavedBillsFrame` calls the repository to retrieve recent bill summaries.

A patient code identifies a patient record. Saving another bill for the same patient code updates the patient's current details and inserts a separate bill record.

## Known limitations

- This is an educational desktop prototype, not a real hospital billing or patient-management system.
- Use fictional data only. Patient contact and address fields are stored in the local database without application-level field encryption.
- The application has no user authentication, role-based access control, audit trail, backup/restore workflow, or healthcare privacy/compliance certification.
- Ward and procedure prices are fixed sample values, not verified hospital prices.
- The tax field is explicitly a demonstration percentage; the program does not calculate or determine statutory GST.
- Days stayed is manually entered and validated as a positive whole number. Admission and discharge dates are validated and ordered, but the program does not calculate billable days from the dates.
- The current receipt contains a legacy “Saved to MySQL” label. The tested database server and configured JDBC driver are MariaDB; the label is cosmetic and does not indicate a MySQL server is in use.
- The Enter-key focus navigation and the full GUI edge cases should be manually retested on the target desktop environment; the automated validation harness tests the billing service, not AWT keyboard events.

## Project contribution and AI assistance

This is an AI-assisted student project evolved from an earlier Java AWT academic calculator. The project retains the original Enter-key navigation usability idea. The student contributor supplied the original project context and feature requirements, set up the local MariaDB environment, created the schema and application database user, ran the application, entered fictional test data, checked the calculations, and independently verified saved records with SQL. AI assistance was used to help redesign, organise, document, and review portions of the code. Do not describe the entire codebase as independently written if that would not accurately represent the development process.

## Additional project notes

- [`docs/PROJECT_WALKTHROUGH.md`](docs/PROJECT_WALKTHROUGH.md) gives a short architecture and interview explanation.
- [`docs/GITHUB_PUBLISHING_GUIDE.md`](docs/GITHUB_PUBLISHING_GUIDE.md) explains how to create an empty repository and push this project from Fedora.
- The automated validation harness completed **22/22 checks** during packaging using a Java 21 environment with `javac --release 17`. The project owner separately compiled and ran the desktop application on Fedora with OpenJDK 25 and manually verified one MariaDB save and retrieval flow.

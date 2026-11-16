# Publish this project to GitHub (Fedora)

## 1. Create an empty repository on GitHub

1. Sign in to GitHub as `ApekshitPalkar`.
2. Click the **+** menu in the top navigation and choose **New repository**.
3. Repository name: `hospital-billing-management-system`.
4. Description: `Educational Java AWT billing application with JDBC and MariaDB persistence.`
5. Choose **Public** only if you are comfortable making the source code public.
6. Do **not** select “Add a README”, “Add .gitignore”, or a license. This project already includes those files.
7. Create the repository.

## 2. Prepare the local project

Extract the ZIP. Open a terminal in the extracted project root (the folder containing `pom.xml`).

If you want to reuse your existing local DB connection, copy the private configuration from your old local project into this new folder. This file is ignored by Git:

```bash
cp ~/Downloads/Hospital-Billing-Management-System-v2/database.properties ./database.properties
chmod 600 database.properties
```

Adjust the source path if your older project is stored elsewhere. Do not upload or paste this file anywhere.

## 3. Run checks before publishing

```bash
./scripts/run-validation-tests.sh
mvn clean compile
```

The first command runs the 22 dependency-free billing validation checks. The second checks the Maven build and resolves the MariaDB JDBC dependency.

## 4. Initialise Git and inspect the staged files

```bash
git init -b main
git add .
git status --short
git check-ignore -v database.properties
```

Confirm `database.properties` is ignored and does not appear in the staged file list. Also confirm there are no real patient details, passwords, API keys, screenshots containing sensitive information, or machine-specific files in the staged changes.

## 5. Commit and push

Use the repository URL shown by GitHub after you create it:

```bash
git commit -m "Initial commit: Java AWT and MariaDB billing application"
git remote add origin https://github.com/ApekshitPalkar/hospital-billing-management-system.git
git push -u origin main
```

GitHub may open a browser authentication prompt. Complete the authentication in the browser; do not place a password or token in the command line.

## 6. Verify the repository

Refresh the repository page. Check that the README renders, the Java source files and SQL schema are visible, and `database.properties` is absent. The GitHub Actions workflow should run the Maven compile and 22 validation checks after the push.

## If the `origin` remote already exists

Check it first:

```bash
git remote -v
```

If it points to the wrong URL, update it rather than adding a second `origin`:

```bash
git remote set-url origin https://github.com/ApekshitPalkar/hospital-billing-management-system.git
```

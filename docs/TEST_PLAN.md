# Test Plan and Current Test Evidence

This document separates automated checks from manual checks performed in the desktop application.

## Automated billing-service checks

Run from the project root:

```bash
./scripts/run-validation-tests.sh
```

The dependency-free harness runs 22 checks:

| Area | Checks |
|---|---|
| Normal calculation | Sample total, blank optional amounts default to zero |
| Monetary arithmetic | `BigDecimal` half-up rounding |
| Tax and discount | 100% tax boundary, tax above range, negative tax, discount greater than subtotal, discount equal to subtotal |
| Dates | Invalid calendar date, incorrect format, discharge earlier than admission |
| Stay duration | Zero, fractional, and excessive days |
| Numeric input | Negative amounts, `NaN`, and infinity |
| Patient details | Missing name, invalid contact number, age above 120 |
| Selection values | Unknown ward and unknown surgery |

The automated checks exercise `BillingService`; they do not launch AWT windows or connect to MariaDB.

## Manual GUI and database checks already performed by the project owner

| Check | Result | Evidence/notes |
|---|---|---|
| Application launches on Fedora | PASS | Owner screenshot of running AWT window |
| Sample bill calculates to INR 4,095.00 | PASS | Receipt showed the expected sample total |
| Patient and bill inserted into MariaDB | PASS | SQL counts showed one patient and one bill |
| Patient record retrieved with SQL | PASS | `SELECT * FROM patients` returned the test row |
| Bill record retrieved with SQL | PASS | `SELECT * FROM bills` returned the INR 4,095.00 row |
| Saved-bills window displays the stored bill | PASS | Owner screenshot showed Bill #1 and patient code DEMO-001 |

## Manual checks still recommended on the desktop

These are not claimed as completed merely because equivalent validation exists in `BillingService`:

1. Enter an impossible date such as `31-02-2026`; confirm a clear validation message and no database insert.
2. Enter a discharge date earlier than admission; confirm it is rejected.
3. Enter a negative charge, `NaN`, or `Infinity`; confirm rejection.
4. Enter a tax rate over 100; confirm rejection.
5. Enter a discount larger than the subtotal; confirm rejection.
6. Use Enter to move through text fields and confirm the intended focus order on the Fedora desktop.
7. Press Clear Form and confirm every field returns to its default value.
8. Stop MariaDB temporarily and confirm the receipt clearly reports that the bill was not saved. Restart MariaDB after the test.

Do not use real patient information for testing.

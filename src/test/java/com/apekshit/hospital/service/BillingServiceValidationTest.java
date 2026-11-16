package com.apekshit.hospital.service;

import com.apekshit.hospital.model.Bill;

import java.math.BigDecimal;

/**
 * Dependency-free validation checks for BillingService.
 * Run with ./scripts/run-validation-tests.sh from the project root.
 */
public final class BillingServiceValidationTest {
    private static int passed;

    private BillingServiceValidationTest() { }

    public static void main(String[] args) {
        run("valid sample bill totals", BillingServiceValidationTest::validSampleBillTotals);
        run("blank optional amounts default to zero", BillingServiceValidationTest::blankAmountsDefaultToZero);
        run("monetary values round HALF_UP", BillingServiceValidationTest::moneyRoundsHalfUp);
        run("100 percent demo tax is accepted", BillingServiceValidationTest::maximumTaxAccepted);
        run("strict calendar date rejects 31-02", BillingServiceValidationTest::invalidCalendarDateRejected);
        run("wrong date format rejected", BillingServiceValidationTest::wrongDateFormatRejected);
        run("discharge before admission rejected", BillingServiceValidationTest::dischargeBeforeAdmissionRejected);
        run("zero days stayed rejected", BillingServiceValidationTest::zeroDaysRejected);
        run("fractional days stayed rejected", BillingServiceValidationTest::fractionalDaysRejected);
        run("excessive days stayed rejected", BillingServiceValidationTest::excessiveDaysRejected);
        run("negative monetary charge rejected", BillingServiceValidationTest::negativeChargeRejected);
        run("NaN monetary input rejected", BillingServiceValidationTest::nanRejected);
        run("infinite monetary input rejected", BillingServiceValidationTest::infinityRejected);
        run("tax above 100 percent rejected", BillingServiceValidationTest::taxAboveRangeRejected);
        run("negative tax rejected", BillingServiceValidationTest::negativeTaxRejected);
        run("discount greater than subtotal rejected", BillingServiceValidationTest::discountAboveSubtotalRejected);
        run("discount equal to subtotal accepted", BillingServiceValidationTest::discountEqualToSubtotalAccepted);
        run("invalid contact number rejected", BillingServiceValidationTest::invalidContactRejected);
        run("age outside 1-120 rejected", BillingServiceValidationTest::invalidAgeRejected);
        run("missing patient name rejected", BillingServiceValidationTest::missingPatientNameRejected);
        run("unknown ward rejected", BillingServiceValidationTest::unknownWardRejected);
        run("unknown surgery rejected", BillingServiceValidationTest::unknownSurgeryRejected);

        System.out.println("\nValidation checks passed: " + passed + "/22");
        if (passed != 22) {
            throw new AssertionError("One or more validation checks failed.");
        }
    }

    private static void validSampleBillTotals() {
        Bill bill = create(valid(), "1500", "500", "750", "250", "100", "5");
        equals("1000.00", bill.getWardAmount().toPlainString(), "ward amount");
        equals("4000.00", bill.getSubtotal().toPlainString(), "subtotal");
        equals("195.00", bill.getTaxAmount().toPlainString(), "tax amount");
        equals("4095.00", bill.getTotalAmount().toPlainString(), "total amount");
    }

    private static void blankAmountsDefaultToZero() {
        Bill bill = create(valid(), "", "", "", "", "", "");
        equals("1000.00", bill.getSubtotal().toPlainString(), "subtotal with blank optional amounts");
        equals("1000.00", bill.getTotalAmount().toPlainString(), "total with no tax/discount");
    }

    private static void moneyRoundsHalfUp() {
        Bill bill = create(valid(), "0.005", "0", "0", "0", "0", "0");
        equals("0.01", bill.getDoctorCharge().toPlainString(), "rounded doctor charge");
        equals("1000.01", bill.getSubtotal().toPlainString(), "rounded subtotal");
    }

    private static void maximumTaxAccepted() {
        Bill bill = create(valid(), "0", "0", "0", "0", "0", "100");
        equals("2000.00", bill.getTotalAmount().toPlainString(), "100 percent tax total");
    }

    private static void invalidCalendarDateRejected() {
        expectInvalid(() -> create(withDates(valid(), "31-02-2026", "03-03-2026"), "0", "0", "0", "0", "0", "0"), "valid calendar date");
    }

    private static void wrongDateFormatRejected() {
        expectInvalid(() -> create(withDates(valid(), "2026-11-01", "03-11-2026"), "0", "0", "0", "0", "0", "0"), "DD-MM-YYYY");
    }

    private static void dischargeBeforeAdmissionRejected() {
        expectInvalid(() -> create(withDates(valid(), "03-11-2026", "01-11-2026"), "0", "0", "0", "0", "0", "0"), "cannot be before");
    }

    private static void zeroDaysRejected() {
        expectInvalid(() -> create(withDays(valid(), "0"), "0", "0", "0", "0", "0", "0"), "positive whole number");
    }

    private static void fractionalDaysRejected() {
        expectInvalid(() -> create(withDays(valid(), "2.5"), "0", "0", "0", "0", "0", "0"), "valid whole number");
    }

    private static void excessiveDaysRejected() {
        expectInvalid(() -> create(withDays(valid(), "3651"), "0", "0", "0", "0", "0", "0"), "must not exceed 3650");
    }

    private static void negativeChargeRejected() {
        expectInvalid(() -> create(valid(), "-1", "0", "0", "0", "0", "0"), "cannot be negative");
    }

    private static void nanRejected() {
        expectInvalid(() -> create(valid(), "NaN", "0", "0", "0", "0", "0"), "valid number");
    }

    private static void infinityRejected() {
        expectInvalid(() -> create(valid(), "Infinity", "0", "0", "0", "0", "0"), "valid number");
    }

    private static void taxAboveRangeRejected() {
        expectInvalid(() -> create(valid(), "0", "0", "0", "0", "0", "100.01"), "between 0 and 100");
    }

    private static void negativeTaxRejected() {
        expectInvalid(() -> create(valid(), "0", "0", "0", "0", "0", "-0.1"), "cannot be negative");
    }

    private static void discountAboveSubtotalRejected() {
        expectInvalid(() -> create(valid(), "0", "0", "0", "0", "1000.01", "0"), "greater than the subtotal");
    }

    private static void discountEqualToSubtotalAccepted() {
        Bill bill = create(valid(), "0", "0", "0", "0", "1000", "0");
        equals("0.00", bill.getTotalAmount().toPlainString(), "total after full discount");
    }

    private static void invalidContactRejected() {
        String[] data = valid();
        data[4] = "12345";
        expectInvalid(() -> create(data, "0", "0", "0", "0", "0", "0"), "exactly 10 digits");
    }

    private static void invalidAgeRejected() {
        String[] data = valid();
        data[2] = "121";
        expectInvalid(() -> create(data, "0", "0", "0", "0", "0", "0"), "between 1 and 120");
    }

    private static void missingPatientNameRejected() {
        String[] data = valid();
        data[0] = " ";
        expectInvalid(() -> create(data, "0", "0", "0", "0", "0", "0"), "Patient name cannot be empty");
    }

    private static void unknownWardRejected() {
        String[] data = valid();
        data[9] = "Unknown Ward";
        expectInvalid(() -> create(data, "0", "0", "0", "0", "0", "0"), "valid ward");
    }

    private static void unknownSurgeryRejected() {
        String[] data = valid();
        data[10] = "Unknown Procedure";
        expectInvalid(() -> create(data, "0", "0", "0", "0", "0", "0"), "valid surgery");
    }

    /** Array order: name, code, age, relative, contact, address, admission, discharge, days, ward, surgery, payment. */
    private static String[] valid() {
        return new String[] {"Demo Patient", "TEST-001", "35", "Demo Relative", "0000000000",
                "Demo Address", "01-11-2026", "03-11-2026", "2", "General Ward", "No Surgery", "UPI"};
    }

    private static String[] withDates(String[] input, String admission, String discharge) {
        String[] copy = input.clone();
        copy[6] = admission;
        copy[7] = discharge;
        return copy;
    }

    private static String[] withDays(String[] input, String days) {
        String[] copy = input.clone();
        copy[8] = days;
        return copy;
    }

    private static Bill create(String[] d, String doctor, String medicine, String tests,
                               String other, String discount, String tax) {
        return BillingService.createBill(d[0], d[1], d[2], d[3], d[4], d[5], d[6], d[7], d[8],
                d[9], d[10], doctor, medicine, tests, other, discount, tax, d[11]);
    }

    private static void expectInvalid(Runnable operation, String messagePart) {
        try {
            operation.run();
            throw new AssertionError("Expected IllegalArgumentException containing: " + messagePart);
        } catch (IllegalArgumentException expected) {
            if (!expected.getMessage().toLowerCase().contains(messagePart.toLowerCase())) {
                throw new AssertionError("Expected message containing '" + messagePart
                        + "' but received: " + expected.getMessage());
            }
        }
    }

    private static void equals(String expected, String actual, String label) {
        if (!expected.equals(actual)) {
            throw new AssertionError(label + ": expected " + expected + " but got " + actual);
        }
    }

    private static void run(String name, Runnable check) {
        check.run();
        passed++;
        System.out.println("PASS: " + name);
    }
}

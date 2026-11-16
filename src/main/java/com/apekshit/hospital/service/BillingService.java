package com.apekshit.hospital.service;

import com.apekshit.hospital.model.Bill;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/** Validation and billing calculations are kept out of the AWT interface. */
public final class BillingService {
    private static final BigDecimal ZERO = new BigDecimal("0.00");
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT);

    private BillingService() { }

    public static BigDecimal getWardRate(String ward) {
        if ("General Ward".equals(ward)) return new BigDecimal("500.00");
        if ("Semi-Private Ward".equals(ward)) return new BigDecimal("1200.00");
        if ("Private Room".equals(ward)) return new BigDecimal("2500.00");
        if ("ICU".equals(ward)) return new BigDecimal("5000.00");
        throw new IllegalArgumentException("Please select a valid ward.");
    }

    public static BigDecimal getSurgeryCharge(String surgery) {
        if ("No Surgery".equals(surgery)) return ZERO;
        if ("Appendix Operation".equals(surgery)) return new BigDecimal("25000.00");
        if ("Eye Operation".equals(surgery)) return new BigDecimal("18000.00");
        if ("Orthopedic Operation".equals(surgery)) return new BigDecimal("45000.00");
        if ("Cardiac Procedure".equals(surgery)) return new BigDecimal("80000.00");
        throw new IllegalArgumentException("Please select a valid surgery/procedure.");
    }

    public static Bill createBill(String patientName, String patientCode, String ageText,
                                  String relativeName, String contactNumber, String address,
                                  String admissionText, String dischargeText, String daysText,
                                  String ward, String surgery, String doctorText,
                                  String medicineText, String testsText, String otherText,
                                  String discountText, String taxRateText, String paymentMode) {
        String cleanName = required(patientName, "Patient name");
        String cleanCode = required(patientCode, "Patient ID");
        String cleanRelative = required(relativeName, "Relative name");
        String cleanContact = required(contactNumber, "Contact number");
        if (!cleanContact.matches("[0-9]{10}")) {
            throw new IllegalArgumentException("Contact number must contain exactly 10 digits.");
        }

        int age = parsePositiveInt(ageText, "Age");
        if (age > 120) throw new IllegalArgumentException("Age must be between 1 and 120.");

        LocalDate admission = parseDate(admissionText, "Admission date");
        LocalDate discharge = parseDate(dischargeText, "Discharge date");
        if (discharge.isBefore(admission)) {
            throw new IllegalArgumentException("Discharge date cannot be before admission date.");
        }

        int days = parsePositiveInt(daysText, "Days stayed");
        if (days > 3650) throw new IllegalArgumentException("Days stayed must not exceed 3650.");

        BigDecimal doctor = money(doctorText, "Doctor charge");
        BigDecimal medicine = money(medicineText, "Medicine charge");
        BigDecimal tests = money(testsText, "Test/diagnostic charge");
        BigDecimal other = money(otherText, "Other charges");
        BigDecimal discount = money(discountText, "Discount");
        BigDecimal taxRate = money(taxRateText, "Demonstration tax rate");
        if (taxRate.compareTo(ONE_HUNDRED) > 0) {
            throw new IllegalArgumentException("Demonstration tax rate must be between 0 and 100%.");
        }

        BigDecimal wardRate = getWardRate(ward);
        BigDecimal surgeryCharge = getSurgeryCharge(surgery);
        BigDecimal wardAmount = wardRate.multiply(BigDecimal.valueOf(days)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal subtotal = wardAmount.add(surgeryCharge).add(doctor).add(medicine)
                .add(tests).add(other).setScale(2, RoundingMode.HALF_UP);

        if (discount.compareTo(subtotal) > 0) {
            throw new IllegalArgumentException("Discount cannot be greater than the subtotal.");
        }

        BigDecimal taxableAmount = subtotal.subtract(discount);
        BigDecimal taxAmount = taxableAmount.multiply(taxRate)
                .divide(ONE_HUNDRED, 2, RoundingMode.HALF_UP);
        BigDecimal total = taxableAmount.add(taxAmount).setScale(2, RoundingMode.HALF_UP);

        String cleanPayment = required(paymentMode, "Payment mode");
        return new Bill(cleanCode, cleanName, age, cleanRelative, cleanContact,
                address == null ? "" : address.trim(), admission, discharge, days,
                ward, wardRate, wardAmount, surgery, surgeryCharge, doctor,
                medicine, tests, other, subtotal, discount, taxRate, taxAmount,
                total, cleanPayment);
    }

    private static String required(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " cannot be empty.");
        }
        return value.trim();
    }

    private static int parsePositiveInt(String value, String field) {
        try {
            int number = Integer.parseInt(required(value, field));
            if (number <= 0) throw new IllegalArgumentException(field + " must be a positive whole number.");
            return number;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(field + " must be a valid whole number.");
        }
    }

    private static LocalDate parseDate(String value, String field) {
        try {
            return LocalDate.parse(required(value, field), DATE_FORMAT);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException(field + " must use DD-MM-YYYY and be a valid calendar date.");
        }
    }

    private static BigDecimal money(String value, String field) {
        String clean = (value == null || value.trim().isEmpty()) ? "0" : value.trim();
        try {
            BigDecimal amount = new BigDecimal(clean);
            if (amount.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException(field + " cannot be negative.");
            }
            return amount.setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException ex) {
            // BigDecimal rejects NaN and Infinity as well as non-numeric text.
            throw new IllegalArgumentException(field + " must be a valid number (for example, 1250 or 1250.50).");
        }
    }
}

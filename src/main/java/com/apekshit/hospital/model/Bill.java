package com.apekshit.hospital.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Immutable calculated bill data shared by the UI, receipt and repository. */
public final class Bill {
    private final String patientCode;
    private final String patientName;
    private final int age;
    private final String relativeName;
    private final String contactNumber;
    private final String address;
    private final LocalDate admissionDate;
    private final LocalDate dischargeDate;
    private final int daysStayed;
    private final String wardType;
    private final BigDecimal wardRate;
    private final BigDecimal wardAmount;
    private final String surgeryType;
    private final BigDecimal surgeryCharge;
    private final BigDecimal doctorCharge;
    private final BigDecimal medicineCharge;
    private final BigDecimal testCharge;
    private final BigDecimal otherCharge;
    private final BigDecimal subtotal;
    private final BigDecimal discountAmount;
    private final BigDecimal demoTaxRate;
    private final BigDecimal taxAmount;
    private final BigDecimal totalAmount;
    private final String paymentMode;
    private final LocalDateTime createdAt;

    public Bill(String patientCode, String patientName, int age, String relativeName,
                String contactNumber, String address, LocalDate admissionDate,
                LocalDate dischargeDate, int daysStayed, String wardType,
                BigDecimal wardRate, BigDecimal wardAmount, String surgeryType,
                BigDecimal surgeryCharge, BigDecimal doctorCharge,
                BigDecimal medicineCharge, BigDecimal testCharge,
                BigDecimal otherCharge, BigDecimal subtotal,
                BigDecimal discountAmount, BigDecimal demoTaxRate,
                BigDecimal taxAmount, BigDecimal totalAmount, String paymentMode) {
        this.patientCode = patientCode;
        this.patientName = patientName;
        this.age = age;
        this.relativeName = relativeName;
        this.contactNumber = contactNumber;
        this.address = address;
        this.admissionDate = admissionDate;
        this.dischargeDate = dischargeDate;
        this.daysStayed = daysStayed;
        this.wardType = wardType;
        this.wardRate = wardRate;
        this.wardAmount = wardAmount;
        this.surgeryType = surgeryType;
        this.surgeryCharge = surgeryCharge;
        this.doctorCharge = doctorCharge;
        this.medicineCharge = medicineCharge;
        this.testCharge = testCharge;
        this.otherCharge = otherCharge;
        this.subtotal = subtotal;
        this.discountAmount = discountAmount;
        this.demoTaxRate = demoTaxRate;
        this.taxAmount = taxAmount;
        this.totalAmount = totalAmount;
        this.paymentMode = paymentMode;
        this.createdAt = LocalDateTime.now();
    }

    public String getPatientCode() { return patientCode; }
    public String getPatientName() { return patientName; }
    public int getAge() { return age; }
    public String getRelativeName() { return relativeName; }
    public String getContactNumber() { return contactNumber; }
    public String getAddress() { return address; }
    public LocalDate getAdmissionDate() { return admissionDate; }
    public LocalDate getDischargeDate() { return dischargeDate; }
    public int getDaysStayed() { return daysStayed; }
    public String getWardType() { return wardType; }
    public BigDecimal getWardRate() { return wardRate; }
    public BigDecimal getWardAmount() { return wardAmount; }
    public String getSurgeryType() { return surgeryType; }
    public BigDecimal getSurgeryCharge() { return surgeryCharge; }
    public BigDecimal getDoctorCharge() { return doctorCharge; }
    public BigDecimal getMedicineCharge() { return medicineCharge; }
    public BigDecimal getTestCharge() { return testCharge; }
    public BigDecimal getOtherCharge() { return otherCharge; }
    public BigDecimal getSubtotal() { return subtotal; }
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public BigDecimal getDemoTaxRate() { return demoTaxRate; }
    public BigDecimal getTaxAmount() { return taxAmount; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public String getPaymentMode() { return paymentMode; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}

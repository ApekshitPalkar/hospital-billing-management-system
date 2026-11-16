package com.apekshit.hospital.db;

import com.apekshit.hospital.model.Bill;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Database persistence only; it contains no GUI or billing calculation code. */
public final class BillRepository {
    private final DatabaseConnection databaseConnection;

    public BillRepository(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public long save(Bill bill) throws SQLException {
        try (Connection connection = databaseConnection.open()) {
            connection.setAutoCommit(false);
            try {
                long patientId = saveOrUpdatePatient(connection, bill);
                long billId = insertBill(connection, patientId, bill);
                connection.commit();
                return billId;
            } catch (SQLException ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    private long saveOrUpdatePatient(Connection connection, Bill bill) throws SQLException {
        String findSql = "SELECT patient_id FROM patients WHERE patient_code = ? FOR UPDATE";
        try (PreparedStatement find = connection.prepareStatement(findSql)) {
            find.setString(1, bill.getPatientCode());
            try (ResultSet result = find.executeQuery()) {
                if (result.next()) {
                    long patientId = result.getLong("patient_id");
                    String updateSql = "UPDATE patients SET full_name=?, age=?, relative_name=?, contact_number=?, address=? WHERE patient_id=?";
                    try (PreparedStatement update = connection.prepareStatement(updateSql)) {
                        update.setString(1, bill.getPatientName());
                        update.setInt(2, bill.getAge());
                        update.setString(3, bill.getRelativeName());
                        update.setString(4, bill.getContactNumber());
                        update.setString(5, bill.getAddress());
                        update.setLong(6, patientId);
                        update.executeUpdate();
                    }
                    return patientId;
                }
            }
        }

        String insertSql = "INSERT INTO patients (patient_code, full_name, age, relative_name, contact_number, address) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement insert = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            insert.setString(1, bill.getPatientCode());
            insert.setString(2, bill.getPatientName());
            insert.setInt(3, bill.getAge());
            insert.setString(4, bill.getRelativeName());
            insert.setString(5, bill.getContactNumber());
            insert.setString(6, bill.getAddress());
            insert.executeUpdate();
            try (ResultSet keys = insert.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
        }
        throw new SQLException("The database did not return a patient record ID.");
    }

    private long insertBill(Connection connection, long patientId, Bill bill) throws SQLException {
        String sql = "INSERT INTO bills (patient_id, admission_date, discharge_date, days_stayed, ward_type, ward_rate, surgery_type, surgery_charge, doctor_charge, medicine_charge, test_charge, other_charge, subtotal, discount_amount, demo_tax_rate, tax_amount, total_amount, payment_mode) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, patientId);
            statement.setDate(2, Date.valueOf(bill.getAdmissionDate()));
            statement.setDate(3, Date.valueOf(bill.getDischargeDate()));
            statement.setInt(4, bill.getDaysStayed());
            statement.setString(5, bill.getWardType());
            statement.setBigDecimal(6, bill.getWardRate());
            statement.setString(7, bill.getSurgeryType());
            statement.setBigDecimal(8, bill.getSurgeryCharge());
            statement.setBigDecimal(9, bill.getDoctorCharge());
            statement.setBigDecimal(10, bill.getMedicineCharge());
            statement.setBigDecimal(11, bill.getTestCharge());
            statement.setBigDecimal(12, bill.getOtherCharge());
            statement.setBigDecimal(13, bill.getSubtotal());
            statement.setBigDecimal(14, bill.getDiscountAmount());
            statement.setBigDecimal(15, bill.getDemoTaxRate());
            statement.setBigDecimal(16, bill.getTaxAmount());
            statement.setBigDecimal(17, bill.getTotalAmount());
            statement.setString(18, bill.getPaymentMode());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
        }
        throw new SQLException("The database did not return a bill number.");
    }

    public List<String> findRecentSummaries(int limit) throws SQLException {
        int safeLimit = Math.max(1, Math.min(limit, 100));
        String sql = "SELECT b.bill_id, p.patient_code, p.full_name, b.total_amount, b.payment_mode, b.created_at "
                + "FROM bills b JOIN patients p ON p.patient_id=b.patient_id "
                + "ORDER BY b.created_at DESC LIMIT ?";
        List<String> summaries = new ArrayList<String>();
        try (Connection connection = databaseConnection.open();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, safeLimit);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    summaries.add("Bill #" + result.getLong("bill_id")
                            + " | Patient: " + result.getString("full_name")
                            + " | Patient ID: " + result.getString("patient_code")
                            + " | Total: INR " + result.getBigDecimal("total_amount")
                            + " | Payment: " + result.getString("payment_mode")
                            + " | Created: " + result.getTimestamp("created_at"));
                }
            }
        }
        return summaries;
    }
}

package com.apekshit.hospital.ui;

import com.apekshit.hospital.model.Bill;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.format.DateTimeFormatter;

public final class BillReceiptFrame extends Frame {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public BillReceiptFrame(Bill bill, String databaseStatus) {
        super("Hospital Bill Receipt - Educational Demo");
        setSize(760, 700);
        setLocation(240, 100);
        setLayout(new BorderLayout(8, 8));
        setBackground(Color.LIGHT_GRAY);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent event) { dispose(); }
        });

        Panel header = new Panel(new BorderLayout());
        header.setBackground(Color.CYAN);
        Label hospital = new Label("ARMANI SPECIALITY HOSPITAL - STUDENT DEMO", Label.CENTER);
        hospital.setFont(new Font("SansSerif", Font.BOLD, 18));
        Label receipt = new Label("PATIENT BILL / PAYMENT SUMMARY", Label.CENTER);
        header.add(hospital, BorderLayout.NORTH);
        header.add(receipt, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        TextArea output = new TextArea(buildReceipt(bill, databaseStatus), 30, 90, TextArea.SCROLLBARS_VERTICAL_ONLY);
        output.setEditable(false);
        output.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        add(output, BorderLayout.CENTER);

        Panel footer = new Panel(new FlowLayout());
        Button close = new Button("Close");
        close.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent event) { dispose(); }
        });
        footer.add(close);
        add(footer, BorderLayout.SOUTH);
        setVisible(true);
    }

    private String buildReceipt(Bill bill, String databaseStatus) {
        StringBuilder text = new StringBuilder();
        text.append("==============================================================\n");
        text.append("                  EDUCATIONAL BILLING SUMMARY\n");
        text.append("==============================================================\n");
        text.append("Patient name       : ").append(bill.getPatientName()).append('\n');
        text.append("Patient ID         : ").append(bill.getPatientCode()).append('\n');
        text.append("Age                : ").append(bill.getAge()).append('\n');
        text.append("Relative name      : ").append(bill.getRelativeName()).append('\n');
        text.append("Contact number     : ").append(bill.getContactNumber()).append('\n');
        text.append("Address            : ").append(bill.getAddress()).append('\n');
        text.append("Admission date     : ").append(DATE_FORMAT.format(bill.getAdmissionDate())).append('\n');
        text.append("Discharge date     : ").append(DATE_FORMAT.format(bill.getDischargeDate())).append('\n');
        text.append("Days stayed        : ").append(bill.getDaysStayed()).append('\n');
        text.append("Payment mode       : ").append(bill.getPaymentMode()).append("\n\n");
        text.append("------------------------ CHARGES ----------------------------\n");
        line(text, "Ward / room", bill.getWardType());
        line(text, "Ward rate per day", "INR " + bill.getWardRate());
        line(text, "Ward total", "INR " + bill.getWardAmount());
        line(text, "Surgery / procedure", bill.getSurgeryType());
        line(text, "Surgery charge", "INR " + bill.getSurgeryCharge());
        line(text, "Doctor charge", "INR " + bill.getDoctorCharge());
        line(text, "Medicine charge", "INR " + bill.getMedicineCharge());
        line(text, "Tests / diagnostics", "INR " + bill.getTestCharge());
        line(text, "Other charges", "INR " + bill.getOtherCharge());
        text.append("--------------------------------------------------------------\n");
        line(text, "Subtotal", "INR " + bill.getSubtotal());
        line(text, "Discount", "INR " + bill.getDiscountAmount());
        line(text, "Demonstration tax rate", bill.getDemoTaxRate() + "%");
        line(text, "Demonstration tax", "INR " + bill.getTaxAmount());
        text.append("==============================================================\n");
        line(text, "TOTAL", "INR " + bill.getTotalAmount());
        text.append("==============================================================\n\n");
        text.append("DATABASE STATUS: ").append(databaseStatus).append("\n\n");
        text.append("IMPORTANT: This is an educational software project.\n");
        text.append("All ward, procedure and tax values are sample values only.\n");
        text.append("This output is NOT a real hospital invoice or tax document.\n");
        text.append("Do not enter real patient data into this student application.\n");
        return text.toString();
    }

    private void line(StringBuilder text, String label, String value) {
        text.append(String.format("%-27s: %s%n", label, value));
    }
}

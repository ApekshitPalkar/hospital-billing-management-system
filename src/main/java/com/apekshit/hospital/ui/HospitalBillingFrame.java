package com.apekshit.hospital.ui;

import com.apekshit.hospital.db.BillRepository;
import com.apekshit.hospital.db.DatabaseConfig;
import com.apekshit.hospital.model.Bill;
import com.apekshit.hospital.service.BillingService;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

public final class HospitalBillingFrame extends Frame implements ActionListener {
    private final TextField patientName = new TextField();
    private final TextField patientCode = new TextField();
    private final TextField patientAge = new TextField();
    private final TextField relativeName = new TextField();
    private final TextField contactNumber = new TextField();
    private final TextField address = new TextField();
    private final TextField admissionDate = new TextField();
    private final TextField dischargeDate = new TextField();

    private final TextField daysStayed = new TextField();
    private final TextField doctorCharge = new TextField("0");
    private final TextField medicineCharge = new TextField("0");
    private final TextField testCharge = new TextField("0");
    private final TextField otherCharge = new TextField("0");
    private final TextField discount = new TextField("0");
    private final TextField demoTaxRate = new TextField("0");

    private final Choice wardChoice = new Choice();
    private final Choice surgeryChoice = new Choice();
    private final Choice paymentChoice = new Choice();
    private final Label wardRateLabel = new Label("INR 500.00 / day");
    private final Label surgeryRateLabel = new Label("INR 0.00");
    private final Label databaseStatusLabel;

    private final Button calculateButton = new Button("Calculate & Save");
    private final Button clearButton = new Button("Clear Form");
    private final Button savedBillsButton = new Button("View Saved Bills");
    private final Button exitButton = new Button("Exit");

    private final DatabaseConfig databaseConfig;
    private final BillRepository billRepository;

    public HospitalBillingFrame(DatabaseConfig databaseConfig, BillRepository billRepository) {
        super("Hospital Billing Management System - Educational Demo");
        this.databaseConfig = databaseConfig;
        this.billRepository = billRepository;
        this.databaseStatusLabel = new Label(databaseConfig.isConfigured()
                ? "Database settings detected - connection will be checked when saving."
                : "Database not configured - bills can be calculated but will NOT be saved.");

        setSize(1120, 760);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(238, 242, 245));
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent event) { dispose(); }
        });

        add(buildHeader(), BorderLayout.NORTH);
        add(buildMainForm(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
        addChoiceItems();
        addListeners();
        configureEnterNavigation();
        updateWardRate();
        updateSurgeryRate();
        setVisible(true);
        patientName.requestFocus();
    }

    private Panel buildHeader() {
        Panel header = new Panel(new GridLayout(3, 1));
        header.setBackground(new Color(210, 238, 242));
        Label title = new Label("ARMANI SPECIALITY HOSPITAL", Label.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 23));
        title.setForeground(new Color(20, 65, 105));
        Label subtitle = new Label("HOSPITAL BILLING MANAGEMENT SYSTEM - STUDENT PROJECT", Label.CENTER);
        subtitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        databaseStatusLabel.setAlignment(Label.CENTER);
        header.add(title);
        header.add(subtitle);
        header.add(databaseStatusLabel);
        return header;
    }

    private Panel buildMainForm() {
        Panel columns = new Panel(new GridLayout(1, 2, 12, 0));
        columns.setBackground(new Color(238, 242, 245));
        columns.add(buildPatientPanel());
        columns.add(buildBillingPanel());
        return columns;
    }

    private Panel buildPatientPanel() {
        Panel section = new Panel(new BorderLayout(5, 5));
        section.setBackground(Color.WHITE);
        Label heading = heading("PATIENT DETAILS");
        Panel fields = new Panel(new GridLayout(0, 2, 7, 9));
        fields.setBackground(Color.WHITE);
        addField(fields, "Patient name *", patientName);
        addField(fields, "Patient ID *", patientCode);
        addField(fields, "Age *", patientAge);
        addField(fields, "Relative name *", relativeName);
        addField(fields, "Contact number *", contactNumber);
        addField(fields, "Address", address);
        addField(fields, "Admission date (DD-MM-YYYY) *", admissionDate);
        addField(fields, "Discharge date (DD-MM-YYYY) *", dischargeDate);
        section.add(heading, BorderLayout.NORTH);
        section.add(fields, BorderLayout.CENTER);
        return section;
    }

    private Panel buildBillingPanel() {
        Panel section = new Panel(new BorderLayout(5, 5));
        section.setBackground(Color.WHITE);
        Label heading = heading("BILLING DETAILS");
        Panel fields = new Panel(new GridLayout(0, 2, 7, 9));
        fields.setBackground(Color.WHITE);
        addField(fields, "Days stayed (whole number) *", daysStayed);
        addField(fields, "Ward / room", wardChoice);
        addField(fields, "Ward rate (demo)", wardRateLabel);
        addField(fields, "Surgery / procedure", surgeryChoice);
        addField(fields, "Surgery rate (demo)", surgeryRateLabel);
        addField(fields, "Doctor charge (INR)", doctorCharge);
        addField(fields, "Medicine charge (INR)", medicineCharge);
        addField(fields, "Tests / diagnostics (INR)", testCharge);
        addField(fields, "Other charges (INR)", otherCharge);
        addField(fields, "Discount (INR)", discount);
        addField(fields, "Demo tax rate (0–100%)", demoTaxRate);
        addField(fields, "Payment mode", paymentChoice);
        section.add(heading, BorderLayout.NORTH);
        section.add(fields, BorderLayout.CENTER);
        return section;
    }

    private Panel buildFooter() {
        Panel footer = new Panel(new GridLayout(2, 1, 4, 4));
        footer.setBackground(new Color(238, 242, 245));
        Label note = new Label("Educational project only. Rates and tax are sample values; do not enter real patient data.", Label.CENTER);
        Panel buttons = new Panel();
        calculateButton.addActionListener(this);
        clearButton.addActionListener(this);
        savedBillsButton.addActionListener(this);
        exitButton.addActionListener(this);
        buttons.add(calculateButton);
        buttons.add(clearButton);
        buttons.add(savedBillsButton);
        buttons.add(exitButton);
        footer.add(note);
        footer.add(buttons);
        return footer;
    }

    private Label heading(String text) {
        Label label = new Label(text, Label.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        label.setForeground(new Color(20, 65, 105));
        return label;
    }

    private void addField(Panel panel, String label, Component component) {
        panel.add(new Label(label));
        panel.add(component);
    }

    private void addChoiceItems() {
        wardChoice.add("General Ward");
        wardChoice.add("Semi-Private Ward");
        wardChoice.add("Private Room");
        wardChoice.add("ICU");

        surgeryChoice.add("No Surgery");
        surgeryChoice.add("Appendix Operation");
        surgeryChoice.add("Eye Operation");
        surgeryChoice.add("Orthopedic Operation");
        surgeryChoice.add("Cardiac Procedure");

        paymentChoice.add("Cash");
        paymentChoice.add("UPI");
        paymentChoice.add("Card");
        paymentChoice.add("Insurance");
    }

    private void addListeners() {
        wardChoice.addItemListener(new ItemListener() {
            @Override public void itemStateChanged(ItemEvent event) { updateWardRate(); }
        });
        surgeryChoice.addItemListener(new ItemListener() {
            @Override public void itemStateChanged(ItemEvent event) { updateSurgeryRate(); }
        });
    }

    private void updateWardRate() {
        wardRateLabel.setText("INR " + BillingService.getWardRate(wardChoice.getSelectedItem()) + " / day");
    }

    private void updateSurgeryRate() {
        surgeryRateLabel.setText("INR " + BillingService.getSurgeryCharge(surgeryChoice.getSelectedItem()));
    }

    /** Retains the user's original Enter-to-next-field usability feature. */
    private void configureEnterNavigation() {
        Component[] order = new Component[] { patientName, patientCode, patientAge,
                relativeName, contactNumber, address, admissionDate, dischargeDate,
                daysStayed, wardChoice, surgeryChoice, doctorCharge, medicineCharge,
                testCharge, otherCharge, discount, demoTaxRate, paymentChoice,
                calculateButton };
        for (int i = 0; i < order.length - 1; i++) {
            moveOnEnter(order[i], order[i + 1]);
        }
    }

    private void moveOnEnter(final Component current, final Component next) {
        current.setFocusTraversalKeysEnabled(false);
        current.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.VK_ENTER) {
                    next.requestFocus();
                }
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();
        if (source == calculateButton) calculateAndSave();
        else if (source == clearButton) clearForm();
        else if (source == savedBillsButton) viewSavedBills();
        else if (source == exitButton) dispose();
    }

    private void calculateAndSave() {
        final Bill bill;
        try {
            bill = BillingService.createBill(patientName.getText(), patientCode.getText(),
                    patientAge.getText(), relativeName.getText(), contactNumber.getText(),
                    address.getText(), admissionDate.getText(), dischargeDate.getText(),
                    daysStayed.getText(), wardChoice.getSelectedItem(),
                    surgeryChoice.getSelectedItem(), doctorCharge.getText(),
                    medicineCharge.getText(), testCharge.getText(), otherCharge.getText(),
                    discount.getText(), demoTaxRate.getText(), paymentChoice.getSelectedItem());
        } catch (IllegalArgumentException ex) {
            showMessage(ex.getMessage());
            return;
        }

        String status;
        if (!databaseConfig.isConfigured()) {
            status = "NOT SAVED: database.properties is not configured. The bill was calculated in memory only.";
        } else {
            try {
                long billId = billRepository.save(bill);
                status = "SAVED TO MYSQL: Bill number " + billId;
                databaseStatusLabel.setText("MariaDB save successful - latest bill number " + billId);
            } catch (SQLException ex) {
                status = "NOT SAVED: MariaDB could not save this bill. Check the local database configuration. Details: " + ex.getMessage();
                databaseStatusLabel.setText("MariaDB save failed - see receipt status.");
            }
        }
        new BillReceiptFrame(bill, status);
    }

    private void viewSavedBills() {
        if (!databaseConfig.isConfigured()) {
            showMessage("MariaDB is not configured. Follow the README instructions to set up database.properties.");
            return;
        }
        new SavedBillsFrame(billRepository);
    }

    private void clearForm() {
        patientName.setText("");
        patientCode.setText("");
        patientAge.setText("");
        relativeName.setText("");
        contactNumber.setText("");
        address.setText("");
        admissionDate.setText("");
        dischargeDate.setText("");
        daysStayed.setText("");
        doctorCharge.setText("0");
        medicineCharge.setText("0");
        testCharge.setText("0");
        otherCharge.setText("0");
        discount.setText("0");
        demoTaxRate.setText("0");
        wardChoice.select(0);
        surgeryChoice.select(0);
        paymentChoice.select(0);
        updateWardRate();
        updateSurgeryRate();
        patientName.requestFocus();
    }

    private void showMessage(String message) {
        final Dialog dialog = new Dialog(this, "Input / Database Message", true);
        dialog.setSize(460, 160);
        dialog.setLayout(new BorderLayout(8, 8));
        Label messageLabel = new Label(message, Label.CENTER);
        Button ok = new Button("OK");
        ok.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent event) { dialog.dispose(); }
        });
        dialog.add(messageLabel, BorderLayout.CENTER);
        Panel buttonPanel = new Panel();
        buttonPanel.add(ok);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(this);
        dialog.addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent event) { dialog.dispose(); }
        });
        dialog.setVisible(true);
    }
}

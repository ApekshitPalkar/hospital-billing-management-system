package com.apekshit.hospital.ui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.List;

import com.apekshit.hospital.db.BillRepository;

public final class SavedBillsFrame extends Frame {
    private final BillRepository repository;
    private final TextArea output;

    public SavedBillsFrame(BillRepository repository) {
        super("Recently Saved Bills");
        this.repository = repository;
        setSize(850, 520);
        setLocation(280, 140);
        setLayout(new BorderLayout(8, 8));
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent event) { dispose(); }
        });
        add(new Label("Recent saved bills (latest 50)", Label.CENTER), BorderLayout.NORTH);
        output = new TextArea("Loading...", 24, 100, TextArea.SCROLLBARS_BOTH);
        output.setEditable(false);
        add(output, BorderLayout.CENTER);
        Panel buttons = new Panel(new FlowLayout());
        Button refresh = new Button("Refresh");
        Button close = new Button("Close");
        refresh.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent event) { loadBills(); }
        });
        close.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent event) { dispose(); }
        });
        buttons.add(refresh);
        buttons.add(close);
        add(buttons, BorderLayout.SOUTH);
        loadBills();
        setVisible(true);
    }

    private void loadBills() {
        try {
            List<String> rows = repository.findRecentSummaries(50);
            if (rows.isEmpty()) {
                output.setText("No bills have been saved yet.");
                return;
            }
            StringBuilder text = new StringBuilder();
            for (String row : rows) text.append(row).append("\n\n");
            output.setText(text.toString());
        } catch (SQLException ex) {
            output.setText("Could not retrieve saved bills. Check that MariaDB is running and database.properties is correct.\n\nDetails: " + ex.getMessage());
        }
    }
}

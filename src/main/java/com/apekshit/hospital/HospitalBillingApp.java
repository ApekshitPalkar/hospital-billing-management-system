package com.apekshit.hospital;

import com.apekshit.hospital.db.BillRepository;
import com.apekshit.hospital.db.DatabaseConfig;
import com.apekshit.hospital.db.DatabaseConnection;
import com.apekshit.hospital.ui.HospitalBillingFrame;

import java.awt.EventQueue;

public final class HospitalBillingApp {
    private HospitalBillingApp() { }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override public void run() {
                DatabaseConfig config = DatabaseConfig.load();
                DatabaseConnection connection = new DatabaseConnection(config);
                BillRepository repository = new BillRepository(connection);
                new HospitalBillingFrame(config, repository);
            }
        });
    }
}

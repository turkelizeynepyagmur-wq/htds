package com.hastane.htds.controller;

import com.hastane.htds.dao.ArizaDao;
import com.hastane.htds.model.Ariza;
import com.hastane.htds.model.Durum;
import com.hastane.htds.model.Personel;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RemoteConnectController {

    @FXML
    private Label targetIpLabel;

    @FXML
    private Label timerLabel;

    @FXML
    private TextArea terminalArea;

    private Ariza ariza;
    private Personel currentUser;
    private final ArizaDao arizaDao = new ArizaDao();

    private Timeline sessionTimer;
    private int secondsElapsed = 0;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void initData(Ariza ariza, Personel currentUser) {
        this.ariza = ariza;
        this.currentUser = currentUser;

        targetIpLabel.setText(ariza.getIpAdresi());
        
        // 1. Terminal Başlangıç Simülasyonu
        simulateStartupLogs();

        // 2. Seans Sayacını Başlat
        startSessionTimer();
    }

    private void startSessionTimer() {
        sessionTimer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            secondsElapsed++;
            int mins = secondsElapsed / 60;
            int secs = secondsElapsed % 60;
            timerLabel.setText(String.format("%02d:%02d", mins, secs));
        }));
        sessionTimer.setCycleCount(Timeline.INDEFINITE);
        sessionTimer.play();
    }

    private void simulateStartupLogs() {
        terminalArea.appendText(">>> HTDS SECURE CONNECTION UTILITY v2.0 <<<\n");
        appendTerminalLog("Connecting to target machine at " + ariza.getIpAdresi() + "...");

        // Aşamalı konsol yazıları simülasyonu
        Timeline logSim = new Timeline();
        
        logSim.getKeyFrames().add(new KeyFrame(Duration.millis(500), e -> 
            appendTerminalLog("Handshake protocol matching... [Success]")
        ));
        logSim.getKeyFrames().add(new KeyFrame(Duration.millis(1000), e -> 
            appendTerminalLog("Authenticating credentials for " + currentUser.getAdSoyad() + "...")
        ));
        logSim.getKeyFrames().add(new KeyFrame(Duration.millis(1500), e -> {
            appendTerminalLog("Access Granted. Loading remote control interface.");
            appendTerminalLog("Remote OS: Windows 11 Enterprise | Host: HTDS-CLIENT-" + ariza.getId());
            appendTerminalLog("CPU Temp: 48°C | RAM Free: 5.4 GB | Latency: 4ms");
            appendTerminalLog("---------------------------------------------------------");
            appendTerminalLog("Uzak bağlantı başarıyla kuruldu. Müdahale komutlarını kullanabilirsiniz.");
        }));
        
        logSim.play();
    }

    @FXML
    private void handleResetNetwork(ActionEvent event) {
        appendCommandLine("ipconfig /release && ipconfig /renew");
        
        Timeline actionSim = new Timeline(new KeyFrame(Duration.millis(800), e -> {
            appendTerminalLog("[RELEASE] IP configuration has been released.");
            appendTerminalLog("[RENEW] Querying DHCP server for new leases...");
            appendTerminalLog("[SUCCESS] Assigned IP address: " + ariza.getIpAdresi());
            appendTerminalLog("[SUCCESS] Network interface cards reset successfully.");
            
            // Veritabanına arıza güncellemesi ve log yazılması
            arizaDao.updateStatusAndAssignment(
                ariza.getId(), 
                Durum.ISLEMDE, 
                currentUser.getId(), 
                currentUser.getId(), 
                "[Uzaktan Destek] Bilgisayarın yerel ağ bağlantı adaptörü başarıyla sıfırlandı."
            );
        }));
        actionSim.play();
    }

    @FXML
    private void handleClearCache(ActionEvent event) {
        appendCommandLine("del /q /f /s %TEMP%\\* && ipconfig /flushdns");
        
        Timeline actionSim = new Timeline(new KeyFrame(Duration.millis(800), e -> {
            appendTerminalLog("[TEMP] Cleared 1,248 temporary cache files (1.42 GB).");
            appendTerminalLog("[DNS] DNS Resolver cache successfully flushed.");
            appendTerminalLog("[SUCCESS] System junk files and DNS resolver caches cleaned.");
            
            arizaDao.updateStatusAndAssignment(
                ariza.getId(), 
                Durum.ISLEMDE, 
                currentUser.getId(), 
                currentUser.getId(), 
                "[Uzaktan Destek] Cihaz sistem önbelleği ve DNS kayıtları uzaktan temizlendi."
            );
        }));
        actionSim.play();
    }

    @FXML
    private void handleRunDiagnostics(ActionEvent event) {
        appendCommandLine("sfc /scannow && systeminfo");
        
        Timeline actionSim = new Timeline(new KeyFrame(Duration.millis(1200), e -> {
            appendTerminalLog("[SCAN] Scanning remote file system integrity...");
            appendTerminalLog("[SCAN] 100% complete. Verification successful.");
            appendTerminalLog("[SUCCESS] No integrity violations found. Hardware registers healthy.");
            appendTerminalLog("[SYSTEM] CPU: Core i7 12700H | RAM: 16 GB | OS Build: 22631");
            
            arizaDao.updateStatusAndAssignment(
                ariza.getId(), 
                Durum.ISLEMDE, 
                currentUser.getId(), 
                currentUser.getId(), 
                "[Uzaktan Destek] Bilgisayar üzerinde genel sistem tanılaması başarıyla tamamlandı."
            );
        }));
        actionSim.play();
    }

    @FXML
    private void handleRestartServices(ActionEvent event) {
        appendCommandLine("net stop spooler && net start spooler");
        
        Timeline actionSim = new Timeline(new KeyFrame(Duration.millis(900), e -> {
            appendTerminalLog("[SERVICES] Stopping spooler and driver services...");
            appendTerminalLog("[SERVICES] Re-initiating network and printing hardware daemons...");
            appendTerminalLog("[SUCCESS] Peripheral hardware routing services restarted successfully.");
            
            arizaDao.updateStatusAndAssignment(
                ariza.getId(), 
                Durum.ISLEMDE, 
                currentUser.getId(), 
                currentUser.getId(), 
                "[Uzaktan Destek] Yazıcı/donanım hizmetleri uzaktan yeniden başlatıldı."
            );
        }));
        actionSim.play();
    }

    @FXML
    private void handleCloseSession(ActionEvent event) {
        appendTerminalLog("Disconnecting from remote host...");
        if (sessionTimer != null) {
            sessionTimer.stop();
        }
        
        // Pencereyi kapat
        Stage stage = (Stage) terminalArea.getScene().getWindow();
        stage.close();
    }

    private void appendTerminalLog(String message) {
        String timestamp = LocalDateTime.now().format(TIME_FORMATTER);
        terminalArea.appendText("[" + timestamp + "] " + message + "\n");
        terminalArea.selectPositionCaret(terminalArea.getLength()); // Auto-scroll to bottom
    }

    private void appendCommandLine(String command) {
        terminalArea.appendText("\n> " + command + "\n");
        terminalArea.selectPositionCaret(terminalArea.getLength());
    }
}

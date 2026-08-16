package com.hastane.htds.controller;

import com.hastane.htds.service.AuthService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.prefs.Preferences;
import java.util.Locale;
import java.util.ResourceBundle;
import com.hastane.htds.util.LanguageManager;

public class LoginController {

    @FXML
    private TextField tcField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button loginButton;

    @FXML
    private CheckBox rememberMeCheckbox;

    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        // Beni Hatırla değerlerini yerel ayarlardan yükle
        Preferences prefs = Preferences.userNodeForPackage(LoginController.class);
        boolean remember = prefs.getBoolean("rememberMe", false);
        if (remember) {
            tcField.setText(prefs.get("savedTc", ""));
            rememberMeCheckbox.setSelected(true);
        }
    }

    @FXML
    private void handleLogin() {
        String kullaniciAdi = tcField.getText();
        String sifre = passwordField.getText();

        if (kullaniciAdi == null || kullaniciAdi.isBlank() || sifre == null || sifre.isBlank()) {
            showError("Kullanıcı adı ve şifre boş bırakılamaz.");
            return;
        }

        try {
            // AuthService üzerinden girişi doğrula
            authService.login(kullaniciAdi, sifre);
            System.out.println("Giriş başarılı. Dashboard yükleniyor...");

            // Beni Hatırla tercihlerini kaydet veya temizle
            Preferences prefs = Preferences.userNodeForPackage(LoginController.class);
            if (rememberMeCheckbox.isSelected()) {
                prefs.put("savedTc", kullaniciAdi);
                prefs.putBoolean("rememberMe", true);
            } else {
                prefs.remove("savedTc");
                prefs.putBoolean("rememberMe", false);
            }

            // Dashboard ekranına geçiş yap
            loadDashboard();

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    // EĞİİTİM MODU HIZLI TEST GİRİ?ş KISAYOLLARI
    @FXML
    private void handleQuickAdmin() {
        tcField.setText("admin");
        passwordField.setText("12345");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }

    @FXML
    private void handleQuickTeknisyen() {
        tcField.setText("teknisyen");
        passwordField.setText("12345");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }

    @FXML
    private void handleQuickPersonel() {
        tcField.setText("personel");
        passwordField.setText("12345");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }

    private void loadDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Objects.requireNonNull(getClass().getResource("/com/hastane/htds/view/dashboard.fxml")),
                    LanguageManager.getBundle()
            );
            Parent root = loader.load();

            Stage stage = (Stage) tcField.getScene().getWindow();
            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(
                    Objects.requireNonNull(getClass().getResource("/com/hastane/htds/view/dashboard.css")).toExternalForm()
            );
            scene.getStylesheets().add(
                    Objects.requireNonNull(getClass().getResource("/com/hastane/htds/view/touch-friendly.css")).toExternalForm()
            );

            // Mevcut login ekranının sahnesini değiştir
            stage.setScene(scene);
            stage.setTitle("Hastane Arıza Takip & HTDS Sistemi - Dashboard");
            stage.setMaximized(true);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            Throwable cause = e;
            while (cause.getCause() != null) {
                cause = cause.getCause();
            }
            showError("Dashboard yüklenirken hata oluştu: " + cause.toString());
        }
    }

    private void showError(String mesaj) {
        errorLabel.setText(mesaj);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    @FXML
    private void handleLangTR() {
        LanguageManager.setLocale(new Locale("tr", "TR"));
        reloadLogin();
    }

    @FXML
    private void handleLangEN() {
        LanguageManager.setLocale(new Locale("en", "US"));
        reloadLogin();
    }

    private void reloadLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Objects.requireNonNull(getClass().getResource("/com/hastane/htds/view/login.fxml")),
                    LanguageManager.getBundle()
            );
            Parent root = loader.load();
            Stage stage = (Stage) tcField.getScene().getWindow();
            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(
                    Objects.requireNonNull(getClass().getResource("/com/hastane/htds/view/login.css")).toExternalForm()
            );
            scene.getStylesheets().add(
                    Objects.requireNonNull(getClass().getResource("/com/hastane/htds/view/touch-friendly.css")).toExternalForm()
            );
            stage.setMaximized(true);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
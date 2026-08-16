package com.hastane.htds.controller;

import com.hastane.htds.dao.ArizaDao;
import com.hastane.htds.dao.KategoriDao;
import com.hastane.htds.model.Ariza;
import com.hastane.htds.model.Durum;
import com.hastane.htds.model.Kategori;
import com.hastane.htds.model.IsTipi;
import com.hastane.htds.model.Oncelik;
import com.hastane.htds.model.Personel;
import com.hastane.htds.service.AuthService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.concurrent.Task;

import java.util.List;

public class ArizaBildirController {

    @FXML
    private TextField titleField;

    @FXML
    private ComboBox<IsTipi> isTipiCombo;

    @FXML
    private ComboBox<Kategori> kategoriCombo;

    @FXML
    private ComboBox<Oncelik> oncelikCombo;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private Label feedbackLabel;

    @FXML
    private VBox categoryHintPane;

    @FXML
    private Label categoryHintLabel;

    @FXML
    private Button geminiAskBtn;

    private final KategoriDao kategoriDao = new KategoriDao();
    private final ArizaDao arizaDao = new ArizaDao();

    @FXML
    public void initialize() {
        // 1. Kategorileri veritabanından çekip ComboBox'a doldur
        List<Kategori> kategoriler = kategoriDao.findAll();
        kategoriCombo.setItems(FXCollections.observableArrayList(kategoriler));

        // 2. Öncelikleri ve İş Tiplerini enum değerleriyle doldur
        oncelikCombo.setItems(FXCollections.observableArrayList(Oncelik.values()));
        oncelikCombo.setValue(Oncelik.NORMAL); // Varsayılan normal öncelik
        
        isTipiCombo.setItems(FXCollections.observableArrayList(IsTipi.values()));
        isTipiCombo.setValue(IsTipi.ARIZA); // Varsayılan tip

        // Geri bildirim etiketini gizle
        hideFeedback();

        // 3. Kategori seçim değişiminde ipucu göster
        kategoriCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                String hint = getHintForCategory(newVal.getId());
                if (hint != null) {
                    categoryHintLabel.setText(hint);
                    categoryHintPane.setVisible(true);
                    categoryHintPane.setManaged(true);
                } else {
                    categoryHintPane.setVisible(false);
                    categoryHintPane.setManaged(false);
                }
            } else {
                categoryHintPane.setVisible(false);
                categoryHintPane.setManaged(false);
            }
        });
    }

    private String getHintForCategory(int categoryId) {
        return switch (categoryId) {
            case 1 -> "Ekranınızda MYS (Malzeme Yönetim Sistemi) açılmıyorsa veya hata veriyorsa, bilgisayarınızın masaüstünde duran mavi logolu 'MYS Kurulum' programına çift tıklayıp açılan ekranda 'Güncelle' veya 'Kur' butonuna basarak kurulumu tazeleyebilirsiniz.";
            case 2 -> "Barkod yazıcınız etiket basmıyorsa veya ekranınız donduysa, cihazların arkasındaki siyah elektrik kablosunu prizden çekip 10 saniye bekleyin, ardından geri takarak cihazları yeniden başlatın. Bu işlem yazıcı hatalarını genellikle hemen düzeltir.";
            case 3 -> "İnternet bağlantınız koptuysa, bilgisayar kasasının arkasında takılı duran gri veya mavi renkli kalın internet kablosunun yerinden gevşeyip gevşemediğini kontrol edin, elinizle hafifçe ittirerek yuvaya tam oturtun.";
            case 4 -> "Tıbbi ölçüm cihazınız hata veriyorsa, cihazı kapatıp 15 saniye bekleyin. Cihaza giren kabloları söküp tekrar taktıktan sonra cihazı açın. Ekranda 'Error 102' gibi bir hata kodu varsa lütfen bunu aşağıdaki açıklama kısmına yazın.";
            default -> null;
        };
    }

    @FXML
    private void handleAskGemini(ActionEvent event) {
        String baslik = titleField.getText();
        String aciklama = descriptionArea.getText();
        Kategori kategori = kategoriCombo.getValue();

        if (baslik == null || baslik.isBlank()) {
            categoryHintLabel.setText("⚠️ Lütfen Gemini analizi için önce arıza başlığı girin.");
            return;
        }

        categoryHintLabel.setText("🤖 Gemini yapay zekası arızayı inceliyor, lütfen bekleyin...");
        geminiAskBtn.setDisable(true);

        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String katAd = (kategori != null) ? kategori.getAd() : "Genel";
                return com.hastane.htds.service.GeminiService.getTroubleshootingAdvice(baslik, aciklama, katAd, false);
            }
        };

        task.setOnSucceeded(e -> {
            categoryHintLabel.setText(task.getValue());
            geminiAskBtn.setDisable(false);
        });

        task.setOnFailed(e -> {
            categoryHintLabel.setText("⚠️ Gemini bağlantısı sırasında hata oluştu.");
            geminiAskBtn.setDisable(false);
        });

        new Thread(task).start();
    }

    @FXML
    private void handleSubmit(ActionEvent event) {
        String baslik = titleField.getText();
        String aciklama = descriptionArea.getText();
        Kategori kategori = kategoriCombo.getValue();
        Oncelik oncelik = oncelikCombo.getValue();
        IsTipi isTipi = isTipiCombo.getValue();

        // Doğrulamalar
        if (baslik == null || baslik.isBlank()) {
            showError("Lütfen iş/görev için kısa bir başlık girin.");
            return;
        }
        if (kategori == null) {
            showError("Lütfen kategoriyi seçin.");
            return;
        }
        if (isTipi == null) {
            showError("Lütfen kayıt tipini seçin.");
            return;
        }
        if (oncelik == null) {
            showError("Lütfen öncelik derecesini seçin.");
            return;
        }

        Personel loggedIn = AuthService.getLoggedPersonel();
        if (loggedIn == null) {
            showError("Oturum açık değil. Lütfen tekrar giriş yapın.");
            return;
        }

        // Arıza Modeli Oluşturma
        Ariza ariza = new Ariza();
        ariza.setBaslik(baslik);
        ariza.setAciklama(aciklama);
        ariza.setKategoriId(kategori.getId());
        ariza.setDepartmanId(loggedIn.getDepartmanId()); // Bildiren personelin departmanına atıyoruz
        ariza.setOncelik(oncelik);
        ariza.setIsTipi(isTipi);
        ariza.setDurum(Durum.YENI);
        ariza.setBildirenPersonelId(loggedIn.getId());

        // Bilgisayarın yerel IP Adresini otomatik tespit et
        String ipAdresi = "Bilinmiyor";
        try {
            ipAdresi = java.net.InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            System.err.println("Yerel IP adresi alınamadı: " + e.getMessage());
        }
        ariza.setIpAdresi(ipAdresi);

        // Veritabanına Kaydet
        boolean success = arizaDao.insert(ariza);

        if (success) {
            showSuccess("Bildirim başarıyla kaydedildi!");
            clearForm();
        } else {
            showError("Bildirim kaydedilirken sistemsel bir hata oluştu.");
        }
    }

    private void clearForm() {
        titleField.clear();
        descriptionArea.clear();
        kategoriCombo.setValue(null);
        isTipiCombo.setValue(IsTipi.ARIZA);
        oncelikCombo.setValue(Oncelik.NORMAL);
    }

    private void showSuccess(String message) {
        feedbackLabel.setText(message);
        feedbackLabel.getStyleClass().clear();
        feedbackLabel.getStyleClass().add("success-message");
        feedbackLabel.setVisible(true);
        feedbackLabel.setManaged(true);
    }

    private void showError(String message) {
        feedbackLabel.setText(message);
        feedbackLabel.getStyleClass().clear();
        feedbackLabel.getStyleClass().add("error-message");
        feedbackLabel.setVisible(true);
        feedbackLabel.setManaged(true);
    }

    private void hideFeedback() {
        feedbackLabel.setVisible(false);
        feedbackLabel.setManaged(false);
    }
}

package com.hastane.htds.controller;

import com.hastane.htds.model.Personel;
import com.hastane.htds.model.Rol;
import com.hastane.htds.service.AuthService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.Map;
import com.hastane.htds.dao.ArizaDao;
import com.hastane.htds.util.LanguageManager;

public class DashboardController {

    @FXML
    private BorderPane rootPane;

    @FXML
    private VBox profileBox;

    @FXML
    private Label avatarLabel;

    @FXML
    private Label nameLabel;

    @FXML
    private Label roleLabel;

    @FXML
    private Label deptLabel;

    @FXML
    private Label welcomeTitle;

    @FXML
    private Button dashboardBtn;

    @FXML
    private Button arizaBildirBtn;

    @FXML
    private Button arizalarBtn;

    @FXML
    private Button personelBtn;

    @FXML
    private Button rehberBtn;

    @FXML
    private Label pendingCountLabel;

    @FXML
    private Label inProgressCountLabel;

    @FXML
    private Label solvedCountLabel;

    @FXML
    private Label tech1Label;

    @FXML
    private Label tech2Label;

    @FXML private javafx.scene.layout.HBox chartsRow;
    @FXML private javafx.scene.layout.VBox heatmapContainer;
    @FXML private javafx.scene.layout.VBox recentActivityContainer;
    @FXML private javafx.scene.layout.VBox recentActivityBox;
    @FXML private javafx.scene.layout.HBox chartBox;
    @FXML private Label heatmapTitle;
    @FXML private javafx.scene.layout.HBox statsContainer;
    @FXML private Label pendingTitleLabel;
    @FXML private Label inProgressTitleLabel;
    @FXML private Label solvedTitleLabel;

    private final com.hastane.htds.dao.PersonelDao personelDao = new com.hastane.htds.dao.PersonelDao();
    private final com.hastane.htds.dao.ArizaDao arizaDao = new com.hastane.htds.dao.ArizaDao();
    private javafx.scene.Node defaultCenter;

    @FXML
    public void initialize() {
        // İlk açılıştaki kontrol paneli görünümünü saklayalım
        defaultCenter = rootPane.getCenter();

        // Oturum açan kullanıcının bilgilerini yükle
        Personel user = AuthService.getLoggedPersonel();

        if (user != null) {
            // İsim Soyisim ve Avatar set et
            nameLabel.setText(user.getAdSoyad());
            avatarLabel.setText(com.hastane.htds.util.GenderUtil.getGenderEmoji(user.getAd()));
            welcomeTitle.setText(String.format(LanguageManager.getString("dashboard.welcome.title"), user.getAd() + " " + user.getSoyad()));

            // İstatistik sayılarını güncelle
            refreshStats();
            
            // Nöbetçi Teknisyenleri yükle
            loadOnDutyTechs();

            // Cinsiyete göre kadın/erkek avatar ikonu yerleştir
            avatarLabel.setText(com.hastane.htds.util.GenderUtil.getGenderEmoji(user.getAd()));
            avatarLabel.setStyle("-fx-font-size: 20px;");

            // Rol badge ve stil ataması
            roleLabel.setText(user.getRol().name());
            roleLabel.getStyleClass().clear();
            roleLabel.getStyleClass().add("user-role-badge");

            switch (user.getRol()) {
                case ADMIN:
                    roleLabel.getStyleClass().add("role-admin");
                    break;
                case TEKNISYEN:
                    roleLabel.getStyleClass().add("role-teknisyen");
                    break;
                case YONETICI:
                    roleLabel.getStyleClass().add("role-yonetici");
                    break;
                case PERSONEL:
                default:
                    roleLabel.getStyleClass().add("role-personel");
                    break;
            }

            // Departman adı
            String deptName = getDepartmentName(user.getDepartmanId());
            deptLabel.setText(deptName);

            // Yetkiye göre butonları kısıtla/düzenle
            configurePermissions(user.getRol());
        } else {
            welcomeTitle.setText(String.format(LanguageManager.getString("dashboard.welcome.title"), "Ziyaretçi"));
            deptLabel.setText(LanguageManager.getString("general.error"));
        }
    }

    private void loadOnDutyTechs() {
        java.util.List<Personel> techs = personelDao.findTeknisyenler();
        if (techs.isEmpty()) {
            tech1Label.setText("Şu an nöbetçi teknisyen bulunmuyor.");
            tech2Label.setText("");
        } else {
            Personel t1 = techs.get(0);
            tech1Label.setText(t1.getDepartmanAd() + " Dahili: " + (t1.getDahiliNo() != null ? t1.getDahiliNo() : "-") + " (" + t1.getAdSoyad() + ")");
            if (techs.size() > 1) {
                Personel t2 = techs.get(1);
                tech2Label.setText(t2.getDepartmanAd() + " Dahili: " + (t2.getDahiliNo() != null ? t2.getDahiliNo() : "-") + " (" + t2.getAdSoyad() + ")");
            } else {
                tech2Label.setText("");
            }
        }
    }

    /**
     * Kullanıcı rolüne göre arayüzdeki buton kısıtlamalarını yönetir.
     */
    private void configurePermissions(Rol rol) {
        // Sadece ADMIN personel yönetimi sayfasını görebilir ve tıklayabilir
        if (rol != Rol.ADMIN) {
            personelBtn.setDisable(true);
            personelBtn.setVisible(false);
            personelBtn.setManaged(false);
        }

        // Rol bazlı dashboard dizaynı
        if (rol == Rol.ADMIN) {
            // ADMIN: Her şeyi görür
            chartsRow.setVisible(true);
            chartsRow.setManaged(true);
            loadHeatmap();
            loadRecentActivities();
        } else if (rol == Rol.TEKNISYEN) {
            // TEKNISYEN: Kırmızı kod ve genel ısı haritası
            chartsRow.setVisible(true);
            chartsRow.setManaged(true);
            pendingTitleLabel.setText("BANA ATANAN / BEKLEYEN");
            loadHeatmap();
            loadRecentActivities();
        } else if (rol == Rol.YONETICI) {
            // YONETICI: Kırmızı kod yok, kendi departmanının pastası
            chartsRow.setVisible(true);
            chartsRow.setManaged(true);
            loadManagerChart();
            loadRecentActivities();
        } else if (rol == Rol.PERSONEL) {
            // PERSONEL: Sade ekran, kendi bildirdikleri
            chartsRow.setVisible(false);
            chartsRow.setManaged(false);
            pendingTitleLabel.setText("BİLDİRDİĞİM (BEKLEYEN)");
            inProgressTitleLabel.setText("BİLDİRDİĞİM (İŞLEMDE)");
            solvedTitleLabel.setText("BİLDİRDİĞİM (ÇÖZÜLEN)");
        }
    }

    private void loadHeatmap() {
        heatmapContainer.setMaxWidth(350); // Küçük bir card olması için genişliği sınırla
        heatmapTitle.setText("Aktif Arızalar (Departman)");
        chartBox.getChildren().clear();
        
        Map<String, Integer> stats = arizaDao.getActiveFaultsByDepartment();
        if (stats.isEmpty()) {
            chartBox.getChildren().add(new Label("Bekleyen arıza yok. g???"));
            return;
        }

        javafx.scene.chart.PieChart pieChart = new javafx.scene.chart.PieChart();
        pieChart.getStyleClass().add("heatmap-pie"); // Mavi-Kırmızı stil ataması için
        
        for (Map.Entry<String, Integer> entry : stats.entrySet()) {
            String sliceLabel = String.format("%s: %d", entry.getKey(), entry.getValue());
            pieChart.getData().add(new javafx.scene.chart.PieChart.Data(sliceLabel, entry.getValue()));
        }
        
        pieChart.setLabelsVisible(true);
        pieChart.setLabelLineLength(10);
        pieChart.setLegendVisible(false); // Küçük olduğu için sadece dilim etiketleri görünsün
        pieChart.setPrefHeight(220);
        pieChart.setPrefWidth(300);
        
        setupSliceInteractivity(pieChart, "Departman Isı Haritası");
        chartBox.getChildren().add(pieChart);
    }

    private void loadManagerChart() {
        heatmapContainer.setMaxWidth(350);
        Personel user = AuthService.getLoggedPersonel();
        String deptName = getDepartmentName(user.getDepartmanId());
        heatmapTitle.setText("Departman Kategorileri");
        chartBox.getChildren().clear();

        Map<String, Integer> stats = arizaDao.getDepartmentFaultStats(user.getDepartmanId() != null ? user.getDepartmanId() : 0);
        if (stats.isEmpty()) {
            chartBox.getChildren().add(new Label("Departmanınızda şu an kayıtlı aktif arıza bulunmuyor."));
            return;
        }

        javafx.scene.chart.PieChart pieChart = new javafx.scene.chart.PieChart();
        for (Map.Entry<String, Integer> entry : stats.entrySet()) {
            // ??ık bir şekilde değerleri kategori isminin yanında gösterelim
            String sliceLabel = String.format("%s: %d", entry.getKey(), entry.getValue());
            pieChart.getData().add(new javafx.scene.chart.PieChart.Data(sliceLabel, entry.getValue()));
        }
        
        pieChart.setTitle("Kategori Dağılımı");
        pieChart.setLabelsVisible(true);
        pieChart.setLabelLineLength(15);
        pieChart.setLegendVisible(true);
        pieChart.setLegendSide(javafx.geometry.Side.RIGHT); // Lejantı (Göstergeyi) sağa alıyoruz, çok daha şık durur.
        pieChart.setPrefHeight(280);
        pieChart.setPrefWidth(600);
        
        setupSliceInteractivity(pieChart, "Kategori Dağılımı Detayı");
        chartBox.getChildren().add(pieChart);
    }

    private void loadRecentActivities() {
        recentActivityBox.getChildren().clear();
        Personel user = AuthService.getLoggedPersonel();
        java.util.List<com.hastane.htds.model.Ariza> arizalar;
        
        if (user.getRol() == Rol.YONETICI) {
            arizalar = arizaDao.findByDepartman(user.getDepartmanId() != null ? user.getDepartmanId() : 0);
        } else {
            arizalar = arizaDao.findAll();
        }
        
        int count = 0;
        for (com.hastane.htds.model.Ariza a : arizalar) {
            if (count >= 3) break;
            
            javafx.scene.layout.VBox box = new javafx.scene.layout.VBox(4);
            box.setStyle("-fx-background-color: #ffffff; -fx-padding: 12; -fx-background-radius: 8; -fx-border-color: #e2e8f0; -fx-border-radius: 8;");
            
            Label titleLbl = new Label(a.getBaslik() + " [" + a.getDurum().name() + "]");
            titleLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #0f172a; -fx-font-size: 13px;");
            
            Label infoLbl = new Label("Kayıt: " + a.getBildirenAdSoyad() + " | Tarih: " + (a.getOlusturmaTarihi() != null ? a.getOlusturmaTarihi().toLocalDate().toString() : "-"));
            infoLbl.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;");
            
            box.getChildren().addAll(titleLbl, infoLbl);
            recentActivityBox.getChildren().add(box);
            
            count++;
        }
        
        if (count == 0) {
            recentActivityBox.getChildren().add(new Label("Henüz bir aktivite bulunmuyor."));
        }
    }

    private void setupSliceInteractivity(javafx.scene.chart.PieChart pieChart, String tipTitle) {
        for (javafx.scene.chart.PieChart.Data data : pieChart.getData()) {
            javafx.scene.Node slice = data.getNode();
            if (slice != null) {
                applySliceEvents(slice, data.getName(), data.getPieValue(), tipTitle);
            }
            data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    applySliceEvents(newNode, data.getName(), data.getPieValue(), tipTitle);
                }
            });
        }
    }

    private void applySliceEvents(javafx.scene.Node slice, String name, double value, String tipTitle) {
        javafx.scene.control.Tooltip tooltip = new javafx.scene.control.Tooltip(name + "\nDetaylar için tıklayın.");
        tooltip.setStyle("-fx-font-size: 13px; -fx-padding: 8px;");
        javafx.scene.control.Tooltip.install(slice, tooltip);
        
        slice.setOnMouseEntered(e -> {
            slice.setOpacity(0.7);
            slice.setCursor(javafx.scene.Cursor.HAND);
        });
        slice.setOnMouseExited(e -> slice.setOpacity(1.0));
        
        slice.setOnMouseClicked(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Grafik Bilgisi");
            alert.setHeaderText(tipTitle);
            alert.setContentText("Seçilen Alan: " + name + "\n\nBu kategoride/departmanda şu an " + (int)value + " adet aktif arıza bulunmaktadır.\nTüm kayıtları görmek ve işlem yapmak için sol menüden 'Arızalar' sayfasına geçebilirsiniz.");
            alert.showAndWait();
        });
    }

    /**
     * Departman ID'sine göre kullanıcı dostu isim döndürür.
     */
    private String getDepartmentName(Integer deptId) {
        if (deptId == null) return "Departmansız";
        return switch (deptId) {
            case 1 -> "Bilgi İşlem Departmanı";
            case 2 -> "Teknik Servis Departmanı";
            case 3 -> "İnsan Kaynakları Departmanı";
            case 4 -> "Kardiyoloji Polikliniği";
            default -> "Departman ID: " + deptId;
        };
    }

    private void setActiveNavButton(Button activeBtn) {
        dashboardBtn.getStyleClass().remove("nav-button-active");
        arizaBildirBtn.getStyleClass().remove("nav-button-active");
        arizalarBtn.getStyleClass().remove("nav-button-active");
        rehberBtn.getStyleClass().remove("nav-button-active");
        personelBtn.getStyleClass().remove("nav-button-active");

        activeBtn.getStyleClass().add("nav-button-active");
    }

    /**
     * Navigasyon menüsündeki butonlara tıklanıldığında ilgili paneli yükler.
     */
    @FXML
    private void handleNavClick(ActionEvent event) {
        Button sourceBtn = (Button) event.getSource();
        setActiveNavButton(sourceBtn);

        if (sourceBtn == dashboardBtn) {
            refreshStats();
            rootPane.setCenter(defaultCenter);
        } else if (sourceBtn == arizaBildirBtn) {
            loadSubView("/com/hastane/htds/view/ariza_bildir.fxml");
        } else if (sourceBtn == arizalarBtn) {
            loadSubView("/com/hastane/htds/view/arizalar.fxml");
        } else if (sourceBtn == rehberBtn) {
            loadSubView("/com/hastane/htds/view/rehber.fxml");
        } else if (sourceBtn == personelBtn) {
            loadSubView("/com/hastane/htds/view/personel_yonetimi.fxml");
        }
    }

    @FXML
    private void handlePendingCardClick() {
        setActiveNavButton(arizalarBtn);
        Object controller = loadSubViewAndGetController("/com/hastane/htds/view/arizalar.fxml");
        if (controller instanceof ArizalarController) {
            ((ArizalarController) controller).filterByStatus("YENI");
        }
    }

    @FXML
    private void handleInProgressCardClick() {
        setActiveNavButton(arizalarBtn);
        Object controller = loadSubViewAndGetController("/com/hastane/htds/view/arizalar.fxml");
        if (controller instanceof ArizalarController) {
            ((ArizalarController) controller).filterByStatus("ISLEMDE");
        }
    }

    @FXML
    private void handleSolvedCardClick() {
        setActiveNavButton(arizalarBtn);
        Object controller = loadSubViewAndGetController("/com/hastane/htds/view/arizalar.fxml");
        if (controller instanceof ArizalarController) {
            ((ArizalarController) controller).filterByStatus("COZULDU");
        }
    }



    /**
     * Dashboard istatistik sayılarını veritabanından çekip günceller.
     */
    public void refreshStats() {
        Personel user = AuthService.getLoggedPersonel();
        if (user != null) {
            Map<String, Integer> stats = arizaDao.getStats(user);
            pendingCountLabel.setText(String.valueOf(stats.get("bekleyen")));
            inProgressCountLabel.setText(String.valueOf(stats.get("islemde")));
            solvedCountLabel.setText(String.valueOf(stats.get("cozulen")));
            
            // Grafikleri ve Aktiviteleri de güncelle
            if (user.getRol() == Rol.ADMIN || user.getRol() == Rol.TEKNISYEN) {
                loadHeatmap();
            } else if (user.getRol() == Rol.YONETICI) {
                loadManagerChart();
            }
            
            if (user.getRol() != Rol.PERSONEL) {
                loadRecentActivities();
            }
        }
    }

    /**
     * Verilen FXML dosyasını yükler ve BorderPane'in merkezine (center) yerleştirir.
     */
    private void loadSubView(String fxmlPath) {
        loadSubViewAndGetController(fxmlPath);
    }

    /**
     * Verilen FXML dosyasını yükler, BorderPane'in merkezine yerleştirir ve Controller nesnesini döner.
     */
    private Object loadSubViewAndGetController(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(fxmlPath)), LanguageManager.getBundle());
            Parent view = loader.load();
            rootPane.setCenter(view);
            return loader.getController();
        } catch (IOException e) {
            System.err.println("Alt sayfa yüklenirken hata: " + fxmlPath + " | " + e.getMessage());
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Sistem Hatası");
            alert.setHeaderText("Sayfa Yüklenemedi");
            alert.setContentText("Görünüm yüklenirken beklenmedik bir hata oluştu: " + e.getMessage());
            alert.showAndWait();
            return null;
        }
    }

    /**
     * Oturumu kapatıp Login ekranına geri döner.
     */
    @FXML
    private void handleLogout(ActionEvent event) {
        AuthService.logout();

        try {
            FXMLLoader loader = new FXMLLoader(
                    Objects.requireNonNull(getClass().getResource("/com/hastane/htds/view/login.fxml")),
                    LanguageManager.getBundle()
            );
            Parent root = loader.load();

            Stage stage = (Stage) rootPane.getScene().getWindow();
            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(
                    Objects.requireNonNull(getClass().getResource("/com/hastane/htds/view/login.css")).toExternalForm()
            );
            scene.getStylesheets().add(
                    Objects.requireNonNull(getClass().getResource("/com/hastane/htds/view/touch-friendly.css")).toExternalForm()
            );

            stage.setScene(scene);
            stage.setTitle("Hastane Arıza Takip Sistemi - Giriş");
            stage.show();

        } catch (IOException e) {
            System.err.println("Giriş ekranına dönerken hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
package com.hastane.htds.controller;

import com.hastane.htds.dao.ArizaDao;
import com.hastane.htds.model.Ariza;
import com.hastane.htds.model.Durum;
import com.hastane.htds.model.Personel;
import com.hastane.htds.model.Rol;
import com.hastane.htds.service.AuthService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.util.List;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import java.time.format.DateTimeFormatter;
import com.hastane.htds.dao.ArizaLogDao;
import com.hastane.htds.model.ArizaLog;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextInputDialog;
import java.util.Optional;
import javafx.concurrent.Task;
import javafx.scene.control.TableRow;

public class ArizalarController {

    @FXML
    private TableView<Ariza> arizaTable;

    @FXML
    private TableColumn<Ariza, Integer> idCol;

    @FXML
    private TableColumn<Ariza, String> isTipiCol;

    @FXML
    private TableColumn<Ariza, String> baslikCol;

    @FXML
    private TableColumn<Ariza, String> kategoriCol;

    @FXML
    private TableColumn<Ariza, String> departmanCol;

    @FXML
    private TableColumn<Ariza, String> oncelikCol;

    @FXML
    private TableColumn<Ariza, String> durumCol;

    @FXML
    private TableColumn<Ariza, String> bildirenCol;

    @FXML
    private TableColumn<Ariza, String> teknisyenCol;

    // Saş Detay Kartş Elemanlar?
    @FXML
    private VBox noSelectionPane;

    @FXML
    private VBox detailsContentPane;

    @FXML
    private Label detailIdLabel;

    @FXML
    private Label detailTitleLabel;

    @FXML
    private Label detailDescLabel;

    @FXML
    private Label detailBildirenLabel;

    @FXML
    private Label detailKategoriLabel;

    @FXML
    private Label detailOncelikLabel;

    @FXML
    private Label detailTeknisyenLabel;

    @FXML
    private Label detailIpLabel;

    @FXML
    private Button remoteConnectBtn;

    // İşlem B?l?m?
    @FXML
    private VBox actionPane;

    @FXML
    private ComboBox<Durum> statusCombo;

    @FXML
    private TextField logCommentField;

    @FXML
    private Button claimBtn;

    @FXML
    private Button updateStatusBtn;

    @FXML
    private VBox logTimelineContainer;

    @FXML
    private VBox reporterActionPane;

    @FXML
    private VBox detailsHintPane;

    @FXML
    private Label detailsHintLabel;

    @FXML
    private Button geminiDiagnosticBtn;

    private final ArizaDao arizaDao = new ArizaDao();
    private final ArizaLogDao arizaLogDao = new ArizaLogDao();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private Personel currentUser;

    @FXML
    public void initialize() {
        currentUser = AuthService.getLoggedPersonel();

        // 1. Tablo Kolonlar?nş Modele E?le
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        isTipiCol.setCellValueFactory(new PropertyValueFactory<>("isTipi"));
        baslikCol.setCellValueFactory(new PropertyValueFactory<>("baslik"));
        kategoriCol.setCellValueFactory(new PropertyValueFactory<>("kategoriAd"));
        departmanCol.setCellValueFactory(new PropertyValueFactory<>("departmanAd"));
        oncelikCol.setCellValueFactory(new PropertyValueFactory<>("oncelik"));
        durumCol.setCellValueFactory(new PropertyValueFactory<>("durum"));
        bildirenCol.setCellValueFactory(new PropertyValueFactory<>("bildirenAdSoyad"));
        teknisyenCol.setCellValueFactory(new PropertyValueFactory<>("teknisyenAdSoyad"));

        // 2. Verileri Y?kle
        refreshTable();

        // 3. Tablo Se?im Dinleyicisi Ekle
        arizaTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                showDetails(newVal);
            } else {
                hideDetails();
            }
        });

        // 4. Durum Combo kutusunu doldur
        statusCombo.setItems(FXCollections.observableArrayList(Durum.values()));
        
        // 5. SLA Takibi i?in Tablo Sat?r Renklendirmesi (RowFactory)
        arizaTable.setRowFactory(tv -> new TableRow<Ariza>() {
            @Override
            protected void updateItem(Ariza item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else {
                    if (item.isSlaIhlali()) {
                        // S?resi ge?miş olanlar Açık Kırmızı arka plan
                        setStyle("-fx-background-color: #fee2e2;");
                    } else if (item.getDurum() == Durum.COZULDU || item.getDurum() == Durum.KAPATILDI) {
                        // Çözülmüş olanlar Açık Yeşil
                        setStyle("-fx-background-color: #f0fdf4;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });
    }

    /**
     * Oturum a?an kullanıcın?n rol?ne g?re arızalarş veritaban?ndan ?eker ve tabloyu g?nceller.
     */
    private void refreshTable() {
        if (currentUser == null) return;

        List<Ariza> arizalar;
        Rol rol = currentUser.getRol();

        if (rol == Rol.ADMIN) {
            // Admin her ?eyi g?r?r
            arizalar = arizaDao.findAll();
        } else if (rol == Rol.YONETICI) {
            // Y?netici kendi departman?ndaki t?m arızalarş g?r?r
            arizalar = arizaDao.findByDepartman(currentUser.getDepartmanId() != null ? currentUser.getDepartmanId() : 0);
        } else if (rol == Rol.TEKNISYEN) {
            // Teknisyen kendi ?zerine atananlarş ve hen?z atanmam?ş olan t?m arızalarş g?r?r
            arizalar = arizaDao.findByTeknisyen(currentUser.getId());
        } else {
            // Normal Personel sadece kendi bildirdi?i arızalarş g?r?r
            arizalar = arizaDao.findByBildiren(currentUser.getId());
        }

        arizaTable.setItems(FXCollections.observableArrayList(arizalar));
    }

    /**
     * Tabloyu belirli bir arıza durumuna g?re filtreler.
     */
    public void filterByStatus(String statusStr) {
        refreshTable(); // Veritaban?ndan g?ncel veriyi ?ek
        if (statusStr == null) return;

        javafx.collections.transformation.FilteredList<Ariza> filteredData = new javafx.collections.transformation.FilteredList<>(
                FXCollections.observableArrayList(arizaTable.getItems()),
                a -> {
                    if (statusStr.equalsIgnoreCase("YENI")) {
                        // "Bekleyen" arızalar: Yeni veya teknisyene Atanm?ş ama hen?z işlem ba?lat?lmam?ş olanlar
                        return a.getDurum() == Durum.YENI || a.getDurum() == Durum.ATANDI;
                    } else if (statusStr.equalsIgnoreCase("ISLEMDE")) {
                        // "İşlemdeki" arızalar
                        return a.getDurum() == Durum.ISLEMDE;
                    } else if (statusStr.equalsIgnoreCase("COZULDU")) {
                        // "Çözülen" arızalar: Çözülmüş veya onaylan?p kapat?lm?ş olanlar
                        return a.getDurum() == Durum.COZULDU || a.getDurum() == Durum.KAPATILDI;
                    }
                    return true;
                }
        );
        arizaTable.setItems(filteredData);
    }

    /**
     * Se?ilen arızan?n detaylar?nş saş taraftaki panele doldurur ve g?sterir.
     */
    private void showDetails(Ariza ariza) {
        String tipAd = ariza.getIsTipi() != null ? ariza.getIsTipi().getDisplayName() : "Arıza";
        detailIdLabel.setText("KAYIT #" + ariza.getId() + " - " + tipAd.toUpperCase());
        detailTitleLabel.setText(ariza.getBaslik());
        detailDescLabel.setText(ariza.getAciklama() != null ? ariza.getAciklama() : "Açıklama bulunmamaktad?r.");
        detailBildirenLabel.setText(ariza.getBildirenAdSoyad());
        detailKategoriLabel.setText(ariza.getKategoriAd());
        
        // SLA Durumunu ?nceli?in Yan?na Ekle
        if (ariza.isSlaIhlali()) {
            detailOncelikLabel.setText(ariza.getOncelik().name() + " - GEC?KT?! (Hedef: " + (ariza.getCozumHedefTarihi() != null ? ariza.getCozumHedefTarihi().format(DATE_FORMATTER) : "Belirsiz") + ")");
            detailOncelikLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold;");
        } else {
            String hedefStr = ariza.getCozumHedefTarihi() != null ? " (Hedef: " + ariza.getCozumHedefTarihi().format(DATE_FORMATTER) + ")" : "";
            detailOncelikLabel.setText(ariza.getOncelik().name() + hedefStr);
            detailOncelikLabel.setStyle("");
        }

        detailTeknisyenLabel.setText(ariza.getTeknisyenAdSoyad());
        detailIpLabel.setText(ariza.getIpAdresi());

        // İşlem paneli g?r?n?rl???nş rol ve atama durumuna g?re ayarla
        if (currentUser.getRol() == Rol.ADMIN || currentUser.getRol() == Rol.TEKNISYEN) {
            actionPane.setVisible(true);
            actionPane.setManaged(true);
            statusCombo.setValue(ariza.getDurum());

            // "?zerime Al" butonu sadece teknisyene hen?z atanmad?ysa g?sterilir
            boolean isUnassigned = (ariza.getAtananTeknisyenId() == null);
            claimBtn.setVisible(isUnassigned);
            claimBtn.setManaged(isUnassigned);
        } else {
            // Personel ve Y?netici durum g?ncelleyemez
            actionPane.setVisible(false);
            actionPane.setManaged(false);
        }

        // Uzaktan ba?lantş yetki ve IP kontrol?
        boolean canConnect = (currentUser.getRol() == Rol.ADMIN || currentUser.getRol() == Rol.TEKNISYEN) 
                             && ariza.getIpAdresi() != null && !ariza.getIpAdresi().equals("Bilinmiyor");
        remoteConnectBtn.setVisible(canConnect);
        remoteConnectBtn.setManaged(canConnect);

        // Bildiren onay paneli g?r?n?rl?k kontrol?
        boolean showReporterActions = (currentUser.getId() == ariza.getBildirenPersonelId()) 
                                      && ariza.getDurum() == Durum.COZULDU;
        reporterActionPane.setVisible(showReporterActions);
        reporterActionPane.setManaged(showReporterActions);

        // Ak?llş Çözüm ?pucu G?ster
        String detailsHint = getDetailsHintForCategory(ariza.getKategoriId());
        if (detailsHint != null) {
            detailsHintLabel.setText(detailsHint);
            detailsHintPane.setVisible(true);
            detailsHintPane.setManaged(true);
        } else {
            detailsHintPane.setVisible(false);
            detailsHintPane.setManaged(false);
        }

        // Panellerin g?r?n?rl?k durumlar?nş değiştir
        noSelectionPane.setVisible(false);
        noSelectionPane.setManaged(false);
        detailsContentPane.setVisible(true);
        detailsContentPane.setManaged(true);

        // İşlem ge?mi?ini y?kle ve timeline'ş oluştur
        logTimelineContainer.getChildren().clear();
        List<ArizaLog> logs = arizaLogDao.findByArizaId(ariza.getId());
        for (ArizaLog log : logs) {
            VBox item = new VBox(4);
            item.getStyleClass().add("timeline-item");

            String status = log.getYeniDurum().toUpperCase();
            if (status.equals("YENI")) {
                item.getStyleClass().add("timeline-item-new");
            } else if (status.equals("ATANDI") || status.equals("ISLEMDE")) {
                item.getStyleClass().add("timeline-item-progress");
            } else if (status.equals("COZULDU") || status.equals("KAPATILDI")) {
                item.getStyleClass().add("timeline-item-solved");
            }

            // Başlık (İşlemi yapan personel ve Tarih)
            HBox header = new HBox(8);
            Label userLabel = new Label(log.getPersonelAdSoyad());
            userLabel.getStyleClass().add("timeline-header");

            Label dateLabel = new Label(log.getTarih() != null ? log.getTarih().format(DATE_FORMATTER) : "");
            dateLabel.getStyleClass().add("timeline-time");

            Pane spacer = new Pane();
            HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
            header.getChildren().addAll(userLabel, spacer, dateLabel);

            // Durum De?i?imi
            String eskiDurumName = log.getEskiDurum() != null ? log.getEskiDurum() : "YEN?";
            String changeText = eskiDurumName + " ??ş " + log.getYeniDurum();
            Label changeLabel = new Label(changeText);
            changeLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #2563eb;");

            // Açıklama
            Label commentLabel = new Label(log.getAciklama() != null ? log.getAciklama() : "");
            commentLabel.getStyleClass().add("timeline-comment");
            commentLabel.setWrapText(true);

            item.getChildren().addAll(header, changeLabel, commentLabel);
            logTimelineContainer.getChildren().add(item);
        }
    }

    /**
     * Detay kart?nş gizler ve ilk "se?im yap?lmad?" mesaj?nş g?sterir.
     */
    private void hideDetails() {
        noSelectionPane.setVisible(true);
        noSelectionPane.setManaged(true);
        detailsContentPane.setVisible(false);
        detailsContentPane.setManaged(false);
        reporterActionPane.setVisible(false);
        reporterActionPane.setManaged(false);
        detailsHintPane.setVisible(false);
        detailsHintPane.setManaged(false);
        logCommentField.clear();
    }

    /**
     * Teknisyenin arızayş kendi ?zerine almas?nş sa?lar (Durumu otomatik olarak ATANDI yapar).
     */
    @FXML
    private void handleClaimAriza(ActionEvent event) {
        Ariza selected = arizaTable.getSelectionModel().getSelectedItem();
        if (selected == null || currentUser == null) return;

        boolean success = arizaDao.updateStatusAndAssignment(
                selected.getId(),
                Durum.ATANDI,
                currentUser.getId(), // Atanan teknisyen: aktif kullanıcı
                currentUser.getId(), // G?ncelleyen ki?i
                "Arıza teknisyen taraf?ndan ?zerine al?nd?."
        );

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Arıza ba?ar?yla ?zerinize atand?.");
            refreshTable();
            // Tablodaki se?imi korumak i?in g?ncel nesneyi bulup tekrar g?sterelim
            selectAndShowArizaInTable(selected.getId());
        } else {
            showAlert(Alert.AlertType.ERROR, "Hata", "Atama işlemi gerçekleştirilemedi.");
        }
    }

    /**
     * Manuel durum ve açıklama g?ncellemesi yap?lmas?nş sa?lar.
     */
    @FXML
    private void handleUpdateStatus(ActionEvent event) {
        Ariza selected = arizaTable.getSelectionModel().getSelectedItem();
        Durum yeniDurum = statusCombo.getValue();
        String comment = logCommentField.getText();

        if (selected == null || yeniDurum == null || currentUser == null) {
            showAlert(Alert.AlertType.WARNING, "Uyar?", "Lütfen ge?erli bir durum se?in.");
            return;
        }

        // E?er teknisyen durum g?ncelliyorsa ama arıza hala atanmam??sa, otomatik olarak kendini teknisyen atas?n
        Integer teknisyenId = selected.getAtananTeknisyenId();
        if (teknisyenId == null && currentUser.getRol() == Rol.TEKNISYEN) {
            teknisyenId = currentUser.getId();
        }

        boolean success = arizaDao.updateStatusAndAssignment(
                selected.getId(),
                yeniDurum,
                teknisyenId,
                currentUser.getId(),
                comment
        );

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Arıza durumu g?ncellendi.");
            logCommentField.clear();
            refreshTable();
            selectAndShowArizaInTable(selected.getId());
        } else {
            showAlert(Alert.AlertType.ERROR, "Hata", "G?ncelleme s?ras?nda bir sorun oluştu.");
        }
    }

    /**
     * Se?ili arıza i?in HTDS Uzaktan Destek penceresini a?ar.
     */
    @FXML
    private void handleRemoteConnect(ActionEvent event) {
        Ariza selected = arizaTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/hastane/htds/view/remote_connect.fxml"));
            Parent view = loader.load();

            RemoteConnectController controller = loader.getController();
            controller.initData(selected, currentUser);

            Stage stage = new Stage();
            stage.setTitle("HTDS Uzaktan Destek - Cihaz IP: " + selected.getIpAdresi());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(view));
            stage.setResizable(false);
            
            stage.setOnHidden(e -> {
                refreshTable();
                selectAndShowArizaInTable(selected.getId());
            });

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Sistem Hatası", "Uzaktan destek penceresi açılamadı: " + e.getMessage());
        }
    }

    /**
     * Tabloda ID bazlş tekrar se?im yap?lmas?nş sa?lar (g?ncel detaylarş yans?tmak i?in).
     */
    private void selectAndShowArizaInTable(int id) {
        for (Ariza item : arizaTable.getItems()) {
            if (item.getId() == id) {
                arizaTable.getSelectionModel().select(item);
                showDetails(item);
                break;
            }
        }
    }

    @FXML
    private void handleApproveAriza(ActionEvent event) {
        Ariza selected = arizaTable.getSelectionModel().getSelectedItem();
        if (selected == null || currentUser == null) return;

        boolean success = arizaDao.updateStatusAndAssignment(
                selected.getId(),
                Durum.KAPATILDI,
                selected.getAtananTeknisyenId(),
                currentUser.getId(),
                "Kullanıcı Çözümş onayladı ve arıza kapat?ld?."
        );

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Çözümş onayladın?z. Arıza kapat?ld?.");
            refreshTable();
            selectAndShowArizaInTable(selected.getId());
        } else {
            showAlert(Alert.AlertType.ERROR, "Hata", "İşlem gerçekleştirilemedi.");
        }
    }

    @FXML
    private void handleReopenAriza(ActionEvent event) {
        Ariza selected = arizaTable.getSelectionModel().getSelectedItem();
        if (selected == null || currentUser == null) return;

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Arıza Yeniden A?");
        dialog.setHeaderText("Arızan?n Devam Etme Nedeni");
        dialog.setContentText("Lütfen karşılaştığınız sorunu açıklay?n:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String reason = result.get().trim();
            if (reason.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Uyar?", "Arızayş yeniden a?mak i?in bir neden belirtmelisiniz.");
                return;
            }

            boolean success = arizaDao.updateStatusAndAssignment(
                    selected.getId(),
                    Durum.ISLEMDE,
                    selected.getAtananTeknisyenId(),
                    currentUser.getId(),
                    "Kullanıcı hatanın devam ettiğini bildirdi. Arıza yeniden açıldı. Açıklama: " + reason
            );

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Bilgi", "Arıza yeniden açıldı ve teknisyene bildirildi.");
                refreshTable();
                selectAndShowArizaInTable(selected.getId());
            } else {
                showAlert(Alert.AlertType.ERROR, "Hata", "İşlem gerçekleştirilemedi.");
            }
        }
    }

    private String getDetailsHintForCategory(int categoryId) {
        return switch (categoryId) {
            case 1 -> "Teknisyen Notu: MYS hatasş veya sistem arızalarş i?in bilgisayara 'Uzaktan Ba?lan' konsolu ile ba?lan?p Spooler hizmetlerini yeniden ba?latabilir veya bilgisayarş yeniden ba?latma komutu g?nderebilirsiniz.";
            case 2 -> "Teknisyen Notu: Barkod yaz?cş donmalar?nda veya yazmama durumlar?nda remote terminalden bilgisayarş yeniden ba?latmayş deneyebilir veya personelden yaz?cş g?ş kablosunu ??kar?p 10 sn sonra takmas?nş isteyebilirsiniz.";
            case 3 -> "Teknisyen Notu: İnternet / Ağ hatalarında remote terminal ?zerinden aş adaptörünü sıfırlama (release/renew) komutunu ?al??t?rarak DHCP yap?land?rmas?nş tazeleyin.";
            case 4 -> "Teknisyen Notu: T?bbi Cihaz ba?lantş kopmalar?nda cihaz?n seri/USB port bağlantıs?nş kontrol edin ve cihaz?n kontrol aray?zş yaz?l?m?nş yeniden ba?lat?n.";
            default -> null;
        };
    }

    @FXML
    private void handleGeminiDiagnostic(ActionEvent event) {
        Ariza selected = arizaTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        detailsHintLabel.setText("🤖 Gemini teknik analiz gerçekleştiriyor, lütfen bekleyin...");
        geminiDiagnosticBtn.setDisable(true);

        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                return com.hastane.htds.service.GeminiService.getTroubleshootingAdvice(
                        selected.getBaslik(),
                        selected.getAciklama(),
                        selected.getKategoriAd(),
                        true
                );
            }
        };

        task.setOnSucceeded(e -> {
            detailsHintLabel.setText(task.getValue());
            geminiDiagnosticBtn.setDisable(false);
        });

        task.setOnFailed(e -> {
            detailsHintLabel.setText("🤖 Gemini ile ba?lantş kurulurken hata oluştu.");
            geminiDiagnosticBtn.setDisable(false);
        });

        new Thread(task).start();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

package com.hastane.htds.controller;

import com.hastane.htds.dao.PersonelDao;
import com.hastane.htds.model.Personel;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Locale;

public class RehberController {

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Personel> rehberTable;

    @FXML
    private TableColumn<Personel, String> nameCol;

    @FXML
    private TableColumn<Personel, String> deptCol;

    @FXML
    private TableColumn<Personel, String> roleCol;

    @FXML
    private TableColumn<Personel, String> dahiliCol;

    // Sağ Detay Paneli Elemanları
    @FXML
    private VBox detailContainer;

    @FXML
    private VBox noSelectionPane;

    @FXML
    private VBox detailsContentPane;

    @FXML
    private Label detailAvatarLabel;

    @FXML
    private Label detailNameLabel;

    @FXML
    private Label detailRoleLabel;

    @FXML
    private Label detailDeptLabel;

    @FXML
    private Label detailTcLabel;

    @FXML
    private VBox tcBox;

    @FXML
    private Label detailDahiliLabel;

    private final PersonelDao personelDao = new PersonelDao();
    private static final Locale TR_LOCALE = Locale.forLanguageTag("tr-TR");

    @FXML
    public void initialize() {
        // 1. Kolon Eşlemeleri
        nameCol.setCellValueFactory(new PropertyValueFactory<>("adSoyad"));
        nameCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    Personel p = getTableView().getItems().get(getIndex());
                    String emoji = com.hastane.htds.util.GenderUtil.getGenderEmoji(p.getAd());
                    setText(emoji + " " + item);
                }
            }
        });
        deptCol.setCellValueFactory(new PropertyValueFactory<>("departmanAd"));
        roleCol.setCellValueFactory(new PropertyValueFactory<>("rol"));
        
        // Dahili hat no kolonu eşleme ve hücre stili
        dahiliCol.setCellValueFactory(new PropertyValueFactory<>("dahiliNo"));
        dahiliCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    getStyleClass().remove("dahili-no-cell");
                } else {
                    setText(item);
                    if (!getStyleClass().contains("dahili-no-cell")) {
                        getStyleClass().add("dahili-no-cell");
                    }
                }
            }
        });

        // 2. Verileri Veritabanından Çek
        List<Personel> aktifPersoneller = personelDao.findAllActive();

        // 3. Arama Filtreleme Mekanizması (FilteredList)
        FilteredList<Personel> filteredData = new FilteredList<>(
                FXCollections.observableArrayList(aktifPersoneller),
                p -> true
        );

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(personel -> {
                if (newValue == null || newValue.isBlank()) {
                    return true;
                }
                
                String query = newValue.toLowerCase(TR_LOCALE);
                
                // İsim soyisim araması
                if (personel.getAdSoyad().toLowerCase(TR_LOCALE).contains(query)) {
                    return true;
                }
                // Departman araması
                if (personel.getDepartmanAd().toLowerCase(TR_LOCALE).contains(query)) {
                    return true;
                }
                // Rol araması
                if (personel.getRol().name().toLowerCase(TR_LOCALE).contains(query)) {
                    return true;
                }
                // Dahili no araması
                if (personel.getDahiliNo().contains(query)) {
                    return true;
                }
                
                return false;
            });
        });

        // 4. Filtrelenmiş Veriyi Tabloya Bağla
        rehberTable.setItems(filteredData);

        // 5. Tablo Seçim Dinleyicisi (Detay Kartı Güncelleme)
        rehberTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                showContactDetails(newVal);
            } else {
                hideContactDetails();
            }
        });

        hideContactDetails();
    }

    private void showContactDetails(Personel p) {
        noSelectionPane.setVisible(false);
        noSelectionPane.setManaged(false);

        // Cinsiyete göre kadın/erkek avatar ikonu yerleştir
        detailAvatarLabel.setText(com.hastane.htds.util.GenderUtil.getGenderEmoji(p.getAd()));
        detailAvatarLabel.setStyle("-fx-font-size: 36px;");

        detailNameLabel.setText(p.getAdSoyad());
        detailDeptLabel.setText(p.getDepartmanAd());

        // TC No görünürlüğü kontrolü (sadece admin)
        Personel loggedInUser = com.hastane.htds.service.AuthService.getLoggedPersonel();
        if (loggedInUser != null && loggedInUser.getRol() == com.hastane.htds.model.Rol.ADMIN) {
            tcBox.setVisible(true);
            tcBox.setManaged(true);
            // TC No maskeleme (güvenlik için)
            if (p.getTcNo() != null && p.getTcNo().length() == 11) {
                String masked = p.getTcNo().substring(0, 3) + "******" + p.getTcNo().substring(9);
                detailTcLabel.setText(masked);
            } else {
                detailTcLabel.setText("Belirtilmedi");
            }
        } else {
            // Admin değilse tamamen gizle
            tcBox.setVisible(false);
            tcBox.setManaged(false);
        }

        detailDahiliLabel.setText(p.getDahiliNo());

        // Rol rozeti ataması ve stili
        detailRoleLabel.setText(p.getRol().name());
        detailRoleLabel.getStyleClass().clear();
        detailRoleLabel.getStyleClass().add("user-role-badge");

        switch (p.getRol()) {
            case ADMIN:
                detailRoleLabel.getStyleClass().add("role-admin");
                break;
            case TEKNISYEN:
                detailRoleLabel.getStyleClass().add("role-teknisyen");
                break;
            case YONETICI:
                detailRoleLabel.getStyleClass().add("role-yonetici");
                break;
            case PERSONEL:
            default:
                detailRoleLabel.getStyleClass().add("role-personel");
                break;
        }

        detailsContentPane.setVisible(true);
        detailsContentPane.setManaged(true);
    }

    private void hideContactDetails() {
        detailsContentPane.setVisible(false);
        detailsContentPane.setManaged(false);
        noSelectionPane.setVisible(true);
        noSelectionPane.setManaged(true);
    }
}

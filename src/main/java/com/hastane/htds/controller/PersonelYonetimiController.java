package com.hastane.htds.controller;

import com.hastane.htds.dao.PersonelDao;
import com.hastane.htds.dao.DepartmanDao;
import com.hastane.htds.model.Personel;
import com.hastane.htds.model.Departman;
import com.hastane.htds.model.Rol;
import com.hastane.htds.util.HashUtil;
import com.hastane.htds.util.TcKimlikValidator;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.util.List;

public class PersonelYonetimiController {

    @FXML
    private TableView<Personel> personelTable;

    @FXML
    private TableColumn<Personel, Integer> idCol;

    @FXML
    private TableColumn<Personel, String> adSoyadCol;

    @FXML
    private TableColumn<Personel, String> tcCol;

    @FXML
    private TableColumn<Personel, String> rolCol;

    @FXML
    private TableColumn<Personel, String> departmanCol;

    @FXML
    private TableColumn<Personel, String> dahiliCol;

    @FXML
    private VBox formContainer;

    @FXML
    private Label formTitleLabel;

    @FXML
    private TextField tcField;

    @FXML
    private TextField adField;

    @FXML
    private TextField soyadField;

    @FXML
    private PasswordField sifreField;

    @FXML
    private Label sifreWarningLabel;

    @FXML
    private ComboBox<Rol> rolCombo;

    @FXML
    private ComboBox<Departman> departmanCombo;

    @FXML
    private TextField dahiliField;

    @FXML
    private Label feedbackLabel;

    @FXML
    private Button clearBtn;

    @FXML
    private Button saveBtn;

    @FXML
    private Button deleteBtn;

    private final PersonelDao personelDao = new PersonelDao();
    private final DepartmanDao departmanDao = new DepartmanDao();
    private Personel selectedPersonel = null;

    @FXML
    public void initialize() {
        // 1. Tablo Kolon Eşleştirmeleri
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        adSoyadCol.setCellValueFactory(new PropertyValueFactory<>("adSoyad"));
        adSoyadCol.setCellFactory(column -> new javafx.scene.control.TableCell<>() {
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
        tcCol.setCellValueFactory(new PropertyValueFactory<>("tcNo"));
        rolCol.setCellValueFactory(new PropertyValueFactory<>("rol"));
        departmanCol.setCellValueFactory(new PropertyValueFactory<>("departmanAd"));
        dahiliCol.setCellValueFactory(new PropertyValueFactory<>("dahiliNo"));

        // 2. Tabloyu Doldur
        refreshTable();

        // 3. ComboBox'ları Doldur
        rolCombo.setItems(FXCollections.observableArrayList(Rol.values()));
        
        List<Departman> depts = departmanDao.findAll();
        departmanCombo.setItems(FXCollections.observableArrayList(depts));

        // 4. Tablodaki Satır Seçim Listener'ı
        personelTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadPersonelToForm(newVal);
            } else {
                clearForm();
            }
        });

        // Başlangıçta form temiz olsun
        clearForm();
    }

    private void refreshTable() {
        List<Personel> activePersonels = personelDao.findAllActive();
        personelTable.setItems(FXCollections.observableArrayList(activePersonels));
    }

    private void loadPersonelToForm(Personel p) {
        selectedPersonel = p;
        formTitleLabel.setText("Personel Düzenle (ID: " + p.getId() + ")");
        tcField.setText(p.getTcNo());
        adField.setText(p.getAd());
        soyadField.setText(p.getSoyad());
        sifreField.clear(); // Şifre temiz gösterilir (güvenlik ve güncelleme mantığı için)
        sifreWarningLabel.setVisible(true);
        sifreWarningLabel.setManaged(true);
        rolCombo.setValue(p.getRol());
        
        // Departmanı bulup ComboBox'ta seçelim
        if (p.getDepartmanId() != null) {
            for (Departman d : departmanCombo.getItems()) {
                if (d.getId() == p.getDepartmanId()) {
                    departmanCombo.setValue(d);
                    break;
                }
            }
        } else {
            departmanCombo.setValue(null);
        }

        dahiliField.setText(p.getDahiliNo().equals("Dahili Yok") ? "" : p.getDahiliNo());
        deleteBtn.setVisible(true);
        deleteBtn.setManaged(true);
        hideFeedback();
    }

    @FXML
    private void handleClearForm(ActionEvent event) {
        personelTable.getSelectionModel().clearSelection();
        clearForm();
    }

    private void clearForm() {
        selectedPersonel = null;
        formTitleLabel.setText("Yeni Personel Ekle");
        tcField.clear();
        adField.clear();
        soyadField.clear();
        sifreField.clear();
        sifreWarningLabel.setVisible(false);
        sifreWarningLabel.setManaged(false);
        rolCombo.setValue(null);
        departmanCombo.setValue(null);
        dahiliField.clear();
        deleteBtn.setVisible(false);
        deleteBtn.setManaged(false);
        hideFeedback();
    }

    @FXML
    private void handleSavePersonel(ActionEvent event) {
        String tc = tcField.getText();
        String ad = adField.getText();
        String soyad = soyadField.getText();
        String password = sifreField.getText();
        Rol rol = rolCombo.getValue();
        Departman dept = departmanCombo.getValue();
        String dahili = dahiliField.getText();

        // VALIDASYONLAR
        if (tc == null || tc.isBlank()) {
            showError("TC Kimlik Numarası boş bırakılamaz.");
            return;
        }
        if (!TcKimlikValidator.gecerliMi(tc)) {
            showError("Geçersiz TC Kimlik Numarası formatı (11 Hane ve Algoritma Kontrolü).");
            return;
        }
        if (ad == null || ad.isBlank()) {
            showError("Ad alanı boş bırakılamaz.");
            return;
        }
        if (soyad == null || soyad.isBlank()) {
            showError("Soyad alanı boş bırakılamaz.");
            return;
        }
        if (rol == null) {
            showError("Lütfen yetki rolünü seçin.");
            return;
        }

        Integer deptId = (dept != null) ? dept.getId() : null;

        if (selectedPersonel == null) {
            // YENİ EKLEME
            if (password == null || password.length() < 5) {
                showError("Yeni personel için en az 5 karakterli şifre girilmelidir.");
                return;
            }

            try {
                String hashedSifre = HashUtil.hashPassword(password);
                String kullaniciAdi = ad.toLowerCase().replaceAll("[^a-zğüşöçı]", "").trim() + "." + soyad.toLowerCase().replaceAll("[^a-zğüşöçı]", "").trim();
                Personel yeniPers = new Personel(tc, kullaniciAdi, ad, soyad, hashedSifre, rol, deptId, dahili.isBlank() ? null : dahili);
                
                boolean success = personelDao.insert(yeniPers);
                if (success) {
                    showSuccess("Yeni personel sisteme başarıyla eklendi!");
                    refreshTable();
                    clearForm();
                } else {
                    showError("Ekleme sırasında veritabanı hatası oluştu. TC no benzersiz olmalıdır.");
                }
            } catch (Exception e) {
                showError("Hata: " + e.getMessage());
            }

        } else {
            // MEVCUT GÜNCELLEME
            try {
                selectedPersonel.setTcNo(tc);
                selectedPersonel.setAd(ad);
                selectedPersonel.setSoyad(soyad);
                selectedPersonel.setRol(rol);
                selectedPersonel.setDepartmanId(deptId);
                selectedPersonel.setDahiliNo(dahili.isBlank() ? null : dahili);

                // Eğer yeni şifre girildiyse hash'leyip güncelle
                if (password != null && !password.isBlank()) {
                    if (password.length() < 5) {
                        showError("Yeni şifre en az 5 karakter uzunluğunda olmalıdır.");
                        return;
                    }
                    selectedPersonel.setSifreHash(HashUtil.hashPassword(password));
                }

                boolean success = personelDao.update(selectedPersonel);
                if (success) {
                    showSuccess("Personel bilgileri başarıyla güncellendi!");
                    refreshTable();
                    personelTable.getSelectionModel().clearSelection();
                    clearForm();
                } else {
                    showError("Güncelleme başarısız oldu. Veritabanı hatası.");
                }
            } catch (Exception e) {
                showError("Hata: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleDeletePersonel(ActionEvent event) {
        if (selectedPersonel != null) {
            boolean success = personelDao.delete(selectedPersonel.getId());
            if (success) {
                showSuccess("Personel pasif hale getirildi (Sistemden silindi).");
                refreshTable();
                personelTable.getSelectionModel().clearSelection();
                clearForm();
            } else {
                showError("Silme işlemi başarısız oldu.");
            }
        }
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

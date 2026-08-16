package com.hastane.htds.model;

import java.time.LocalDateTime;

public class Ariza {
    private int id;
    private String baslik;
    private String aciklama;
    private int kategoriId;
    private Integer departmanId; // Nullable
    private Oncelik oncelik;
    private Durum durum;
    private IsTipi isTipi;
    private int bildirenPersonelId;
    private Integer atananTeknisyenId; // Nullable
    private LocalDateTime olusturmaTarihi;
    private LocalDateTime cozumHedefTarihi;
    private LocalDateTime guncellemeTarihi;

    // Arayüzde (TableView) kolay göstermek için birleştirilmiş/ilişkisel alanlar
    private String kategoriAd;
    private String departmanAd;
    private String bildirenAdSoyad;
    private String teknisyenAdSoyad;
    private String ipAdresi;

    public Ariza() {
        this.oncelik = Oncelik.ORTA;
        this.durum = Durum.YENI;
        this.isTipi = IsTipi.ARIZA; // Varsayılan tip
    }

    // --- Getter / Setter Metotları ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBaslik() {
        return baslik;
    }

    public void setBaslik(String baslik) {
        this.baslik = baslik;
    }

    public String getAciklama() {
        return aciklama;
    }

    public void setAciklama(String aciklama) {
        this.aciklama = aciklama;
    }

    public int getKategoriId() {
        return kategoriId;
    }

    public void setKategoriId(int kategoriId) {
        this.kategoriId = kategoriId;
    }

    public Integer getDepartmanId() {
        return departmanId;
    }

    public void setDepartmanId(Integer departmanId) {
        this.departmanId = departmanId;
    }

    public Oncelik getOncelik() {
        return oncelik;
    }

    public void setOncelik(Oncelik oncelik) {
        this.oncelik = oncelik;
    }

    public Durum getDurum() {
        return durum;
    }

    public void setDurum(Durum durum) {
        this.durum = durum;
    }

    public int getBildirenPersonelId() {
        return bildirenPersonelId;
    }

    public void setBildirenPersonelId(int bildirenPersonelId) {
        this.bildirenPersonelId = bildirenPersonelId;
    }

    public Integer getAtananTeknisyenId() {
        return atananTeknisyenId;
    }

    public void setAtananTeknisyenId(Integer atananTeknisyenId) {
        this.atananTeknisyenId = atananTeknisyenId;
    }

    public LocalDateTime getOlusturmaTarihi() {
        return olusturmaTarihi;
    }

    public void setOlusturmaTarihi(LocalDateTime olusturmaTarihi) {
        this.olusturmaTarihi = olusturmaTarihi;
    }

    public LocalDateTime getCozumHedefTarihi() {
        return cozumHedefTarihi;
    }

    public void setCozumHedefTarihi(LocalDateTime cozumHedefTarihi) {
        this.cozumHedefTarihi = cozumHedefTarihi;
    }

    public LocalDateTime getGuncellemeTarihi() {
        return guncellemeTarihi;
    }

    public void setGuncellemeTarihi(LocalDateTime guncellemeTarihi) {
        this.guncellemeTarihi = guncellemeTarihi;
    }

    public IsTipi getIsTipi() {
        return isTipi;
    }

    public void setIsTipi(IsTipi isTipi) {
        this.isTipi = isTipi;
    }

    /**
     * SLA İhlali (Gecikme) durumunu kontrol eder.
     * Arıza Çözülmemişse ve hedef tarih şimdiki zamandan önceyse true döner.
     */
    public boolean isSlaIhlali() {
        if (durum == Durum.COZULDU || durum == Durum.KAPATILDI) {
            return false;
        }
        if (cozumHedefTarihi != null) {
            return LocalDateTime.now().isAfter(cozumHedefTarihi);
        }
        return false;
    }

    // İlişkisel Alanlar için Getter / Setter'lar

    public String getKategoriAd() {
        return kategoriAd;
    }

    public void setKategoriAd(String kategoriAd) {
        this.kategoriAd = kategoriAd;
    }

    public String getDepartmanAd() {
        return departmanAd;
    }

    public void setDepartmanAd(String departmanAd) {
        this.departmanAd = departmanAd;
    }

    public String getBildirenAdSoyad() {
        return bildirenAdSoyad;
    }

    public void setBildirenAdSoyad(String bildirenAdSoyad) {
        this.bildirenAdSoyad = bildirenAdSoyad;
    }

    public String getTeknisyenAdSoyad() {
        return teknisyenAdSoyad != null ? teknisyenAdSoyad : "Atanmadı";
    }

    public void setTeknisyenAdSoyad(String teknisyenAdSoyad) {
        this.teknisyenAdSoyad = teknisyenAdSoyad;
    }

    public String getIpAdresi() {
        return ipAdresi != null ? ipAdresi : "Bilinmiyor";
    }

    public void setIpAdresi(String ipAdresi) {
        this.ipAdresi = ipAdresi;
    }
}

package com.hastane.htds.model;

import java.time.LocalDateTime;

/**
 * Personel: veritabanindaki "personel" tablosunun Java karsiligi.
 *
 * ENCAPSULATION (kapsulleme) burada nasil calisiyor:
 * Tum alanlar (fields) "private" - disaridan DOGRUDAN erisilemez.
 * Ornegin "personel.tcNo = "..."" diye disaridan direkt atama YAPAMAZSIN.
 * Bunun yerine getter/setter metodlari kullanmak zorundasin.
 *
 * Bu neden onemli: setTcNo() metodunun icine yarin bir kontrol eklemek
 * istersen (orn. "TC 11 haneli olmali" kontrolu), TEK bir yerde
 * degisiklik yaparsin ve bu kural HER YERDE gecerli olur. Eger alanlar
 * public olsaydi, projenin her koesesinde biri yanlis bir deger atayabilirdi
 * ve bunu engelleyecek hicbir mekanizma olmazdi.
 */
public class Personel {

    private int id;
    private String tcNo;
    private String kullaniciAdi;
    private String ad;
    private String soyad;
    private String sifreHash;
    private Rol rol;
    private Integer departmanId; // Integer (kucuk harf degil) cunku NULL olabilir, int NULL tasiyamaz
    private boolean aktif;
    private LocalDateTime olusturmaTarihi;
    private String dahiliNo;
    private String departmanAd;

    // Bos constructor - DAO katmani veritabanindan veri okurken
    // once bos bir nesne yaratip sonra setter'larla dolduracak.
    public Personel() {
    }

    // Yeni bir personel olustururken kullanacagimiz constructor -
    // id ve olusturmaTarihi burada yok cunku onlari veritabani
    // (AUTO_INCREMENT ve DEFAULT CURRENT_TIMESTAMP ile) kendisi atayacak.
    public Personel(String tcNo, String kullaniciAdi, String ad, String soyad, String sifreHash, Rol rol, Integer departmanId, String dahiliNo) {
        this.tcNo = tcNo;
        this.kullaniciAdi = kullaniciAdi;
        this.ad = ad;
        this.soyad = soyad;
        this.sifreHash = sifreHash;
        this.rol = rol;
        this.departmanId = departmanId;
        this.dahiliNo = dahiliNo;
        this.aktif = true;
    }

    // --- Getter / Setter'lar ---
    // IntelliJ'de bunlarin hepsini elle yazmana gerek yok:
    // sinif icinde sag tik -> Generate -> Getter and Setter -> hepsini sec.

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTcNo() {
        return tcNo;
    }

    public void setTcNo(String tcNo) {
        this.tcNo = tcNo;
    }

    public String getKullaniciAdi() {
        return kullaniciAdi;
    }

    public void setKullaniciAdi(String kullaniciAdi) {
        this.kullaniciAdi = kullaniciAdi;
    }

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public String getSoyad() {
        return soyad;
    }

    public void setSoyad(String soyad) {
        this.soyad = soyad;
    }

    public String getSifreHash() {
        return sifreHash;
    }

    public void setSifreHash(String sifreHash) {
        this.sifreHash = sifreHash;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public Integer getDepartmanId() {
        return departmanId;
    }

    public void setDepartmanId(Integer departmanId) {
        this.departmanId = departmanId;
    }

    public boolean isAktif() {
        return aktif;
    }

    public void setAktif(boolean aktif) {
        this.aktif = aktif;
    }

    public LocalDateTime getOlusturmaTarihi() {
        return olusturmaTarihi;
    }

    public void setOlusturmaTarihi(LocalDateTime olusturmaTarihi) {
        this.olusturmaTarihi = olusturmaTarihi;
    }

    // Ekranlarda (orn. TableView) "Ad Soyad" gibi tek bir metin
    // gostermek istedigimizde bu yardimci metodu kullanacagiz.
    public String getAdSoyad() {
        return ad + " " + soyad;
    }

    public String getDahiliNo() {
        return dahiliNo != null ? dahiliNo : "Dahili Yok";
    }

    public void setDahiliNo(String dahiliNo) {
        this.dahiliNo = dahiliNo;
    }

    public String getDepartmanAd() {
        return departmanAd != null ? departmanAd : "Departmansız";
    }

    public void setDepartmanAd(String departmanAd) {
        this.departmanAd = departmanAd;
    }
}
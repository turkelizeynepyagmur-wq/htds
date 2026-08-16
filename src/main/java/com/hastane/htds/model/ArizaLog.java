package com.hastane.htds.model;

import java.time.LocalDateTime;

public class ArizaLog {
    private int id;
    private int arizaId;
    private int personelId;
    private String eskiDurum;
    private String yeniDurum;
    private String aciklama;
    private LocalDateTime tarih;
    
    // Arayüzde kimin işlem yaptığını göstermek için
    private String personelAdSoyad;

    public ArizaLog() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getArizaId() {
        return arizaId;
    }

    public void setArizaId(int arizaId) {
        this.arizaId = arizaId;
    }

    public int getPersonelId() {
        return personelId;
    }

    public void setPersonelId(int personelId) {
        this.personelId = personelId;
    }

    public String getEskiDurum() {
        return eskiDurum;
    }

    public void setEskiDurum(String eskiDurum) {
        this.eskiDurum = eskiDurum;
    }

    public String getYeniDurum() {
        return yeniDurum;
    }

    public void setYeniDurum(String yeniDurum) {
        this.yeniDurum = yeniDurum;
    }

    public String getAciklama() {
        return aciklama;
    }

    public void setAciklama(String aciklama) {
        this.aciklama = aciklama;
    }

    public LocalDateTime getTarih() {
        return tarih;
    }

    public void setTarih(LocalDateTime tarih) {
        this.tarih = tarih;
    }

    public String getPersonelAdSoyad() {
        return personelAdSoyad;
    }

    public void setPersonelAdSoyad(String personelAdSoyad) {
        this.personelAdSoyad = personelAdSoyad;
    }
}

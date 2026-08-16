package com.hastane.htds.model;

public enum IsTipi {
    ARIZA("Arıza Bildirimi"),
    KURULUM("Yeni Kurulum"),
    TALEP("Genel Talep"),
    BAKIM("Periyodik Bakım");

    private final String displayName;

    IsTipi(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

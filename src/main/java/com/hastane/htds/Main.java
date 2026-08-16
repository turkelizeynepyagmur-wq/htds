package com.hastane.htds;

public class Main {
    public static void main(String[] args) {
        // App, Application'ı extend ediyor ama Main etmiyor.
        // JVM'in modül kontrolü sadece "doğrudan çalıştırılan" sınıfa bakar,
        // bu yüzden Main üzerinden dolaylı çağırmak kontrolü atlatır.
        App.main(args);
    }
}
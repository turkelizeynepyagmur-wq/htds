package com.hastane.htds.util;

/**
 * TC Kimlik No dogrulama algoritmasi.
 *
 * Bu sinifi "util" paketine koyduk cunku hicbir veritabani/ekran bilgisi
 * icermiyor - sadece saf bir matematik/mantik islemi. Boyle "durumsuz"
 * (stateless) yardimci siniflari static metodlarla yaziyoruz, nesne
 * olusturmaya gerek yok.
 */
public class TcKimlikValidator {

    private TcKimlikValidator() {
        // Bu sinifin ornegi olusturulmasin - sadece static metod kullanilacak.
    }

    /**
     * Verilen TC kimlik numarasinin gecerli olup olmadigini kontrol eder.
     *
     * @param tcNo kontrol edilecek metin (11 haneli rakam beklenir)
     * @return gecerliyse true, degilse false
     */
    public static boolean gecerliMi(String tcNo) {
        // 1. ADIM: temel format kontrolu.
        // 11 karakter mi, hepsi rakam mi, ilk hane 0 mi?
        if (tcNo == null || tcNo.length() != 11) {
            return false;
        }
        if (!tcNo.chars().allMatch(Character::isDigit)) {
            return false;
        }
        if (tcNo.charAt(0) == '0') {
            return false;
        }

        // 2. ADIM: her haneyi ayri ayri int dizisine cevirelim.
        // String'den tek tek karakter okuyup rakama ceviriyoruz.
        int[] d = new int[11];
        for (int i = 0; i < 11; i++) {
            d[i] = Character.getNumericValue(tcNo.charAt(i));
        }

        // 3. ADIM: tek ve cift indeksli hanelerin toplamlarini hesapla.
        // Dikkat: burada "d[0]" aslinda 1. hane demek (index 0'dan basliyor).
        int tekToplam = d[0] + d[2] + d[4] + d[6] + d[8];   // 1,3,5,7,9. haneler
        int ciftToplam = d[1] + d[3] + d[5] + d[7];          // 2,4,6,8. haneler

        // 4. ADIM: 10. hane kontrolu.
        // Formul: ((tek*7) - cift) mod 10, 10. haneye (d[9]) esit olmali.
        int hesaplananOnuncu = ((tekToplam * 7) - ciftToplam) % 10;
        // Not: eger sonuc negatifse (teorik olarak olmamali ama guvenlik icin),
        // Java'da negatif sayilarin mod'u negatif cikabilir, bunu duzeltiyoruz.
        if (hesaplananOnuncu < 0) {
            hesaplananOnuncu += 10;
        }
        if (hesaplananOnuncu != d[9]) {
            return false;
        }

        // 5. ADIM: 11. hane kontrolu.
        // Formul: ilk 10 hanenin toplaminin 10'a gore modu, 11. haneye esit olmali.
        int ilkOnHaneToplami = 0;
        for (int i = 0; i < 10; i++) {
            ilkOnHaneToplami += d[i];
        }
        int hesaplananOnbirinci = ilkOnHaneToplami % 10;

        return hesaplananOnbirinci == d[10];
    }
}
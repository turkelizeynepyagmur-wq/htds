package com.hastane.htds.service;

import com.hastane.htds.dao.PersonelDao;
import com.hastane.htds.model.Personel;
import com.hastane.htds.util.HashUtil;

public class AuthService {

    private final PersonelDao personelDao = new PersonelDao();
    
    // Oturum açmış aktif personeli hafızada tutan static alan (Session yönetimi)
    private static Personel loggedPersonel;

    /**
     * Kullanıcı girişi işlemini doğrular.
     *
     * @param kullaniciAdi     Kullanıcı adı (Örn: ali.yilmaz)
     * @param password Şifre
     * @return Başarılıysa oturum açan Personel nesnesi
     * @throws Exception Geçersiz giriş durumlarında açıklayıcı hata fırlatır
     */
    public Personel login(String kullaniciAdi, String password) throws Exception {
        // 1. Girdilerin boş olup olmadığını kontrol et
        if (kullaniciAdi == null || kullaniciAdi.isBlank()) {
            throw new Exception("Kullanıcı adı boş bırakılamaz.");
        }
        if (password == null || password.isBlank()) {
            throw new Exception("Şifre alanı boş bırakılamaz.");
        }

        // 3. Veritabanından kullanıcıyı sorgula
        Personel personel = personelDao.findByKullaniciAdi(kullaniciAdi);
        if (personel == null) {
            throw new Exception("Kullanıcı bulunamadı veya hesap aktif değil.");
        }

        // 4. Şifre eşleşmesini kontrol et (Girdi şifresini hashleyip DB'dekiyle karşılaştırıyoruz)
        String hashedInput = HashUtil.hashPassword(password);
        if (!hashedInput.equals(personel.getSifreHash())) {
            throw new Exception("Hatalı şifre. Lütfen tekrar deneyin.");
        }

        // 5. Başarılıysa oturum bilgisini sakla ve nesneyi dön
        loggedPersonel = personel;
        return personel;
    }

    /**
     * Aktif oturumu sonlandırır.
     */
    public static void logout() {
        loggedPersonel = null;
    }

    /**
     * Mevcut oturum açmış personeli döner.
     *
     * @return Oturum açan personel (oturum yoksa null)
     */
    public static Personel getLoggedPersonel() {
        return loggedPersonel;
    }
    
    /**
     * Aktif oturum olup olmadığını kontrol eder.
     *
     * @return Oturum varsa true
     */
    public static boolean isLoggedIn() {
        return loggedPersonel != null;
    }
}

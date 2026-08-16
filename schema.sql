-- Mevcut veritabanını kaldır ve sıfırdan oluştur
DROP DATABASE IF EXISTS htds_db;
CREATE DATABASE htds_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE htds_db;

-- DEPARTMAN TABLOSU
CREATE TABLE IF NOT EXISTS departman (
    id INT PRIMARY KEY AUTO_INCREMENT,
    ad VARCHAR(100) NOT NULL
);

-- KATEGORİ TABLOSU
CREATE TABLE IF NOT EXISTS kategori (
    id INT PRIMARY KEY AUTO_INCREMENT,
    ad VARCHAR(100) NOT NULL
);

-- PERSONEL TABLOSU
CREATE TABLE IF NOT EXISTS personel (
    id INT PRIMARY KEY AUTO_INCREMENT,
    tc_no VARCHAR(11) UNIQUE NOT NULL,
    kullanici_adi VARCHAR(50) UNIQUE NOT NULL,
    ad VARCHAR(50) NOT NULL,
    soyad VARCHAR(50) NOT NULL,
    sifre_hash VARCHAR(255) NOT NULL,
    rol VARCHAR(20) NOT NULL, -- ADMIN, YONETICI, TEKNISYEN, PERSONEL
    departman_id INT,
    dahili_no VARCHAR(10),
    aktif TINYINT(1) DEFAULT 1,
    olusturma_tarihi TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (departman_id) REFERENCES departman(id)
);

-- ARIZA TABLOSU
CREATE TABLE IF NOT EXISTS ariza (
    id INT PRIMARY KEY AUTO_INCREMENT,
    baslik VARCHAR(255) NOT NULL,
    aciklama TEXT NOT NULL,
    kategori_id INT,
    departman_id INT,
    bildiren_personel_id INT,
    atanan_teknisyen_id INT,
    ip_adresi VARCHAR(50),
    durum VARCHAR(50) DEFAULT 'ACIK', -- ACIK, ISLEMDE, COZULDU, KAPALI
    is_tipi VARCHAR(50) DEFAULT 'ARIZA',
    oncelik VARCHAR(20) DEFAULT 'NORMAL', -- DUSUK, NORMAL, YUKSEK, ACIL
    cozum_hedef_tarihi TIMESTAMP,
    olusturma_tarihi TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    guncelleme_tarihi TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (kategori_id) REFERENCES kategori(id),
    FOREIGN KEY (departman_id) REFERENCES departman(id),
    FOREIGN KEY (bildiren_personel_id) REFERENCES personel(id),
    FOREIGN KEY (atanan_teknisyen_id) REFERENCES personel(id)
);

-- ARIZA LOG / HAREKETLER TABLOSU
CREATE TABLE IF NOT EXISTS ariza_log (
    id INT PRIMARY KEY AUTO_INCREMENT,
    ariza_id INT NOT NULL,
    personel_id INT NOT NULL,
    eski_durum VARCHAR(50),
    yeni_durum VARCHAR(50),
    aciklama TEXT,
    tarih TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ariza_id) REFERENCES ariza(id),
    FOREIGN KEY (personel_id) REFERENCES personel(id)
);

-- DEPARTMAN VERİLERİ
INSERT IGNORE INTO departman (id, ad) VALUES 
(1, 'Bilgi İşlem (IT)'), 
(2, 'Teknik Servis'), 
(3, 'İnsan Kaynakları'),
(4, 'Başhekimlik'),
(5, 'Poliklinikler');

-- KATEGORİ VERİLERİ
INSERT IGNORE INTO kategori (id, ad) VALUES 
(1, 'Yazılım / Bilgi Sistemi'), 
(2, 'Donanım / Bilgisayar'), 
(3, 'Ağ / İnternet Hataları'),
(4, 'Tıbbi Cihaz Arızası'),
(5, 'Altyapı / Elektrik');

-- KAPSAMLI PERSONEL VERİLERİ (Şifre Hepsinde: 12345)
-- '12345' SHA-256: 5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5

-- ================= ADMIN / BİLGİ İŞLEM =================
INSERT INTO personel (tc_no, kullanici_adi, ad, soyad, sifre_hash, rol, departman_id, dahili_no) VALUES 
('11111111111', 'admin', 'Ahmet', 'Yılmaz', '5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5', 'ADMIN', 1, '1001'),
('11111111112', 'zeynep.kaya', 'Zeynep', 'Kaya', '5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5', 'ADMIN', 1, '1002'),
('11111111113', 'hasan.demir', 'Hasan', 'Demir', '5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5', 'ADMIN', 1, '1003');

-- ================= TEKNİSYENLER =================
INSERT INTO personel (tc_no, kullanici_adi, ad, soyad, sifre_hash, rol, departman_id, dahili_no) VALUES 
('22222222222', 'teknisyen', 'Mehmet', 'Kaya', '5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5', 'TEKNISYEN', 2, '2001'),
('22222222223', 'ali.kurt', 'Ali', 'Kurt', '5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5', 'TEKNISYEN', 2, '2002'),
('22222222224', 'canan.sahin', 'Canan', 'Şahin', '5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5', 'TEKNISYEN', 2, '2003'),
('22222222225', 'burak.yilmaz', 'Burak', 'Yılmaz', '5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5', 'TEKNISYEN', 2, '2004'),
('22222222226', 'melis.celik', 'Melis', 'Çelik', '5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5', 'TEKNISYEN', 1, '2005');

-- ================= YÖNETİCİLER =================
INSERT INTO personel (tc_no, kullanici_adi, ad, soyad, sifre_hash, rol, departman_id, dahili_no) VALUES 
('44444444441', 'yonetici', 'Hakan', 'Erkmen', '5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5', 'YONETICI', 4, '4001'),
('44444444442', 'elif.turan', 'Elif', 'Turan', '5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5', 'YONETICI', 4, '4002');

-- ================= GENEL PERSONELLER =================
INSERT INTO personel (tc_no, kullanici_adi, ad, soyad, sifre_hash, rol, departman_id, dahili_no) VALUES 
('33333333333', 'personel', 'Ayşe', 'Demir', '5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5', 'PERSONEL', 3, '3001'),
('33333333334', 'fatma.yildiz', 'Fatma', 'Yıldız', '5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5', 'PERSONEL', 3, '3002'),
('33333333335', 'asli.dogan', 'Aslı', 'Doğan', '5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5', 'PERSONEL', 5, '5001'),
('33333333336', 'busra.koc', 'Büşra', 'Koç', '5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5', 'PERSONEL', 5, '5002'),
('33333333337', 'seda.aydin', 'Seda', 'Aydın', '5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5', 'PERSONEL', 5, '5003'),
('33333333338', 'kemal.sunar', 'Kemal', 'Sunar', '5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5', 'PERSONEL', 5, '5004'),
('33333333339', 'veli.guler', 'Veli', 'Güler', '5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5', 'PERSONEL', 3, '3003');

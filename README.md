# Hastane Teknik Destek Sistemi (HTDS)

Hastane Teknik Destek Sistemi (HTDS), hastane içerisindeki tüm donanım, yazılım, tıbbi cihaz ve altyapı arızalarının bildirilmesi, takip edilmesi ve çözümlenmesi süreçlerini dijitalleştiren kapsamlı bir **JavaFX** masaüstü uygulamasıdır.

Sistem, hastane personelinin arıza bildiriminde bulunmasını, teknisyenlerin bu arızalara müdahale etmesini ve yöneticilerin tüm süreci istatistiksel olarak takip etmesini sağlar. Ayrıca **Yapay Zeka (Gemini AI)** entegrasyonu sayesinde arızalar için anında çözüm ve teşhis önerileri sunar.

---

## 🚀 Özellikler

- **Rol Bazlı Erişim (Yetkilendirme):** 
  - 🛠️ **Teknisyen:** Kendisine atanan veya havuzdaki arızaları görür, durumlarını günceller ve çözer.
  - 🏥 **Personel (Doktor, Hemşire vb.):** Kendi departmanındaki arızaları bildirir ve süreçlerini takip eder.
  - 📊 **Yönetici / Admin:** Hastane genelindeki tüm arıza istatistiklerini izler, personelleri yönetir ve sistem durumunu denetler.
- **Dinamik Kontrol Paneli (Dashboard):** Bekleyen, işlemde olan ve çözülen arızaların anlık istatistikleri ve departman bazlı yoğunluk grafikleri (PieChart).
- **Arıza Yönetimi:** Yeni arıza oluşturma, teknisyen atama, öncelik belirleme (Acil, Yüksek, Normal, Düşük) ve SLA (zaman aşımı) takibi.
- **Yapay Zeka (Gemini AI) Desteği:** Teknisyenlerin arıza açıklamalarına dayanarak yapay zekadan anında çözüm önerileri alabilmesi.
- **Dahili Rehber:** Hastane içi personellerin iletişim bilgilerine hızlı erişim.
- **Personel Yönetimi:** Yöneticilerin yeni personel ekleyebilmesi, rollerini ve departmanlarını düzenleyebilmesi.

---

## 🛠️ Kullanılan Teknolojiler

- **Programlama Dili:** Java (JDK 21 önerilir)
- **Kullanıcı Arayüzü (GUI):** JavaFX 21 & FXML
- **Veritabanı:** MySQL
- **Bağımlılık Yönetimi:** Maven
- **Şifreleme (Güvenlik):** SHA-256 (Parola Hashleme)
- **Birim Testleri:** JUnit 5
- **Yapay Zeka API:** Google Gemini Pro API

---

## ⚙️ Kurulum ve Çalıştırma

Projeyi kendi bilgisayarınızda çalıştırmak için aşağıdaki adımları izleyin:

### 1. Gereksinimler
- **Java Development Kit (JDK):** En az Sürüm 21 (Amazon Corretto, OpenJDK vb.)
- **Veritabanı:** MySQL Server (XAMPP, WAMP veya bağımsız MySQL kurulumu)
- **IDE (Önerilen):** IntelliJ IDEA (JavaFX desteği çok daha iyidir)

### 2. Veritabanının Hazırlanması
1. MySQL veritabanı sunucunuzu başlatın (Örn: XAMPP üzerinden MySQL'i "Start" yapın).
2. Proje ana dizininde bulunan **`schema.sql`** dosyasını herhangi bir veritabanı yönetim aracı (phpMyAdmin, MySQL Workbench, DBeaver vb.) kullanarak içeri aktarın (Import).
3. Bu işlem `htds_db` adında yeni bir veritabanı oluşturacak, gerekli tabloları kuracak ve içerisine test kullanıcılarını otomatik olarak ekleyecektir.

### 3. Veritabanı Bağlantı Ayarları
Projenin veritabanına bağlanabilmesi için varsayılan şifre ayarları `root` ve şifresiz (`""`) olarak ayarlıdır. Kendi MySQL yapılandırmanız farklıysa aşağıdaki dosyayı güncelleyin:
- **Dosya:** `src/main/java/com/hastane/htds/dao/DatabaseConnection.java`
- **Satırlar:** `DB_USER` ve `DB_PASSWORD` değişkenlerini kendi MySQL bilgilerinize göre değiştirin.

### 4. Projeyi IDE'de Çalıştırma
1. IntelliJ IDEA'yı açın ve **"Open"** diyerek projeyi (htds klasörünü) seçin.
2. Maven'ın bağımlılıkları indirmesini bekleyin (Sağ altta "Resolving Dependencies" çubuğu dolana kadar).
3. `src/main/java/com/hastane/htds/Main.java` dosyasını açın.
4. Sınıfın yanındaki yeşil oynatma (Run) tuşuna basarak uygulamayı başlatın.

---

## 🔑 Test Kullanıcı Hesapları

`schema.sql` dosyasını veritabanına yüklediğinizde sisteme otomatik olarak aşağıdaki test hesapları tanımlanır. Tüm hesapların şifresi **`12345`**'tir.

| Rol | Kullanıcı Adı | Şifre | Departman |
| :--- | :--- | :--- | :--- |
| **Admin** | `admin` | 12345 | Bilgi İşlem (IT) |
| **Teknisyen** | `teknisyen` | 12345 | Teknik Servis |
| **Teknisyen** | `ali.kurt` | 12345 | Teknik Servis |
| **Yönetici** | `yonetici` | 12345 | Başhekimlik |
| **Personel** | `personel` | 12345 | İnsan Kaynakları |
| **Personel** | `asli.dogan` | 12345 | Poliklinikler |

*(Giriş ekranındaki "Hızlı Test" butonlarını kullanarak da tek tıkla şifre yazmadan giriş yapabilirsiniz.)*

---

## 📂 Proje Mimarisi

Proje **MVC (Model-View-Controller)** tasarım desenine uygun olarak tasarlanmıştır.

- **`model/`**: Veritabanındaki tabloların Java sınıfı karşılıkları (Personel, Ariza, Departman vb.) ve Enum sınıfları.
- **`dao/`**: (Data Access Object) Veritabanı bağlantısı ve SQL (INSERT, SELECT, UPDATE) işlemlerinin yapıldığı sınıflar.
- **`service/`**: İş mantığının yürütüldüğü sınıflar (Örn: AuthService giriş yetkilendirmesi yapar, GeminiService yapay zeka entegrasyonunu yönetir).
- **`controller/`**: JavaFX arayüzleri (`.fxml` dosyaları) ile arka plan kodlarını birbirine bağlayan sınıflar.
- **`util/`**: Tüm projede kullanılan ortak araçlar (TC Kimlik Doğrulama, Şifre Hashleme, Dil Yönetimi vb.).
- **`resources/view/`**: Arayüz tasarımlarının yapıldığı `.fxml` ve şekillendirmelerin yapıldığı `.css` dosyaları.

---

## 🤖 Gemini AI Özelliğinin Etkinleştirilmesi

Eğer arıza teşhisinde gerçek yapay zeka desteğini kullanmak isterseniz:
1. Google AI Studio'dan (aistudio.google.com) bir adet **Gemini API Key** edinin.
2. `src/main/java/com/hastane/htds/service/GeminiService.java` dosyasını açın.
3. `API_KEY` sabitinin içerisine kendi şifrenizi yapıştırın.
4. Arıza kayıtları sayfasından bir arızaya tıklayıp sağ paneldeki "Gemini Yapay Zeka Teşhis" butonuna basarak yapay zekanın arıza hakkındaki çözüm önerilerini okuyabilirsiniz. (API Key boş bırakılırsa sistem rastgele yerel öneriler sunacak şekilde simüle edilmiştir).

---

## 🔒 Güvenlik

- Projede hiçbir kullanıcının parolası veritabanında düz metin (plain-text) olarak saklanmaz. SHA-256 algoritması ile özet (hash) haline getirilerek kaydedilir.
- Tüm SQL sorguları `PreparedStatement` kullanılarak oluşturulmuş ve SQL Injection saldırılarına karşı koruma sağlanmıştır.
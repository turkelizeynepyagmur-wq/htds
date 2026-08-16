package com.hastane.htds.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class GeminiService {

    // Eger gercek bir API anahtariniz varsa buraya yazabilirsiniz.
    // Yoksa sistem otomatik olarak gercekci bir yerel yapay zeka simülasyonu çalıştıracaktır.
    private static final String API_KEY = "";

    public static String getTroubleshootingAdvice(String title, String description, String category, boolean isTechnical) {
        String envKey = System.getenv("GEMINI_API_KEY");
        String activeKey = (envKey != null && !envKey.isBlank()) ? envKey : API_KEY;

        if (activeKey == null || activeKey.isBlank()) {
            return generateMockAdvice(title, description, category, isTechnical);
        }

        try {
            String roleText = isTechnical ? "Sen hastane bilgi işlem teknisyenlerine teknik arıza çözme önerileri veren uzman bir AI asistanısın. Kullanıcı şu arızayı bildirdi: Başlık: " + title + ", Açıklama: " + description + ", Kategori: " + category + ". Lütfen bu arıza için bilgi işlem teknisyeninin (uzman kişinin) uygulayabileceği 2-3 maddelik teknik ve pratik çözüm/müdahale önerisi yaz. Teknik dil kullan. Tamamen Türkçe ve kısa olmalı."
                : "Sen hastane personelinin (doktor, hemşire, sekreter vb.) teknik sorunlarını çözmelerine yardımcı olan Bilgi İşlem yardım masası asistanısın. Kullanıcı şu sorunu bildirdi: Başlık: " + title + ", Açıklama: " + description + ", Kategori: " + category + ". Lütfen bu personelin anlayabileceği sadelikte (teknik kelimelerden uzak, adım adım) 2-3 maddelik pratik çözüm önerisi yaz. Tamamen Türkçe ve kısa olmalı.";

            String requestBody = "{\"contents\": [{\"parts\":[{\"text\": \"" + escapeJson(roleText) + "\"}]}]}";

            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + activeKey))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return parseTextFromGeminiResponse(response.body());
            } else {
                System.err.println("Gemini API Hata Kodu: " + response.statusCode() + " | Gövde: " + response.body());
                return "Gemini API bağlantısı başarısız oldu (Hata Kodu: " + response.statusCode() + "). Yerel Öneri:\n\n" + 
                        generateMockAdvice(title, description, category, isTechnical);
            }
        } catch (Exception e) {
            System.err.println("Gemini API çağrısı sırasında hata: " + e.getMessage());
            return "Gemini API bağlantı hatası. Yerel Öneri:\n\n" + 
                    generateMockAdvice(title, description, category, isTechnical);
        }
    }

    private static String parseTextFromGeminiResponse(String json) {
        String textKey = "\"text\": \"";
        int startIndex = json.indexOf(textKey);
        if (startIndex != -1) {
            startIndex += textKey.length();
            int endIndex = json.indexOf("\"", startIndex);
            while (endIndex != -1 && json.charAt(endIndex - 1) == '\\') {
                endIndex = json.indexOf("\"", endIndex + 1);
            }
            if (endIndex != -1) {
                String text = json.substring(startIndex, endIndex);
                // Unescape JSON string
                return text.replace("\\n", "\n")
                           .replace("\\\"", "\"")
                           .replace("\\\\", "\\")
                           .replace("\\t", "\t")
                           .trim();
            }
        }
        return "Yapay zeka yanıtı çözümlenemedi. Ham veri:\n" + json;
    }

    private static String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }

    private static String generateMockAdvice(String title, String description, String category, boolean isTechnical) {
        String lowerTitle = (title + " " + (description != null ? description : "")).toLowerCase(java.util.Locale.forLanguageTag("tr-TR"));
        
        if (isTechnical) {
            // Technicians get technical troubleshooting tips
            if (lowerTitle.contains("barkod") || lowerTitle.contains("yazıcı") || lowerTitle.contains("basmı")) {
                return "🤖 Gemini Teknik Çözüm Önerisi (Yerel Mod):\n\n" +
                        "1. Spooler (Yazdırma Biriktiricisi) hizmetini uzaktan yeniden başlatın (`net stop spooler && net start spooler`).\n" +
                        "2. Cihazın Windows üzerindeki bağlantı portunu (LPT, USB veya TCP/IP Port) kontrol edin; port çakışmasını giderin.\n" +
                        "3. Yazıcı sürücüsünü (Driver) kaldırıp güncel Zebra/Argox ZPL sürücüsünü kurun.";
            } else if (lowerTitle.contains("mys") || lowerTitle.contains("kurulum") || lowerTitle.contains("malzeme")) {
                return "🤖 Gemini Teknik Çözüm Önerisi (Yerel Mod):\n\n" +
                        "1. İlgili bilgisayarın kayıt defterindeki (Registry) MYS servis yollarını kontrol edin.\n" +
                        "2. Uzaktan bağlanarak MYS veri dosyalarını (%appdata%/MYS) temizleyip MYS_Setup.exe dosyasını yönetici olarak tekrar çalıştırın.\n" +
                        "3. Java Runtime (JRE) ve veritabanı bağlantı portlarının (3306/1433) antivirüs tarafından engellenmediğinden emin olun.";
            } else if (lowerTitle.contains("internet") || lowerTitle.contains("bağlantı") || lowerTitle.contains("ağ")) {
                return "🤖 Gemini Teknik Çözüm Önerisi (Yerel Mod):\n\n" +
                        "1. Bilgisayara uzaktan bağlanarak ethernet kartı sürücüsünü güncelleyin.\n" +
                        "2. DHCP IP havuzunu kontrol ederek IP çakışması olup olmadığını denetleyin ve `ipconfig /flushdns` komutu çalıştırın.\n" +
                        "3. Cihazın bağlı olduğu switch portunun VLAN ve port güvenlik ayarlarını (MAC limiting) kontrol edin.";
            }
            return "🤖 Gemini Teknik Çözüm Önerisi (Yerel Mod):\n\n" +
                    "1. Uzaktan destek konsolu ile bilgisayara bağlanarak Olay Görüntüleyicisi (Event Viewer) hata loglarını inceleyin.\n" +
                    "2. Antivirüs veya Güvenlik Duvarı ayarlarının uygulamaları engellemediğinden emin olun.\n" +
                    "3. Bilgisayarı güvenli modda çalıştırarak çakışan sürücü veya uygulamaları tespit edin.";
        } else {
            // Personnel (doctors, nurses) get simple, friendly tips
            if (lowerTitle.contains("barkod") || lowerTitle.contains("yazıcı") || lowerTitle.contains("basmı")) {
                return "🤖 Gemini Çözüm Önerisi (Yerel Mod):\n\n" +
                        "1. Barkod yazıcısının arkasındaki siyah elektrik kablosunu prizden çekip 10 saniye bekleyin ve geri takın.\n" +
                        "2. Yazıcının önündeki yeşil ışığın sürekli yandığından (kırmızı yanmadığından veya yanıp sönmediğinden) emin olun.\n" +
                        "3. Barkod rulosunun doğru takıldığını ve kağıdın sıkışmadığını kontrol edin.";
            } else if (lowerTitle.contains("mys") || lowerTitle.contains("kurulum") || lowerTitle.contains("malzeme")) {
                return "🤖 Gemini Çözüm Önerisi (Yerel Mod):\n\n" +
                        "1. Masaüstünüzdeki 'MYS Kurulum' veya 'MYS Güncelle' programını açıp 'Kur' butonuna basarak dosyaları yenileyin.\n" +
                        "2. Programı açarken üzerine sağ tıklayıp 'Yönetici Olarak Çalıştır' seçeneğini tıklayarak açmayı deneyin.\n" +
                        "3. Sorun devam ederse bilgisayarınızı yeniden başlatıp tekrar deneyin.";
            } else if (lowerTitle.contains("internet") || lowerTitle.contains("bağlantı") || lowerTitle.contains("ağ")) {
                return "🤖 Gemini Çözüm Önerisi (Yerel Mod):\n\n" +
                        "1. Bilgisayar kasanızın arkasına bağlı olan mavi veya gri renkli internet kablosunun yerinden oynamadığını kontrol edin.\n" +
                        "2. Kablonun takılı olduğu yuvada küçük yeşil veya sarı ışıkların yanıp söndüğünden emin olun.\n" +
                        "3. Ekranınızın sağ alt köşesindeki dünya veya bilgisayar simgesine sağ tıklayarak 'Sorunları gider' deyin.";
            }
            return "🤖 Gemini Çözüm Önerisi (Yerel Mod):\n\n" +
                    "1. Bilgisayarınızı kapatıp 15 saniye bekledikten sonra tekrar açmayı (yeniden başlatmayı) deneyin. Bu işlem çoğu geçici hatayı giderir.\n" +
                    "2. Açmaya çalıştığınız programın açık kalmış eski pencereleri varsa bunları kapatıp programı yeniden açın.\n" +
                    "3. Hataya neden olan işlemleri sırasıyla tekrar deneyerek hatanın sürekli mi yoksa anlık mı olduğunu gözlemleyin.";
        }
    }
}

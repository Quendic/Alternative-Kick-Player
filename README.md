# Alternatif Kick Player 🎮

Kick.com platformu için geliştirilmiş, hafif, hızlı ve kullanıcı dostu bir Android canlı yayın izleme uygulaması. Resmi uygulamanın ağırlığından uzak, sadece yayına odaklanan bir deneyim sunar.

![App Mockup](https://raw.githubusercontent.com/Quendic/Alternative-Kick-Player/main/app/src/main/res/drawable/ic_launcher_foreground.png)

## ✨ Özellikler

- **⚡ Hızlı Erişim:** Yayıncı ismini yazın ve anında izlemeye başlayın.
- **⭐ Favori Sistemi:** Sevdiğiniz yayıncıları favorilerinize ekleyin, canlı olup olmadıklarını anlık olarak takip edin.
- **⚙️ Manuel Kalite Seçimi:** İnternet hızınıza göre 160p'den 1080p'ye kadar manuel kalite geçişi (Adaptive Bitrate).
- **📺 Gelişmiş Player:** 
    - Tam ekran (Immersive Mode) desteği.
    - Otomatik gizlenen kontroller (Daha temiz bir izleme alanı).
    - Basit Oynat/Durdur arayüzü.
- **🌙 Modern Tasarım:** Neon yeşili aksanları ile göz yormayan karanlık tema.
- **🔐 Güvenli:** Resmi olmayan Kick API'lerini kullanarak en güncel yayın verilerini çeker.

## 🛠️ Kullanılan Teknolojiler

- **Dil:** Kotlin
- **Video Engine:** [Media3 ExoPlayer](https://developer.android.com/guide/topics/media/exoplayer) (HLS & Adaptive Bitrate)
- **Networking:** OkHttp & Gson
- **UI:** ViewBinding, ConstraintLayout, Material Design 3
- **Veri Saklama:** SharedPreferences (Favoriler için)

## 🚀 Kurulum ve Derleme

Projeyi yerel makinenizde derlemek için:

1. Depoyu klonlayın:
   ```bash
   git clone https://github.com/Quendic/Alternative-Kick-Player.git
   ```
2. Android Studio ile açın.
3. Gradle senkronizasyonunun tamamlanmasını bekleyin.
4. APK oluşturmak için:
   ```bash
   ./gradlew assembleRelease
   ```

## 📸 Ekran Görüntüleri

| Ana Ekran | Oynatıcı | Kalite Seçimi |
|-----------|----------|---------------|
| ![Main](https://via.placeholder.com/200x400?text=Main+Screen) | ![Player](https://via.placeholder.com/200x400?text=Player+Screen) | ![Quality](https://via.placeholder.com/200x400?text=Quality+Dialog) |

---
**Not:** Bu uygulama eğitim amaçlı geliştirilmiş bir "Alternative Client" projesidir. Kick.com ile resmi bir bağı yoktur.

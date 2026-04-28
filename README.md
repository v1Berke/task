# Task Manager

Kişisel görev takip uygulaması. Spring Boot tabanlı REST API + tek dosya SPA frontend.

---

## İçindekiler

- [Özellikler](#özellikler)
- [Teknoloji Yığını](#teknoloji-yığını)
- [Kurulum ve Çalıştırma](#kurulum-ve-çalıştırma)
- [API Referansı](#api-referansı)

---

## Özellikler

- Kullanıcı kaydı ve girişi (session tabanlı, cookie)
- Görev oluşturma, düzenleme, silme
- Durum yönetimi: `TODO` → `IN_PROGRESS` → `DONE`
- Deadline belirleme ve gecikme uyarısı
- Görevleri duruma göre filtreleme
- Şifre değiştirme
- Veritabanı istatistikleri ve bakım araçları (VACUUM, ANALYZE)
- Schema migration geçmişi
- Otomatik demo veri yükleme (ilk başlatmada)
- Swagger UI ile interaktif API dokümantasyonu

---

## Teknoloji Yığını

| Katman | Teknoloji |
|---|---|
| Backend Framework | Spring Boot 3.2.0 (Java 21) |
| Güvenlik | Spring Security 6 — Session / Cookie |
| Veritabanı | SQLite 3.45.1 |
| ORM | Spring Data JPA + Hibernate |
| Migrations | Özel `DatabaseMigrationManager` |
| API Dokümantasyon | SpringDoc OpenAPI 2.3.0 (Swagger UI) |
| Build | Maven 3.9+ |
| Frontend | Vanilla HTML/CSS/JavaScript (tek dosya) |

---

## Kurulum ve Çalıştırma

### Gereksinimler

- JDK 21+ (Java 24 ile test edilmiştir)
- Maven 3.9+ (ya da proje içindeki `./mvnw` wrapper kullanılabilir)

### Çalıştırma

```bash
# Projeyi klonla
git clone <repo-url>
cd TaskManager

# Derle
./mvnw compile

# Çalıştır
./mvnw spring-boot:run
```

Uygulama `http://localhost:8080` adresinde başlar.

### Demo Hesabı

İlk başlatmada veritabanı boşsa otomatik olarak oluşturulur:

| Alan | Değer |
|---|---|
| Kullanıcı Adı | `demo` |
| Şifre | `demo123` |
| E-posta | `demo@example.com` |

> Veritabanı zaten doluysa demo verisi oluşturulmaz.

### IntelliJ IDEA ile Çalıştırma

1. Proje kök dizinini aç
2. `TaskManagerApplication.java` → sağ tık → **Run**
3. Uygulamayı durdurmak için Run panelindeki **kırmızı kare** butonuna tıkla ve tamamen kapanmasını bekle

---

## API Referansı

### Swagger UI

```
http://localhost:8080/swagger-ui.html
```

Tüm endpoint'leri interaktif olarak test edebilirsiniz. Login yapıp cookie aldıktan sonra session otomatik olarak kullanılır.

# 🎬 CinemaApp - Aplikasi Pemesanan Tiket Bioskop 🎟️



## 📝 Deskripsi Proyek
CinemaApp merupakan aplikasi yang dibangun menggunakan JavaFX sebagai bagian dari proyek akhir praktikum Pemrograman Berbasis Objek pada semester dua. Aplikasi ini bertujuan untuk mempermudah pengguna dalam melakukan pemesanan tiket bioskop secara digital. Melalui CinemaApp, pengguna dapat menelusuri jadwal film, memilih kursi sesuai keinginan, dan melakukan reservasi tiket dengan mudah. Fitur-fitur yang disediakan dirancang untuk memberikan pengalaman pemesanan yang cepat, nyaman, dan terintegrasi.

## 🎬 Fitur Utama

- **Registrasi Akun User**: Pengguna dapat membuat akun baru dengan mengisi data pribadi untuk mulai menggunakan aplikasi.
- **Login Akun User**: Pengguna yang telah terdaftar dapat masuk ke dalam sistem menggunakan username dan password.
- **Pemilihan Film**: Menampilkan daftar film yang tersedia lengkap dengan detail
- **Pemilihan Waktu Film**: Pengguna dapat memilih jadwal tayang film sesuai preferensi waktu.
- **Pemilihan Kursi**: Menyediakan visualisasi kursi teater dan memungkinkan pengguna memilih kursi yang masih tersedia.
- **Pemilihan Snack**: Pengguna dapat menambahkan makanan atau minuman sebagai bagian dari pembelian tiket.
- **Pengaturan Saldo Balance**: Pengguna dapat melihat dan mengatur saldo, termasuk fitur *top-up* untuk menambahkan dana sebelum melakukan transaksi.

## 🧩 Penerapan 4 Pilar OOP 

### 1. Encapsulation (Enkapsulasi)

**Tujuan**: Menyembunyikan detail implementasi dan hanya mengekspos yang dibutuhkan melalui method publik.

### Diterapkan di:
- **Model**:
  - `User`, `Movie`, `Ticket`: Menggunakan `private` field dan `public getter/setter`.
- **Service**:
  - `UserManager`, `BookingManager`, dll: Menyediakan method publik untuk memanipulasi data pengguna, film, dan tiket tanpa mengekspos langsung detail struktur penyimpanan.

---

### 2. Inheritance (Pewarisan)

**Tujuan**: Memungkinkan kelas untuk mewarisi atribut dan method dari kelas lain.

### Diterapkan di:
- **Controller**:
  - Semua controller (`AuthController`, `BookingController`, `TopUpController`, dll) mewarisi dari `BaseController`, yang menyediakan method umum seperti `setScene` dan `showMessage`.
- **Service**:
  - `BaseFileService` digunakan sebagai kelas induk untuk `UserManager`, `MovieManager`, dan `BookingManager`, yang berbagi logika pemrosesan file dasar.

---

## 3. Polymorphism (Polimorfisme)

**Tujuan**: Memungkinkan satu antarmuka digunakan untuk berbagai jenis objek.

### Diterapkan di:
- **Service**:
  - `IFileService` sebagai interface untuk layanan berbasis file, yang diimplementasikan oleh beberapa kelas seperti `UserManager`, `MovieManager`, `BookingManager`. Ini memungkinkan pemanggilan method `readFromFile()` atau `writeToFile()` tanpa peduli jenis objek spesifik.
- **Controller**:
  - Polimorfisme melalui overriding method `show()` di berbagai controller yang diwarisi dari `BaseController`.

---

## 4. Abstraction (Abstraksi)

**Tujuan**: Menyembunyikan kompleksitas dan hanya menampilkan fungsionalitas yang relevan kepada pengguna.

### Diterapkan di:
- **Controller**:
  - Setiap controller menyederhanakan interaksi UI menjadi fungsi-fungsi seperti `showLoginForm`, `showTopUpScene`, dll, tanpa harus menunjukkan bagaimana JavaFX bekerja secara internal.
- **Service**:
  - `BaseFileService` dan interface `IFileService` menyembunyikan detail pembacaan/penulisan file dari controller atau model.

---
## 🛠️ Cara menjalankan projek

1. Clone repository ini ke komputer Anda menggunakan Git:

    ```bash
    git clone https://github.com/username/KLP-7-Final.git
    ```

2. Masuk ke direktori proyek:

    ```bash
    cd KLP-7-Final
    ```

3. Pastikan Anda sudah menginstal **JDK 23** dan **Gradle 8.8**.

4. Bangun proyek dengan perintah berikut:

    ```bash
    gradle build
    ```

5. Jalankan aplikasi menggunakan perintah berikut:

    ```bash
    gradle run
    ```

6. Aplikasi akan terbuka dengan tampilan login. Anda dapat mendaftar akun baru atau login jika sudah memiliki akun.

7. Setelah masuk, Anda dapat:
    - Memilih film dan waktu tayang
    - Memilih kursi dan snack
    - Melakukan pembayaran dengan saldo
    - Top up saldo jika diperlukan

## 👥 Kontributor
- Nabila Salsabila
- Andi Sophie Banuna Amrie
- Andi Eryn Nur Alisya





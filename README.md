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

## Penerapan Pillar OOP

| **Konsep**        | **Implementasi**                                                                                                                                                                                                     |
| ----------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Encapsulation** | Atribut seperti `username`, `fullname`, dan `balance` di kelas `User` bersifat `private`, hanya bisa diakses lewat getter/setter untuk menjaga data tetap aman dan konsisten.                                        |
| **Abstraction**   | Proses seperti pembayaran dan booking dikemas dalam class seperti `PaymentController` dan `BookingController`, sehingga kita hanya perlu pakai method seperti `showTopUpScreen()` tanpa tahu detail internalnya.     |
| **Inheritance**   | Kelas `Main` mewarisi `Application` dari JavaFX untuk bisa menjalankan method `start()`. |
| **Polymorphism**  | Method seperti `pay()` bisa punya perilaku berbeda tergantung jenis pembayaran. Semua controller juga punya method `show*Screen()` dengan nama serupa tapi tampilan dan fungsi yang berbeda.                         |


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

## Struktur Direktori

## Kontributor
- Nabila Salsabila
- Andi Sophie Banuna Amrie
- Andi Eryn Nur Alisya

<h1 align="center"> 
    🎬 PETIK 🎟️
</h1> 
    <p align="center"> 
        <b> 
        Pemesanan Tiket
        </b> 
    </p>


## 📝 Deskripsi Proyek
`PETIK` atau `Pemesanan Tiket` merupakan aplikasi yang dibangun menggunakan JavaFX sebagai bagian dari proyek akhir praktikum Pemrograman Berbasis Objek pada semester dua. Aplikasi ini bertujuan untuk mempermudah pengguna dalam melakukan pemesanan tiket bioskop secara digital. Melalui `PETIK`, pengguna dapat menelusuri jadwal film, memilih kursi sesuai keinginan, dan melakukan reservasi tiket dengan mudah. Fitur-fitur yang disediakan dirancang untuk memberikan pengalaman pemesanan yang cepat, nyaman, dan terintegrasi.

---

## 🎬 Fitur Utama

1. **Registrasi Akun User**: Pengguna dapat membuat akun baru dengan mengisi data berupa `Username` dan `Password` untuk mulai menggunakan aplikasi.
2. **Login Akun User**: Pengguna yang telah terdaftar dapat masuk ke dalam sistem menggunakan username dan password.
3. **Pemilihan Film**: Menampilkan daftar film yang tersedia
4. **Pemutaran Thriller Film**: Menampilkan cuplikan thriller film kepada user sebagai bahan pertimbangan dalam pembelian tiket
4. **Pemilihan Waktu Film**: Pengguna dapat memilih jadwal tayang film sesuai preferensi waktu.
5. **Pemilihan Kursi**: Menyediakan visualisasi kursi teater dan memungkinkan pengguna memilih kursi yang masih tersedia.
6. **Pemilihan Snack**: Pengguna dapat menambahkan makanan atau minuman sebagai bagian dari pembelian tiket.
7. **Pengaturan Saldo Balance**: Pengguna dapat melihat dan mengatur saldo, termasuk fitur *top-up* untuk menambahkan dana sebelum melakukan transaksi.

---

## 🧩 Penerapan 4 Pilar OOP 

### 1. Encapsulation (Enkapsulasi)

**Tujuan**: Menyembunyikan detail implementasi dan hanya mengekspos yang dibutuhkan melalui method publik.

#### Diterapkan di:
- **Model**:
  - `User`, `Movie`, `Ticket`: Menggunakan `private` field dan `public getter/setter`.
- **Service**:
  - `UserManager`, `BookingManager`, dll: Menyediakan method publik untuk memanipulasi data pengguna, film, dan tiket tanpa mengekspos langsung detail struktur penyimpanan.

---

### 2. Inheritance (Pewarisan)

**Tujuan**: Memungkinkan kelas untuk mewarisi atribut dan method dari kelas lain.

#### Diterapkan di:
- **Controller**:
  - Semua controller (`AuthController`, `BookingController`, `TopUpController`, dll) mewarisi dari `BaseController`, yang menyediakan method umum seperti `setScene` dan `showMessage`.
- **Service**:
  - `BaseFileService` digunakan sebagai kelas induk untuk `UserManager`, `MovieManager`, dan `BookingManager`, yang berbagi logika pemrosesan file dasar.

---

### 3. Polymorphism (Polimorfisme)

**Tujuan**: Memungkinkan satu antarmuka digunakan untuk berbagai jenis objek.

#### Diterapkan di:
- **Service**:
  - `IFileService` sebagai interface untuk layanan berbasis file, yang diimplementasikan oleh beberapa kelas seperti `UserManager`, `MovieManager`, `BookingManager`. Ini memungkinkan pemanggilan method `readFromFile()` atau `writeToFile()` tanpa peduli jenis objek spesifik.
- **Controller**:
  - Polimorfisme melalui overriding method `show()` di berbagai controller yang diwarisi dari `BaseController`.

---

### 4. Abstraction (Abstraksi)

**Tujuan**: Menyembunyikan kompleksitas dan hanya menampilkan fungsionalitas yang relevan kepada pengguna.

#### Diterapkan di:
- **Controller**:
  - Setiap controller menyederhanakan interaksi UI menjadi fungsi-fungsi seperti `showLoginForm`, `showTopUpScene`, dll, tanpa harus menunjukkan bagaimana JavaFX bekerja secara internal.
- **Service**:
  - `BaseFileService` dan interface `IFileService` menyembunyikan detail pembacaan/penulisan file dari controller atau model.

---

## 🛠️ Cara menjalankan projek

1. Pastikan Anda sudah menginstal 
    - **Java Development Kit (JDK) 23**
    - **Gradle versi 8.8**

2. Clone repository ini ke komputer Anda menggunakan Git:

    ```bash
    git clone https://github.com/sophieamrie/PETIK.git
    ```

3. Masuk ke direktori proyek:

    ```bash
    cd KLP-7-Final
    ```

    lalu :
    ```bash
    cd PetikApp
    ```

5. Bangun proyek dengan perintah berikut:

    ```bash
    gradle build
    ```

6. Jalankan aplikasi menggunakan perintah berikut:

    ```bash
    gradle run
    ```

7. Aplikasi akan terbuka dengan tampilan login. Anda dapat mendaftar akun baru atau login jika sudah memiliki akun.

8. Setelah masuk, Anda dapat:
    - Memilih film dan waktu tayang
    - Memutar thriller film
    - Memilih kursi dan snack
    - Melakukan pembayaran dengan saldo
    - Top up saldo jika diperlukan

## 👥 Kontributor

**KELOMPOK 19**
o Andi Sophie Banuna Amrie
o Andi Eryn Nur Alisya
o Nabila Salsabila

<a href="https://github.com/sophieamrie/KLP7-FINAL/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=sophieamrie/KLP7-FINAL" />
</a>
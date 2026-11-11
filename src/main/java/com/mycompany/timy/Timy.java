package com.mycompany.timy;

import java.util.ArrayList;

public class Timy {

    private static final String[] HARI = {"Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu"};
    private static DataTimy data;

    public static void main(String[] args) {
        data = PenyimpananData.muat();

        System.out.println("==========================================");
        System.out.println("   TIMY - Time Intelligence for Managing  ");
        System.out.println("              Your Time                   ");
        System.out.println("==========================================");

        boolean berjalan = true;
        while (berjalan) {
            System.out.println("\n=== MENU UTAMA ===");
            System.out.println("1. Kelola Jadwal Kuliah");
            System.out.println("2. Hitung Rekomendasi Keberangkatan");
            System.out.println("3. Riwayat & Evaluasi Perjalanan");
            System.out.println("4. Keluar");
            int pilihan = Input.angka("Pilih menu (1-4): ", 1, 4);

            switch (pilihan) {
                case 1:
                    menuJadwal();
                    break;
                case 2:
                    hitungRekomendasi();
                    break;
                case 3:
                    menuRiwayat();
                    break;
                case 4:
                    PenyimpananData.simpan(data);
                    System.out.println("Data tersimpan. Terima kasih, semoga tidak terlambat!");
                    berjalan = false;
                    break;
            }
        }
        Input.tutup();
    }

    // ====================== JADWAL ======================
    private static void menuJadwal() {
        boolean kembali = false;
        while (!kembali) {
            System.out.println("\n--- Kelola Jadwal Kuliah ---");
            System.out.println("1. Tambah  2. Lihat  3. Ubah  4. Hapus  5. Kembali");
            int p = Input.angka("Pilih (1-5): ", 1, 5);
            switch (p) {
                case 1:
                    tambahJadwal();
                    break;
                case 2:
                    lihatJadwal();
                    break;
                case 3:
                    ubahJadwal();
                    break;
                case 4:
                    hapusJadwal();
                    break;
                case 5:
                    kembali = true;
                    break;
            }
        }
    }

    private static String pilihHari() {
        for (int i = 0; i < HARI.length; i++) {
            System.out.println((i + 1) + ". " + HARI[i]);
        }
        return HARI[Input.angka("Pilih hari (1-" + HARI.length + "): ", 1, HARI.length) - 1];
    }

    private static void tambahJadwal() {
        String mk = Input.teks("Nama mata kuliah : ");
        String hari = pilihHari();
        int jam = Input.waktu("Jam mulai (HH:mm) : ");
        data.getDaftarJadwal().add(new JadwalKuliah(mk, hari, jam));
        PenyimpananData.simpan(data);
        System.out.println("Jadwal berhasil ditambahkan.");
    }

    private static boolean lihatJadwal() {
        ArrayList<JadwalKuliah> list = data.getDaftarJadwal();
        if (list.isEmpty()) {
            System.out.println("Belum ada jadwal kuliah.");
            return false;
        }
        System.out.println("\n No | Mata Kuliah                   | Hari    | Jam");
        for (int i = 0; i < list.size(); i++) {
            System.out.printf("%3d | %s%n", i + 1, list.get(i));
        }
        return true;
    }

    private static void ubahJadwal() {
        if (!lihatJadwal()) return;
        int idx = Input.angka("Nomor jadwal yang diubah: ", 1, data.getDaftarJadwal().size()) - 1;
        JadwalKuliah j = data.getDaftarJadwal().get(idx);

        String mk = Input.teksOpsional("Mata kuliah baru (kosong = tetap): ");
        if (!mk.isEmpty()) j.setMataKuliah(mk);

        String ubahHari = Input.teksOpsional("Ubah hari? (y/n): ");
        if (ubahHari.equalsIgnoreCase("y")) j.setHari(pilihHari());

        int jam = Input.waktuOpsional("Jam mulai baru HH:mm (kosong = tetap): ");
        if (jam >= 0) j.setJamMulaiMenit(jam);

        PenyimpananData.simpan(data);
        System.out.println("Jadwal berhasil diubah.");
    }

    private static void hapusJadwal() {
        if (!lihatJadwal()) return;
        int idx = Input.angka("Nomor jadwal yang dihapus: ", 1, data.getDaftarJadwal().size()) - 1;
        JadwalKuliah j = data.getDaftarJadwal().remove(idx);
        PenyimpananData.simpan(data);
        System.out.println("Jadwal \"" + j.getMataKuliah() + "\" dihapus.");
    }

    // ====================== REKOMENDASI (CORE FEATURE) ======================
    private static KondisiPerjalanan pilihKondisi() {
        System.out.println("Kondisi perjalanan:");
        System.out.println("1. Lancar");
        System.out.println("2. Padat");
        System.out.println("3. Macet");
        int p = Input.angka("Pilih kondisi (1-3): ", 1, 3);
        // Polimorfisme: variabel superclass menampung objek subclass
        KondisiPerjalanan kondisi;
        switch (p) {
            case 1: kondisi = new KondisiLancar(); break;
            case 2: kondisi = new KondisiPadat(); break;
            default: kondisi = new KondisiMacet(); break;
        }
        return kondisi;
    }

    private static void hitungRekomendasi() {
        System.out.println("\n--- Hitung Rekomendasi Keberangkatan ---");
        if (!lihatJadwal()) {
            System.out.println("Tambahkan jadwal kuliah terlebih dahulu di menu 1.");
            return;
        }
        int idx = Input.angka("Pilih nomor jadwal: ", 1, data.getDaftarJadwal().size()) - 1;
        JadwalKuliah jadwal = data.getDaftarJadwal().get(idx);

        int durasi = Input.angka("Estimasi durasi perjalanan normal (menit): ", 1, 600);
        KondisiPerjalanan kondisi = pilihKondisi();
        int cadangan = Input.angka("Waktu cadangan (menit): ", 0, 120);
        int rencana = Input.waktuOpsional("Jam berangkat rencana HH:mm (kosong = pakai rekomendasi): ");

        RiwayatPerjalanan hasil = Prediktor.hitung(jadwal, durasi, kondisi, cadangan, rencana);
        tampilkanHasil(hasil);

        String simpan = Input.teksOpsional("Simpan ke riwayat? (y/n): ");
        if (simpan.equalsIgnoreCase("y")) {
            data.getDaftarRiwayat().add(hasil);
            PenyimpananData.simpan(data);
            System.out.println("Tersimpan ke riwayat.");
        }
    }

    private static void tampilkanHasil(RiwayatPerjalanan r) {
        int sisa = Prediktor.hitungSisa(r);
        System.out.println("\n========== HASIL PREDIKSI ==========");
        System.out.println("Mata kuliah        : " + r.getMataKuliah() + " (" + r.getHari() + ")");
        System.out.println("Jam kuliah         : " + Waktu.format(r.getJamMulaiMenit()));
        System.out.println(kondisiBaris(r));
        System.out.println("Durasi perjalanan  : " + r.getDurasiNormal() + " + " + r.getTambahan()
                + " = " + r.getDurasiEstimasi() + " menit");
        System.out.println("Waktu cadangan     : " + r.getCadangan() + " menit");
        System.out.println("Waktu berangkat    : " + Waktu.format(r.getBerangkatMenit()));
        System.out.println("Perkiraan tiba     : " + Waktu.format(r.getTibaMenit()));
        if (sisa >= 0) {
            System.out.println("Sisa waktu         : " + sisa + " menit sebelum kuliah");
        } else {
            System.out.println("Terlambat          : " + (-sisa) + " menit");
        }
        System.out.println("Risiko terlambat   : " + r.getRisiko());
        System.out.println("====================================");
    }

    private static String kondisiBaris(RiwayatPerjalanan r) {
        return "Kondisi            : " + r.getKondisi().getDeskripsi();
    }

    // ====================== RIWAYAT ======================
    private static void menuRiwayat() {
        boolean kembali = false;
        while (!kembali) {
            System.out.println("\n--- Riwayat & Evaluasi Perjalanan ---");
            System.out.println("1. Lihat  2. Catat durasi aktual  3. Hapus  4. Kembali");
            int p = Input.angka("Pilih (1-4): ", 1, 4);
            switch (p) {
                case 1:
                    lihatRiwayat();
                    break;
                case 2:
                    catatAktual();
                    break;
                case 3:
                    hapusRiwayat();
                    break;
                case 4:
                    kembali = true;
                    break;
            }
        }
    }

    private static boolean lihatRiwayat() {
        ArrayList<RiwayatPerjalanan> list = data.getDaftarRiwayat();
        if (list.isEmpty()) {
            System.out.println("Riwayat masih kosong.");
            return false;
        }
        for (int i = 0; i < list.size(); i++) {
            RiwayatPerjalanan r = list.get(i);
            System.out.printf("%2d. %s%n", i + 1, r);
            System.out.printf("    Estimasi %d mnt | Aktual %s | Evaluasi: %s%n",
                    r.getDurasiEstimasi(),
                    r.sudahDievaluasi() ? r.getDurasiAktual() + " mnt" : "-",
                    r.getEvaluasi());
        }
        return true;
    }

    private static void catatAktual() {
        if (!lihatRiwayat()) return;
        int idx = Input.angka("Nomor riwayat: ", 1, data.getDaftarRiwayat().size()) - 1;
        RiwayatPerjalanan r = data.getDaftarRiwayat().get(idx);
        int aktual = Input.angka("Durasi perjalanan sebenarnya (menit): ", 1, 600);
        r.setDurasiAktual(aktual);
        PenyimpananData.simpan(data);
        System.out.println("Evaluasi: " + r.getEvaluasi());
    }

    private static void hapusRiwayat() {
        if (!lihatRiwayat()) return;
        int idx = Input.angka("Nomor riwayat yang dihapus: ", 1, data.getDaftarRiwayat().size()) - 1;
        data.getDaftarRiwayat().remove(idx);
        PenyimpananData.simpan(data);
        System.out.println("Riwayat dihapus.");
    }
}

package com.mycompany.timy;

/**
 * CORE FEATURE: prediksi waktu keberangkatan dan risiko keterlambatan
 * berbasis aturan (tanpa GPS / AI), sesuai laporan perencanaan.
 */
public class Prediktor {

    public static final String RISIKO_RENDAH = "RENDAH";
    public static final String RISIKO_SEDANG = "SEDANG";
    public static final String RISIKO_TINGGI = "TINGGI";

    /**
     * @param berangkatRencana jam berangkat pilihan pengguna (menit), atau -1
     *                         untuk memakai waktu berangkat rekomendasi.
     */
    public static RiwayatPerjalanan hitung(JadwalKuliah jadwal, int durasiNormal,
                                           KondisiPerjalanan kondisi, int cadangan,
                                           int berangkatRencana) {
        int tambahan = kondisi.hitungTambahan(durasiNormal);
        int totalButuh = durasiNormal + tambahan + cadangan;

        // Waktu berangkat rekomendasi = jam kuliah - total waktu yang dibutuhkan
        int berangkatRekomendasi = jadwal.getJamMulaiMenit() - totalButuh;
        int berangkat = (berangkatRencana >= 0) ? berangkatRencana : berangkatRekomendasi;

        // Perkiraan tiba = berangkat + durasi normal + tambahan kondisi
        int tiba = berangkat + durasiNormal + tambahan;

        // Sisa waktu sebelum kuliah dimulai
        int sisa = jadwal.getJamMulaiMenit() - tiba;
        String risiko;
        if (sisa <= 0) {
            risiko = RISIKO_TINGGI;
        } else if (sisa < 10) {
            risiko = RISIKO_SEDANG;
        } else {
            risiko = RISIKO_RENDAH;
        }

        return new RiwayatPerjalanan(jadwal.getMataKuliah(), jadwal.getHari(),
                jadwal.getJamMulaiMenit(), durasiNormal, kondisi, tambahan,
                cadangan, berangkat, tiba, risiko);
    }

    /** Sisa waktu (menit) antara perkiraan tiba dan jam kuliah. */
    public static int hitungSisa(RiwayatPerjalanan r) {
        return r.getJamMulaiMenit() - r.getTibaMenit();
    }
}

package com.mycompany.timy;

import java.io.Serializable;

/**
 * Representasi satu jadwal kuliah (Encapsulation: semua field private).
 */
public class JadwalKuliah implements Serializable {
    private static final long serialVersionUID = 1L;

    private String mataKuliah;
    private String hari;
    private int jamMulaiMenit; // menit sejak 00:00

    public JadwalKuliah(String mataKuliah, String hari, int jamMulaiMenit) {
        this.mataKuliah = mataKuliah;
        this.hari = hari;
        setJamMulaiMenit(jamMulaiMenit);
    }

    public String getMataKuliah() { return this.mataKuliah; }
    public void setMataKuliah(String mataKuliah) { this.mataKuliah = mataKuliah; }

    public String getHari() { return this.hari; }
    public void setHari(String hari) { this.hari = hari; }

    public int getJamMulaiMenit() { return this.jamMulaiMenit; }

    // Setter dengan validasi: jam harus di antara 00:00 - 23:59
    public void setJamMulaiMenit(int jamMulaiMenit) {
        if (jamMulaiMenit >= 0 && jamMulaiMenit < 1440) {
            this.jamMulaiMenit = jamMulaiMenit;
        } else {
            System.out.println("Jam mulai tidak valid! Nilai diatur ke 08:00.");
            this.jamMulaiMenit = 8 * 60;
        }
    }

    public String getJamMulaiTeks() { return Waktu.format(jamMulaiMenit); }

    @Override
    public String toString() {
        return String.format("%-30s | %-7s | %s", mataKuliah, hari, getJamMulaiTeks());
    }
}

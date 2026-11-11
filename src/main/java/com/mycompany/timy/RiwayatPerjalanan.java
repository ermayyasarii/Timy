package com.mycompany.timy;

import java.io.Serializable;

/**
 * Menyimpan hasil prediksi satu perjalanan beserta durasi aktual (untuk evaluasi).
 */
public class RiwayatPerjalanan implements Serializable {
    private static final long serialVersionUID = 1L;

    private String mataKuliah;
    private String hari;
    private int jamMulaiMenit;
    private int durasiNormal;
    private KondisiPerjalanan kondisi;
    private int tambahan;
    private int cadangan;
    private int berangkatMenit;
    private int tibaMenit;
    private String risiko;
    private int durasiAktual = -1; // -1 = belum dicatat

    public RiwayatPerjalanan(String mataKuliah, String hari, int jamMulaiMenit,
                             int durasiNormal, KondisiPerjalanan kondisi, int tambahan,
                             int cadangan, int berangkatMenit, int tibaMenit, String risiko) {
        this.mataKuliah = mataKuliah;
        this.hari = hari;
        this.jamMulaiMenit = jamMulaiMenit;
        this.durasiNormal = durasiNormal;
        this.kondisi = kondisi;
        this.tambahan = tambahan;
        this.cadangan = cadangan;
        this.berangkatMenit = berangkatMenit;
        this.tibaMenit = tibaMenit;
        this.risiko = risiko;
    }

    public String getMataKuliah() { return mataKuliah; }
    public String getHari() { return hari; }
    public int getJamMulaiMenit() { return jamMulaiMenit; }
    public int getDurasiNormal() { return durasiNormal; }
    public KondisiPerjalanan getKondisi() { return kondisi; }
    public int getTambahan() { return tambahan; }
    public int getCadangan() { return cadangan; }
    public int getBerangkatMenit() { return berangkatMenit; }
    public int getTibaMenit() { return tibaMenit; }
    public String getRisiko() { return risiko; }
    public int getDurasiAktual() { return durasiAktual; }

    /** Durasi estimasi = durasi normal + tambahan kondisi. */
    public int getDurasiEstimasi() { return durasiNormal + tambahan; }

    // Setter dengan validasi: durasi aktual harus positif
    public void setDurasiAktual(int durasiAktual) {
        if (durasiAktual > 0) {
            this.durasiAktual = durasiAktual;
        } else {
            System.out.println("Durasi aktual harus lebih dari 0 menit!");
        }
    }

    public boolean sudahDievaluasi() { return durasiAktual > 0; }

    /** Teks evaluasi: membandingkan estimasi dengan durasi aktual. */
    public String getEvaluasi() {
        if (!sudahDievaluasi()) return "Belum dievaluasi";
        int selisih = durasiAktual - getDurasiEstimasi();
        if (selisih > 0) return "Lebih lama " + selisih + " menit dari estimasi";
        if (selisih < 0) return "Lebih cepat " + (-selisih) + " menit dari estimasi";
        return "Tepat sesuai estimasi";
    }

    @Override
    public String toString() {
        return String.format("%-22s | %-6s | kuliah %s | berangkat %s | tiba %s | %-6s | risiko %s",
                mataKuliah, hari, Waktu.format(jamMulaiMenit), Waktu.format(berangkatMenit),
                Waktu.format(tibaMenit), kondisi.getNama(), risiko);
    }
}

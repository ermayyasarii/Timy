package com.mycompany.timy;

import java.io.Serializable;
import java.util.ArrayList;

/** Wadah seluruh data aplikasi agar bisa di-serialize sekaligus. */
public class DataTimy implements Serializable {
    private static final long serialVersionUID = 1L;

    private ArrayList<JadwalKuliah> daftarJadwal = new ArrayList<>();
    private ArrayList<RiwayatPerjalanan> daftarRiwayat = new ArrayList<>();

    public ArrayList<JadwalKuliah> getDaftarJadwal() { return daftarJadwal; }
    public ArrayList<RiwayatPerjalanan> getDaftarRiwayat() { return daftarRiwayat; }
}

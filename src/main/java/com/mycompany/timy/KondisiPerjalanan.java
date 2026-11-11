package com.mycompany.timy;

import java.io.Serializable;

/**
 * SUPERCLASS: kondisi perjalanan secara umum.
 * Subclass (KondisiLancar, KondisiPadat, KondisiMacet) mewarisi class ini
 * dan meng-override cara menghitung tambahan waktu.
 */
public class KondisiPerjalanan implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nama;

    public KondisiPerjalanan(String nama) {
        this.nama = nama;
    }

    public String getNama() { return this.nama; }
    public void setNama(String nama) { this.nama = nama; }

    /** Tambahan menit akibat kondisi. Default: tidak ada tambahan. */
    public int hitungTambahan(int durasiNormal) {
        return 0;
    }

    public String getDeskripsi() {
        return "Kondisi " + this.nama;
    }
}

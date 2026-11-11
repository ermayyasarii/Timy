package com.mycompany.timy;

/** SUBCLASS: lalu lintas lancar, tanpa tambahan waktu. */
public class KondisiLancar extends KondisiPerjalanan {
    private static final long serialVersionUID = 1L;

    public KondisiLancar() {
        super("Lancar");
    }

    @Override
    public int hitungTambahan(int durasiNormal) {
        return 0;
    }

    @Override
    public String getDeskripsi() {
        return super.getDeskripsi() + " (tambahan 0%)";
    }
}

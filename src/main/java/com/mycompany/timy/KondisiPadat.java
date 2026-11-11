package com.mycompany.timy;

/** SUBCLASS: lalu lintas padat, tambahan 40% (minimal 5 menit). */
public class KondisiPadat extends KondisiPerjalanan {
    private static final long serialVersionUID = 1L;

    public KondisiPadat() {
        super("Padat");
    }

    @Override
    public int hitungTambahan(int durasiNormal) {
        int tambahan = (int) Math.ceil(durasiNormal * 0.40);
        return Math.max(tambahan, 5);
    }

    @Override
    public String getDeskripsi() {
        return super.getDeskripsi() + " (tambahan 40%, min. 5 menit)";
    }
}

package com.mycompany.timy;

/** SUBCLASS: lalu lintas macet, tambahan 80% (minimal 10 menit). */
public class KondisiMacet extends KondisiPerjalanan {
    private static final long serialVersionUID = 1L;

    public KondisiMacet() {
        super("Macet");
    }

    @Override
    public int hitungTambahan(int durasiNormal) {
        int tambahan = (int) Math.ceil(durasiNormal * 0.80);
        return Math.max(tambahan, 10);
    }

    @Override
    public String getDeskripsi() {
        return super.getDeskripsi() + " (tambahan 80%, min. 10 menit)";
    }
}

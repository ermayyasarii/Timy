package com.mycompany.timy;

/**
 * Utilitas konversi waktu. Waktu disimpan sebagai jumlah menit sejak 00:00.
 */
public class Waktu {

    /** Mengubah menit (sejak tengah malam) menjadi teks "HH:mm". */
    public static String format(int totalMenit) {
        int m = ((totalMenit % 1440) + 1440) % 1440; // aman untuk nilai negatif
        return String.format("%02d:%02d", m / 60, m % 60);
    }

    /** Mengubah teks "HH:mm" menjadi menit. Mengembalikan -1 jika format salah. */
    public static int parse(String teks) {
        if (teks == null) return -1;
        String[] bagian = teks.trim().split("[:.]");
        if (bagian.length != 2) return -1;
        try {
            int jam = Integer.parseInt(bagian[0].trim());
            int menit = Integer.parseInt(bagian[1].trim());
            if (jam < 0 || jam > 23 || menit < 0 || menit > 59) return -1;
            return jam * 60 + menit;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}

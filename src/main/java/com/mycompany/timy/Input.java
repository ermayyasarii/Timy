package com.mycompany.timy;

import java.util.Scanner;

/** Pembantu input berbasis Scanner dengan validasi. */
public class Input {
    private static final Scanner scanner = new Scanner(System.in);

    public static String teks(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = scanner.nextLine().trim();
            if (!s.isEmpty()) return s;
            System.out.println("Input tidak boleh kosong!");
        }
    }

    /** Boleh kosong (mengembalikan string kosong). */
    public static String teksOpsional(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public static int angka(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String s = scanner.nextLine().trim();
            try {
                int n = Integer.parseInt(s);
                if (n >= min && n <= max) return n;
                System.out.println("Masukkan angka " + min + " - " + max + "!");
            } catch (NumberFormatException e) {
                System.out.println("Input harus berupa angka!");
            }
        }
    }

    public static int waktu(String prompt) {
        while (true) {
            System.out.print(prompt);
            int m = Waktu.parse(scanner.nextLine());
            if (m >= 0) return m;
            System.out.println("Format salah! Gunakan HH:mm, contoh 07:30");
        }
    }

    /** Waktu opsional: kosong mengembalikan -1. */
    public static int waktuOpsional(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = scanner.nextLine().trim();
            if (s.isEmpty()) return -1;
            int m = Waktu.parse(s);
            if (m >= 0) return m;
            System.out.println("Format salah! Gunakan HH:mm atau kosongkan.");
        }
    }

    public static void tutup() { scanner.close(); }
}

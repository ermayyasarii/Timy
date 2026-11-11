package com.mycompany.timy;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.File;

/** File I/O: menyimpan & memuat data dengan Object Serialization (Pertemuan 11). */
public class PenyimpananData {
    private static final String NAMA_FILE = "timy_data.dat";

    public static void simpan(DataTimy data) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(NAMA_FILE))) {
            out.writeObject(data);
        } catch (Exception e) {
            System.out.println("Gagal menyimpan data: " + e.getMessage());
        }
    }

    public static DataTimy muat() {
        File f = new File(NAMA_FILE);
        if (!f.exists()) return new DataTimy();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(f))) {
            return (DataTimy) in.readObject();
        } catch (Exception e) {
            System.out.println("Gagal memuat data lama, memulai data baru: " + e.getMessage());
            return new DataTimy();
        }
    }
}

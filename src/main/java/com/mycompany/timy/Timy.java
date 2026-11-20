package com.mycompany.timy;

import java.util.Optional;
import java.util.function.Function;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

/**
 * TIMY - Time Intelligence for Managing Your Time (versi GUI JavaFX).
 *
 * Layout   : BorderPane, TabPane, VBox, HBox, GridPane
 * Kontrol  : Label, TextField, ComboBox, Button, TableView
 * Event    : setOnAction pada semua tombol
 * Validasi : Alert peringatan + try-catch untuk input angka
 *
 * Seluruh logika (JadwalKuliah, Prediktor, KondisiPerjalanan dan subclass-nya,
 * PenyimpananData, dsb.) memakai kode dari versi terminal.
 */
public class Timy extends Application {

    private static final String[] HARI = {"Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu"};

    private DataTimy data;
    private String css;

    private final ObservableList<JadwalKuliah> jadwalList = FXCollections.observableArrayList();
    private final ObservableList<RiwayatPerjalanan> riwayatList = FXCollections.observableArrayList();

    private Label statusLabel;

    // --- Tab Jadwal ---
    private TextField tfMataKuliah;
    private ComboBox<String> cbHari;
    private TextField tfJamMulai;
    private TableView<JadwalKuliah> tabelJadwal;

    // --- Tab Hitung ---
    private ComboBox<JadwalKuliah> cbJadwal;
    private TextField tfDurasi;
    private ComboBox<String> cbKondisi;
    private TextField tfCadangan;
    private TextField tfRencana;
    private Label lblHasil;
    private Label lblRisiko;
    private Button btnSimpanRiwayat;
    private RiwayatPerjalanan hasilTerakhir;

    // --- Tab Riwayat ---
    private TableView<RiwayatPerjalanan> tabelRiwayat;
    private TextField tfAktual;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        data = PenyimpananData.muat();
        java.net.URL cssUrl = Timy.class.getResource("timy.css");
        css = (cssUrl != null) ? cssUrl.toExternalForm() : null;

        BorderPane root = new BorderPane();
        root.setTop(buatHeader());

        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.getTabs().addAll(
                new Tab("\uD83D\uDCC5  Jadwal Kuliah", buatTabJadwal()),
                new Tab("\uD83D\uDE97  Hitung Keberangkatan", buatTabHitung()),
                new Tab("\uD83D\uDCCA  Riwayat & Evaluasi", buatTabRiwayat()));
        root.setCenter(tabs);

        statusLabel = new Label("Siap. Data dimuat dari timy_data.dat");
        statusLabel.getStyleClass().add("status-label");
        HBox statusBar = new HBox(statusLabel);
        statusBar.getStyleClass().add("status-bar");
        root.setBottom(statusBar);

        muatUlangJadwal();
        muatUlangRiwayat();

        Scene scene = new Scene(root, 1080, 700);
        if (css != null) scene.getStylesheets().add(css);
        stage.setTitle("Timy - Time Intelligence for Managing Your Time");
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> PenyimpananData.simpan(data));
        stage.show();
    }

    // ============================================================
    //  HEADER
    // ============================================================
    private HBox buatHeader() {
        Label judul = new Label("\u23F0 TIMY");
        judul.getStyleClass().add("app-title");
        Label sub = new Label("Time Intelligence for Managing Your Time \u2014 prediksi berangkat kuliah biar nggak telat");
        sub.getStyleClass().add("app-subtitle");
        VBox teks = new VBox(2, judul, sub);
        HBox header = new HBox(teks);
        header.getStyleClass().add("header-box");
        header.setAlignment(Pos.CENTER_LEFT);
        return header;
    }

    // ============================================================
    //  TAB 1 : JADWAL KULIAH  (Tahap 1 & 2)
    // ============================================================
    private HBox buatTabJadwal() {
        // ---- Form input (GridPane) ----
        tfMataKuliah = new TextField();
        tfMataKuliah.setPromptText("contoh: Pemrograman Berorientasi Objek");

        cbHari = new ComboBox<>(FXCollections.observableArrayList(HARI));
        cbHari.setPromptText("Pilih hari");
        cbHari.setMaxWidth(Double.MAX_VALUE);

        tfJamMulai = new TextField();
        tfJamMulai.setPromptText("HH:mm, contoh 07:30");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(12);
        form.add(fieldLabel("Mata Kuliah"), 0, 0);
        form.add(tfMataKuliah, 0, 1);
        form.add(fieldLabel("Hari"), 0, 2);
        form.add(cbHari, 0, 3);
        form.add(fieldLabel("Jam Mulai"), 0, 4);
        form.add(tfJamMulai, 0, 5);
        GridPane.setHgrow(tfMataKuliah, Priority.ALWAYS);

        Button btnTambah = tombol("Tambah", "btn-hijau");
        Button btnUbah = tombol("Ubah", "btn-pink");
        Button btnHapus = tombol("Hapus", "btn-pink");
        Button btnBersih = tombol("Bersihkan", "btn-netral");

        // ---- Event Handling ----
        btnTambah.setOnAction(e -> tambahJadwal());
        btnUbah.setOnAction(e -> ubahJadwal());
        btnHapus.setOnAction(e -> hapusJadwal());
        btnBersih.setOnAction(e -> bersihkanFormJadwal());

        HBox baris1 = new HBox(8, btnTambah, btnUbah);
        HBox baris2 = new HBox(8, btnHapus, btnBersih);

        Label judulForm = new Label("Form Jadwal");
        judulForm.getStyleClass().add("card-title");
        Label hint = new Label("Klik baris pada tabel untuk mengubah / menghapus.");
        hint.getStyleClass().add("hint-label");
        hint.setWrapText(true);

        VBox kartuForm = new VBox(14, judulForm, form, baris1, baris2, hint);
        kartuForm.getStyleClass().add("card");
        kartuForm.setPrefWidth(320);
        kartuForm.setMinWidth(300);

        // ---- Tabel ----
        tabelJadwal = new TableView<>(jadwalList);
        tabelJadwal.setPlaceholder(new Label("Belum ada jadwal kuliah \uD83C\uDF38"));
        tabelJadwal.getColumns().add(kolomNomor(tabelJadwal));
        tabelJadwal.getColumns().add(kolom("Mata Kuliah", 280, (JadwalKuliah j) -> j.getMataKuliah()));
        tabelJadwal.getColumns().add(kolom("Hari", 100, (JadwalKuliah j) -> j.getHari()));
        tabelJadwal.getColumns().add(kolom("Jam Mulai", 100, (JadwalKuliah j) -> j.getJamMulaiTeks()));
        tabelJadwal.getSelectionModel().selectedItemProperty().addListener((obs, lama, baru) -> {
            if (baru != null) {
                tfMataKuliah.setText(baru.getMataKuliah());
                cbHari.setValue(baru.getHari());
                tfJamMulai.setText(baru.getJamMulaiTeks());
            }
        });
        VBox.setVgrow(tabelJadwal, Priority.ALWAYS);

        Label judulTabel = new Label("Daftar Jadwal Kuliah");
        judulTabel.getStyleClass().add("card-title");
        VBox kartuTabel = new VBox(10, judulTabel, tabelJadwal);
        kartuTabel.getStyleClass().add("card");
        HBox.setHgrow(kartuTabel, Priority.ALWAYS);

        HBox isi = new HBox(16, kartuForm, kartuTabel);
        isi.setPadding(new Insets(16));
        return isi;
    }

    private void tambahJadwal() {
        String mk = tfMataKuliah.getText().trim();
        String hari = cbHari.getValue();
        if (mk.isEmpty() || hari == null || tfJamMulai.getText().trim().isEmpty()) {
            peringatan("Data belum lengkap", "Nama mata kuliah, hari, dan jam mulai wajib diisi!");
            return;
        }
        int jam = Waktu.parse(tfJamMulai.getText());
        if (jam < 0) {
            peringatan("Format jam salah", "Gunakan format HH:mm, contoh 07:30");
            return;
        }
        data.getDaftarJadwal().add(new JadwalKuliah(mk, hari, jam));
        PenyimpananData.simpan(data);
        muatUlangJadwal();
        bersihkanFormJadwal();
        setStatus("Jadwal \"" + mk + "\" berhasil ditambahkan.");
        info("Berhasil", "Jadwal \"" + mk + "\" berhasil ditambahkan.");
    }

    private void ubahJadwal() {
        JadwalKuliah j = tabelJadwal.getSelectionModel().getSelectedItem();
        if (j == null) {
            peringatan("Belum memilih jadwal", "Pilih dulu baris jadwal pada tabel yang ingin diubah.");
            return;
        }
        String mk = tfMataKuliah.getText().trim();
        String hari = cbHari.getValue();
        if (mk.isEmpty() || hari == null || tfJamMulai.getText().trim().isEmpty()) {
            peringatan("Data belum lengkap", "Nama mata kuliah, hari, dan jam mulai wajib diisi!");
            return;
        }
        int jam = Waktu.parse(tfJamMulai.getText());
        if (jam < 0) {
            peringatan("Format jam salah", "Gunakan format HH:mm, contoh 07:30");
            return;
        }
        j.setMataKuliah(mk);
        j.setHari(hari);
        j.setJamMulaiMenit(jam);
        PenyimpananData.simpan(data);
        tabelJadwal.refresh();
        cbJadwal.setItems(null);
        cbJadwal.setItems(jadwalList);
        setStatus("Jadwal \"" + mk + "\" berhasil diubah.");
        info("Berhasil", "Jadwal berhasil diubah.");
    }

    private void hapusJadwal() {
        JadwalKuliah j = tabelJadwal.getSelectionModel().getSelectedItem();
        if (j == null) {
            peringatan("Belum memilih jadwal", "Pilih dulu baris jadwal pada tabel yang ingin dihapus.");
            return;
        }
        if (!konfirmasi("Hapus jadwal", "Yakin ingin menghapus jadwal \"" + j.getMataKuliah() + "\"?")) return;
        data.getDaftarJadwal().remove(j);
        PenyimpananData.simpan(data);
        muatUlangJadwal();
        bersihkanFormJadwal();
        setStatus("Jadwal \"" + j.getMataKuliah() + "\" dihapus.");
    }

    private void bersihkanFormJadwal() {
        tfMataKuliah.clear();
        tfJamMulai.clear();
        cbHari.setValue(null);
        tabelJadwal.getSelectionModel().clearSelection();
    }

    private void muatUlangJadwal() {
        jadwalList.setAll(data.getDaftarJadwal());
    }

    // ============================================================
    //  TAB 2 : HITUNG REKOMENDASI (CORE FEATURE)
    // ============================================================
    private HBox buatTabHitung() {
        cbJadwal = new ComboBox<>(jadwalList);
        cbJadwal.setPromptText("Pilih jadwal kuliah");
        cbJadwal.setMaxWidth(Double.MAX_VALUE);
        cbJadwal.setConverter(new StringConverter<JadwalKuliah>() {
            @Override
            public String toString(JadwalKuliah j) {
                if (j == null) return "";
                return j.getMataKuliah() + " - " + j.getHari() + " " + j.getJamMulaiTeks();
            }

            @Override
            public JadwalKuliah fromString(String s) {
                return null;
            }
        });

        tfDurasi = new TextField();
        tfDurasi.setPromptText("menit, contoh 30");

        cbKondisi = new ComboBox<>(FXCollections.observableArrayList("Lancar", "Padat", "Macet"));
        cbKondisi.setValue("Lancar");
        cbKondisi.setMaxWidth(Double.MAX_VALUE);

        tfCadangan = new TextField("10");
        tfCadangan.setPromptText("menit, contoh 10");

        tfRencana = new TextField();
        tfRencana.setPromptText("HH:mm (kosong = pakai rekomendasi)");

        GridPane form = new GridPane();
        form.setVgap(10);
        form.add(fieldLabel("Jadwal Kuliah"), 0, 0);
        form.add(cbJadwal, 0, 1);
        form.add(fieldLabel("Durasi Perjalanan Normal (menit)"), 0, 2);
        form.add(tfDurasi, 0, 3);
        form.add(fieldLabel("Kondisi Perjalanan"), 0, 4);
        form.add(cbKondisi, 0, 5);
        form.add(fieldLabel("Waktu Cadangan (menit)"), 0, 6);
        form.add(tfCadangan, 0, 7);
        form.add(fieldLabel("Jam Berangkat Rencana (opsional)"), 0, 8);
        form.add(tfRencana, 0, 9);
        GridPane.setHgrow(cbJadwal, Priority.ALWAYS);

        Button btnHitung = tombol("Hitung Prediksi", "btn-hijau");
        btnSimpanRiwayat = tombol("Simpan ke Riwayat", "btn-pink");
        btnSimpanRiwayat.setDisable(true);
        Button btnReset = tombol("Reset", "btn-netral");

        btnHitung.setOnAction(e -> hitungPrediksi());
        btnSimpanRiwayat.setOnAction(e -> simpanKeRiwayat());
        btnReset.setOnAction(e -> resetFormHitung());

        HBox tombolBaris = new HBox(8, btnHitung, btnSimpanRiwayat, btnReset);

        Label judulForm = new Label("Data Perjalanan");
        judulForm.getStyleClass().add("card-title");
        VBox kartuForm = new VBox(14, judulForm, form, tombolBaris);
        kartuForm.getStyleClass().add("card");
        kartuForm.setPrefWidth(470);
        kartuForm.setMinWidth(470);

        // ---- Panel hasil ----
        Label judulHasil = new Label("Hasil Prediksi");
        judulHasil.getStyleClass().add("card-title");

        lblRisiko = new Label();
        lblRisiko.setVisible(false);
        lblRisiko.setManaged(false);

        lblHasil = new Label("Isi data di sebelah kiri lalu tekan \"Hitung Prediksi\" \uD83C\uDF38");
        lblHasil.getStyleClass().add("hasil-teks");
        lblHasil.setWrapText(true);

        VBox boxHasil = new VBox(12, lblRisiko, lblHasil);
        boxHasil.getStyleClass().add("hasil-box");
        VBox.setVgrow(boxHasil, Priority.ALWAYS);

        VBox kartuHasil = new VBox(12, judulHasil, boxHasil);
        kartuHasil.getStyleClass().add("card");
        HBox.setHgrow(kartuHasil, Priority.ALWAYS);

        HBox isi = new HBox(16, kartuForm, kartuHasil);
        isi.setPadding(new Insets(16));
        return isi;
    }

    private void hitungPrediksi() {
        JadwalKuliah jadwal = cbJadwal.getValue();
        if (jadwal == null) {
            peringatan("Jadwal belum dipilih",
                    "Pilih jadwal kuliah dulu. Jika belum ada, tambahkan di tab Jadwal Kuliah.");
            return;
        }
        if (tfDurasi.getText().trim().isEmpty() || tfCadangan.getText().trim().isEmpty()) {
            peringatan("Data belum lengkap", "Durasi perjalanan dan waktu cadangan wajib diisi!");
            return;
        }

        int durasi;
        int cadangan;
        try {
            durasi = Integer.parseInt(tfDurasi.getText().trim());
            cadangan = Integer.parseInt(tfCadangan.getText().trim());
        } catch (NumberFormatException ex) {
            peringatan("Format angka salah", "Durasi dan waktu cadangan harus berupa angka bulat!");
            return;
        }
        if (durasi < 1 || durasi > 600) {
            peringatan("Durasi tidak valid", "Durasi perjalanan harus antara 1 - 600 menit.");
            return;
        }
        if (cadangan < 0 || cadangan > 120) {
            peringatan("Waktu cadangan tidak valid", "Waktu cadangan harus antara 0 - 120 menit.");
            return;
        }

        int rencana = -1;
        String teksRencana = tfRencana.getText().trim();
        if (!teksRencana.isEmpty()) {
            rencana = Waktu.parse(teksRencana);
            if (rencana < 0) {
                peringatan("Format jam salah", "Jam berangkat rencana harus HH:mm, contoh 06:45 (atau kosongkan).");
                return;
            }
        }

        // Polimorfisme: variabel superclass menampung objek subclass
        KondisiPerjalanan kondisi;
        String pilihan = cbKondisi.getValue();
        if ("Padat".equals(pilihan)) {
            kondisi = new KondisiPadat();
        } else if ("Macet".equals(pilihan)) {
            kondisi = new KondisiMacet();
        } else {
            kondisi = new KondisiLancar();
        }

        hasilTerakhir = Prediktor.hitung(jadwal, durasi, kondisi, cadangan, rencana);
        tampilkanHasil(hasilTerakhir);
        btnSimpanRiwayat.setDisable(false);
        setStatus("Prediksi dihitung untuk " + jadwal.getMataKuliah() + ".");
    }

    private void tampilkanHasil(RiwayatPerjalanan r) {
        int sisa = Prediktor.hitungSisa(r);
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Mata kuliah       : %s (%s)%n", r.getMataKuliah(), r.getHari()));
        sb.append(String.format("Jam kuliah        : %s%n", Waktu.format(r.getJamMulaiMenit())));
        sb.append(String.format("Kondisi           : %s%n", r.getKondisi().getDeskripsi()));
        sb.append(String.format("Durasi perjalanan : %d + %d = %d menit%n",
                r.getDurasiNormal(), r.getTambahan(), r.getDurasiEstimasi()));
        sb.append(String.format("Waktu cadangan    : %d menit%n%n", r.getCadangan()));
        sb.append(String.format("Waktu berangkat   : %s%n", Waktu.format(r.getBerangkatMenit())));
        sb.append(String.format("Perkiraan tiba    : %s%n", Waktu.format(r.getTibaMenit())));
        if (sisa >= 0) {
            sb.append(String.format("Sisa waktu        : %d menit sebelum kuliah", sisa));
        } else {
            sb.append(String.format("Terlambat         : %d menit", -sisa));
        }
        lblHasil.setText(sb.toString());

        lblRisiko.setText("Risiko terlambat: " + r.getRisiko());
        lblRisiko.getStyleClass().removeAll("risk-rendah", "risk-sedang", "risk-tinggi");
        if (Prediktor.RISIKO_TINGGI.equals(r.getRisiko())) {
            lblRisiko.getStyleClass().add("risk-tinggi");
        } else if (Prediktor.RISIKO_SEDANG.equals(r.getRisiko())) {
            lblRisiko.getStyleClass().add("risk-sedang");
        } else {
            lblRisiko.getStyleClass().add("risk-rendah");
        }
        lblRisiko.setVisible(true);
        lblRisiko.setManaged(true);
    }

    private void simpanKeRiwayat() {
        if (hasilTerakhir == null) {
            peringatan("Belum ada hasil", "Hitung prediksi terlebih dahulu sebelum menyimpan.");
            return;
        }
        data.getDaftarRiwayat().add(hasilTerakhir);
        PenyimpananData.simpan(data);
        muatUlangRiwayat();
        hasilTerakhir = null;
        btnSimpanRiwayat.setDisable(true);
        setStatus("Hasil prediksi tersimpan ke riwayat.");
        info("Berhasil", "Hasil prediksi tersimpan ke riwayat.");
    }

    private void resetFormHitung() {
        cbJadwal.setValue(null);
        tfDurasi.clear();
        cbKondisi.setValue("Lancar");
        tfCadangan.setText("10");
        tfRencana.clear();
        hasilTerakhir = null;
        btnSimpanRiwayat.setDisable(true);
        lblRisiko.setVisible(false);
        lblRisiko.setManaged(false);
        lblHasil.setText("Isi data di sebelah kiri lalu tekan \"Hitung Prediksi\" \uD83C\uDF38");
    }

    // ============================================================
    //  TAB 3 : RIWAYAT & EVALUASI
    // ============================================================
    private VBox buatTabRiwayat() {
        tabelRiwayat = new TableView<>(riwayatList);
        tabelRiwayat.setPlaceholder(new Label("Riwayat masih kosong \uD83C\uDF3F"));
        tabelRiwayat.getColumns().add(kolomNomor(tabelRiwayat));
        tabelRiwayat.getColumns().add(kolom("Mata Kuliah", 170, (RiwayatPerjalanan r) -> r.getMataKuliah()));
        tabelRiwayat.getColumns().add(kolom("Hari", 70, (RiwayatPerjalanan r) -> r.getHari()));
        tabelRiwayat.getColumns().add(kolom("Kuliah", 65, (RiwayatPerjalanan r) -> Waktu.format(r.getJamMulaiMenit())));
        tabelRiwayat.getColumns().add(kolom("Berangkat", 80, (RiwayatPerjalanan r) -> Waktu.format(r.getBerangkatMenit())));
        tabelRiwayat.getColumns().add(kolom("Tiba", 65, (RiwayatPerjalanan r) -> Waktu.format(r.getTibaMenit())));
        tabelRiwayat.getColumns().add(kolom("Kondisi", 75, (RiwayatPerjalanan r) -> r.getKondisi().getNama()));
        tabelRiwayat.getColumns().add(kolom("Risiko", 75, (RiwayatPerjalanan r) -> r.getRisiko()));
        tabelRiwayat.getColumns().add(kolom("Estimasi", 80, (RiwayatPerjalanan r) -> r.getDurasiEstimasi() + " mnt"));
        tabelRiwayat.getColumns().add(kolom("Aktual", 75,
                (RiwayatPerjalanan r) -> r.sudahDievaluasi() ? r.getDurasiAktual() + " mnt" : "-"));
        tabelRiwayat.getColumns().add(kolom("Evaluasi", 220, (RiwayatPerjalanan r) -> r.getEvaluasi()));
        VBox.setVgrow(tabelRiwayat, Priority.ALWAYS);

        tfAktual = new TextField();
        tfAktual.setPromptText("durasi sebenarnya (menit)");
        tfAktual.setPrefWidth(220);

        Button btnCatat = tombol("Catat Durasi Aktual", "btn-hijau");
        Button btnHapus = tombol("Hapus Riwayat", "btn-pink");

        btnCatat.setOnAction(e -> catatAktual());
        btnHapus.setOnAction(e -> hapusRiwayat());

        HBox kontrol = new HBox(10, fieldLabel("Durasi aktual:"), tfAktual, btnCatat, btnHapus);
        kontrol.setAlignment(Pos.CENTER_LEFT);

        Label judul = new Label("Riwayat Perjalanan");
        judul.getStyleClass().add("card-title");
        Label hint = new Label("Pilih satu baris, isi durasi perjalanan sebenarnya, lalu catat untuk melihat evaluasi estimasi.");
        hint.getStyleClass().add("hint-label");

        VBox kartu = new VBox(10, judul, hint, tabelRiwayat, kontrol);
        kartu.getStyleClass().add("card");
        VBox.setVgrow(kartu, Priority.ALWAYS);

        VBox isi = new VBox(kartu);
        isi.setPadding(new Insets(16));
        return isi;
    }

    private void catatAktual() {
        RiwayatPerjalanan r = tabelRiwayat.getSelectionModel().getSelectedItem();
        if (r == null) {
            peringatan("Belum memilih riwayat", "Pilih dulu baris riwayat pada tabel.");
            return;
        }
        if (tfAktual.getText().trim().isEmpty()) {
            peringatan("Kolom kosong", "Isi durasi perjalanan sebenarnya (menit)!");
            return;
        }
        int aktual;
        try {
            aktual = Integer.parseInt(tfAktual.getText().trim());
        } catch (NumberFormatException ex) {
            peringatan("Format angka salah", "Durasi aktual harus berupa angka bulat!");
            return;
        }
        if (aktual < 1 || aktual > 600) {
            peringatan("Durasi tidak valid", "Durasi aktual harus antara 1 - 600 menit.");
            return;
        }
        r.setDurasiAktual(aktual);
        PenyimpananData.simpan(data);
        tabelRiwayat.refresh();
        tfAktual.clear();
        setStatus("Evaluasi: " + r.getEvaluasi());
        info("Evaluasi", r.getEvaluasi());
    }

    private void hapusRiwayat() {
        RiwayatPerjalanan r = tabelRiwayat.getSelectionModel().getSelectedItem();
        if (r == null) {
            peringatan("Belum memilih riwayat", "Pilih dulu baris riwayat yang ingin dihapus.");
            return;
        }
        if (!konfirmasi("Hapus riwayat", "Yakin ingin menghapus riwayat \"" + r.getMataKuliah() + "\"?")) return;
        data.getDaftarRiwayat().remove(r);
        PenyimpananData.simpan(data);
        muatUlangRiwayat();
        setStatus("Riwayat dihapus.");
    }

    private void muatUlangRiwayat() {
        riwayatList.setAll(data.getDaftarRiwayat());
    }

    // ============================================================
    //  HELPER UI
    // ============================================================
    private Label fieldLabel(String teks) {
        Label l = new Label(teks);
        l.getStyleClass().add("field-label");
        return l;
    }

    private Button tombol(String teks, String gaya) {
        Button b = new Button(teks);
        b.getStyleClass().add(gaya);
        b.setMinWidth(Region.USE_PREF_SIZE); // supaya teks tombol tidak terpotong
        return b;
    }

    private <T> TableColumn<T, String> kolom(String judul, double lebar, Function<T, String> isi) {
        TableColumn<T, String> col = new TableColumn<>(judul);
        col.setPrefWidth(lebar);
        col.setCellValueFactory(c -> new SimpleStringProperty(isi.apply(c.getValue())));
        return col;
    }

    private <T> TableColumn<T, String> kolomNomor(TableView<T> tabel) {
        TableColumn<T, String> col = new TableColumn<>("No");
        col.setPrefWidth(45);
        col.setSortable(false);
        col.setCellValueFactory(c -> new SimpleStringProperty(
                String.valueOf(tabel.getItems().indexOf(c.getValue()) + 1)));
        return col;
    }

    private void setStatus(String teks) {
        statusLabel.setText(teks);
    }

    // ---- Alert ----
    private Alert buatAlert(Alert.AlertType tipe, String judul, String pesan) {
        Alert a = new Alert(tipe);
        a.setTitle("Timy");
        a.setHeaderText(judul);
        a.setContentText(pesan);
        if (css != null) a.getDialogPane().getStylesheets().add(css);
        return a;
    }

    private void peringatan(String judul, String pesan) {
        buatAlert(Alert.AlertType.WARNING, "\u26A0 " + judul, pesan).showAndWait();
    }

    private void info(String judul, String pesan) {
        buatAlert(Alert.AlertType.INFORMATION, "\u2705 " + judul, pesan).showAndWait();
    }

    private boolean konfirmasi(String judul, String pesan) {
        Alert a = buatAlert(Alert.AlertType.CONFIRMATION, judul, pesan);
        Optional<ButtonType> hasil = a.showAndWait();
        return hasil.isPresent() && hasil.get() == ButtonType.OK;
    }
}

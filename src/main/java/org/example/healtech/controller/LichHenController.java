package org.example.healtech.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import org.example.healtech.dao.BenhNhanDAO; // <<< BẠN SẼ CẦN TẠO DAO NÀY
import org.example.healtech.dao.LichHenDAO;
import org.example.healtech.dao.NhanVienDAO; // <<< BẠN SẼ CẦN TẠO DAO NÀY
import org.example.healtech.model.BenhNhan; // <<< BẠN SẼ CẦN TẠO MODEL NÀY
import org.example.healtech.model.LichHen;
import org.example.healtech.model.LichHenDisplay;
import org.example.healtech.model.NhanVien; // <<< BẠN SẼ CẦN TẠO MODEL NÀY

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public class LichHenController {

    // === 1. THAY ĐỔI TableView ===
    // TableView giờ đây dùng LichHenDisplay để hiển thị TÊN
    @FXML
    private TableView<LichHenDisplay> tableLichHen; // <<< THAY ĐỔI: LichHen -> LichHenDisplay
    @FXML
    private TableColumn<LichHenDisplay, Integer> colMaLichHen;
    @FXML
    private TableColumn<LichHenDisplay, String> colTenBenhNhan; // <<< THAY ĐỔI: Tên
    @FXML
    private TableColumn<LichHenDisplay, String> colTenBacSi; // <<< THAY ĐỔI: Tên
    @FXML
    private TableColumn<LichHenDisplay, LocalDateTime> colThoiGianHen;
    @FXML
    private TableColumn<LichHenDisplay, String> colLyDoKham;
    @FXML
    private TableColumn<LichHenDisplay, String> colTrangThai;

    // === 2. THAY ĐỔI Form Nhập Liệu ===
    // Dùng ComboBox để CHỌN TÊN, không dùng TextField để GÕ ID
    @FXML
    private ComboBox<BenhNhan> cbBenhNhan; // <<< THAY ĐỔI: TextField -> ComboBox
    @FXML
    private ComboBox<NhanVien> cbBacSi; // <<< THAY ĐỔI: TextField -> ComboBox
    @FXML
    private DatePicker dateNgayHen;
    @FXML
    private TextField txtGioHen; // định dạng: HH:mm
    @FXML
    private TextField txtLyDoKham;
    @FXML
    private ComboBox<String> cbTrangThai;

    @FXML
    private Button btnThem, btnCapNhat, btnXoa, btnTaiLai;

    // === 3. THAY ĐỔI Kiến trúc DAO ===
    // Bỏ 'static', tạo đối tượng DAO
    private LichHenDAO lichHenDAO;
    private BenhNhanDAO benhNhanDAO;
    private NhanVienDAO nhanVienDAO;

    @FXML
    public void initialize() {
        // Khởi tạo các DAO
        lichHenDAO = new LichHenDAO();
        benhNhanDAO = new BenhNhanDAO();
        nhanVienDAO = new NhanVienDAO();

        // Thiết lập cột cho bảng
        setupTableColumns();

        // Thiết lập ComboBox trạng thái
        cbTrangThai.getItems().addAll("Đã đặt", "Đã đến", "Đã hủy");

        // Tải dữ liệu cho các ComboBox (Bệnh nhân, Bác sĩ)
        loadComboBoxData();

        // Tải dữ liệu chính từ DB
        loadLichHenTable();

        // Thêm Listener: Click vào bảng thì hiển thị thông tin lên Form
        tableLichHen.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showLichHenDetails(newValue)
        );
    }

    // Dùng PropertyValueFactory cho ngắn gọn
    private void setupTableColumns() {
        colMaLichHen.setCellValueFactory(new PropertyValueFactory<>("maLichHen"));
        colTenBenhNhan.setCellValueFactory(new PropertyValueFactory<>("tenBenhNhan"));
        colTenBacSi.setCellValueFactory(new PropertyValueFactory<>("tenBacSi"));
        colThoiGianHen.setCellValueFactory(new PropertyValueFactory<>("thoiGianHen"));
        colLyDoKham.setCellValueFactory(new PropertyValueFactory<>("lyDoKham"));
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
    }

    // Tải dữ liệu cho 2 ComboBox chính
    private void loadComboBoxData() {
        // Tải Bệnh nhân
        cbBenhNhan.setItems(FXCollections.observableArrayList(benhNhanDAO.getAllBenhNhan()));
        // Hiển thị tên bệnh nhân trong ComboBox
        cbBenhNhan.setConverter(new StringConverter<BenhNhan>() {
            @Override public String toString(BenhNhan benhNhan) {
                return (benhNhan == null) ? "" : benhNhan.getHoTen();
            }
            @Override public BenhNhan fromString(String string) { return null; }
        });

        // Tải Bác sĩ (Giả sử NhanVienDAO có hàm chỉ lấy bác sĩ)
        cbBacSi.setItems(FXCollections.observableArrayList(nhanVienDAO.getAllBacSi()));
        // Hiển thị tên bác sĩ trong ComboBox
        cbBacSi.setConverter(new StringConverter<NhanVien>() {
            @Override public String toString(NhanVien nhanVien) {
                return (nhanVien == null) ? "" : nhanVien.getHoTen() + " (" + nhanVien.getChuyenKhoa() + ")";
            }
            @Override public NhanVien fromString(String string) { return null; }
        });
    }

    // Tải dữ liệu (đã JOIN) vào TableView
    private void loadLichHenTable() {
        // <<< THAY ĐỔI: Gọi hàm getLichHenForView() mới
        tableLichHen.setItems(FXCollections.observableArrayList(lichHenDAO.getLichHenForView()));
    }

    @FXML
    private void handleThem(ActionEvent event) {
        // Lấy dữ liệu từ Form (getLichHenFromForm trả về Optional<LichHen>)
        Optional<LichHen> lichHenOpt = getLichHenFromForm();

        if (lichHenOpt.isPresent()) {
            LichHen lichHen = lichHenOpt.get();
            // <<< THAY ĐỔI: Gọi hàm addLichHen (không static)
            boolean success = lichHenDAO.addLichHen(lichHen);

            if (success) {
                showAlert("✅ Thêm lịch hẹn thành công!", Alert.AlertType.INFORMATION);
                loadLichHenTable();
                clearForm();
            } else {
                showAlert("❌ Không thể thêm lịch hẹn!", Alert.AlertType.ERROR);
            }
        }
        // Lỗi (như validate) đã được hiển thị bên trong hàm getLichHenFromForm
    }

    @FXML
    private void handleCapNhat(ActionEvent event) {
        LichHenDisplay selected = tableLichHen.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("⚠️ Vui lòng chọn lịch hẹn cần cập nhật!", Alert.AlertType.WARNING);
            return;
        }

        // Lấy dữ liệu mới từ Form
        Optional<LichHen> lichHenOpt = getLichHenFromForm();
        if (lichHenOpt.isPresent()) {
            LichHen lichHenMoi = lichHenOpt.get();
            // Gán MaLichHen (từ bản ghi đã chọn) vào đối tượng mới
            lichHenMoi.setMaLichHen(selected.getMaLichHen());

            // <<< THAY ĐỔI: Gọi hàm updateLichHen đầy đủ
            boolean success = lichHenDAO.updateLichHen(lichHenMoi);

            if (success) {
                showAlert("✅ Cập nhật lịch hẹn thành công!", Alert.AlertType.INFORMATION);
                loadLichHenTable();
            } else {
                showAlert("❌ Không thể cập nhật lịch hẹn!", Alert.AlertType.ERROR);
            }
        }
    }

    // Hàm private để lấy và validate dữ liệu từ form
    private Optional<LichHen> getLichHenFromForm() {
        try {
            BenhNhan benhNhan = cbBenhNhan.getValue();
            NhanVien bacSi = cbBacSi.getValue();
            LocalDate ngayHen = dateNgayHen.getValue();
            LocalTime gioHen = LocalTime.parse(txtGioHen.getText()); // VD: 14:30
            String lyDo = txtLyDoKham.getText();
            String trangThai = cbTrangThai.getValue();

            if (benhNhan == null || bacSi == null || ngayHen == null || lyDo.isEmpty() || trangThai == null) {
                showAlert("⚠️ Vui lòng nhập đầy đủ thông tin!", Alert.AlertType.WARNING);
                return Optional.empty();
            }

            LocalDateTime thoiGianHen = LocalDateTime.of(ngayHen, gioHen);

            // Dùng constructor rỗng và setters (an toàn hơn)
            LichHen lichHen = new LichHen();
            lichHen.setMaBenhNhan(benhNhan.getMaBenhNhan()); // <<< THAY ĐỔI
            lichHen.setMaBacSi(bacSi.getMaNhanVien());     // <<< THAY ĐỔI
            lichHen.setThoiGianHen(thoiGianHen);
            lichHen.setLyDoKham(lyDo);
            lichHen.setTrangThai(trangThai);

            return Optional.of(lichHen);

        } catch (DateTimeParseException e) {
            showAlert("⚠️ Lỗi định dạng giờ hẹn! Phải là HH:mm (ví dụ: 14:30)", Alert.AlertType.ERROR);
            return Optional.empty();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("⚠️ Lỗi khi lấy dữ liệu: " + e.getMessage(), Alert.AlertType.ERROR);
            return Optional.empty();
        }
    }

    @FXML
    private void handleXoa(ActionEvent event) {
        LichHenDisplay selected = tableLichHen.getSelectionModel().getSelectedItem(); // <<< THAY ĐỔI
        if (selected == null) {
            showAlert("⚠️ Vui lòng chọn lịch hẹn cần xóa!", Alert.AlertType.WARNING);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có chắc muốn xóa lịch hẹn này?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait();

        if (confirm.getResult() == ButtonType.YES) {
            // <<< THAY ĐỔI: Gọi hàm deleteLichHen (không static)
            boolean success = lichHenDAO.deleteLichHen(selected.getMaLichHen());
            if (success) {
                showAlert("✅ Xóa lịch hẹn thành công!", Alert.AlertType.INFORMATION);
                loadLichHenTable();
            } else {
                showAlert("❌ Không thể xóa lịch hẹn!", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleTaiLai(ActionEvent event) {
        loadLichHenTable();
    }

    // Hiển thị chi tiết khi click vào TableView
    private void showLichHenDetails(LichHenDisplay lichHen) {
        if (lichHen != null) {
            // Tìm BenhNhan trong ComboBox dựa trên Tên (hoặc ID nếu có)
            Optional<BenhNhan> bn = cbBenhNhan.getItems().stream()
                    .filter(b -> b.getHoTen().equals(lichHen.getTenBenhNhan()))
                    .findFirst();
            bn.ifPresent(cbBenhNhan::setValue);

            // Tìm Bác sĩ
            Optional<NhanVien> bs = cbBacSi.getItems().stream()
                    .filter(b -> b.getHoTen().equals(lichHen.getTenBacSi()))
                    .findFirst();
            bs.ifPresent(cbBacSi::setValue);

            // Set các trường còn lại
            dateNgayHen.setValue(lichHen.getThoiGianHen().toLocalDate());
            txtGioHen.setText(lichHen.getThoiGianHen().toLocalTime().toString());
            txtLyDoKham.setText(lichHen.getLyDoKham());
            cbTrangThai.setValue(lichHen.getTrangThai());
        }
    }

    private void clearForm() {
        cbBenhNhan.setValue(null);
        cbBacSi.setValue(null);
        txtGioHen.clear();
        txtLyDoKham.clear();
        dateNgayHen.setValue(null);
        cbTrangThai.setValue("Đã đặt");
        tableLichHen.getSelectionModel().clearSelection();
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
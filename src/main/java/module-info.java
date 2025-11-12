module org.example.healtech {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    // (Bạn có thể cần 'requires mysql.connector.java;' nếu bạn dùng
    // file jar driver mysql như một module)

    // Dòng này cho phép FXML tải controller
    opens org.example.healtech.controller to javafx.fxml;

    // === THÊM DÒNG NÀY ĐỂ SỬA LỖI ===
    // Dòng này cho phép TableView (javafx.base) truy cập các hàm
    // get() trong các class Model của bạn (như LichHenDisplay)
    opens org.example.healtech.model to javafx.base;

    // Exports
    exports org.example.healtech;
    exports org.example.healtech.controller;
    // (Bạn không cần export model hoặc dao)
}
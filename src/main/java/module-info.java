module org.example.healtech {
    requires javafx.controls;
    requires javafx.fxml;

    // --- THÊM 2 DÒNG QUAN TRỌNG NÀY ---
    requires java.sql;              // Để dùng Connection, ResultSet, DriverManager
    requires mysql.connector.j;     // Để nhận diện Driver MySQL
    // ----------------------------------

    opens org.example.healtech to javafx.fxml;
    exports org.example.healtech;

    // Mở quyền truy cập cho các gói con (Controller, DAO, Model)
    exports org.example.healtech.controller;
    opens org.example.healtech.controller to javafx.fxml;

    exports org.example.healtech.dao;
    opens org.example.healtech.dao to javafx.fxml; // Có thể cần nếu dùng reflection

    exports org.example.healtech.model;
}
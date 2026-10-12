package com.dvc.library.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Tien ich ket noi CSDL bang JDBC thuan.
 * Cau hinh doc tu file db.properties trong src/main/resources.
 */
public class DBConnection {

    private static String URL;
    private static String USERNAME;
    private static String PASSWORD;
    private static String DRIVER;

    static {
        try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                // Gia tri mac dinh neu khong tim thay file cau hinh
                URL = "jdbc:mysql://localhost:3306/library_dvc?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Ho_Chi_Minh";
                USERNAME = "root";
                PASSWORD = "";
                DRIVER = "com.mysql.cj.jdbc.Driver";
            } else {
                prop.load(input);
                URL = prop.getProperty("db.url");
                USERNAME = prop.getProperty("db.username");
                PASSWORD = prop.getProperty("db.password");
                DRIVER = prop.getProperty("db.driver");
            }
            Class.forName(DRIVER);
        } catch (Exception e) {
            throw new RuntimeException("Khong the khoi tao cau hinh ket noi CSDL: " + e.getMessage(), e);
        }
    }

    private DBConnection() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    /** Dong cac tai nguyen JDBC an toan (khong nem exception). */
    public static void closeQuietly(AutoCloseable... resources) {
        for (AutoCloseable r : resources) {
            if (r != null) {
                try {
                    r.close();
                } catch (Exception ignored) {
                }
            }
        }
    }
}

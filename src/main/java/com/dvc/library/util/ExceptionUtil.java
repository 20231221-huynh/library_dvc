package com.dvc.library.util;

import java.sql.SQLException;

/**
 * Chuyen doi cac loai Exception ky thuat (SQLException, IOException, ...)
 * thanh thong bao tieng Viet than thien de hien thi cho nguoi dung,
 * tranh de lo thong tin ky thuat nhay cam (ten bang, cau truc CSDL...) ra giao dien.
 */
public class ExceptionUtil {

    private ExceptionUtil() {}

    public static String friendlyMessage(Throwable e) {
        if (e == null) return "Đã có lỗi không xác định xảy ra.";

        // Loi do nguoi dung nhap sai du lieu (IllegalArgumentException tu ValidationUtil)
        // -> thong bao da ro rang bang tieng Viet, hien thi truc tiep.
        if (e instanceof IllegalArgumentException) {
            return e.getMessage();
        }

        // Tim nguyen nhan goc neu bi bao boc trong RuntimeException
        Throwable rootCause = e;
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
            rootCause = rootCause.getCause();
        }

        if (rootCause instanceof SQLException) {
            String sqlState = ((SQLException) rootCause).getSQLState();
            String msg = rootCause.getMessage() != null ? rootCause.getMessage().toLowerCase() : "";
            if (msg.contains("communications link failure") || msg.contains("connection refused")
                    || msg.contains("could not create connection")) {
                return "Không thể kết nối cơ sở dữ liệu. Vui lòng kiểm tra MySQL đã khởi động và cấu hình trong db.properties.";
            }
            if (msg.contains("access denied")) {
                return "Sai thông tin đăng nhập cơ sở dữ liệu (username/password trong db.properties).";
            }
            if (msg.contains("unknown database")) {
                return "Không tìm thấy cơ sở dữ liệu. Vui lòng chạy lại script database/library_dvc.sql.";
            }
            if ("23000".equals(sqlState) || msg.contains("foreign key constraint") || msg.contains("duplicate entry")) {
                // Da co thong bao cu the tu tang DAO (vi du "Khong the xoa: ..."),
                // neu khong thi tra ve thong bao rang buoc du lieu chung.
                return (e.getMessage() != null && !e.getMessage().trim().isEmpty())
                        ? e.getMessage()
                        : "Dữ liệu đang được sử dụng ở nơi khác nên không thể thực hiện thao tác này.";
            }
            return "Lỗi cơ sở dữ liệu. Vui lòng thử lại hoặc liên hệ quản trị viên.";
        }

        // Cac RuntimeException tu tang DAO da co san thong bao tieng Viet ro rang (vi du:
        // "Không thể xóa: ...", "Sách mã số ... không còn đủ số lượng...") -> tra ve nguyen van.
        if (e.getMessage() != null && !e.getMessage().trim().isEmpty()) {
            return e.getMessage();
        }

        return "Đã có lỗi xảy ra. Vui lòng thử lại.";
    }
}

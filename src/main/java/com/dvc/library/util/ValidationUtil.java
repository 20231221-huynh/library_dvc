package com.dvc.library.util;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * Tien ich kiem tra & chuyen doi du lieu dau vao mot cach an toan,
 * tra ve thong bao loi tieng Viet de hieu thay vi de exception ky thuat
 * (NumberFormatException, ...) lot ra ngoai giao dien nguoi dung.
 */
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^0\\d{9,10}$");

    private ValidationUtil() {}

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    /** Bao loi neu gia tri rong (dung cho cac truong bat buoc). */
    public static void requireNotBlank(String value, String tenTruong) {
        if (isBlank(value)) {
            throw new IllegalArgumentException("Vui lòng nhập " + tenTruong + ".");
        }
    }

    /**
     * Kiem tra do dai toi da cua chuoi nhap vao (thuong khop voi do dai cot VARCHAR trong CSDL),
     * phong truong hop nguoi dung bo qua thuoc tinh maxlength phia trinh duyet (VD: goi thang qua
     * Postman/curl). Bo qua kiem tra neu gia tri rong (de danh cho requireNotBlank/parse... khac
     * xu ly truong hop bat buoc).
     */
    public static void validateMaxLength(String value, String tenTruong, int doDaiToiDa) {
        if (value != null && value.trim().length() > doDaiToiDa) {
            throw new IllegalArgumentException(
                    tenTruong + " không được vượt quá " + doDaiToiDa + " ký tự.");
        }
    }

    /** Parse so nguyen an toan, bao loi tieng Viet ro rang neu sai dinh dang hoac rong. */
    public static int parseIntSafe(String value, String tenTruong) {
        if (isBlank(value)) {
            throw new IllegalArgumentException("Vui lòng chọn/nhập " + tenTruong + ".");
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(tenTruong + " phải là số nguyên hợp lệ.");
        }
    }

    /** Parse so nguyen khong bat buoc: rong -> tra ve mac dinh, sai dinh dang -> bao loi. */
    public static int parseIntOptional(String value, String tenTruong, int macDinh) {
        if (isBlank(value)) return macDinh;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(tenTruong + " phải là số nguyên hợp lệ.");
        }
    }

    /**
     * Parse nam (VD: nam xuat ban sach) khong bat buoc: rong -> tra ve macDinh (thuong la 0, nghia la
     * "chua ro nam xuat ban"); neu co nhap thi phai la mot nam hop ly (tu 1450 - nam Gutenberg phat minh
     * may in - cho toi nam hien tai + 1, cho phep sach sap phat hanh). Chan duoc cac gia tri vo ly nhu
     * so am hoac nam qua xa trong tuong lai (VD go nham "99999").
     */
    public static int parseYearOptional(String value, String tenTruong, int macDinh) {
        int nam = parseIntOptional(value, tenTruong, macDinh);
        if (nam != 0) {
            int namHienTai = java.time.Year.now().getValue();
            if (nam < 1450 || nam > namHienTai + 1) {
                throw new IllegalArgumentException(
                        tenTruong + " phải nằm trong khoảng từ 1450 đến " + (namHienTai + 1) + ".");
            }
        }
        return nam;
    }

    public static int parseIntNonNegative(String value, String tenTruong) {
        int v = parseIntSafe(value, tenTruong);
        if (v < 0) {
            throw new IllegalArgumentException(tenTruong + " không được nhỏ hơn 0.");
        }
        return v;
    }

    public static BigDecimal parseDecimalSafe(String value, String tenTruong) {
        if (isBlank(value)) return BigDecimal.ZERO;
        try {
            BigDecimal v = new BigDecimal(value.trim());
            if (v.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException(tenTruong + " không được nhỏ hơn 0.");
            }
            return v;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(tenTruong + " phải là số hợp lệ.");
        }
    }

    public static void validateEmailIfPresent(String email) {
        if (isBlank(email)) return;
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new IllegalArgumentException("Địa chỉ email không hợp lệ.");
        }
    }

    public static void validatePhoneIfPresent(String sdt) {
        if (isBlank(sdt)) return;
        if (!PHONE_PATTERN.matcher(sdt.trim()).matches()) {
            throw new IllegalArgumentException("Số điện thoại không hợp lệ (phải bắt đầu bằng 0, đủ 10-11 chữ số).");
        }
    }

    /** Kiem tra do dai mat khau toi thieu. */
    public static void validatePasswordLength(String matKhau, int doDaiToiThieu) {
        if (matKhau == null || matKhau.length() < doDaiToiThieu) {
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất " + doDaiToiThieu + " ký tự.");
        }
    }

    /**
     * Parse ngay thang (dinh dang yyyy-MM-dd, dung voi gia tri tra ve tu the <input type="date">)
     * mot cach khong bat buoc: neu rong thi tra ve gia tri mac dinh duoc truyen vao; neu co nhap
     * nhung sai dinh dang thi bao loi tieng Viet ro rang thay vi de DateTimeParseException lot ra ngoai.
     */
    public static java.time.LocalDate parseLocalDateOptional(String value, String tenTruong, java.time.LocalDate macDinh) {
        if (isBlank(value)) return macDinh;
        try {
            return java.time.LocalDate.parse(value.trim());
        } catch (java.time.format.DateTimeParseException e) {
            throw new IllegalArgumentException(tenTruong + " không đúng định dạng ngày hợp lệ.");
        }
    }
}

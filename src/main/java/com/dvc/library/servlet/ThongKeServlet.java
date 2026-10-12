package com.dvc.library.servlet;

import com.dvc.library.dao.PhieuMuonDAO;
import com.dvc.library.util.Constants;
import com.dvc.library.util.DBConnection;

import jakarta.inject.Inject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/thongke")
public class ThongKeServlet extends HttpServlet {

    @Inject
    private PhieuMuonDAO phieuMuonDAO;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        phieuMuonDAO.capNhatTrangThaiQuaHan();

        // ===== Xu ly tuy chon khoang thoi gian (dung de "thay doi cac chi so" tren trang thong ke) =====
        String khoang = req.getParameter("khoang");
        if (khoang == null || khoang.trim().isEmpty()) khoang = "tatca";

        LocalDate homNay = LocalDate.now();
        LocalDate tuNgay = null;
        LocalDate denNgay = null;

        switch (khoang) {
            case "7ngay":
                tuNgay = homNay.minusDays(6);
                denNgay = homNay;
                break;
            case "30ngay":
                tuNgay = homNay.minusDays(29);
                denNgay = homNay;
                break;
            case "thang":
                tuNgay = homNay.withDayOfMonth(1);
                denNgay = homNay;
                break;
            case "nam":
                tuNgay = homNay.withDayOfYear(1);
                denNgay = homNay;
                break;
            case "tuychon":
                tuNgay = parseDateSafe(req.getParameter("tuNgay"));
                denNgay = parseDateSafe(req.getParameter("denNgay"));
                break;
            case "tatca":
            default:
                khoang = "tatca";
                tuNgay = null;
                denNgay = null;
        }
        // Neu nguoi dung nhap nguoc khoang ngay thi tu dong doi lai cho dung thu tu, tranh loi truy van.
        if (tuNgay != null && denNgay != null && tuNgay.isAfter(denNgay)) {
            LocalDate tmp = tuNgay;
            tuNgay = denNgay;
            denNgay = tmp;
        }

        req.setAttribute("khoang", khoang);
        req.setAttribute("tuNgay", tuNgay);
        req.setAttribute("denNgay", denNgay);

        // ===== Cac chi so theo khoang thoi gian da chon =====
        List<Object[]> docGiaMuonNhieuNhat = getDocGiaMuonNhieuNhat(tuNgay, denNgay);
        req.setAttribute("docGiaLabelsJson", jsonLabels(docGiaMuonNhieuNhat));
        req.setAttribute("docGiaValuesJson", jsonValues(docGiaMuonNhieuNhat));

        List<Object[]> sachMuonNhieuNhat = getSachMuonNhieuNhat(tuNgay, denNgay);
        req.setAttribute("sachMuonLabelsJson", jsonLabels(sachMuonNhieuNhat));
        req.setAttribute("sachMuonValuesJson", jsonValues(sachMuonNhieuNhat));
        req.setAttribute("soPhieuDangMuon", phieuMuonDAO.countByTrangThaiTrongKhoang(Constants.TT_DANG_MUON, tuNgay, denNgay));
        req.setAttribute("soPhieuQuaHan", phieuMuonDAO.countByTrangThaiTrongKhoang(Constants.TT_QUA_HAN, tuNgay, denNgay));
        req.setAttribute("soPhieuDaTra", phieuMuonDAO.countByTrangThaiTrongKhoang(Constants.TT_DA_TRA, tuNgay, denNgay));
        req.setAttribute("soPhieuDaHuy", phieuMuonDAO.countByTrangThaiTrongKhoang(Constants.TT_DA_HUY, tuNgay, denNgay));

        // ===== So lieu ve danh muc sach: khong phu thuoc khoang thoi gian (phan anh kho sach hien co) =====
        List<Object[]> theLoaiThongKe = getSoLuongSachTheoTheLoai();
        List<Object[]> sachTrangThaiThongKe = getSachTheoTrangThai();

        // ===== Du lieu JSON san sang cho Chart.js (tranh loi escape ky tu dac biet/tieng Viet khi
        // dung truc tiep JSTL de sinh mang JavaScript trong JSP) =====
        req.setAttribute("theLoaiLabelsJson", jsonLabels(theLoaiThongKe));
        req.setAttribute("theLoaiValuesJson", jsonValues(theLoaiThongKe));
        req.setAttribute("sachLabelsJson", jsonLabels(sachTrangThaiThongKe));
        req.setAttribute("sachValuesJson", jsonValues(sachTrangThaiThongKe));

        req.getRequestDispatcher("/WEB-INF/views/thongke/thongke.jsp").forward(req, resp);
    }

    /** Sinh mang JSON cac nhan (chuoi) tu cot dau tien cua danh sach Object[]{nhan, giaTri}. */
    private String jsonLabels(List<Object[]> rows) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < rows.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(jsonEscape(String.valueOf(rows.get(i)[0])));
        }
        return sb.append("]").toString();
    }

    /** Sinh mang JSON cac gia tri so tu cot thu hai cua danh sach Object[]{nhan, giaTri}. */
    private String jsonValues(List<Object[]> rows) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < rows.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(rows.get(i)[1]);
        }
        return sb.append("]").toString();
    }

    private String jsonEscape(String s) {
        StringBuilder sb = new StringBuilder("\"");
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                default: sb.append(c);
            }
        }
        return sb.append("\"").toString();
    }

    private LocalDate parseDateSafe(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        try {
            return LocalDate.parse(s.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private List<Object[]> getDocGiaMuonNhieuNhat(LocalDate tuNgay, LocalDate denNgay) {
        List<Object[]> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT dg.ho_ten, COUNT(pm.ma_pm) AS so_phieu " +
            "FROM phieumuon pm " +
            "JOIN docgia dg ON pm.ma_dg = dg.ma_dg WHERE 1=1 ");
        List<Object> params = new ArrayList<>();
        if (tuNgay != null) { sql.append("AND pm.ngay_muon >= ? "); params.add(Date.valueOf(tuNgay)); }
        if (denNgay != null) { sql.append("AND pm.ngay_muon <= ? "); params.add(Date.valueOf(denNgay)); }
        sql.append("GROUP BY dg.ma_dg, dg.ho_ten ORDER BY so_phieu DESC LIMIT 5");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{rs.getString("ho_ten"), rs.getInt("so_phieu")});
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi thong ke doc gia muon nhieu nhat: " + e.getMessage(), e);
        }
        return list;
    }

    /** Top 5 sach duoc muon nhieu nhat (theo so luong luot muon trong chi tiet phieu muon), theo khoang thoi gian da chon. */
    private List<Object[]> getSachMuonNhieuNhat(LocalDate tuNgay, LocalDate denNgay) {
        List<Object[]> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT s.ten_sach, COUNT(ct.ma_ct) AS so_luot " +
            "FROM chitietphieumuon ct " +
            "JOIN sach s ON ct.ma_sach = s.ma_sach " +
            "JOIN phieumuon pm ON ct.ma_pm = pm.ma_pm WHERE 1=1 ");
        List<Object> params = new ArrayList<>();
        if (tuNgay != null) { sql.append("AND pm.ngay_muon >= ? "); params.add(Date.valueOf(tuNgay)); }
        if (denNgay != null) { sql.append("AND pm.ngay_muon <= ? "); params.add(Date.valueOf(denNgay)); }
        sql.append("GROUP BY s.ma_sach, s.ten_sach ORDER BY so_luot DESC LIMIT 5");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{rs.getString("ten_sach"), rs.getInt("so_luot")});
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi thong ke sach muon nhieu nhat: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * So luong dau sach theo tung the loai (dung cho ca bang so lieu, bieu do cot va bieu do tron %).
     * Object[]: {tenTheLoai, soLuongSach}
     */
    private List<Object[]> getSoLuongSachTheoTheLoai() {
        List<Object[]> list = new ArrayList<>();
        String sql =
            "SELECT tl.ten_theloai, COUNT(s.ma_sach) AS so_luong " +
            "FROM theloai tl LEFT JOIN sach s ON s.ma_tl = tl.ma_tl " +
            "GROUP BY tl.ma_tl, tl.ten_theloai " +
            "ORDER BY so_luong DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Object[]{rs.getString("ten_theloai"), rs.getInt("so_luong")});
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi thong ke sach theo the loai: " + e.getMessage(), e);
        }
        return list;
    }

    
    private List<Object[]> getSachTheoTrangThai() {
        List<Object[]> list = new ArrayList<>();
        
        String sql = "SELECT trang_thai, COUNT(*) AS so_luong FROM sach GROUP BY trang_thai ORDER BY trang_thai DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String nhan = rs.getInt("trang_thai") == 1 ? "Đang lưu hành" : "Ngừng lưu hành";
                list.add(new Object[]{nhan, rs.getInt("so_luong")});
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi thong ke sach theo trang thai: " + e.getMessage(), e);
        }
        return list;
    }
}

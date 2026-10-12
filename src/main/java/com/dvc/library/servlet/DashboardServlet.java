package com.dvc.library.servlet;

import com.dvc.library.dao.DocGiaDAO;
import com.dvc.library.dao.PhieuMuonDAO;
import com.dvc.library.dao.SachDAO;
import com.dvc.library.dao.TacGiaDAO;
import com.dvc.library.dao.TaiKhoanDAO;
import com.dvc.library.dao.TheLoaiDAO;
import com.dvc.library.model.PhieuMuon;
import com.dvc.library.model.Sach;
import com.dvc.library.model.TaiKhoan;
import com.dvc.library.util.Constants;
import com.dvc.library.util.DBConnection;

import jakarta.inject.Inject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private static final int NGUONG_SAP_HET = 3; // soLuongCon <= nguong nay thi canh bao "sap het"
    private static final int SO_DONG_HIEN_THI = 5; // so dong hien thi cho moi bang tom tat tren trang chu

    @Inject
    private SachDAO sachDAO;
    @Inject
    private DocGiaDAO docGiaDAO;
    @Inject
    private PhieuMuonDAO phieuMuonDAO;
    @Inject
    private TheLoaiDAO theLoaiDAO;
    @Inject
    private TacGiaDAO tacGiaDAO;
    @Inject
    private TaiKhoanDAO taiKhoanDAO;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Cap nhat tu dong cac phieu qua han moi lan vao dashboard
        phieuMuonDAO.capNhatTrangThaiQuaHan();

        // ===== Xu ly tuy chon khoang thoi gian (giong trang Thong ke) =====
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

        // ===== Hang 1: chi so hoat dong phieu muon THEO KHOANG THOI GIAN DA CHON =====
        req.setAttribute("soPhieuDangMuon", phieuMuonDAO.countByTrangThaiTrongKhoang(Constants.TT_DANG_MUON, tuNgay, denNgay));
        req.setAttribute("soPhieuQuaHan", phieuMuonDAO.countByTrangThaiTrongKhoang(Constants.TT_QUA_HAN, tuNgay, denNgay));
        req.setAttribute("soPhieuDaTra", phieuMuonDAO.countByTrangThaiTrongKhoang(Constants.TT_DA_TRA, tuNgay, denNgay));
        req.setAttribute("soPhieuDaHuy", phieuMuonDAO.countByTrangThaiTrongKhoang(Constants.TT_DA_HUY, tuNgay, denNgay));

        req.setAttribute("tongSoSach", sachDAO.countAll());
        req.setAttribute("tongSoDocGia", docGiaDAO.countAll());
        req.setAttribute("tongSoTheLoai", theLoaiDAO.countAll());
        req.setAttribute("tongSoTacGia", tacGiaDAO.countAll());

        // Chi hien thi cho ADMIN (trang taikhoan chi ADMIN duoc xem); tinh san o day, JSP se an/hien bang c:if
        List<TaiKhoan> dsTaiKhoan = taiKhoanDAO.getAll();
        req.setAttribute("tongSoTaiKhoan", dsTaiKhoan.size());

        // ===== Bieu do: so luong dau sach theo tung the loai (co cau kho sach, khong phu thuoc thoi gian) =====
        List<Object[]> theoTheLoai = getSoLuongSachTheoTheLoai();
        req.setAttribute("theLoaiLabelsJson", jsonLabels(theoTheLoai));
        req.setAttribute("theLoaiValuesJson", jsonValues(theoTheLoai));

        // ===== Canh bao: sach con it ban (<= nguong), dang con luu hanh - trang thai tuc thoi, khong loc theo thoi gian =====
        List<Sach> dsSachSapHet = new ArrayList<>();
        for (Sach s : sachDAO.getAll()) {
            if (s.getTrangThai() == 1 && s.getSoLuongCon() <= NGUONG_SAP_HET) {
                dsSachSapHet.add(s);
            }
        }
        dsSachSapHet.sort(Comparator.comparingInt(Sach::getSoLuongCon));
        if (dsSachSapHet.size() > SO_DONG_HIEN_THI) {
            dsSachSapHet = dsSachSapHet.subList(0, SO_DONG_HIEN_THI);
        }
        req.setAttribute("dsSachSapHet", dsSachSapHet);

        // ===== Phieu qua han can xu ly: LA TRANG THAI HIEN TAI (dang qua han ngay luc xem), nen
        // KHONG loc theo khoang thoi gian da chon (giong logic voi "sach sap het" o tren) - neu loc
        // theo ngay muon se co the AN MAT nhung phieu qua han rat lau (muon tu truoc do lau) khoi danh
        // sach "can xu ly", trong khi day moi la nhung phieu can uu tien xu ly nhat. Sap xep theo han
        // tra SOM NHAT truoc (qua han lau nhat) de nhan ra ngay phieu can xu ly gap nhat.
        List<PhieuMuon> dsQuaHan = new ArrayList<>(phieuMuonDAO.getByTrangThai(Constants.TT_QUA_HAN));
        dsQuaHan.sort(Comparator.comparing(PhieuMuon::getNgayHenTra));
        if (dsQuaHan.size() > SO_DONG_HIEN_THI) dsQuaHan = dsQuaHan.subList(0, SO_DONG_HIEN_THI);
        req.setAttribute("dsPhieuQuaHanGanDay", dsQuaHan);

        List<PhieuMuon> dsMoiLap = locTheoNgayMuon(phieuMuonDAO.getAll(), tuNgay, denNgay);
        if (dsMoiLap.size() > SO_DONG_HIEN_THI) dsMoiLap = dsMoiLap.subList(0, SO_DONG_HIEN_THI);
        req.setAttribute("dsPhieuMoiLap", dsMoiLap);

        req.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(req, resp);
    }

    /** Loc danh sach phieu muon theo ngay_muon nam trong [tuNgay, denNgay] (null = khong gioi han phia do). */
    private List<PhieuMuon> locTheoNgayMuon(List<PhieuMuon> ds, LocalDate tuNgay, LocalDate denNgay) {
        List<PhieuMuon> ketQua = new ArrayList<>();
        for (PhieuMuon pm : ds) {
            LocalDate ngayMuon = pm.getNgayMuon();
            if (tuNgay != null && (ngayMuon == null || ngayMuon.isBefore(tuNgay))) continue;
            if (denNgay != null && (ngayMuon == null || ngayMuon.isAfter(denNgay))) continue;
            ketQua.add(pm);
        }
        return ketQua;
    }

    /**
     * So luong dau sach theo tung the loai. Object[]: {tenTheLoai, soLuongSach}.
     * (Cung logic voi ThongKeServlet, lay rieng o day de trang chu khong phu thuoc trang thong ke.)
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
}

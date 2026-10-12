package com.dvc.library.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.dvc.library.model.ChiTietPhieuMuon;
import com.dvc.library.model.PhieuMuon;
import com.dvc.library.util.Constants;
import com.dvc.library.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@ApplicationScoped
public class PhieuMuonDAO {

    @Inject
    private SachDAO sachDAO;

    private static final String SELECT_JOIN =
        "SELECT pm.*, dg.ho_ten AS ten_docgia, tk.ho_ten AS ten_nhanvien " +
        "FROM phieumuon pm " +
        "JOIN docgia dg ON pm.ma_dg = dg.ma_dg " +
        "JOIN taikhoan tk ON pm.ma_tk = tk.ma_tk ";

    public List<PhieuMuon> getAll() {
        List<PhieuMuon> list = new ArrayList<>();
        String sql = SELECT_JOIN + "ORDER BY pm.ma_pm DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Loi lay danh sach phieu muon: " + e.getMessage(), e);
        }
        return list;
    }

    public List<PhieuMuon> getByTrangThai(String trangThai) {
        List<PhieuMuon> list = new ArrayList<>();
        String sql = SELECT_JOIN + "WHERE pm.trang_thai = ? ORDER BY pm.ma_pm DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trangThai);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi lay phieu muon theo trang thai: " + e.getMessage(), e);
        }
        return list;
    }

    public List<PhieuMuon> search(String keyword, String trangThai) {
        List<PhieuMuon> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(SELECT_JOIN + "WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND dg.ho_ten LIKE ? ");
            params.add("%" + keyword.trim() + "%");
        }
        if (trangThai != null && !trangThai.trim().isEmpty()) {
            sql.append("AND pm.trang_thai = ? ");
            params.add(trangThai.trim());
        }
        sql.append("ORDER BY pm.ma_pm DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi tim kiem phieu muon: " + e.getMessage(), e);
        }
        return list;
    }

    public PhieuMuon getById(int maPm) {
        String sql = SELECT_JOIN + "WHERE pm.ma_pm = ?";
        PhieuMuon pm = null;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maPm);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) pm = mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi lay phieu muon: " + e.getMessage(), e);
        }
        if (pm != null) {
            pm.setChiTietList(getChiTietByPhieuMuon(maPm));
        }
        return pm;
    }

    public List<ChiTietPhieuMuon> getChiTietByPhieuMuon(int maPm) {
        List<ChiTietPhieuMuon> list = new ArrayList<>();

        String sql = "SELECT ct.*, s.ten_sach, pm.ngay_hen_tra, pm.trang_thai AS pm_trang_thai " +
                     "FROM chitietphieumuon ct " +
                     "JOIN sach s ON ct.ma_sach = s.ma_sach " +
                     "JOIN phieumuon pm ON ct.ma_pm = pm.ma_pm " +
                     "WHERE ct.ma_pm = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maPm);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ChiTietPhieuMuon ct = new ChiTietPhieuMuon();
                    ct.setMaCt(rs.getInt("ma_ct"));
                    ct.setMaPm(rs.getInt("ma_pm"));
                    ct.setMaSach(rs.getInt("ma_sach"));
                    ct.setTenSach(rs.getString("ten_sach"));
                    ct.setSoLuong(rs.getInt("so_luong"));
                    Date ngayTra = rs.getDate("ngay_tra_thucte");
                    ct.setNgayTraThucTe(ngayTra != null ? ngayTra.toLocalDate() : null);
                    ct.setTienPhat(rs.getBigDecimal("tien_phat"));
                    ct.setDaTra(rs.getInt("da_tra"));


                    if (ct.getDaTra() == 0 && !Constants.TT_DA_HUY.equals(rs.getString("pm_trang_thai"))) {
                        LocalDate ngayHenTra = rs.getDate("ngay_hen_tra").toLocalDate();
                        long soNgayTre = java.time.temporal.ChronoUnit.DAYS.between(ngayHenTra, LocalDate.now());
                        if (soNgayTre > 0) {
                            ct.setTienPhatUocTinh(BigDecimal.valueOf(soNgayTre * Constants.TIEN_PHAT_MOI_NGAY * ct.getSoLuong()));
                        }
                    }
                    list.add(ct);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi lay chi tiet phieu muon: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Tao phieu muon moi kem chi tiet cac dau sach, dung transaction:
     * 1) Insert phieumuon
     * 2) Insert tung chitietphieumuon
     * 3) Giam so_luong_con tuong ung trong bang sach
     * Neu buoc nao that bai (VD: het sach) -> rollback toan bo.
     */
    public int taoPhieuMuon(PhieuMuon pm) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            String sqlPm = "INSERT INTO phieumuon (ma_dg, ma_tk, ngay_muon, ngay_hen_tra, trang_thai, ghi_chu) VALUES (?,?,?,?,?,?)";
            int maPmMoi;
            try (PreparedStatement ps = conn.prepareStatement(sqlPm, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, pm.getMaDg());
                ps.setInt(2, pm.getMaTk());
                ps.setDate(3, Date.valueOf(pm.getNgayMuon()));
                ps.setDate(4, Date.valueOf(pm.getNgayHenTra()));
                ps.setString(5, Constants.TT_DANG_MUON);
                ps.setString(6, pm.getGhiChu());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) throw new SQLException("Khong lay duoc ma phieu muon moi.");
                    maPmMoi = keys.getInt(1);
                }
            }

            String sqlCt = "INSERT INTO chitietphieumuon (ma_pm, ma_sach, so_luong) VALUES (?,?,?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlCt)) {
                for (ChiTietPhieuMuon ct : pm.getChiTietList()) {
                    // Giam so luong con - neu khong con du sach se tra ve false -> rollback
                    boolean ok = sachDAO.decreaseSoLuongCon(conn, ct.getMaSach(), ct.getSoLuong());
                    if (!ok) {
                        throw new SQLException("Sách mã số " + ct.getMaSach() + " không còn đủ số lượng để cho mượn.");
                    }
                    ps.setInt(1, maPmMoi);
                    ps.setInt(2, ct.getMaSach());
                    ps.setInt(3, ct.getSoLuong());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            conn.commit();
            return maPmMoi;
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    /**
     * Xu ly tra 1 dau sach trong phieu muon: cap nhat da_tra, tinh tien phat neu tra tre,
     * tang lai so_luong_con cua sach. Neu tat ca chi tiet da tra -> cap nhat trang thai phieu = DA_TRA.
     */
    public boolean traSach(int maCt, LocalDate ngayTraThucTe) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            int maPm, maSach, soLuong;
            LocalDate ngayHenTra;
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT ct.ma_pm, ct.ma_sach, ct.so_luong, pm.ngay_hen_tra " +
                    "FROM chitietphieumuon ct JOIN phieumuon pm ON ct.ma_pm = pm.ma_pm WHERE ct.ma_ct = ?")) {
                ps.setInt(1, maCt);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) throw new SQLException("Khong tim thay chi tiet phieu muon.");
                    maPm = rs.getInt("ma_pm");
                    maSach = rs.getInt("ma_sach");
                    soLuong = rs.getInt("so_luong");
                    ngayHenTra = rs.getDate("ngay_hen_tra").toLocalDate();
                }
            }

            BigDecimal tienPhat = BigDecimal.ZERO;
            if (ngayTraThucTe.isAfter(ngayHenTra)) {
                long soNgayTre = java.time.temporal.ChronoUnit.DAYS.between(ngayHenTra, ngayTraThucTe);
                tienPhat = BigDecimal.valueOf(soNgayTre * Constants.TIEN_PHAT_MOI_NGAY * soLuong);
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE chitietphieumuon SET ngay_tra_thucte=?, tien_phat=?, da_tra=1 WHERE ma_ct=?")) {
                ps.setDate(1, Date.valueOf(ngayTraThucTe));
                ps.setBigDecimal(2, tienPhat);
                ps.setInt(3, maCt);
                ps.executeUpdate();
            }

            sachDAO.increaseSoLuongCon(conn, maSach, soLuong);

            // Kiem tra neu tat ca chi tiet cua phieu da tra het thi cap nhat trang thai phieu
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) FROM chitietphieumuon WHERE ma_pm = ? AND da_tra = 0")) {
                ps.setInt(1, maPm);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    if (rs.getInt(1) == 0) {
                        try (PreparedStatement ps2 = conn.prepareStatement(
                                "UPDATE phieumuon SET trang_thai=? WHERE ma_pm=?")) {
                            ps2.setString(1, Constants.TT_DA_TRA);
                            ps2.setInt(2, maPm);
                            ps2.executeUpdate();
                        }
                    }
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    /**
     * Gia han ngay hen tra cho mot phieu muon dang con hieu luc (DANG_MUON hoac QUA_HAN).
     * Dieu kien (kiem tra atomic ngay trong cau UPDATE de tranh race condition khi nhieu
     * nguoi cung thao tac dong thoi, giong cach lam cua SachDAO.decreaseSoLuongCon):
     *   - Phieu phai dang o trang thai DANG_MUON hoac QUA_HAN (khong gia han duoc phieu da
     *     DA_TRA hoac DA_HUY - do la du lieu da "chot", khong con y nghia gia han).
     *   - Ngay hen tra moi phai SAU ngay hen tra hien tai (gia han nghia la keo dai them thoi
     *     gian, khong dung de rut ngan - muon rut ngan thi nen dung nghiep vu khac).
     *   - Chua vuot qua so lan gia han toi da (Constants.SO_LAN_GIA_HAN_TOI_DA) de tranh bi
     *     lam dung keo dai vo thoi han.
     * Neu gia han thanh cong va phieu dang QUA_HAN, tu dong chuyen lai ve DANG_MUON (vi da co
     * ngay hen tra moi lon hon ngay hen tra cu nen khong con "qua han" theo nghia cu nua).
     * KHONG dung tram giao dich nhieu buoc vi day chi la 1 cau UPDATE duy nhat, tu than da atomic.
     */
    public void giaHanNgayHenTra(int maPm, LocalDate ngayHenTraMoi) throws SQLException {
        String sql =
            "UPDATE phieumuon SET ngay_hen_tra = ?, so_lan_gia_han = so_lan_gia_han + 1, " +
            "trang_thai = CASE WHEN trang_thai = 'QUA_HAN' THEN 'DANG_MUON' ELSE trang_thai END " +
            "WHERE ma_pm = ? AND trang_thai IN ('DANG_MUON','QUA_HAN') " +
            "AND so_lan_gia_han < ? AND ngay_hen_tra < ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(ngayHenTraMoi));
            ps.setInt(2, maPm);
            ps.setInt(3, Constants.SO_LAN_GIA_HAN_TOI_DA);
            ps.setDate(4, Date.valueOf(ngayHenTraMoi));
            int rows = ps.executeUpdate();
            if (rows == 0) {
                // Khong co dong nao duoc cap nhat -> tra cuu lai de bao chinh xac ly do that bai
                PhieuMuon hienTai = getById(maPm);
                if (hienTai == null) {
                    throw new SQLException("Không tìm thấy phiếu mượn.");
                }
                if (Constants.TT_DA_TRA.equals(hienTai.getTrangThai())) {
                    throw new SQLException("Phiếu mượn đã trả, không thể gia hạn.");
                }
                if (Constants.TT_DA_HUY.equals(hienTai.getTrangThai())) {
                    throw new SQLException("Phiếu mượn đã hủy, không thể gia hạn.");
                }
                if (hienTai.getSoLanGiaHan() >= Constants.SO_LAN_GIA_HAN_TOI_DA) {
                    throw new SQLException("Phiếu mượn đã gia hạn tối đa " +
                            Constants.SO_LAN_GIA_HAN_TOI_DA + " lần, không thể gia hạn thêm.");
                }
                throw new SQLException("Ngày hẹn trả mới phải sau ngày hẹn trả hiện tại (" +
                        hienTai.getNgayHenTra() + ").");
            }
        }
    }

    /**
     * Huy toan bo mot phieu muon: CHI cho phep khi TOAN BO cac dong sach trong phieu deu chua
     * tra (khong co dong nao da_tra = 1). Hoan tra lai ton kho cho tat ca dau sach trong phieu,
     * roi chuyen trang thai phieu sang DA_HUY - KHONG xoa han ban ghi, de giu lai lich su/audit
     * trail (giong nguyen tac "khong sua/xoa dong da tra" da ap dung cho tung dong chi tiet).
     */
    public void huyPhieuMuon(int maPm) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            String trangThaiHienTai;
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT trang_thai FROM phieumuon WHERE ma_pm = ? FOR UPDATE")) {
                ps.setInt(1, maPm);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) throw new SQLException("Không tìm thấy phiếu mượn.");
                    trangThaiHienTai = rs.getString("trang_thai");
                }
            }

            if (Constants.TT_DA_TRA.equals(trangThaiHienTai)) {
                throw new SQLException("Phiếu mượn đã trả, không thể hủy.");
            }
            if (Constants.TT_DA_HUY.equals(trangThaiHienTai)) {
                throw new SQLException("Phiếu mượn đã được hủy trước đó.");
            }

            // Chi cho huy khi chua co dong sach nao duoc tra
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) FROM chitietphieumuon WHERE ma_pm = ? AND da_tra = 1")) {
                ps.setInt(1, maPm);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    if (rs.getInt(1) > 0) {
                        throw new SQLException("Phiếu mượn đã có sách được trả, không thể hủy toàn bộ. " +
                                "Vui lòng ghi nhận trả nốt các đầu sách còn lại.");
                    }
                }
            }

            // Hoan tra lai ton kho cho tung dau sach trong phieu truoc khi huy
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT ma_sach, so_luong FROM chitietphieumuon WHERE ma_pm = ?")) {
                ps.setInt(1, maPm);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        sachDAO.increaseSoLuongCon(conn, rs.getInt("ma_sach"), rs.getInt("so_luong"));
                    }
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE phieumuon SET trang_thai = ? WHERE ma_pm = ?")) {
                ps.setString(1, Constants.TT_DA_HUY);
                ps.setInt(2, maPm);
                ps.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    /** Cap nhat trang thai QUA_HAN cho cac phieu dang muon nhung da qua ngay hen tra. */
    public int capNhatTrangThaiQuaHan() {
        String sql = "UPDATE phieumuon SET trang_thai = ? WHERE trang_thai = ? AND ngay_hen_tra < CURDATE()";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, Constants.TT_QUA_HAN);
            ps.setString(2, Constants.TT_DANG_MUON);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Loi cap nhat trang thai qua han: " + e.getMessage(), e);
        }
    }

    public int countByTrangThai(String trangThai) {
        String sql = "SELECT COUNT(*) FROM phieumuon WHERE trang_thai = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trangThai);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    /**
     * Giong countByTrangThai nhung co the loc them theo khoang thoi gian lap phieu (ngay_muon),
     * dung cho trang Thong ke khi nguoi dung chon khoang thoi gian (7 ngay/30 ngay/thang nay/nam nay/
     * tuy chon...). Truyen null cho tuNgay va/hoac denNgay de bo qua dieu kien tuong ung (khong loc = tat ca).
     */
    public int countByTrangThaiTrongKhoang(String trangThai, LocalDate tuNgay, LocalDate denNgay) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM phieumuon WHERE trang_thai = ? ");
        List<Object> params = new ArrayList<>();
        params.add(trangThai);
        if (tuNgay != null) {
            sql.append("AND ngay_muon >= ? ");
            params.add(Date.valueOf(tuNgay));
        }
        if (denNgay != null) {
            sql.append("AND ngay_muon <= ? ");
            params.add(Date.valueOf(denNgay));
        }
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    /**
     * "Danh sách đen": liet ke cac doc gia HIEN dang giu sach QUA HAN (chua tra), tuc la da phat sinh
     * tien phat theo so ngay tre (Constants.TIEN_PHAT_MOI_NGAY) nhung VAN CHUA TRA SACH - du da bi
     * "phat" (tinh tien phat) ho van khong chiu tra. Dung de thu thu/quan tri phat hien nhanh va co
     * the khoa tai khoan doc gia vi pham (dua vao danh sach den) truc tiep tu man hinh nay.
     * Object[]: {maDg, hoTen, sdt, email, trangThaiDocGia, soPhieuQuaHan, tongSoNgayTreLuyKe, tienPhatUocTinh}
     */
    public List<Object[]> getDanhSachDen() {
        List<Object[]> list = new ArrayList<>();
        // Luu y: phai tinh so ngay tre va tien phat CHO TUNG PHIEU MUON truoc (subquery "sub"), roi moi
        // gop theo doc gia o ngoai. Neu gop truc tiep tu chitietphieumuon (JOIN thang voi phieumuon) thi
        // 1 phieu co nhieu dau sach se bi JOIN ra nhieu dong, khien SUM(so ngay tre) bi nhan len nhieu lan
        // (vi du phieu tre 5 ngay co 2 dau sach se cong 5+5=10 ngay thay vi 5 ngay) -> sai ca so ngay tre
        // lan tien phat hien thi cho doc gia.
        String sql =
            "SELECT dg.ma_dg, dg.ho_ten, dg.sdt, dg.email, dg.trang_thai, " +
            "COUNT(sub.ma_pm) AS so_phieu_qua_han, " +
            "COALESCE(SUM(sub.so_ngay_tre), 0) AS tong_ngay_tre, " +
            "COALESCE(SUM(sub.tien_phat), 0) AS tien_phat_uoc_tinh " +
            "FROM ( " +
            "    SELECT pm.ma_pm, pm.ma_dg, " +
            "           GREATEST(DATEDIFF(CURDATE(), pm.ngay_hen_tra), 0) AS so_ngay_tre, " +
            "           GREATEST(DATEDIFF(CURDATE(), pm.ngay_hen_tra), 0) * SUM(ct.so_luong) * ? AS tien_phat " +
            "    FROM phieumuon pm " +
            "    JOIN chitietphieumuon ct ON ct.ma_pm = pm.ma_pm AND ct.da_tra = 0 " +
            "    WHERE pm.trang_thai = ? " +
            "    GROUP BY pm.ma_pm, pm.ma_dg, pm.ngay_hen_tra " +
            ") sub " +
            "JOIN docgia dg ON dg.ma_dg = sub.ma_dg " +
            "GROUP BY dg.ma_dg, dg.ho_ten, dg.sdt, dg.email, dg.trang_thai " +
            "ORDER BY tien_phat_uoc_tinh DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, BigDecimal.valueOf(Constants.TIEN_PHAT_MOI_NGAY));
            ps.setString(2, Constants.TT_QUA_HAN);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                        rs.getInt("ma_dg"), rs.getString("ho_ten"), rs.getString("sdt"),
                        rs.getString("email"), rs.getInt("trang_thai"),
                        rs.getInt("so_phieu_qua_han"), rs.getInt("tong_ngay_tre"),
                        rs.getBigDecimal("tien_phat_uoc_tinh")
                    });
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi lay danh sach den: " + e.getMessage(), e);
        }
        return list;
    }

    private PhieuMuon mapRow(ResultSet rs) throws SQLException {
        PhieuMuon pm = new PhieuMuon();
        pm.setMaPm(rs.getInt("ma_pm"));
        pm.setMaDg(rs.getInt("ma_dg"));
        pm.setMaTk(rs.getInt("ma_tk"));
        pm.setTenDocGia(rs.getString("ten_docgia"));
        pm.setTenNhanVien(rs.getString("ten_nhanvien"));
        pm.setNgayMuon(rs.getDate("ngay_muon").toLocalDate());
        pm.setNgayHenTra(rs.getDate("ngay_hen_tra").toLocalDate());
        pm.setTrangThai(rs.getString("trang_thai"));
        pm.setSoLanGiaHan(rs.getInt("so_lan_gia_han"));
        pm.setGhiChu(rs.getString("ghi_chu"));
        return pm;
    }
}

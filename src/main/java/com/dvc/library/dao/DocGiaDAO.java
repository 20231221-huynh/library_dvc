package com.dvc.library.dao;

import jakarta.enterprise.context.ApplicationScoped;

import com.dvc.library.model.DocGia;
import com.dvc.library.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CDI bean pham vi ApplicationScoped: chi khoi tao MOT the hien duy nhat dung chung cho toan bo
 * ung dung (thay vi moi Servlet tu \"new\" rieng mot ban). An toan vi DAO khong luu tru bat ky
 * trang thai khac (moi phuong thuc tu mo/dong Connection JDBC rieng, khong chia se bien instance).
 */
@ApplicationScoped
public class DocGiaDAO {

    public List<DocGia> getAll() {
        List<DocGia> list = new ArrayList<>();
        String sql = "SELECT * FROM docgia ORDER BY ma_dg DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Loi lay danh sach doc gia: " + e.getMessage(), e);
        }
        return list;
    }

    public List<DocGia> search(String keyword) {
        List<DocGia> list = new ArrayList<>();
        String sql = "SELECT * FROM docgia WHERE ho_ten LIKE ? OR sdt LIKE ? ORDER BY ma_dg DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String kw = "%" + keyword + "%";
            ps.setString(1, kw);
            ps.setString(2, kw);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi tim kiem doc gia: " + e.getMessage(), e);
        }
        return list;
    }

    public DocGia getById(int maDg) {
        String sql = "SELECT * FROM docgia WHERE ma_dg = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maDg);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi lay doc gia: " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * Kiem tra trung so dien thoai (chi kiem tra khi sdt khong rong).
     * excludeId: ma doc gia can loai tru (dung khi cap nhat, truyen null khi them moi).
     */
    public boolean existsBySdt(String sdt, Integer excludeId) {
        if (sdt == null || sdt.trim().isEmpty()) return false;
        String sql = "SELECT 1 FROM docgia WHERE sdt = ? " + (excludeId != null ? "AND ma_dg <> ? " : "");
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sdt.trim());
            if (excludeId != null) ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi kiem tra trung so dien thoai: " + e.getMessage(), e);
        }
    }

    /**
     * Kiem tra trung email (chi kiem tra khi email khong rong).
     * excludeId: ma doc gia can loai tru (dung khi cap nhat, truyen null khi them moi).
     */
    public boolean existsByEmail(String email, Integer excludeId) {
        if (email == null || email.trim().isEmpty()) return false;
        String sql = "SELECT 1 FROM docgia WHERE LOWER(TRIM(email)) = LOWER(TRIM(?)) " +
                     (excludeId != null ? "AND ma_dg <> ? " : "");
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            if (excludeId != null) ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi kiem tra trung email: " + e.getMessage(), e);
        }
    }

    public boolean insert(DocGia dg) {
        String sql = "INSERT INTO docgia (ho_ten, ngay_sinh, gioi_tinh, dia_chi, sdt, email, trang_thai) VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dg.getHoTen());
            ps.setDate(2, dg.getNgaySinh() != null ? Date.valueOf(dg.getNgaySinh()) : null);
            ps.setString(3, dg.getGioiTinh());
            ps.setString(4, dg.getDiaChi());
            ps.setString(5, dg.getSdt());
            ps.setString(6, dg.getEmail());
            ps.setInt(7, dg.getTrangThai());
            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new RuntimeException("Số điện thoại hoặc email này đã được đăng ký bởi độc giả khác.", e);
        } catch (SQLException e) {
            throw new RuntimeException("Loi them doc gia: " + e.getMessage(), e);
        }
    }

    public boolean update(DocGia dg) {
        String sql = "UPDATE docgia SET ho_ten=?, ngay_sinh=?, gioi_tinh=?, dia_chi=?, sdt=?, email=?, trang_thai=? WHERE ma_dg=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dg.getHoTen());
            ps.setDate(2, dg.getNgaySinh() != null ? Date.valueOf(dg.getNgaySinh()) : null);
            ps.setString(3, dg.getGioiTinh());
            ps.setString(4, dg.getDiaChi());
            ps.setString(5, dg.getSdt());
            ps.setString(6, dg.getEmail());
            ps.setInt(7, dg.getTrangThai());
            ps.setInt(8, dg.getMaDg());
            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new RuntimeException("Số điện thoại hoặc email này đã được đăng ký bởi độc giả khác.", e);
        } catch (SQLException e) {
            throw new RuntimeException("Loi cap nhat doc gia: " + e.getMessage(), e);
        }
    }

    public boolean delete(int maDg) {
        String sql = "DELETE FROM docgia WHERE ma_dg = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maDg);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Không thể xóa: độc giả đang có phiếu mượn liên quan.", e);
        }
    }

    /**
     * Cap nhat nhanh trang thai hoat dong/khoa cua doc gia (dung cho chuc nang "Danh sách đen":
     * khoa tai khoan doc gia vi pham qua han/khong tra sach, hoac mo khoa lai khi can), khong dong
     * cham den cac truong thong tin khac (khac voi update() dung cho form sua day du).
     */
    public boolean capNhatTrangThai(int maDg, int trangThai) {
        String sql = "UPDATE docgia SET trang_thai = ? WHERE ma_dg = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, trangThai);
            ps.setInt(2, maDg);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Loi cap nhat trang thai doc gia: " + e.getMessage(), e);
        }
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM docgia";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    private DocGia mapRow(ResultSet rs) throws SQLException {
        DocGia dg = new DocGia();
        dg.setMaDg(rs.getInt("ma_dg"));
        dg.setHoTen(rs.getString("ho_ten"));
        Date ns = rs.getDate("ngay_sinh");
        dg.setNgaySinh(ns != null ? ns.toLocalDate() : null);
        dg.setGioiTinh(rs.getString("gioi_tinh"));
        dg.setDiaChi(rs.getString("dia_chi"));
        dg.setSdt(rs.getString("sdt"));
        dg.setEmail(rs.getString("email"));
        Date ndk = rs.getDate("ngay_dang_ky");
        dg.setNgayDangKy(ndk != null ? ndk.toLocalDate() : null);
        dg.setTrangThai(rs.getInt("trang_thai"));
        return dg;
    }
}

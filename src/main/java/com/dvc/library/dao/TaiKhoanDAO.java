package com.dvc.library.dao;

import jakarta.enterprise.context.ApplicationScoped;

import com.dvc.library.model.TaiKhoan;
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
public class TaiKhoanDAO {

    public TaiKhoan login(String tenDangNhap, String matKhauHash) {
        String sql = "SELECT * FROM taikhoan WHERE ten_dang_nhap = ? AND mat_khau = ? AND trang_thai = 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tenDangNhap);
            ps.setString(2, matKhauHash);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi dang nhap: " + e.getMessage(), e);
        }
        return null;
    }

    public List<TaiKhoan> getAll() {
        List<TaiKhoan> list = new ArrayList<>();
        String sql = "SELECT * FROM taikhoan ORDER BY ma_tk DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Loi lay danh sach tai khoan: " + e.getMessage(), e);
        }
        return list;
    }

    public TaiKhoan getById(int maTk) {
        String sql = "SELECT * FROM taikhoan WHERE ma_tk = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maTk);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi lay tai khoan: " + e.getMessage(), e);
        }
        return null;
    }

    public boolean existsByUsername(String tenDangNhap) {
        String sql = "SELECT 1 FROM taikhoan WHERE ten_dang_nhap = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tenDangNhap);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi kiem tra ten dang nhap: " + e.getMessage(), e);
        }
    }

    public boolean insert(TaiKhoan tk) {
        String sql = "INSERT INTO taikhoan (ten_dang_nhap, mat_khau, ho_ten, vai_tro, trang_thai) VALUES (?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tk.getTenDangNhap());
            ps.setString(2, tk.getMatKhau());
            ps.setString(3, tk.getHoTen());
            ps.setString(4, tk.getVaiTro());
            ps.setInt(5, tk.getTrangThai());
            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại.", e);
        } catch (SQLException e) {
            throw new RuntimeException("Loi them tai khoan: " + e.getMessage(), e);
        }
    }

    public boolean update(TaiKhoan tk) {
        String sql = "UPDATE taikhoan SET ho_ten=?, vai_tro=?, trang_thai=? WHERE ma_tk=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tk.getHoTen());
            ps.setString(2, tk.getVaiTro());
            ps.setInt(3, tk.getTrangThai());
            ps.setInt(4, tk.getMaTk());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Loi cap nhat tai khoan: " + e.getMessage(), e);
        }
    }

    public boolean updatePassword(int maTk, String newHashedPassword) {
        String sql = "UPDATE taikhoan SET mat_khau=? WHERE ma_tk=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newHashedPassword);
            ps.setInt(2, maTk);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Loi doi mat khau: " + e.getMessage(), e);
        }
    }

    public boolean delete(int maTk) {
        String sql = "DELETE FROM taikhoan WHERE ma_tk = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maTk);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Loi xoa tai khoan: " + e.getMessage(), e);
        }
    }

    private TaiKhoan mapRow(ResultSet rs) throws SQLException {
        TaiKhoan tk = new TaiKhoan();
        tk.setMaTk(rs.getInt("ma_tk"));
        tk.setTenDangNhap(rs.getString("ten_dang_nhap"));
        tk.setMatKhau(rs.getString("mat_khau"));
        tk.setHoTen(rs.getString("ho_ten"));
        tk.setVaiTro(rs.getString("vai_tro"));
        tk.setTrangThai(rs.getInt("trang_thai"));
        return tk;
    }
}

package com.dvc.library.dao;

import jakarta.enterprise.context.ApplicationScoped;

import com.dvc.library.model.TheLoai;
import com.dvc.library.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class TheLoaiDAO {

    public List<TheLoai> getAll() {
        List<TheLoai> list = new ArrayList<>();
        String sql = "SELECT * FROM theloai ORDER BY ma_tl";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Loi lay danh sach the loai: " + e.getMessage(), e);
        }
        return list;
    }

    //Giống getAll() nhưng kèm theo số lượng đầu sách đang có trong từng thể lọai
    public List<TheLoai> getAllVoiSoLuongSach() {
        List<TheLoai> list = new ArrayList<>();
        String sql = "SELECT tl.ma_tl, tl.ten_theloai, tl.mo_ta, COUNT(s.ma_sach) AS so_luong_sach " +
                     "FROM theloai tl LEFT JOIN sach s ON s.ma_tl = tl.ma_tl " +
                     "GROUP BY tl.ma_tl, tl.ten_theloai, tl.mo_ta " + //gom các sách theo từng thể loại để đếm riêng từng thể loại
                     "ORDER BY tl.ma_tl";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                TheLoai tl = mapRow(rs);
                tl.setSoLuongSach(rs.getInt("so_luong_sach"));
                list.add(tl);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi lay danh sach the loai: " + e.getMessage(), e);
        }
        return list;
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM theloai";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    public TheLoai getById(int maTl) {
        String sql = "SELECT * FROM theloai WHERE ma_tl = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maTl);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi lay the loai: " + e.getMessage(), e);
        }
        return null;
    }

    public boolean existsDuplicate(String tenTheLoai, Integer excludeId) {
        String sql = "SELECT 1 FROM theloai WHERE LOWER(TRIM(ten_theloai)) = LOWER(TRIM(?)) " +
                     (excludeId != null ? "AND ma_tl <> ? " : "");
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tenTheLoai);
            if (excludeId != null) ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi kiem tra trung lap the loai: " + e.getMessage(), e);
        }
    }

    public boolean insert(TheLoai tl) {
        String sql = "INSERT INTO theloai (ten_theloai, mo_ta) VALUES (?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tl.getTenTheLoai());
            ps.setString(2, tl.getMoTa());
            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new RuntimeException("Thể loại này đã tồn tại.", e);
        } catch (SQLException e) {
            throw new RuntimeException("Loi them the loai: " + e.getMessage(), e);
        }
    }

    public boolean update(TheLoai tl) {
        String sql = "UPDATE theloai SET ten_theloai=?, mo_ta=? WHERE ma_tl=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tl.getTenTheLoai());
            ps.setString(2, tl.getMoTa());
            ps.setInt(3, tl.getMaTl());
            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new RuntimeException("Thể loại này đã tồn tại.", e);
        } catch (SQLException e) {
            throw new RuntimeException("Loi cap nhat the loai: " + e.getMessage(), e);
        }
    }

    public boolean delete(int maTl) {
        String sql = "DELETE FROM theloai WHERE ma_tl = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maTl);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            // Loi khoa ngoai (the loai dang duoc sach su dung)
            throw new RuntimeException("Không thể xóa: thể loại đang được sử dụng bởi sách.", e);
        }
    }

    private TheLoai mapRow(ResultSet rs) throws SQLException {
        return new TheLoai(rs.getInt("ma_tl"), rs.getString("ten_theloai"), rs.getString("mo_ta"));
    }
}

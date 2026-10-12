package com.dvc.library.dao;

import jakarta.enterprise.context.ApplicationScoped;

import com.dvc.library.model.TacGia;
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
public class TacGiaDAO {

    public List<TacGia> getAll() {
        List<TacGia> list = new ArrayList<>();
        String sql = "SELECT * FROM tacgia ORDER BY ma_tg";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Loi lay danh sach tac gia: " + e.getMessage(), e);
        }
        return list;
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM tacgia";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    public TacGia getById(int maTg) {
        String sql = "SELECT * FROM tacgia WHERE ma_tg = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maTg);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi lay tac gia: " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * Kiem tra trung lap tac gia: cung ten (khong phan biet hoa/thuong, bo khoang trang thua)
     * VA cung quoc tich thi moi coi la trung (vd: 2 tac gia trung ten nhung khac quoc tich
     * van duoc coi la 2 nguoi khac nhau).
     * excludeId: ma tac gia can loai tru khoi kiem tra (dung khi cap nhat, truyen null khi them moi).
     */
    public boolean existsDuplicate(String tenTacGia, String quocTich, Integer excludeId) {
        String sql = "SELECT 1 FROM tacgia WHERE LOWER(TRIM(ten_tacgia)) = LOWER(TRIM(?)) " +
                     "AND LOWER(TRIM(IFNULL(quoc_tich, ''))) = LOWER(TRIM(IFNULL(?, ''))) " +
                     (excludeId != null ? "AND ma_tg <> ? " : "");
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tenTacGia);
            ps.setString(2, quocTich);
            if (excludeId != null) ps.setInt(3, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi kiem tra trung lap tac gia: " + e.getMessage(), e);
        }
    }

    public boolean insert(TacGia tg) {
        String sql = "INSERT INTO tacgia (ten_tacgia, quoc_tich) VALUES (?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tg.getTenTacGia());
            ps.setString(2, tg.getQuocTich());
            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new RuntimeException("Tác giả với tên và quốc tịch này đã tồn tại.", e);
        } catch (SQLException e) {
            throw new RuntimeException("Loi them tac gia: " + e.getMessage(), e);
        }
    }

    public boolean update(TacGia tg) {
        String sql = "UPDATE tacgia SET ten_tacgia=?, quoc_tich=? WHERE ma_tg=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tg.getTenTacGia());
            ps.setString(2, tg.getQuocTich());
            ps.setInt(3, tg.getMaTg());
            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new RuntimeException("Tác giả với tên và quốc tịch này đã tồn tại.", e);
        } catch (SQLException e) {
            throw new RuntimeException("Loi cap nhat tac gia: " + e.getMessage(), e);
        }
    }

    public boolean delete(int maTg) {
        String sql = "DELETE FROM tacgia WHERE ma_tg = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maTg);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Không thể xóa: tác giả đang được sử dụng bởi sách.", e);
        }
    }

    private TacGia mapRow(ResultSet rs) throws SQLException {
        return new TacGia(rs.getInt("ma_tg"), rs.getString("ten_tacgia"), rs.getString("quoc_tich"));
    }
}

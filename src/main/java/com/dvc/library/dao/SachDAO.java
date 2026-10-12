package com.dvc.library.dao;

import jakarta.enterprise.context.ApplicationScoped;

import com.dvc.library.model.Sach;
import com.dvc.library.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class SachDAO {

    private static final String SELECT_JOIN
            = "SELECT s.*, tg.ten_tacgia, tl.ten_theloai "
            + "FROM sach s "
            + "LEFT JOIN tacgia tg ON s.ma_tg = tg.ma_tg "
            + "LEFT JOIN theloai tl ON s.ma_tl = tl.ma_tl ";

    public List<Sach> getAll() {
        List<Sach> list = new ArrayList<>();
        String sql = SELECT_JOIN + "ORDER BY s.ma_sach DESC";
        try (Connection conn = DBConnection.getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi lay danh sach sach: " + e.getMessage(), e);
        }
        return list;
    }

    public List<Sach> search(String keyword, Integer maTl, Integer maTg) {
        List<Sach> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(SELECT_JOIN + "WHERE 1=1 ");
        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND s.ten_sach LIKE ? ");
            params.add("%" + keyword.trim() + "%");
        }
        if (maTl != null) {
            sql.append("AND s.ma_tl = ? ");
            params.add(maTl);
        }
        if (maTg != null) {
            sql.append("AND s.ma_tg = ? ");
            params.add(maTg);
        }
        sql.append("ORDER BY s.ma_sach DESC");

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {////chuẩn bị câu SQL và gán giá trị cho các dấu ?
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi tim kiem sach: " + e.getMessage(), e);
        }
        return list;
    }

    public Sach getById(int maSach) {
        String sql = SELECT_JOIN + "WHERE s.ma_sach = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maSach);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi lay sach: " + e.getMessage(), e);
        }
        return null;
    }

    //Tìm sách có cùng tên + cùng tác giả, nhưng mã sách phải khác
    public boolean existsDuplicate(String tenSach, int maTg, Integer excludeId) { //excludeId: loại trừ sách đang sửa 
        String sql = "SELECT 1 FROM sach WHERE LOWER(TRIM(ten_sach)) = LOWER(TRIM(?)) "
                + "AND ma_tg = ? "
                + (excludeId != null ? "AND ma_sach <> ? " : ""); 
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tenSach);
            ps.setInt(2, maTg);
            if (excludeId != null) {
                ps.setInt(3, excludeId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi kiem tra trung lap sach: " + e.getMessage(), e);
        }
    }

    public int insertAndGetId(Sach s) { //Thêm sách và lấy mã sách (nhận vào một đối tượng sách)
        String sql = "INSERT INTO sach (ten_sach, ma_tg, ma_tl, nha_xuat_ban, nam_xb, so_luong, so_luong_con, don_gia, mo_ta, hinh_anh, trang_thai) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, s.getTenSach());
            ps.setInt(2, s.getMaTg());
            ps.setInt(3, s.getMaTl());
            ps.setString(4, s.getNhaXuatBan());
            ps.setInt(5, s.getNamXb());
            ps.setInt(6, s.getSoLuong());
            ps.setInt(7, s.getSoLuong()); // luc them moi, so luong con = so luong
            ps.setBigDecimal(8, s.getDonGia());
            ps.setString(9, s.getMoTa());
            ps.setString(10, s.getHinhAnh());
            ps.setInt(11, s.getTrangThai());
            ps.executeUpdate(); // thực hiện insert
            //Lấy mã vừa tạo
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1); //lấy tại cột 1 của dòng hiện tại
                }
            }
            return 0; //Không có mã nào được trả về
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new RuntimeException("Sách với tên và tác giả này đã tồn tại.", e);
        } catch (SQLException e) {
            throw new RuntimeException("Loi them sach: " + e.getMessage(), e);
        }
    }

    public boolean insert(Sach s) {
        return insertAndGetId(s) > 0;
    }

    //Cập nhật riêng tên file hình ảnh cho một sách
    public boolean updateHinhAnh(int maSach, String tenFileHinhAnh) {
        String sql = "UPDATE sach SET hinh_anh = ? WHERE ma_sach = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tenFileHinhAnh);
            ps.setInt(2, maSach);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Loi cap nhat hinh anh sach: " + e.getMessage(), e);
        }
    }


    //Cập nhật thông tin sách, không đổi hình_ảnh 
    public boolean update(Sach s) {
        String sql = "UPDATE sach SET ten_sach=?, ma_tg=?, ma_tl=?, nha_xuat_ban=?, nam_xb=?, so_luong=?, don_gia=?, mo_ta=?, trang_thai=? WHERE ma_sach=?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getTenSach());
            ps.setInt(2, s.getMaTg());
            ps.setInt(3, s.getMaTl());
            ps.setString(4, s.getNhaXuatBan());
            ps.setInt(5, s.getNamXb());
            ps.setInt(6, s.getSoLuong());
            ps.setBigDecimal(7, s.getDonGia());
            ps.setString(8, s.getMoTa());
            ps.setInt(9, s.getTrangThai());
            ps.setInt(10, s.getMaSach());
            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new RuntimeException("Sách với tên và tác giả này đã tồn tại.", e);
        } catch (SQLException e) {
            throw new RuntimeException("Loi cap nhat sach: " + e.getMessage(), e);
        }
    }

    //Cập nhật thông tin sách kèm hình ảnh mới
    public boolean updateWithImage(Sach s) {
        String sql = "UPDATE sach SET ten_sach=?, ma_tg=?, ma_tl=?, nha_xuat_ban=?, nam_xb=?, so_luong=?, don_gia=?, mo_ta=?, hinh_anh=?, trang_thai=? WHERE ma_sach=?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getTenSach());
            ps.setInt(2, s.getMaTg());
            ps.setInt(3, s.getMaTl());
            ps.setString(4, s.getNhaXuatBan());
            ps.setInt(5, s.getNamXb());
            ps.setInt(6, s.getSoLuong());
            ps.setBigDecimal(7, s.getDonGia());
            ps.setString(8, s.getMoTa());
            ps.setString(9, s.getHinhAnh());
            ps.setInt(10, s.getTrangThai());
            ps.setInt(11, s.getMaSach());
            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new RuntimeException("Sách với tên và tác giả này đã tồn tại.", e);
        } catch (SQLException e) {
            throw new RuntimeException("Loi cap nhat sach: " + e.getMessage(), e);
        }
    }

    public boolean delete(int maSach) {
        String sql = "DELETE FROM sach WHERE ma_sach = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maSach);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Không thể xóa: sách đang có trong phiếu mượn.", e);
        }
    }

    //Giảm số lượng còn khi mượn sách
    public boolean decreaseSoLuongCon(Connection conn, int maSach, int soLuong) throws SQLException { //soLuong = số lượng muốn mượn
        String sql = "UPDATE sach SET so_luong_con = so_luong_con - ? WHERE ma_sach = ? AND so_luong_con >= ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, soLuong);
            ps.setInt(2, maSach);
            ps.setInt(3, soLuong);
            return ps.executeUpdate() > 0;
        }
    }

    //Tăng số lượng còn khi trả sách
    public boolean increaseSoLuongCon(Connection conn, int maSach, int soLuong) throws SQLException {
        String sql = "UPDATE sach SET so_luong_con = so_luong_con + ? WHERE ma_sach = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, soLuong);
            ps.setInt(2, maSach);
            return ps.executeUpdate() > 0;
        }
    }

    
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM sach";
        try (Connection conn = DBConnection.getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    private Sach mapRow(ResultSet rs) throws SQLException { //Trong quá trình chạy, nếu thao tác với ResultSet xảy ra lỗi SQL thì lỗi đó có thể được ném ra
        Sach s = new Sach();
        s.setMaSach(rs.getInt("ma_sach"));
        s.setTenSach(rs.getString("ten_sach"));
        s.setMaTg(rs.getInt("ma_tg"));
        s.setMaTl(rs.getInt("ma_tl"));
        s.setTenTacGia(rs.getString("ten_tacgia"));
        s.setTenTheLoai(rs.getString("ten_theloai"));
        s.setNhaXuatBan(rs.getString("nha_xuat_ban"));
        s.setNamXb(rs.getInt("nam_xb"));
        s.setSoLuong(rs.getInt("so_luong"));
        s.setSoLuongCon(rs.getInt("so_luong_con"));
        s.setDonGia(rs.getBigDecimal("don_gia"));
        s.setMoTa(rs.getString("mo_ta"));
        s.setHinhAnh(rs.getString("hinh_anh"));
        s.setTrangThai(rs.getInt("trang_thai"));
        return s;
    }
}

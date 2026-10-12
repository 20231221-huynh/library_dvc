package com.dvc.library.servlet;

import com.dvc.library.dao.TaiKhoanDAO;
import com.dvc.library.model.TaiKhoan;
import com.dvc.library.util.Constants;
import com.dvc.library.util.ExceptionUtil;
import com.dvc.library.util.PasswordUtil;
import com.dvc.library.util.ValidationUtil;

import jakarta.inject.Inject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/taikhoan")
public class TaiKhoanServlet extends HttpServlet {

    private static final String MAT_KHAU_MAC_DINH = "123456";

    @Inject
    private TaiKhoanDAO taiKhoanDAO;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) action = "list";

        TaiKhoan nguoiDangDangNhap = (TaiKhoan) req.getSession().getAttribute(Constants.SESSION_USER);

        if ("delete".equals(action)) {
            try {
                int id = Integer.parseInt(req.getParameter("id"));
                if (nguoiDangDangNhap != null && nguoiDangDangNhap.getMaTk() == id) {
                    throw new IllegalArgumentException("Không thể tự xóa tài khoản đang đăng nhập.");
                }
                taiKhoanDAO.delete(id);
                req.getSession().setAttribute("flashSuccess", "Xóa tài khoản thành công.");
            } catch (Exception e) {
                req.getSession().setAttribute("flashError", ExceptionUtil.friendlyMessage(e));
            }
            resp.sendRedirect(req.getContextPath() + "/taikhoan");
            return;
        }

        if ("resetPassword".equals(action)) {
            try {
                int id = Integer.parseInt(req.getParameter("id"));
                boolean ok = taiKhoanDAO.updatePassword(id, PasswordUtil.hash(MAT_KHAU_MAC_DINH));
                if (ok) {
                    req.getSession().setAttribute("flashSuccess",
                            "Đã đặt lại mật khẩu về mặc định (" + MAT_KHAU_MAC_DINH + "). Vui lòng thông báo cho nhân viên đổi lại mật khẩu sau khi đăng nhập.");
                } else {
                    req.getSession().setAttribute("flashError", "Không tìm thấy tài khoản để đặt lại mật khẩu.");
                }
            } catch (Exception e) {
                req.getSession().setAttribute("flashError", ExceptionUtil.friendlyMessage(e));
            }
            resp.sendRedirect(req.getContextPath() + "/taikhoan");
            return;
        }

        if ("edit".equals(action)) {
            req.setAttribute("taiKhoan", taiKhoanDAO.getById(Integer.parseInt(req.getParameter("id"))));
        }

        req.setAttribute("taiKhoanList", taiKhoanDAO.getAll());
        req.getRequestDispatcher("/WEB-INF/views/taikhoan/list.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        try {
            if ("update".equals(action)) {
                TaiKhoan nguoiDangDangNhap = (TaiKhoan) req.getSession().getAttribute(Constants.SESSION_USER);
                int maTk = ValidationUtil.parseIntSafe(req.getParameter("maTk"), "mã tài khoản");

                String hoTen = req.getParameter("hoTen");
                ValidationUtil.requireNotBlank(hoTen, "họ tên");
                ValidationUtil.validateMaxLength(hoTen, "họ tên", 100);

                boolean trangThaiMoi = req.getParameter("trangThai") != null;
                if (nguoiDangDangNhap != null && nguoiDangDangNhap.getMaTk() == maTk && !trangThaiMoi) {
                    throw new IllegalArgumentException("Không thể tự khóa tài khoản đang đăng nhập.");
                }

                TaiKhoan tk = new TaiKhoan();
                tk.setMaTk(maTk);
                tk.setHoTen(hoTen.trim());
                tk.setVaiTro(req.getParameter("vaiTro"));
                tk.setTrangThai(trangThaiMoi ? 1 : 0);
                taiKhoanDAO.update(tk);
                req.getSession().setAttribute("flashSuccess", "Cập nhật tài khoản thành công.");
            } else {
                String tenDangNhap = req.getParameter("tenDangNhap");
                ValidationUtil.requireNotBlank(tenDangNhap, "tên đăng nhập");
                if (tenDangNhap.trim().length() < 4) {
                    throw new IllegalArgumentException("Tên đăng nhập phải có ít nhất 4 ký tự.");
                }
                ValidationUtil.validateMaxLength(tenDangNhap, "tên đăng nhập", 50);

                String matKhau = req.getParameter("matKhau");
                ValidationUtil.validatePasswordLength(matKhau, 6);

                String hoTen = req.getParameter("hoTen");
                ValidationUtil.requireNotBlank(hoTen, "họ tên");
                ValidationUtil.validateMaxLength(hoTen, "họ tên", 100);

                if (taiKhoanDAO.existsByUsername(tenDangNhap.trim())) {
                    throw new IllegalArgumentException("Tên đăng nhập đã tồn tại.");
                }

                TaiKhoan tk = new TaiKhoan();
                tk.setTenDangNhap(tenDangNhap.trim());
                tk.setMatKhau(PasswordUtil.hash(matKhau));
                tk.setHoTen(hoTen.trim());
                tk.setVaiTro(req.getParameter("vaiTro"));
                tk.setTrangThai(1);
                taiKhoanDAO.insert(tk);
                req.getSession().setAttribute("flashSuccess", "Thêm tài khoản thành công.");
            }
        } catch (Exception e) {
            req.getSession().setAttribute("flashError", "Lỗi lưu tài khoản: " + ExceptionUtil.friendlyMessage(e));
        }
        resp.sendRedirect(req.getContextPath() + "/taikhoan");
    }
}

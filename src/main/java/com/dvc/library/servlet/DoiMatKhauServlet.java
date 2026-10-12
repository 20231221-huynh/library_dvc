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

@WebServlet("/doimatkhau")
public class DoiMatKhauServlet extends HttpServlet {

    @Inject
    private TaiKhoanDAO taiKhoanDAO;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/doimatkhau.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        TaiKhoan nguoiDung = (TaiKhoan) session.getAttribute(Constants.SESSION_USER);

        if (nguoiDung == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String matKhauCu = req.getParameter("matKhauCu");
        String matKhauMoi = req.getParameter("matKhauMoi");
        String xacNhanMatKhauMoi = req.getParameter("xacNhanMatKhauMoi");

        try {
            ValidationUtil.requireNotBlank(matKhauCu, "mật khẩu hiện tại");
            ValidationUtil.requireNotBlank(matKhauMoi, "mật khẩu mới");
            ValidationUtil.validatePasswordLength(matKhauMoi, 6);

            if (!matKhauMoi.equals(xacNhanMatKhauMoi)) {
                throw new IllegalArgumentException("Xác nhận mật khẩu mới không khớp.");
            }

            // Xac thuc lai mat khau hien tai truoc khi cho doi
            TaiKhoan kiemTra = taiKhoanDAO.login(nguoiDung.getTenDangNhap(), PasswordUtil.hash(matKhauCu));
            if (kiemTra == null) {
                throw new IllegalArgumentException("Mật khẩu hiện tại không đúng.");
            }

            if (matKhauCu.equals(matKhauMoi)) {
                throw new IllegalArgumentException("Mật khẩu mới phải khác mật khẩu hiện tại.");
            }

            taiKhoanDAO.updatePassword(nguoiDung.getMaTk(), PasswordUtil.hash(matKhauMoi));
            session.setAttribute("flashSuccess", "Đổi mật khẩu thành công. Vui lòng ghi nhớ mật khẩu mới.");
            resp.sendRedirect(req.getContextPath() + "/dashboard");
        } catch (Exception e) {
            req.setAttribute("errorMessage", ExceptionUtil.friendlyMessage(e));
            req.getRequestDispatcher("/WEB-INF/views/doimatkhau.jsp").forward(req, resp);
        }
    }
}

package com.dvc.library.servlet;

import com.dvc.library.dao.TaiKhoanDAO;
import com.dvc.library.model.TaiKhoan;
import com.dvc.library.util.Constants;
import com.dvc.library.util.ExceptionUtil;
import com.dvc.library.util.PasswordUtil;

import jakarta.inject.Inject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final String SO_LAN_SAI = "SO_LAN_DANG_NHAP_SAI";
    private static final int SO_LAN_TOI_DA = 5;

    @Inject
    private TaiKhoanDAO taiKhoanDAO;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute(Constants.SESSION_USER) != null) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }
        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String tenDangNhap = req.getParameter("tenDangNhap");
        String matKhau = req.getParameter("matKhau");
        HttpSession session = req.getSession(true);

        // Chong do mat khau (brute-force) don gian dua tren session:
        // sau qua nhieu lan nhap sai lien tiep, tam thoi chan thu tiep.
        Integer soLanSai = (Integer) session.getAttribute(SO_LAN_SAI);
        if (soLanSai != null && soLanSai >= SO_LAN_TOI_DA) {
            req.setAttribute("errorMessage",
                    "Bạn đã nhập sai quá " + SO_LAN_TOI_DA + " lần. Vui lòng đóng và mở lại trình duyệt (hoặc chờ ít phút) rồi thử lại.");
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
            return;
        }

        if (tenDangNhap == null || tenDangNhap.trim().isEmpty() || matKhau == null || matKhau.isEmpty()) {
            req.setAttribute("errorMessage", "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu.");
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
            return;
        }

        try {
            String hashed = PasswordUtil.hash(matKhau);
            TaiKhoan user = taiKhoanDAO.login(tenDangNhap.trim(), hashed);
            if (user == null) {
                int lanSaiMoi = (soLanSai == null ? 0 : soLanSai) + 1;
                session.setAttribute(SO_LAN_SAI, lanSaiMoi);
                int conLai = SO_LAN_TOI_DA - lanSaiMoi;
                String canhBao = conLai > 0
                        ? " (còn " + conLai + " lần thử trước khi bị tạm khóa)"
                        : "";
                req.setAttribute("errorMessage",
                        "Tên đăng nhập hoặc mật khẩu không đúng, hoặc tài khoản đã bị khóa." + canhBao);
                req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
                return;
            }
            // Dang nhap thanh cong -> xoa bo dem so lan sai
            session.removeAttribute(SO_LAN_SAI);
            session.setAttribute(Constants.SESSION_USER, user);
            session.setMaxInactiveInterval(30 * 60);
            resp.sendRedirect(req.getContextPath() + "/dashboard");
        } catch (RuntimeException e) {
            req.setAttribute("errorMessage", ExceptionUtil.friendlyMessage(e));
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
        }
    }
}

package com.dvc.library.filter;

import com.dvc.library.model.TaiKhoan;
import com.dvc.library.util.Constants;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Chi cho phep ADMIN truy cap cac chuc nang quan ly tai khoan nhan vien.
 */
@WebFilter("/taikhoan")
public class AdminFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        HttpSession session = request.getSession(false);
        TaiKhoan user = (session != null) ? (TaiKhoan) session.getAttribute(Constants.SESSION_USER) : null;

        if (user != null && user.isAdmin()) {
            chain.doFilter(req, resp);
        } else {
            request.setAttribute("errorMessage", "Bạn không có quyền truy cập chức năng này (chỉ dành cho Quản trị viên).");
            request.getRequestDispatcher("/WEB-INF/views/error/403.jsp").forward(request, response);
        }
    }
}

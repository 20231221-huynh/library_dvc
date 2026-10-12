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
 * Chan tat ca cac request (tru login, resources, error page) neu chua dang nhap.
 */
@WebFilter("/*")
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String path = uri.substring(contextPath.length());

        boolean isPublic = path.equals("/login")
                || path.equals("/")
                || path.startsWith("/assets/");

        HttpSession session = request.getSession(false);
        TaiKhoan user = (session != null) ? (TaiKhoan) session.getAttribute(Constants.SESSION_USER) : null;

        if (isPublic || user != null) {
            chain.doFilter(req, resp);
        } else {
            response.sendRedirect(contextPath + "/login");
        }
    }
}

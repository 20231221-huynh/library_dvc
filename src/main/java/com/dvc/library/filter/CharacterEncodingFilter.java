package com.dvc.library.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Bat buoc phai co filter nay khi ung dung nhap/hien thi tieng Viet co dau.
 * Neu khong goi setCharacterEncoding("UTF-8") TRUOC KHI doc bat ky parameter nao
 * cua request (vi du req.getParameter(...)), Tomcat se mac dinh doc du lieu form POST
 * theo ISO-8859-1, lam sai lech (loi font / dau "???") toan bo du lieu tieng Viet
 * nguoi dung nhap vao (ten sach, ten doc gia, ghi chu...).
 */
@WebFilter("/*")
public class CharacterEncodingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        chain.doFilter(req, resp);
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }
}

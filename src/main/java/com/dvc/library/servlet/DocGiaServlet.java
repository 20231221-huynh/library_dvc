package com.dvc.library.servlet;

import com.dvc.library.dao.DocGiaDAO;
import com.dvc.library.dao.PhieuMuonDAO;
import com.dvc.library.model.DocGia;
import com.dvc.library.util.ExceptionUtil;
import com.dvc.library.util.ValidationUtil;

import jakarta.inject.Inject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@WebServlet("/docgia")
public class DocGiaServlet extends HttpServlet {

    @Inject
    private DocGiaDAO docGiaDAO;

    @Inject
    private PhieuMuonDAO phieuMuonDAO;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) action = "list";

        switch (action) {
            case "add":
                req.getRequestDispatcher("/WEB-INF/views/docgia/form.jsp").forward(req, resp);
                break;
            case "edit":
                DocGia dg = docGiaDAO.getById(Integer.parseInt(req.getParameter("id")));
                if (dg == null) {
                    resp.sendRedirect(req.getContextPath() + "/docgia");
                    return;
                }
                req.setAttribute("docGia", dg);
                req.getRequestDispatcher("/WEB-INF/views/docgia/form.jsp").forward(req, resp);
                break;
            case "delete":
                try {
                    docGiaDAO.delete(Integer.parseInt(req.getParameter("id")));
                    req.getSession().setAttribute("flashSuccess", "Xóa độc giả thành công.");
                } catch (RuntimeException e) {
                    req.getSession().setAttribute("flashError", ExceptionUtil.friendlyMessage(e));
                }
                resp.sendRedirect(req.getContextPath() + "/docgia");
                break;
            case "search":
                String keyword = req.getParameter("keyword");
                req.setAttribute("docGiaList",
                    (keyword == null || keyword.trim().isEmpty()) ? docGiaDAO.getAll() : docGiaDAO.search(keyword.trim()));
                req.setAttribute("keyword", keyword);
                req.getRequestDispatcher("/WEB-INF/views/docgia/list.jsp").forward(req, resp);
                break;
            case "blacklist":
                phieuMuonDAO.capNhatTrangThaiQuaHan();
                req.setAttribute("danhSachDen", phieuMuonDAO.getDanhSachDen());
                req.getRequestDispatcher("/WEB-INF/views/docgia/blacklist.jsp").forward(req, resp);
                break;
            case "khoa":
            case "moKhoa":
                try {
                    int maDg = Integer.parseInt(req.getParameter("id"));
                    docGiaDAO.capNhatTrangThai(maDg, "khoa".equals(action) ? 0 : 1);
                    req.getSession().setAttribute("flashSuccess",
                        "khoa".equals(action)
                            ? "Đã khóa tài khoản độc giả (đưa vào danh sách đen)."
                            : "Đã mở khóa tài khoản độc giả.");
                } catch (RuntimeException e) {
                    req.getSession().setAttribute("flashError", ExceptionUtil.friendlyMessage(e));
                }
                resp.sendRedirect(req.getContextPath() + "/docgia?action=blacklist");
                break;
            case "list":
            default:
                req.setAttribute("docGiaList", docGiaDAO.getAll());
                req.getRequestDispatcher("/WEB-INF/views/docgia/list.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        try {
            DocGia dg = new DocGia();

            String hoTen = req.getParameter("hoTen");
            ValidationUtil.requireNotBlank(hoTen, "họ tên");
            ValidationUtil.validateMaxLength(hoTen, "họ tên", 100);
            dg.setHoTen(hoTen.trim());

            String ns = req.getParameter("ngaySinh");
            if (ns != null && !ns.isEmpty()) {
                try {
                    LocalDate ngaySinh = LocalDate.parse(ns);
                    if (ngaySinh.isAfter(LocalDate.now())) {
                        throw new IllegalArgumentException("Ngày sinh không được lớn hơn ngày hiện tại.");
                    }
                    dg.setNgaySinh(ngaySinh);
                } catch (DateTimeParseException e) {
                    throw new IllegalArgumentException("Ngày sinh không hợp lệ.");
                }
            }

            String sdt = req.getParameter("sdt");
            ValidationUtil.requireNotBlank(sdt, "số điện thoại");
            ValidationUtil.validatePhoneIfPresent(sdt);
            dg.setSdt(sdt.trim());

            String email = req.getParameter("email");
            ValidationUtil.requireNotBlank(email, "email");
            ValidationUtil.validateEmailIfPresent(email);
            dg.setEmail(email.trim());

            dg.setGioiTinh(req.getParameter("gioiTinh"));
            String diaChi = req.getParameter("diaChi");
            ValidationUtil.validateMaxLength(diaChi, "địa chỉ", 255);
            dg.setDiaChi(diaChi);
            dg.setTrangThai(req.getParameter("trangThai") != null ? 1 : 0);

            if ("update".equals(action)) {
                dg.setMaDg(ValidationUtil.parseIntSafe(req.getParameter("maDg"), "mã độc giả"));
                if (docGiaDAO.existsBySdt(dg.getSdt(), dg.getMaDg())) {
                    throw new IllegalArgumentException("Số điện thoại này đã được đăng ký bởi độc giả khác.");
                }
                if (docGiaDAO.existsByEmail(dg.getEmail(), dg.getMaDg())) {
                    throw new IllegalArgumentException("Email này đã được đăng ký bởi độc giả khác.");
                }
                docGiaDAO.update(dg);
                req.getSession().setAttribute("flashSuccess", "Cập nhật độc giả thành công.");
            } else {
                if (docGiaDAO.existsBySdt(dg.getSdt(), null)) {
                    throw new IllegalArgumentException("Số điện thoại này đã được đăng ký bởi độc giả khác.");
                }
                if (docGiaDAO.existsByEmail(dg.getEmail(), null)) {
                    throw new IllegalArgumentException("Email này đã được đăng ký bởi độc giả khác.");
                }
                docGiaDAO.insert(dg);
                req.getSession().setAttribute("flashSuccess", "Thêm độc giả thành công.");
            }
        } catch (Exception e) {
            req.getSession().setAttribute("flashError", "Lỗi lưu độc giả: " + ExceptionUtil.friendlyMessage(e));
        }
        resp.sendRedirect(req.getContextPath() + "/docgia");
    }
}

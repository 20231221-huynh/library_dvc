package com.dvc.library.servlet;

import com.dvc.library.dao.TheLoaiDAO;
import com.dvc.library.model.TheLoai;
import com.dvc.library.util.ExceptionUtil;
import com.dvc.library.util.ValidationUtil;//kiểm tra dữ liệu đầu vào

import jakarta.inject.Inject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/theloai")
public class TheLoaiServlet extends HttpServlet {

    @Inject
    private TheLoaiDAO theLoaiDAO;

    /*doGet()có nhiệm vụ nhận yêu cầu GET từ trình duyệt rồi xem action là gì 
    để quyết định hiển thị danh sách, sửa, xóa*/
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) action = "list";

        if ("delete".equals(action)) {
            try {
                theLoaiDAO.delete(Integer.parseInt(req.getParameter("id")));
                req.getSession().setAttribute("flashSuccess", "Xóa thể loại thành công."); //Lưu thông báo vào session và jsp lấy ra để hiển thị thông báo
            } catch (RuntimeException e) {
                req.getSession().setAttribute("flashError", ExceptionUtil.friendlyMessage(e));
            }
            resp.sendRedirect(req.getContextPath() + "/theloai");
            return;
        }

        if ("edit".equals(action)) {
            TheLoai tl = theLoaiDAO.getById(Integer.parseInt(req.getParameter("id")));
            req.setAttribute("theLoai", tl); //Đưa object đó sang JSP
        }

        req.setAttribute("theLoaiList", theLoaiDAO.getAllVoiSoLuongSach());
        req.getRequestDispatcher("/WEB-INF/views/theloai/list.jsp").forward(req, resp);//Chuyển sang JSP
    }

    /*doPost() dùng để nhận dữ liệu từ form thêm/sửa
    rồi kiểm tra, xử lý và lưu vào database*/
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        try {
            String tenTheLoai = req.getParameter("tenTheLoai");
            ValidationUtil.requireNotBlank(tenTheLoai, "tên thể loại");
            ValidationUtil.validateMaxLength(tenTheLoai, "tên thể loại", 100);

            TheLoai tl = new TheLoai();
            tl.setTenTheLoai(tenTheLoai.trim());
            String moTaTheLoai = req.getParameter("moTa");
            ValidationUtil.validateMaxLength(moTaTheLoai, "mô tả", 255);
            tl.setMoTa(moTaTheLoai);

            if ("update".equals(action)) {
                //gán mã thể loại vaò obj tl
                tl.setMaTl(ValidationUtil.parseIntSafe(req.getParameter("maTl"), "mã thể loại")); //Giá trị và tên trường
                if (theLoaiDAO.existsDuplicate(tl.getTenTheLoai(), tl.getMaTl())) {
                    throw new IllegalArgumentException("Thể loại này đã tồn tại.");
                }
                theLoaiDAO.update(tl);
                req.getSession().setAttribute("flashSuccess", "Cập nhật thể loại thành công.");
            } else {
                if (theLoaiDAO.existsDuplicate(tl.getTenTheLoai(), null)) {
                    throw new IllegalArgumentException("Thể loại này đã tồn tại.");
                }
                theLoaiDAO.insert(tl);
                req.getSession().setAttribute("flashSuccess", "Thêm thể loại thành công.");
            }
        } catch (Exception e) {
            req.getSession().setAttribute("flashError", "Lỗi lưu thể loại: " + ExceptionUtil.friendlyMessage(e));
        }
        resp.sendRedirect(req.getContextPath() + "/theloai");
    }
}

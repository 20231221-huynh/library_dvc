package com.dvc.library.servlet;

import com.dvc.library.dao.TacGiaDAO;
import com.dvc.library.model.TacGia;
import com.dvc.library.util.ExceptionUtil;
import com.dvc.library.util.ValidationUtil;

import jakarta.inject.Inject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/tacgia")
public class TacGiaServlet extends HttpServlet {

    @Inject
    private TacGiaDAO tacGiaDAO;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) action = "list";

        if ("delete".equals(action)) {
            try {
                tacGiaDAO.delete(Integer.parseInt(req.getParameter("id")));
                req.getSession().setAttribute("flashSuccess", "Xóa tác giả thành công.");
            } catch (RuntimeException e) {
                req.getSession().setAttribute("flashError", ExceptionUtil.friendlyMessage(e));
            }
            resp.sendRedirect(req.getContextPath() + "/tacgia");
            return;
        }

        if ("edit".equals(action)) {
            TacGia tg = tacGiaDAO.getById(Integer.parseInt(req.getParameter("id")));
            req.setAttribute("tacGia", tg);
        }

        req.setAttribute("tacGiaList", tacGiaDAO.getAll());
        req.getRequestDispatcher("/WEB-INF/views/tacgia/list.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        try {
            String tenTacGia = req.getParameter("tenTacGia");
            ValidationUtil.requireNotBlank(tenTacGia, "tên tác giả");
            ValidationUtil.validateMaxLength(tenTacGia, "tên tác giả", 100);

            TacGia tg = new TacGia();
            tg.setTenTacGia(tenTacGia.trim());
            String quocTich = req.getParameter("quocTich");
            ValidationUtil.validateMaxLength(quocTich, "quốc tịch", 50);
            tg.setQuocTich(quocTich != null ? quocTich.trim() : null);

            if ("update".equals(action)) {
                tg.setMaTg(ValidationUtil.parseIntSafe(req.getParameter("maTg"), "mã tác giả"));
                if (tacGiaDAO.existsDuplicate(tg.getTenTacGia(), tg.getQuocTich(), tg.getMaTg())) {
                    throw new IllegalArgumentException("Tác giả với tên và quốc tịch này đã tồn tại.");
                }
                tacGiaDAO.update(tg);
                req.getSession().setAttribute("flashSuccess", "Cập nhật tác giả thành công.");
            } else {
                if (tacGiaDAO.existsDuplicate(tg.getTenTacGia(), tg.getQuocTich(), null)) {
                    throw new IllegalArgumentException("Tác giả với tên và quốc tịch này đã tồn tại.");
                }
                tacGiaDAO.insert(tg);
                req.getSession().setAttribute("flashSuccess", "Thêm tác giả thành công.");
            }
        } catch (Exception e) {
            req.getSession().setAttribute("flashError", "Lỗi lưu tác giả: " + ExceptionUtil.friendlyMessage(e));
        }
        resp.sendRedirect(req.getContextPath() + "/tacgia");
    }
}

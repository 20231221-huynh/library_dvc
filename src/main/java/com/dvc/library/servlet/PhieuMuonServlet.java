package com.dvc.library.servlet;

import com.dvc.library.dao.DocGiaDAO;
import com.dvc.library.dao.PhieuMuonDAO;
import com.dvc.library.dao.SachDAO;
import com.dvc.library.model.ChiTietPhieuMuon;
import com.dvc.library.model.PhieuMuon;
import com.dvc.library.model.TaiKhoan;
import com.dvc.library.util.Constants;
import com.dvc.library.util.ExceptionUtil;
import com.dvc.library.util.ValidationUtil;

import jakarta.inject.Inject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/phieumuon")
public class PhieuMuonServlet extends HttpServlet {

    @Inject
    private PhieuMuonDAO phieuMuonDAO;
    @Inject
    private DocGiaDAO docGiaDAO;
    @Inject
    private SachDAO sachDAO;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) action = "list";

        phieuMuonDAO.capNhatTrangThaiQuaHan();

        switch (action) {
            case "add":
                req.setAttribute("docGiaList", docGiaDAO.getAll());
                req.setAttribute("sachList", sachDAO.getAll());
                
                LocalDate ngayMuonMacDinh = LocalDate.now();
                req.setAttribute("ngayMuonMacDinh", ngayMuonMacDinh);
                req.setAttribute("ngayHenTraMacDinh", ngayMuonMacDinh.plusDays(Constants.SO_NGAY_MUON_MAC_DINH));
                req.setAttribute("soNgayMuonMacDinh", Constants.SO_NGAY_MUON_MAC_DINH);
                req.getRequestDispatcher("/WEB-INF/views/phieumuon/form.jsp").forward(req, resp);
                break;
            case "detail":
                PhieuMuon pmDetail = phieuMuonDAO.getById(Integer.parseInt(req.getParameter("id")));
                req.setAttribute("phieuMuon", pmDetail);
                if (pmDetail != null) {
                    boolean conHieuLuc = Constants.TT_DANG_MUON.equals(pmDetail.getTrangThai())
                            || Constants.TT_QUA_HAN.equals(pmDetail.getTrangThai());
                    boolean coDongDaTra = false;
                    if (pmDetail.getChiTietList() != null) {
                        for (ChiTietPhieuMuon ct : pmDetail.getChiTietList()) {
                            if (ct.getDaTra() == 1) { coDongDaTra = true; break; }
                        }
                    }
                    // Gia han: chi khi phieu con hieu luc (chua tra/chua huy) va chua vuot so lan gia han toi da.
                    req.setAttribute("coTheGiaHan",
                            conHieuLuc && pmDetail.getSoLanGiaHan() < Constants.SO_LAN_GIA_HAN_TOI_DA);
                    req.setAttribute("soLanGiaHanConLai",
                            Constants.SO_LAN_GIA_HAN_TOI_DA - pmDetail.getSoLanGiaHan());
                    // Huy toan bo: chi khi phieu con hieu luc VA chua co dong sach nao duoc tra.
                    req.setAttribute("coTheHuy", conHieuLuc && !coDongDaTra);
                }
                req.getRequestDispatcher("/WEB-INF/views/phieumuon/detail.jsp").forward(req, resp);
                break;
            case "traSach":
                int maCt = Integer.parseInt(req.getParameter("maCt"));
                int maPmForRedirect = Integer.parseInt(req.getParameter("maPm"));
                try {
                    phieuMuonDAO.traSach(maCt, LocalDate.now());
                    req.getSession().setAttribute("flashSuccess", "Ghi nhận trả sách thành công.");
                } catch (SQLException e) {
                    req.getSession().setAttribute("flashError", "Lỗi khi trả sách: " + ExceptionUtil.friendlyMessage(e));
                }
                resp.sendRedirect(req.getContextPath() + "/phieumuon?action=detail&id=" + maPmForRedirect);
                break;
            case "huy":
  
                int maPmHuy = Integer.parseInt(req.getParameter("maPm"));
                try {
                    phieuMuonDAO.huyPhieuMuon(maPmHuy);
                    req.getSession().setAttribute("flashSuccess", "Đã hủy phiếu mượn #" + maPmHuy + ".");
                } catch (SQLException e) {
                    req.getSession().setAttribute("flashError", "Không thể hủy phiếu mượn: " + ExceptionUtil.friendlyMessage(e));
                }
                resp.sendRedirect(req.getContextPath() + "/phieumuon?action=detail&id=" + maPmHuy);
                break;
            case "list":
            default:
                String filter = req.getParameter("filter");
                String keyword = req.getParameter("keyword");
                req.setAttribute("phieuMuonList", phieuMuonDAO.search(keyword, filter));
                req.setAttribute("filter", filter);
                req.setAttribute("keyword", keyword);
                req.getRequestDispatcher("/WEB-INF/views/phieumuon/list.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        TaiKhoan nhanVien = (TaiKhoan) session.getAttribute(Constants.SESSION_USER);

        String action = req.getParameter("action");
        if (action == null) action = "add";

        if ("giaHan".equals(action)) {
            xuLyGiaHan(req, resp, session);
            return;
        }

        try {
            PhieuMuon pm = new PhieuMuon();
            pm.setMaDg(Integer.parseInt(req.getParameter("maDg")));
            pm.setMaTk(nhanVien.getMaTk());

            LocalDate ngayMuon = ValidationUtil.parseLocalDateOptional(
                    req.getParameter("ngayMuon"), "Ngày mượn", LocalDate.now());
            LocalDate ngayHenTraMacDinh = ngayMuon.plusDays(Constants.SO_NGAY_MUON_MAC_DINH);
            LocalDate ngayHenTra = ValidationUtil.parseLocalDateOptional(
                    req.getParameter("ngayHenTra"), "Ngày hẹn trả", ngayHenTraMacDinh);

            if (ngayHenTra.isBefore(ngayMuon)) {
                throw new IllegalArgumentException("Ngày hẹn trả không được trước ngày mượn.");
            }

            pm.setNgayMuon(ngayMuon);
            pm.setNgayHenTra(ngayHenTra);
            String ghiChu = req.getParameter("ghiChu");
            ValidationUtil.validateMaxLength(ghiChu, "ghi chú", 255);
            pm.setGhiChu(ghiChu);

            String[] maSachArr = req.getParameterValues("maSach");
            String[] soLuongArr = req.getParameterValues("soLuongMuon");

            List<ChiTietPhieuMuon> chiTietList = new ArrayList<>();
            if (maSachArr != null) {
                for (int i = 0; i < maSachArr.length; i++) {
                    if (maSachArr[i] == null || maSachArr[i].isEmpty()) continue;
                    ChiTietPhieuMuon ct = new ChiTietPhieuMuon();
                    ct.setMaSach(Integer.parseInt(maSachArr[i]));
                    int sl = (soLuongArr != null && i < soLuongArr.length && !soLuongArr[i].isEmpty())
                            ? Integer.parseInt(soLuongArr[i]) : 1;
                   
                    if (sl < 1) {
                        throw new IllegalArgumentException("Số lượng mượn của mỗi đầu sách phải lớn hơn hoặc bằng 1.");
                    }
                    ct.setSoLuong(sl);
                    chiTietList.add(ct);
                }
            }

            if (chiTietList.isEmpty()) {
                session.setAttribute("flashError", "Vui lòng chọn ít nhất một đầu sách để lập phiếu mượn.");
                resp.sendRedirect(req.getContextPath() + "/phieumuon?action=add");
                return;
            }
            pm.setChiTietList(chiTietList);

            int maPmMoi = phieuMuonDAO.taoPhieuMuon(pm);
            session.setAttribute("flashSuccess", "Lập phiếu mượn thành công. Mã phiếu: " + maPmMoi);
            resp.sendRedirect(req.getContextPath() + "/phieumuon?action=detail&id=" + maPmMoi);
        } catch (SQLException e) {
            session.setAttribute("flashError", "Lỗi khi lập phiếu mượn: " + ExceptionUtil.friendlyMessage(e));
            resp.sendRedirect(req.getContextPath() + "/phieumuon?action=add");
        } catch (Exception e) {
            session.setAttribute("flashError", "Dữ liệu không hợp lệ: " + ExceptionUtil.friendlyMessage(e));
            resp.sendRedirect(req.getContextPath() + "/phieumuon?action=add");
        }
    }

    private void xuLyGiaHan(HttpServletRequest req, HttpServletResponse resp, HttpSession session) throws IOException {
        int maPm = ValidationUtil.parseIntSafe(req.getParameter("maPm"), "mã phiếu mượn");
        try {
            LocalDate ngayHenTraMoi = ValidationUtil.parseLocalDateOptional(
                    req.getParameter("ngayHenTraMoi"), "ngày hẹn trả mới", null);
            if (ngayHenTraMoi == null) {
                throw new IllegalArgumentException("Vui lòng chọn ngày hẹn trả mới.");
            }
            phieuMuonDAO.giaHanNgayHenTra(maPm, ngayHenTraMoi);
            session.setAttribute("flashSuccess", "Gia hạn ngày hẹn trả thành công.");
        } catch (SQLException e) {
            session.setAttribute("flashError", "Không thể gia hạn: " + ExceptionUtil.friendlyMessage(e));
        } catch (Exception e) {
            session.setAttribute("flashError", "Dữ liệu không hợp lệ: " + ExceptionUtil.friendlyMessage(e));
        }
        resp.sendRedirect(req.getContextPath() + "/phieumuon?action=detail&id=" + maPm);
    }
}

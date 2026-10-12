package com.dvc.library.servlet;

import com.dvc.library.dao.SachDAO;
import com.dvc.library.dao.TacGiaDAO;
import com.dvc.library.dao.TheLoaiDAO;
import com.dvc.library.model.Sach;
import com.dvc.library.util.ExceptionUtil;
import com.dvc.library.util.ValidationUtil; //kiểm tra dữ liệu đầu vào

import jakarta.inject.Inject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig; //dùng khi tải lên file(định dạng file)
import jakarta.servlet.annotation.WebServlet;//để đăng ký Servlet và URL của Servlet
import jakarta.servlet.http.*;
import java.io.File;//để làm việc với file/thư mục trên máy tính/server.
import java.io.IOException;//lỗi khi đọc/ghi dữ liệu
import java.io.InputStream;
import java.io.OutputStream; 
import java.math.BigDecimal; //xử lý số thập phân chính xác
import java.nio.file.Files;//cung cấp các phương thức để đọc, ghi, sao chép, tạo file/thư mục
import java.nio.file.StandardCopyOption; //chứa các tùy chọn khi sao chép file
import java.util.UUID;//tạo một chuỗi gần như duy nhất dùng khi tải lên file ảnh

@WebServlet("/sach")
@MultipartConfig(
        maxFileSize = 5 * 1024 * 1024, // tối đa 5MB / ảnh
        maxRequestSize = 10 * 1024 * 1024, // tối đa 10MB / request
        fileSizeThreshold = 1024 * 1024 //Ngưỡng để xử lý/lưu tạm file
)
public class SachServlet extends HttpServlet {

    private static final String THU_MUC_ANH = "/assets/uploads/sach";

    private static final java.util.Set<String> DUOI_ANH_HOP_LE
            = new java.util.HashSet<>(java.util.Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".webp"));

    @Inject
    private SachDAO sachDAO;
    @Inject
    private TheLoaiDAO theLoaiDAO;
    @Inject
    private TacGiaDAO tacGiaDAO;

    /*doGet()có nhiệm vụ nhận yêu cầu GET từ trình duyệt rồi xem action là gì 
    để quyết định hiển thị danh sách, thêm, sửa, xóa hay tìm kiếm sách*/
    @Override//
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) {
            action = "list";
        }

        switch (action) {
            case "add":
                req.setAttribute("theLoaiList", theLoaiDAO.getAll());
                req.setAttribute("tacGiaList", tacGiaDAO.getAll());
                req.setAttribute("namXbToiDa", java.time.Year.now().getValue() + 1);
                req.getRequestDispatcher("/WEB-INF/views/sach/form.jsp").forward(req, resp);//chuyển request hiện tại sang form.jsp để hiển thị form thêm sách
                break;//thoát khỏi switch
            case "edit":
                int idEdit = Integer.parseInt(req.getParameter("id")); //chuyển kiểu chuỗi sang int
                Sach sEdit = sachDAO.getById(idEdit);
                if (sEdit == null) {
                    resp.sendRedirect(req.getContextPath() + "/sach");
                    return; //Dừng luôn doGet() tại đây
                }
                req.setAttribute("sach", sEdit);
                req.setAttribute("theLoaiList", theLoaiDAO.getAll());
                req.setAttribute("tacGiaList", tacGiaDAO.getAll());
                req.setAttribute("namXbToiDa", java.time.Year.now().getValue() + 1);
                req.getRequestDispatcher("/WEB-INF/views/sach/form.jsp").forward(req, resp);
                break;
            case "delete":
                int idDel = Integer.parseInt(req.getParameter("id"));
                try {
                    Sach s = sachDAO.getById(idDel);
                    sachDAO.delete(idDel);
                    if (s != null) {
                        xoaFileAnh(s.getHinhAnh());
                    }
                    req.getSession().setAttribute("flashSuccess", "Xóa sách thành công.");
                } catch (RuntimeException e) {
                    req.getSession().setAttribute("flashError", e.getMessage());
                }
                resp.sendRedirect(req.getContextPath() + "/sach");
                break;
            case "search":
                String keyword = req.getParameter("keyword");
                String maTlStr = req.getParameter("maTl");
                String maTgStr = req.getParameter("maTg");
                Integer maTl = (maTlStr != null && !maTlStr.isEmpty()) ? Integer.parseInt(maTlStr) : null;
                Integer maTg = (maTgStr != null && !maTgStr.isEmpty()) ? Integer.parseInt(maTgStr) : null;
                req.setAttribute("sachList", sachDAO.search(keyword, maTl, maTg));
                req.setAttribute("keyword", keyword);
                req.setAttribute("theLoaiList", theLoaiDAO.getAll());
                req.setAttribute("tacGiaList", tacGiaDAO.getAll());
                req.getRequestDispatcher("/WEB-INF/views/sach/list.jsp").forward(req, resp);
                break;
            case "list":
            default:
                req.setAttribute("sachList", sachDAO.getAll());
                req.setAttribute("theLoaiList", theLoaiDAO.getAll());
                req.setAttribute("tacGiaList", tacGiaDAO.getAll());
                req.getRequestDispatcher("/WEB-INF/views/sach/list.jsp").forward(req, resp);
        }
    }

    /*doPost() dùng để nhận dữ liệu từ form thêm/sửa sách 
    rồi kiểm tra, xử lý và lưu vào database*/
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        try {
            Sach s = new Sach();

            String tenSach = req.getParameter("tenSach");
            ValidationUtil.requireNotBlank(tenSach, "tên sách"); //giá trị và tên trường để hiển thị thông báo
            ValidationUtil.validateMaxLength(tenSach, "tên sách", 200); //giá trị, tên trường và độ dài để hiển thị thông báo
            s.setTenSach(tenSach.trim()); //đưa vào obj sách

            s.setMaTg(ValidationUtil.parseIntSafe(req.getParameter("maTg"), "tác giả"));
            s.setMaTl(ValidationUtil.parseIntSafe(req.getParameter("maTl"), "thể loại"));
            String nhaXuatBan = req.getParameter("nhaXuatBan");
            ValidationUtil.validateMaxLength(nhaXuatBan, "nhà xuất bản", 150);
            s.setNhaXuatBan(nhaXuatBan);
            s.setNamXb(ValidationUtil.parseYearOptional(req.getParameter("namXb"), "năm xuất bản", 0)); //0 là năm mặc định để tránh lỗi
            s.setSoLuong(ValidationUtil.parseIntNonNegative(req.getParameter("soLuong"), "số lượng"));
            s.setDonGia(ValidationUtil.parseDecimalSafe(req.getParameter("donGia"), "đơn giá"));
            String moTaSach = req.getParameter("moTa");
            ValidationUtil.validateMaxLength(moTaSach, "mô tả", 500);
            s.setMoTa(moTaSach);
            s.setTrangThai(req.getParameter("trangThai") != null ? 1 : 0); //nếu tích chọn trên checkbox thì trạng thái là 1 (lưu hành) và ngược lại 0 (Ngừng)

            if ("update".equals(action)) {
                int maSach = ValidationUtil.parseIntSafe(req.getParameter("maSach"), "mã sách");
                if (sachDAO.existsDuplicate(s.getTenSach(), s.getMaTg(), maSach)) {
                    throw new IllegalArgumentException("Sách với tên và tác giả này đã tồn tại.");
                }
            } else {
                if (sachDAO.existsDuplicate(s.getTenSach(), s.getMaTg(), null)) {
                    throw new IllegalArgumentException("Sách với tên và tác giả này đã tồn tại.");
                }
            }

            // Xử lý file ảnh (nếu người dùng có chọn)
            Part filePart = req.getPart("hinhAnhFile"); //Part chứa tên file, kích thước, loại ảnh
            String tenFileMoi = luuFileAnhNeuCo(req, filePart);

            if ("update".equals(action)) {
                int maSach = ValidationUtil.parseIntSafe(req.getParameter("maSach"), "mã sách");
                s.setMaSach(maSach);
                if (tenFileMoi != null) {
                    // Có ảnh mới -> xóa ảnh cũ (nếu có) và ghi đè
                    Sach sachCu = sachDAO.getById(maSach);
                    s.setHinhAnh(tenFileMoi);
                    sachDAO.updateWithImage(s);
                    if (sachCu != null) {
                        xoaFileAnh(sachCu.getHinhAnh());
                    }
                } else {
                    // Không đổi ảnh -> giữ nguyên ảnh cũ
                    sachDAO.update(s);
                }
                req.getSession().setAttribute("flashSuccess", "Cập nhật sách thành công.");
            } else {
                s.setHinhAnh(tenFileMoi); // đưa ảnh vào sách mới có thể null nếu không chọn ảnh
                sachDAO.insert(s);
                req.getSession().setAttribute("flashSuccess", "Thêm sách mới thành công.");
            }
        } catch (Exception e) {
            req.getSession().setAttribute("flashError", "Lỗi lưu dữ liệu sách: " + ExceptionUtil.friendlyMessage(e));
        }
        resp.sendRedirect(req.getContextPath() + "/sach");
    }

     /* Lưu file ảnh upload (nếu có chọn file)
     * Trả về tên file đã lưu (duy nhất, sinh bởi UUID), hoặc null nếu không chọn ảnh
     */
    private String luuFileAnhNeuCo(HttpServletRequest req, Part filePart) throws IOException {
        if (filePart == null || filePart.getSize() <= 0) { //có part nhưng file có kích thước 0 byte
            return null;
        }

        String tenGoc = getFileName(filePart);
        if (tenGoc == null || tenGoc.trim().isEmpty()) {
            return null;
        }

        //Lấy đuôi file ảnh
        String duoiFile = tenGoc.substring(tenGoc.lastIndexOf('.')).toLowerCase();//Lấy từ dấu . đó cho đến hết chuỗi
        if (!DUOI_ANH_HOP_LE.contains(duoiFile)) { //nếu trả về true thì chạy bên trong if
            throw new IOException("Định dạng ảnh không hợp lệ. Chỉ chấp nhận: jpg, jpeg, png, gif, webp.");
        }

        //lấy đường dẫn thật
        String thuMucThuc = req.getServletContext().getRealPath(THU_MUC_ANH); //biến nó thành đường dẫn thật trên ổ đĩa
        if (thuMucThuc == null) {
            throw new IOException("Không xác định được thư mục lưu ảnh trên máy chủ.");
        }
        File thuMuc = new File(thuMucThuc);
        if (!thuMuc.exists()) { //Đường dẫn này tồn tại trên máy tính chưa?
            thuMuc.mkdirs();//Nếu thư mục chưa tồn tại thì sẽ tạo nó
        }

        String tenFileMoi = UUID.randomUUID().toString().replace("-", "") + duoiFile; //replace: thay dấu - thành để trống để tạo file ảnh thành chuỗi liền nhau
        File fileDich = new File(thuMuc, tenFileMoi);

        //đọc và ghi file ảnh đã upload
        try (InputStream in = filePart.getInputStream(); OutputStream out = Files.newOutputStream(fileDich.toPath())) {
            byte[] buffer = new byte[8192]; //Bộ đệm 8 KB
            int len; //để lưu: số byte thực tế vừa đọc
            while ((len = in.read(buffer)) != -1) { //Đọc dữ liệu vào buffer
                out.write(buffer, 0, len); //0: bắt đầu từ vị trí đầu trong buffer theo các lần lặp tới khi đọc hết
            }
        }

        // Copy thêm 1 bản vào src/main/webapp/assets/uploads/sach để ảnh không bị mất khi Clean and Build
        File thuMucSrc = timThuMucSrc(thuMucThuc);
        if (thuMucSrc != null) {
            try {
                if (!thuMucSrc.exists()) {
                    thuMucSrc.mkdirs();
                }
                //Tạo đường dẫn đến file đích
                Files.copy(fileDich.toPath(), new File(thuMucSrc, tenFileMoi).toPath(),
                        StandardCopyOption.REPLACE_EXISTING); //Nếu file đích đã tồn tại thì ghi đè nó
            } catch (IOException e) {
                System.err.println("Khong copy duoc anh vao thu muc src: " + e.getMessage());
            }
        }
        return tenFileMoi;
    }

    //
    private File timThuMucSrc(String thuMucChay) {
        if (thuMucChay == null) {
            return null;
        }
        File f = new File(thuMucChay).getAbsoluteFile();//đảm bảo đường dẫn tuyệt đối
        while (f != null) { //đi từ thư mục hiện tại lên thư mục cha
            if ("target".equals(f.getName()) && f.getParentFile() != null) {
                File webapp = new File(f.getParentFile(), "src" + File.separator + "main" + File.separator + "webapp");
                if (webapp.isDirectory()) {
                    return new File(webapp, "assets" + File.separator + "uploads" + File.separator + "sach");
                }
                return null;
            }
            f = f.getParentFile();
        }
        return null;
    }

    /**
     * Xoa file anh vat ly (dung khi xoa sach hoac thay anh moi). Bo qua neu
     * khong ton tai.
     */
    private void xoaFileAnh(String tenFile) {
        if (tenFile == null || tenFile.trim().isEmpty()) {
            return;
        }
        try {
            String thuMucThuc = getServletContext().getRealPath(THU_MUC_ANH);
            if (thuMucThuc == null) {
                return;
            }
            File f = new File(thuMucThuc, tenFile);
            if (f.exists()) {
                f.delete();
            }

            // Xoa ca ban luu trong thu muc src (neu co) de khong bi thua file
            File thuMucSrc = timThuMucSrc(thuMucThuc);
            if (thuMucSrc != null) {
                File fSrc = new File(thuMucSrc, tenFile);
                if (fSrc.exists()) {
                    fSrc.delete();
                }
            }
        } catch (Exception ignored) {
            // Khong lam gian doan luong xu ly chinh neu xoa file that bai
        }
    }

    private String getFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        if (contentDisp == null) {
            return null;
        }
        for (String token : contentDisp.split(";")) {
            token = token.trim();
            if (token.startsWith("filename")) {
                String name = token.substring(token.indexOf('=') + 1).trim().replace("\"", "");
                return name.isEmpty() ? null : name;
            }
        }
        return null;
    }
}

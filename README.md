Thư viện DVC – Hệ thống quản lý thư viện

Ứng dụng web quản lý thư viện xây dựng bằng Java Servlet/JSP theo mô hình MVC (Model – DAO – Servlet – JSP), dùng MySQL với JDBC thuần và Maven để build. Đồ án môn Công nghệ Java.
---------------
Chức năng chính
---------------
Đăng nhập / đăng xuất, phân quyền Quản trị viên (ADMIN) và Thủ thư (THUTHU) bằng Filter
Quản lý sách: thêm, sửa, xóa, tìm kiếm theo tên, thể loại, tác giả; tải ảnh bìa lên khi thêm/sửa
Quản lý thể loại, tác giả
Quản lý độc giả: thêm, sửa, xóa, tìm kiếm, danh sách đen
Phiếu mượn – trả sách: một phiếu nhiều đầu sách, trả sách, gia hạn, hủy phiếu, tự động tính tiền phạt trả trễ
Tự động cập nhật trạng thái "Quá hạn" cho phiếu mượn quá ngày hẹn trả
Thống kê: top sách được mượn nhiều, top độc giả mượn nhiều, số liệu tổng quan
Quản lý tài khoản nhân viên (chỉ ADMIN)
Đổi mật khẩu, trang lỗi 403 / 404 / 500
---------
Công nghệ
---------
Lớp	                    | Công nghệ
------------------------------------------------------------
Ngôn ngữ	            | Java 11
Web	                    | Jakarta Servlet 6.0, JSP, JSTL 3.0
Giao diện	            | Bootstrap 5, CSS
Dependency Injection	| CDI (Weld 5.1)
Cơ sở dữ liệu	        | MySQL 8, JDBC thuần
Build	                | Maven 3.6+
Server	                | Apache Tomcat 10.1.x
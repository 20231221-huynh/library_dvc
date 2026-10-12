<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Quản lý Tài khoản - Thư Viện DVC" scope="request"/>
<%@ include file="../common/header.jsp" %>

<h4 class="mb-3"><i class="bi bi-person-gear"></i> Quản lý Tài khoản Nhân viên</h4>

<div class="row g-3">
  <div class="col-md-4">
    <div class="card">
      <div class="card-body">
        <h6>${empty taiKhoan ? 'Thêm tài khoản mới' : 'Sửa tài khoản'}</h6>
        <form method="post" action="${pageContext.request.contextPath}/taikhoan">
          <input type="hidden" name="action" value="${empty taiKhoan ? 'insert' : 'update'}">
          <c:if test="${not empty taiKhoan}">
            <input type="hidden" name="maTk" value="${taiKhoan.maTk}">
          </c:if>
          <c:if test="${empty taiKhoan}">
            <div class="mb-2">
              <label class="form-label">Tên đăng nhập <span class="text-danger">*</span></label>
              <input type="text" name="tenDangNhap" class="form-control" minlength="4" maxlength="50" required>
            </div>
            <div class="mb-2">
              <label class="form-label">Mật khẩu <span class="text-danger">*</span></label>
              <input type="password" name="matKhau" class="form-control" required minlength="6">
            </div>
          </c:if>
          <div class="mb-2">
            <label class="form-label">Họ tên <span class="text-danger">*</span></label>
            <input type="text" name="hoTen" class="form-control" maxlength="100" required value="${taiKhoan.hoTen}">
          </div>
          <div class="mb-2">
            <label class="form-label">Vai trò</label>
            <select name="vaiTro" class="form-select">
              <option value="THUTHU" ${taiKhoan.vaiTro == 'THUTHU' ? 'selected' : ''}>Thủ thư</option>
              <option value="ADMIN" ${taiKhoan.vaiTro == 'ADMIN' ? 'selected' : ''}>Quản trị viên</option>
            </select>
          </div>
          <c:if test="${not empty taiKhoan}">
            <div class="form-check mb-2">
              <input class="form-check-input" type="checkbox" name="trangThai" id="trangThai" ${taiKhoan.trangThai == 1 ? 'checked' : ''}>
              <label class="form-check-label" for="trangThai">Tài khoản hoạt động</label>
            </div>
          </c:if>
          <button type="submit" class="btn btn-primary btn-sm"><i class="bi bi-save"></i> Lưu</button>
          <c:if test="${not empty taiKhoan}">
            <a href="${pageContext.request.contextPath}/taikhoan" class="btn btn-outline-secondary btn-sm">Hủy</a>
          </c:if>
        </form>
      </div>
    </div>
  </div>
  <div class="col-md-8">
    <div class="card">
      <div class="card-body table-responsive">
        <table class="table table-hover align-middle">
          <thead><tr><th>#</th><th>Tên đăng nhập</th><th>Họ tên</th><th>Vai trò</th><th>Trạng thái</th><th class="text-center">Thao tác</th></tr></thead>
          <tbody>
            <c:forEach var="tk" items="${taiKhoanList}">
              <tr>
                <td>${tk.maTk}</td>
                <td>${tk.tenDangNhap}</td>
                <td>${tk.hoTen}</td>
                <td><span class="badge bg-info text-dark">${tk.vaiTro}</span></td>
                <td>
                  <c:choose>
                    <c:when test="${tk.trangThai == 1}"><span class="badge bg-success">Hoạt động</span></c:when>
                    <c:otherwise><span class="badge bg-secondary">Khóa</span></c:otherwise>
                  </c:choose>
                </td>
                <td class="text-center">
                  <a href="${pageContext.request.contextPath}/taikhoan?action=edit&id=${tk.maTk}" class="btn btn-sm btn-outline-warning" title="Sửa"><i class="bi bi-pencil"></i></a>
                  <a href="${pageContext.request.contextPath}/taikhoan?action=resetPassword&id=${tk.maTk}" class="btn btn-sm btn-outline-info" title="Đặt lại mật khẩu về mặc định (123456)"
                     onclick="return confirm('Đặt lại mật khẩu tài khoản ${tk.tenDangNhap} về mặc định (123456)?');"><i class="bi bi-key"></i></a>
                  <a href="${pageContext.request.contextPath}/taikhoan?action=delete&id=${tk.maTk}" class="btn btn-sm btn-outline-danger" title="Xóa"
                     onclick="return confirm('Xóa tài khoản này?');"><i class="bi bi-trash"></i></a>
                </td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</div>

<%@ include file="../common/footer.jsp" %>

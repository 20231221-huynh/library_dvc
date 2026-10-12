<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="${empty docGia ? 'Thêm độc giả' : 'Sửa độc giả'} - Thư Viện DVC" scope="request"/>
<%@ include file="../common/header.jsp" %>

<h4 class="mb-3"><i class="bi bi-person-plus"></i> ${empty docGia ? 'Thêm độc giả mới' : 'Sửa thông tin độc giả'}</h4>

<div class="card">
  <div class="card-body">
    <form method="post" action="${pageContext.request.contextPath}/docgia">
      <input type="hidden" name="action" value="${empty docGia ? 'insert' : 'update'}">
      <c:if test="${not empty docGia}">
        <input type="hidden" name="maDg" value="${docGia.maDg}">
      </c:if>

      <div class="row g-3">
        <div class="col-md-6">
          <label class="form-label">Họ tên <span class="text-danger">*</span></label>
          <input type="text" name="hoTen" class="form-control" maxlength="100" required value="${docGia.hoTen}">
        </div>
        <div class="col-md-3">
          <label class="form-label">Ngày sinh</label>
          <input type="date" name="ngaySinh" class="form-control" value="${docGia.ngaySinh}">
        </div>
        <div class="col-md-3">
          <label class="form-label">Giới tính</label>
          <select name="gioiTinh" class="form-select">
            <option value="Nam" ${docGia.gioiTinh == 'Nam' ? 'selected' : ''}>Nam</option>
            <option value="Nữ" ${docGia.gioiTinh == 'Nữ' ? 'selected' : ''}>Nữ</option>
            <option value="Khác" ${docGia.gioiTinh == 'Khác' ? 'selected' : ''}>Khác</option>
          </select>
        </div>
        <div class="col-md-6">
          <label class="form-label">Địa chỉ</label>
          <input type="text" name="diaChi" class="form-control" maxlength="255" value="${docGia.diaChi}">
        </div>
        <div class="col-md-3">
          <label class="form-label">Số điện thoại <span class="text-danger">*</span></label>
          <input type="text" name="sdt" class="form-control" pattern="0[0-9]{9,10}" title="Bắt đầu bằng số 0, đủ 10-11 chữ số" maxlength="15" required value="${docGia.sdt}">
        </div>
        <div class="col-md-3">
          <label class="form-label">Email <span class="text-danger">*</span></label>
          <input type="email" name="email" class="form-control" maxlength="100" required value="${docGia.email}">
        </div>
        <div class="col-md-12">
          <div class="form-check">
            <input class="form-check-input" type="checkbox" name="trangThai" id="trangThai"
                   ${empty docGia || docGia.trangThai == 1 ? 'checked' : ''}>
            <label class="form-check-label" for="trangThai">Tài khoản độc giả đang hoạt động</label>
          </div>
        </div>
      </div>

      <div class="mt-4">
        <button type="submit" class="btn btn-primary"><i class="bi bi-save"></i> Lưu</button>
        <a href="${pageContext.request.contextPath}/docgia" class="btn btn-outline-secondary">Hủy</a>
      </div>
    </form>
  </div>
</div>

<%@ include file="../common/footer.jsp" %>

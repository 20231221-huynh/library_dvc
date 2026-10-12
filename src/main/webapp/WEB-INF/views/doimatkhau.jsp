<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Đổi mật khẩu - Thư Viện DVC" scope="request"/>
<%@ include file="common/header.jsp" %>

<h4 class="mb-3"><i class="bi bi-key"></i> Đổi mật khẩu</h4>

<div class="row">
  <div class="col-md-5">
    <div class="card">
      <div class="card-body">
        <c:if test="${not empty errorMessage}">
          <div class="alert alert-danger py-2">${errorMessage}</div>
        </c:if>
        <form method="post" action="${pageContext.request.contextPath}/doimatkhau">
          <div class="mb-3">
            <label class="form-label">Mật khẩu hiện tại <span class="text-danger">*</span></label>
            <input type="password" name="matKhauCu" class="form-control" required autofocus>
          </div>
          <div class="mb-3">
            <label class="form-label">Mật khẩu mới <span class="text-danger">*</span></label>
            <input type="password" name="matKhauMoi" class="form-control" required minlength="6">
            <small class="text-muted">Tối thiểu 6 ký tự.</small>
          </div>
          <div class="mb-3">
            <label class="form-label">Xác nhận mật khẩu mới <span class="text-danger">*</span></label>
            <input type="password" name="xacNhanMatKhauMoi" class="form-control" required minlength="6">
          </div>
          <button type="submit" class="btn btn-primary"><i class="bi bi-save"></i> Đổi mật khẩu</button>
          <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-outline-secondary">Hủy</a>
        </form>
      </div>
    </div>
  </div>
</div>

<%@ include file="common/footer.jsp" %>

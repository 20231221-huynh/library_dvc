<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng nhập - Thư Viện DVC</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
<div class="login-wrapper">
  <div class="card login-card shadow-lg">
    <div class="card-body p-4">
      <div class="text-center mb-4">
        <i class="bi bi-book-half" style="font-size:2.5rem;color:#2c3e50;"></i>
        <h4 class="mt-2 fw-bold">THƯ VIỆN DVC</h4>
        <p class="text-muted small mb-0">Hệ thống quản lý thư viện</p>
      </div>
      <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger py-2">${errorMessage}</div>
      </c:if>
      <form method="post" action="${pageContext.request.contextPath}/login">
        <div class="mb-3">
          <label class="form-label">Tên đăng nhập</label>
          <input type="text" name="tenDangNhap" class="form-control" required autofocus>
        </div>
        <div class="mb-3">
          <label class="form-label">Mật khẩu</label>
          <input type="password" name="matKhau" class="form-control" required>
        </div>
        <button type="submit" class="btn btn-primary w-100">
          <i class="bi bi-box-arrow-in-right"></i> Đăng nhập
        </button>
      </form>
      <p class="text-center text-muted small mt-3 mb-0">
        Demo: admin / 123456 (Quản trị) &mdash; thuthu1 / 123456 (Thủ thư)
      </p>
    </div>
  </div>
</div>
</body>
</html>

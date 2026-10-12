<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${pageTitle}" default="Thư Viện DVC"/></title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/tom-select@2.3.1/dist/css/tom-select.bootstrap5.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <!-- Tom Select PHAI nap o day (trong <head>, truoc noi dung trang) chu KHONG duoc nap o footer.jsp:
         cac trang co doan <script> rieng goi new TomSelect(...) ngay sau noi dung form, ma doan script
         do lai chay TRUOC khi footer.jsp (nam cuoi trang) duoc include. Neu de thu vien nay o footer,
         luc trang goi "new TomSelect(...)" thi ham TomSelect chua ton tai -> loi "TomSelect is not
         defined" -> o chon van la <select> thuong, khong the go tim kiem duoc (dung loi nguoi dung gap phai). -->
    <script src="https://cdn.jsdelivr.net/npm/tom-select@2.3.1/dist/js/tom-select.complete.min.js"></script>
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-dark app-navbar">
  <div class="container-fluid">
    <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/dashboard">
      <i class="bi bi-book-half"></i> Thư Viện DVC
    </a>
    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navMain">
      <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navMain">
      <ul class="navbar-nav me-auto mb-2 mb-lg-0">
        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/dashboard"><i class="bi bi-speedometer2"></i> Trang chủ</a></li>
        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/sach"><i class="bi bi-journal-bookmark"></i> Sách</a></li>
        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/theloai"><i class="bi bi-tags"></i> Thể loại</a></li>
        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/tacgia"><i class="bi bi-person-lines-fill"></i> Tác giả</a></li>
        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/docgia"><i class="bi bi-people"></i> Độc giả</a></li>
        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/phieumuon"><i class="bi bi-arrow-left-right"></i> Mượn/Trả sách</a></li>
        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/thongke"><i class="bi bi-bar-chart"></i> Thống kê</a></li>
        <c:if test="${sessionScope.LOGGED_IN_USER.vaiTro == 'ADMIN'}">
          <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/taikhoan"><i class="bi bi-person-gear"></i> Tài khoản NV</a></li>
        </c:if>
      </ul>
      <ul class="navbar-nav">
        <li class="nav-item dropdown">
          <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown">
            <i class="bi bi-person-circle"></i> ${sessionScope.LOGGED_IN_USER.hoTen}
            <span class="badge bg-light text-dark ms-1">${sessionScope.LOGGED_IN_USER.vaiTro}</span>
          </a>
          <ul class="dropdown-menu dropdown-menu-end">
            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/doimatkhau"><i class="bi bi-key"></i> Đổi mật khẩu</a></li>
            <li><hr class="dropdown-divider"></li>
            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/logout"><i class="bi bi-box-arrow-right"></i> Đăng xuất</a></li>
          </ul>
        </li>
      </ul>
    </div>
  </div>
</nav>
<div class="container-fluid px-4 py-4">
<c:if test="${not empty sessionScope.flashSuccess}">
  <div class="alert alert-success alert-dismissible fade show" role="alert">
    ${sessionScope.flashSuccess}
    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
  </div>
  <c:remove var="flashSuccess" scope="session"/>
</c:if>
<c:if test="${not empty sessionScope.flashError}">
  <div class="alert alert-danger alert-dismissible fade show" role="alert">
    ${sessionScope.flashError}
    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
  </div>
  <c:remove var="flashError" scope="session"/>
</c:if>

<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Lỗi hệ thống - Thư Viện DVC</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
</head>
<body class="bg-light">
<div class="d-flex flex-column align-items-center justify-content-center text-center" style="min-height:100vh;">
  <i class="bi bi-exclamation-octagon" style="font-size:4rem;color:#e74a3b;"></i>
  <h2 class="mt-3">Đã có lỗi xảy ra</h2>
  <p class="text-muted" style="max-width:480px;">
    Hệ thống gặp sự cố khi xử lý yêu cầu của bạn. Vui lòng thử lại sau ít phút,
    hoặc liên hệ quản trị viên nếu lỗi vẫn tiếp tục xảy ra.
  </p>
  <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-primary mt-2">
    <i class="bi bi-house"></i> Về trang chủ
  </a>
</div>
</body>
</html>

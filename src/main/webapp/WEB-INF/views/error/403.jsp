<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Không có quyền truy cập" scope="request"/>
<%@ include file="../common/header.jsp" %>
<div class="text-center py-5">
  <i class="bi bi-shield-lock" style="font-size:4rem;color:#e74a3b;"></i>
  <h3 class="mt-3">403 - Không có quyền truy cập</h3>
  <p class="text-muted">${errorMessage}</p>
  <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-primary mt-2">Về trang chủ</a>
</div>
<%@ include file="../common/footer.jsp" %>

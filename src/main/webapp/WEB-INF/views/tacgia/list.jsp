<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Quản lý Tác giả - Thư Viện DVC" scope="request"/>
<%@ include file="../common/header.jsp" %>

<h4 class="mb-3"><i class="bi bi-person-lines-fill"></i> Quản lý Tác giả</h4>

<div class="row g-3">
  <div class="col-md-4">
    <div class="card">
      <div class="card-body">
        <h6>${empty tacGia ? 'Thêm tác giả mới' : 'Sửa tác giả'}</h6>
        <form method="post" action="${pageContext.request.contextPath}/tacgia">
          <input type="hidden" name="action" value="${empty tacGia ? 'insert' : 'update'}">
          <c:if test="${not empty tacGia}">
            <input type="hidden" name="maTg" value="${tacGia.maTg}">
          </c:if>
          <div class="mb-2">
            <label class="form-label">Tên tác giả <span class="text-danger">*</span></label>
            <input type="text" name="tenTacGia" class="form-control" maxlength="100" required value="${tacGia.tenTacGia}">
          </div>
          <div class="mb-2">
            <label class="form-label">Quốc tịch</label>
            <input type="text" name="quocTich" class="form-control" maxlength="50" value="${tacGia.quocTich}">
          </div>
          <button type="submit" class="btn btn-primary btn-sm"><i class="bi bi-save"></i> Lưu</button>
          <c:if test="${not empty tacGia}">
            <a href="${pageContext.request.contextPath}/tacgia" class="btn btn-outline-secondary btn-sm">Hủy</a>
          </c:if>
        </form>
      </div>
    </div>
  </div>
  <div class="col-md-8">
    <div class="card">
      <div class="card-body table-responsive">
        <table class="table table-hover align-middle">
          <thead><tr><th>#</th><th>Tên tác giả</th><th>Quốc tịch</th><th class="text-center">Thao tác</th></tr></thead>
          <tbody>
            <c:forEach var="tg" items="${tacGiaList}">
              <tr>
                <td>${tg.maTg}</td><td>${tg.tenTacGia}</td><td>${tg.quocTich}</td>
                <td class="text-center">
                  <a href="${pageContext.request.contextPath}/tacgia?action=edit&id=${tg.maTg}" class="btn btn-sm btn-outline-warning"><i class="bi bi-pencil"></i></a>
                  <a href="${pageContext.request.contextPath}/tacgia?action=delete&id=${tg.maTg}" class="btn btn-sm btn-outline-danger"
                     onclick="return confirm('Xóa tác giả này?');"><i class="bi bi-trash"></i></a>
                </td>
              </tr>
            </c:forEach>
            <c:if test="${empty tacGiaList}"><tr><td colspan="4" class="text-center text-muted py-3">Chưa có tác giả nào.</td></tr></c:if>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</div>

<%@ include file="../common/footer.jsp" %>

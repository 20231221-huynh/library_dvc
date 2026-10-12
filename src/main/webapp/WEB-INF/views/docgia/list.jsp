<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Quản lý Độc giả - Thư Viện DVC" scope="request"/>
<%@ include file="../common/header.jsp" %>

<div class="d-flex justify-content-between align-items-center mb-3">
  <h4><i class="bi bi-people"></i> Quản lý Độc giả</h4>
  <div>
    <a href="${pageContext.request.contextPath}/docgia?action=blacklist" class="btn btn-outline-danger">
      <i class="bi bi-slash-circle"></i> Danh sách đen
    </a>
    <a href="${pageContext.request.contextPath}/docgia?action=add" class="btn btn-primary"><i class="bi bi-plus-circle"></i> Thêm độc giả mới</a>
  </div>
</div>

<div class="card mb-3">
  <div class="card-body">
    <form method="get" action="${pageContext.request.contextPath}/docgia" class="row g-2" id="formTimKiemDocGia">
      <input type="hidden" name="action" value="search">
      <div class="col-md-10">
        <input type="text" id="inputKeywordDocGia" name="keyword" class="form-control" placeholder="Tìm theo tên hoặc số điện thoại..." value="${keyword}">
      </div>
    </form>
  </div>
</div>
<script>
  // Tim kiem TU DONG, khong can bam nut: go chu thi doi mot chut (debounce) roi tu nop form.
  (function() {
    var form = document.getElementById('formTimKiemDocGia');
    var kw = document.getElementById('inputKeywordDocGia');
    var timer;
    kw.addEventListener('input', function() {
      clearTimeout(timer);
      timer = setTimeout(function() { form.submit(); }, 500);
    });
    if (kw.value) {
      var v = kw.value;
      kw.focus();
      kw.value = '';
      kw.value = v;
    }
  })();
</script>

<div class="card">
  <div class="card-body table-responsive">
    <table class="table table-hover align-middle">
      <thead>
        <tr><th>#</th><th>Họ tên</th><th>Ngày sinh</th><th>Giới tính</th><th>SĐT</th><th>Email</th><th>Trạng thái</th><th class="text-center">Thao tác</th></tr>
      </thead>
      <tbody>
        <c:forEach var="dg" items="${docGiaList}">
          <tr>
            <td>${dg.maDg}</td>
            <td>${dg.hoTen}</td>
            <td>${dg.ngaySinh}</td>
            <td>${dg.gioiTinh}</td>
            <td>${dg.sdt}</td>
            <td>${dg.email}</td>
            <td>
              <c:choose>
                <c:when test="${dg.trangThai == 1}"><span class="badge bg-success">Hoạt động</span></c:when>
                <c:otherwise><span class="badge bg-secondary">Khóa</span></c:otherwise>
              </c:choose>
            </td>
            <td class="text-center">
              <a href="${pageContext.request.contextPath}/docgia?action=edit&id=${dg.maDg}" class="btn btn-sm btn-outline-warning"><i class="bi bi-pencil"></i></a>
              <a href="${pageContext.request.contextPath}/docgia?action=delete&id=${dg.maDg}" class="btn btn-sm btn-outline-danger"
                 onclick="return confirm('Xóa độc giả này?');"><i class="bi bi-trash"></i></a>
            </td>
          </tr>
        </c:forEach>
        <c:if test="${empty docGiaList}"><tr><td colspan="8" class="text-center text-muted py-4">Không có độc giả nào.</td></tr></c:if>
      </tbody>
    </table>
  </div>
</div>

<%@ include file="../common/footer.jsp" %>

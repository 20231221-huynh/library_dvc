<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Phiếu mượn - Thư Viện DVC" scope="request"/>
<%@ include file="../common/header.jsp" %>

<div class="d-flex justify-content-between align-items-center mb-3">
  <h4><i class="bi bi-arrow-left-right"></i> Danh sách Phiếu mượn</h4>
  <a href="${pageContext.request.contextPath}/phieumuon?action=add" class="btn btn-primary"><i class="bi bi-plus-circle"></i> Lập phiếu mượn mới</a>
</div>

<div class="card mb-3">
  <div class="card-body">
    <form method="get" action="${pageContext.request.contextPath}/phieumuon" class="row g-2 align-items-center" id="formTimKiemPhieuMuon">
      <c:if test="${not empty filter}"><input type="hidden" name="filter" value="${filter}"></c:if>
      <div class="col-md-6">
        <input type="text" id="inputKeywordPhieuMuon" name="keyword" class="form-control" placeholder="Tìm theo tên độc giả..." value="${keyword}">
      </div>
      <div class="col-md-6">
        <c:url var="urlAll" value="/phieumuon"><c:if test="${not empty keyword}"><c:param name="keyword" value="${keyword}"/></c:if></c:url>
        <c:url var="urlDangMuon" value="/phieumuon"><c:param name="filter" value="DANG_MUON"/><c:if test="${not empty keyword}"><c:param name="keyword" value="${keyword}"/></c:if></c:url>
        <c:url var="urlQuaHan" value="/phieumuon"><c:param name="filter" value="QUA_HAN"/><c:if test="${not empty keyword}"><c:param name="keyword" value="${keyword}"/></c:if></c:url>
        <c:url var="urlDaTra" value="/phieumuon"><c:param name="filter" value="DA_TRA"/><c:if test="${not empty keyword}"><c:param name="keyword" value="${keyword}"/></c:if></c:url>
        <c:url var="urlDaHuy" value="/phieumuon"><c:param name="filter" value="DA_HUY"/><c:if test="${not empty keyword}"><c:param name="keyword" value="${keyword}"/></c:if></c:url>
        <div class="btn-group">
          <a class="btn btn-outline-secondary ${empty filter ? 'active' : ''}" href="${urlAll}">Tất cả</a>
          <a class="btn btn-outline-primary ${filter == 'DANG_MUON' ? 'active' : ''}" href="${urlDangMuon}">Đang mượn</a>
          <a class="btn btn-outline-danger ${filter == 'QUA_HAN' ? 'active' : ''}" href="${urlQuaHan}">Quá hạn</a>
          <a class="btn btn-outline-success ${filter == 'DA_TRA' ? 'active' : ''}" href="${urlDaTra}">Đã trả</a>
          <a class="btn btn-outline-dark ${filter == 'DA_HUY' ? 'active' : ''}" href="${urlDaHuy}">Đã hủy</a>
        </div>
      </div>
      <c:if test="${not empty keyword}">
        <c:url var="urlClearKeyword" value="/phieumuon"><c:if test="${not empty filter}"><c:param name="filter" value="${filter}"/></c:if></c:url>
        <div class="col-12">
          <small class="text-muted">
            Đang tìm: <strong>"${keyword}"</strong>
            &nbsp;·&nbsp;
            <a href="${urlClearKeyword}">Xóa tìm kiếm</a>
          </small>
        </div>
      </c:if>
    </form>
  </div>
</div>
<script>
  // Tim kiem TU DONG, khong can bam nut: go chu thi doi mot chut (debounce) roi tu nop form.
  // Cac nut loc trang thai (Dang muon/Qua han/...) da la lien ket <a> nen bam vao la chuyen ngay,
  // khong can sua them.
  (function() {
    var form = document.getElementById('formTimKiemPhieuMuon');
    var kw = document.getElementById('inputKeywordPhieuMuon');
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
        <tr><th>Mã PM</th><th>Độc giả</th><th>Nhân viên lập</th><th>Ngày mượn</th><th>Hạn trả</th><th>Trạng thái</th><th class="text-center">Thao tác</th></tr>
      </thead>
      <tbody>
        <c:forEach var="pm" items="${phieuMuonList}">
          <tr>
            <td>#${pm.maPm}</td>
            <td>${pm.tenDocGia}</td>
            <td>${pm.tenNhanVien}</td>
            <td>${pm.ngayMuon}</td>
            <td>${pm.ngayHenTra}</td>
            <td>
              <c:choose>
                <c:when test="${pm.trangThai == 'DANG_MUON'}"><span class="badge bg-primary">Đang mượn</span></c:when>
                <c:when test="${pm.trangThai == 'QUA_HAN'}"><span class="badge bg-danger">Quá hạn</span></c:when>
                <c:when test="${pm.trangThai == 'DA_HUY'}"><span class="badge bg-secondary">Đã hủy</span></c:when>
                <c:otherwise><span class="badge bg-success">Đã trả</span></c:otherwise>
              </c:choose>
            </td>
            <td class="text-center">
              <a href="${pageContext.request.contextPath}/phieumuon?action=detail&id=${pm.maPm}" class="btn btn-sm btn-outline-primary"><i class="bi bi-eye"></i> Chi tiết</a>
            </td>
          </tr>
        </c:forEach>
        <c:if test="${empty phieuMuonList}">
          <tr>
            <td colspan="7" class="text-center text-muted py-4">
              <c:choose>
                <c:when test="${not empty keyword}">Không tìm thấy phiếu mượn nào của độc giả có tên chứa "${keyword}".</c:when>
                <c:otherwise>Không có phiếu mượn nào.</c:otherwise>
              </c:choose>
            </td>
          </tr>
        </c:if>
      </tbody>
    </table>
  </div>
</div>

<%@ include file="../common/footer.jsp" %>

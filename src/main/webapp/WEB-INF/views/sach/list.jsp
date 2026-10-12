<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Quản lý Sách - Thư Viện DVC" scope="request"/>
<%@ include file="../common/header.jsp" %>

<div class="d-flex justify-content-between align-items-center mb-3">
  <h4><i class="bi bi-journal-bookmark"></i> Quản lý Sách</h4>
  <a href="${pageContext.request.contextPath}/sach?action=add" class="btn btn-primary"><i class="bi bi-plus-circle"></i> Thêm sách mới</a>
</div>

<div class="card mb-3">
  <div class="card-body">
    <form method="get" action="${pageContext.request.contextPath}/sach" class="row g-2" id="formTimKiemSach">
      <input type="hidden" name="action" value="search">
      <div class="col-md-6">
        <input type="text" id="inputKeywordSach" name="keyword" class="form-control" placeholder="Tìm theo tên sách..." value="${keyword}">
      </div>
      <div class="col-md-3">
        <select name="maTl" id="selectMaTl" class="form-select">
          <option value="">-- Tất cả thể loại --</option>
          <c:forEach var="tl" items="${theLoaiList}">
            <option value="${tl.maTl}" ${param.maTl == tl.maTl ? 'selected' : ''}>${tl.tenTheLoai}</option>
          </c:forEach>
        </select>
      </div>
      <div class="col-md-3">
        <select name="maTg" id="selectMaTg" class="form-select">
          <option value="">-- Tất cả tác giả --</option>
          <c:forEach var="tg" items="${tacGiaList}">
            <option value="${tg.maTg}" ${param.maTg == tg.maTg ? 'selected' : ''}>${tg.tenTacGia}</option>
          </c:forEach>
        </select>
      </div>
    </form>
  </div>
</div>
<script>
  // Tim kiem/loc TU DONG, khong can bam nut: go chu thi doi mot chut (debounce) roi tu nop form;
  // doi lua chon the loai/tac gia thi nop form NGAY LAP TUC.
  (function() {
    var form = document.getElementById('formTimKiemSach');
    var kw = document.getElementById('inputKeywordSach');
    var timer;
    kw.addEventListener('input', function() {
      clearTimeout(timer);
      timer = setTimeout(function() { form.submit(); }, 500);
    });
    document.getElementById('selectMaTl').addEventListener('change', function() { form.submit(); });
    document.getElementById('selectMaTg').addEventListener('change', function() { form.submit(); });
    // Dua con tro ve cuoi o tim kiem sau khi trang tai lai, de go tiep khong bi mat vi tri
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
        <tr>
          <th>Ảnh</th><th>#</th><th>Tên sách</th><th>Tác giả</th><th>Thể loại</th><th>NXB</th><th>Năm XB</th>
          <th class="text-center">SL / Còn</th><th class="text-end">Đơn giá</th><th>Trạng thái</th><th class="text-center">Thao tác</th>
        </tr>
      </thead>
      <tbody>
        <c:forEach var="s" items="${sachList}">
          <tr>
            <td>
              <c:choose>
                <c:when test="${not empty s.hinhAnh}">
                  <img src="${pageContext.request.contextPath}/assets/uploads/sach/${s.hinhAnh}" alt="${s.tenSach}" class="book-thumb">
                </c:when>
                <c:otherwise>
                  <div class="book-thumb book-thumb-placeholder"><i class="bi bi-book"></i></div>
                </c:otherwise>
              </c:choose>
            </td>
            <td>${s.maSach}</td>
            <td>${s.tenSach}</td>
            <td>${s.tenTacGia}</td>
            <td>${s.tenTheLoai}</td>
            <td>${s.nhaXuatBan}</td>
            <td>${s.namXb}</td>
            <td class="text-center">${s.soLuong} / ${s.soLuongCon}</td>
            <td class="text-end"><fmt:formatNumber value="${s.donGia}" type="number"/></td>
            <td>
              <c:choose>
                <c:when test="${s.trangThai == 1}"><span class="badge bg-success">Lưu hành</span></c:when>
                <c:otherwise><span class="badge bg-secondary">Ngừng</span></c:otherwise>
              </c:choose>
            </td>
            <td class="text-center">
              <a href="${pageContext.request.contextPath}/sach?action=edit&id=${s.maSach}" class="btn btn-sm btn-outline-warning"><i class="bi bi-pencil"></i></a>
              <a href="${pageContext.request.contextPath}/sach?action=delete&id=${s.maSach}" class="btn btn-sm btn-outline-danger"
                 onclick="return confirm('Bạn có chắc muốn xóa sách này?');"><i class="bi bi-trash"></i></a>
            </td>
          </tr>
        </c:forEach>
        <c:if test="${empty sachList}">
          <tr><td colspan="11" class="text-center text-muted py-4">Không có dữ liệu sách nào.</td></tr>
        </c:if>
      </tbody>
    </table>
  </div>
</div>

<%@ include file="../common/footer.jsp" %>

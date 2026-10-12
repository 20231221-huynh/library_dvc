<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="${empty sach ? 'Thêm sách mới' : 'Sửa thông tin sách'} - Thư Viện DVC" scope="request"/>
<%@ include file="../common/header.jsp" %>

<h4 class="mb-3">
  <i class="bi bi-journal-plus"></i> ${empty sach ? 'Thêm sách mới' : 'Sửa thông tin sách'}
</h4>

<div class="card">
  <div class="card-body">
    <form method="post" action="${pageContext.request.contextPath}/sach" enctype="multipart/form-data">
      <c:choose>
        <c:when test="${not empty sach}">
          <input type="hidden" name="action" value="update">
          <input type="hidden" name="maSach" value="${sach.maSach}">
        </c:when>
        <c:otherwise>
          <input type="hidden" name="action" value="insert">
        </c:otherwise>
      </c:choose>

      <div class="row g-3">
        <div class="col-md-3 text-center">
          <label class="form-label d-block">Ảnh bìa sách</label>
          <c:choose>
            <c:when test="${not empty sach.hinhAnh}">
              <img id="imgPreview" src="${pageContext.request.contextPath}/assets/uploads/sach/${sach.hinhAnh}" class="book-cover-preview mb-2">
            </c:when>
            <c:otherwise>
              <div id="imgPreviewPlaceholder" class="book-cover-preview book-cover-preview-placeholder mb-2 mx-auto"><i class="bi bi-image"></i></div>
              <img id="imgPreview" src="" class="book-cover-preview mb-2 d-none">
            </c:otherwise>
          </c:choose>
          <input type="file" name="hinhAnhFile" id="hinhAnhFile" class="form-control form-control-sm" accept="image/png,image/jpeg,image/gif,image/webp">
          <small class="text-muted d-block mt-1">Định dạng: JPG, PNG, GIF, WEBP (tối đa 5MB)</small>
        </div>

        <div class="col-md-9">
          <div class="row g-3">
            <div class="col-md-8">
              <label class="form-label">Tên sách <span class="text-danger">*</span></label>
              <input type="text" name="tenSach" class="form-control" maxlength="200" required value="${sach.tenSach}">
            </div>
            <div class="col-md-4">
              <label class="form-label">Năm xuất bản</label>
              <input type="number" name="namXb" class="form-control" min="1450" max="${namXbToiDa}" value="${sach.namXb}">
            </div>

            <div class="col-md-6">
              <label class="form-label">Tác giả <span class="text-danger">*</span></label>
              <select name="maTg" id="selMaTg" class="form-select" required>
                <option value="">-- Chọn tác giả --</option>
                <c:forEach var="tg" items="${tacGiaList}">
                  <option value="${tg.maTg}" ${sach.maTg == tg.maTg ? 'selected' : ''}>${tg.tenTacGia}</option>
                </c:forEach>
              </select>
            </div>
            <div class="col-md-6">
              <label class="form-label">Thể loại <span class="text-danger">*</span></label>
              <select name="maTl" id="selMaTl" class="form-select" required>
                <option value="">-- Chọn thể loại --</option>
                <c:forEach var="tl" items="${theLoaiList}">
                  <option value="${tl.maTl}" ${sach.maTl == tl.maTl ? 'selected' : ''}>${tl.tenTheLoai}</option>
                </c:forEach>
              </select>
            </div>

            <div class="col-md-6">
              <label class="form-label">Nhà xuất bản</label>
              <input type="text" name="nhaXuatBan" class="form-control" maxlength="150" value="${sach.nhaXuatBan}">
            </div>
            <div class="col-md-3">
              <label class="form-label">Số lượng <span class="text-danger">*</span></label>
              <input type="number" name="soLuong" class="form-control" min="0" required value="${empty sach ? 1 : sach.soLuong}">
              <c:if test="${not empty sach}">
                <small class="text-muted">Số lượng còn: ${sach.soLuongCon}</small>
              </c:if>
            </div>
            <div class="col-md-3">
              <label class="form-label">Đơn giá (VND)</label>
              <input type="number" name="donGia" class="form-control" min="0" step="1000" value="${sach.donGia}">
            </div>

            <div class="col-md-12">
              <label class="form-label">Mô tả</label>
              <textarea name="moTa" class="form-control" rows="3" maxlength="500">${sach.moTa}</textarea>
            </div>

            <div class="col-md-12">
              <div class="form-check">
                <input class="form-check-input" type="checkbox" name="trangThai" id="trangThai"
                       ${empty sach || sach.trangThai == 1 ? 'checked' : ''}>
                <label class="form-check-label" for="trangThai">Đang lưu hành (cho phép mượn)</label>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="mt-4">
        <button type="submit" class="btn btn-primary"><i class="bi bi-save"></i> Lưu</button>
        <a href="${pageContext.request.contextPath}/sach" class="btn btn-outline-secondary">Hủy</a>
      </div>
    </form>
  </div>
</div>

<script>
document.getElementById('hinhAnhFile').addEventListener('change', function (e) {
  var file = e.target.files[0];
  if (!file) return;
  var reader = new FileReader();
  var img = document.getElementById('imgPreview');
  var placeholder = document.getElementById('imgPreviewPlaceholder');
  reader.onload = function (evt) {
    img.src = evt.target.result;
    img.classList.remove('d-none');
    if (placeholder) placeholder.classList.add('d-none');
  };
  reader.readAsDataURL(file);
});

// Bien 2 o chon Tac gia / The loai thanh combobox co the go de tim kiem (huu ich khi danh sach dai)
new TomSelect('#selMaTg', {
  create: false,
  allowEmptyOption: true,
  placeholder: '-- Chọn tác giả --',
  sortField: { field: 'text', direction: 'asc' }
});
new TomSelect('#selMaTl', {
  create: false,
  allowEmptyOption: true,
  placeholder: '-- Chọn thể loại --',
  sortField: { field: 'text', direction: 'asc' }
});
</script>

<%@ include file="../common/footer.jsp" %>

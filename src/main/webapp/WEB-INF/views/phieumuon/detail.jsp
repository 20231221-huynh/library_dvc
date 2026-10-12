<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Chi tiết phiếu mượn - Thư Viện DVC" scope="request"/>
<%@ include file="../common/header.jsp" %>

<h4 class="mb-3"><i class="bi bi-file-earmark-text"></i> Chi tiết Phiếu mượn #${phieuMuon.maPm}</h4>

<div class="row g-3 mb-3">
  <div class="col-md-3"><strong>Độc giả:</strong> ${phieuMuon.tenDocGia}</div>
  <div class="col-md-3"><strong>Nhân viên lập:</strong> ${phieuMuon.tenNhanVien}</div>
  <div class="col-md-2"><strong>Ngày mượn:</strong> ${phieuMuon.ngayMuon}</div>
  <div class="col-md-2"><strong>Hạn trả:</strong> ${phieuMuon.ngayHenTra}</div>
  <div class="col-md-2">
    <strong>Trạng thái:</strong>
    <c:choose>
      <c:when test="${phieuMuon.trangThai == 'DANG_MUON'}"><span class="badge bg-primary">Đang mượn</span></c:when>
      <c:when test="${phieuMuon.trangThai == 'QUA_HAN'}"><span class="badge bg-danger">Quá hạn</span></c:when>
      <c:when test="${phieuMuon.trangThai == 'DA_HUY'}"><span class="badge bg-secondary">Đã hủy</span></c:when>
      <c:otherwise><span class="badge bg-success">Đã trả</span></c:otherwise>
    </c:choose>
  </div>
</div>
<c:if test="${not empty phieuMuon.ghiChu}">
  <p><strong>Ghi chú:</strong> ${phieuMuon.ghiChu}</p>
</c:if>

<c:if test="${coTheGiaHan || coTheHuy}">
  <div class="card mb-3">
    <div class="card-body">
      <form method="post" action="${pageContext.request.contextPath}/phieumuon">
        <input type="hidden" name="action" value="giaHan">
        <input type="hidden" name="maPm" value="${phieuMuon.maPm}">
        <div class="d-flex flex-wrap gap-2 align-items-end">
          <c:if test="${coTheGiaHan}">
            <div>
              <label class="form-label mb-1">Gia hạn ngày hẹn trả</label>
              <input type="date" name="ngayHenTraMoi" class="form-control" min="${phieuMuon.ngayHenTra}" required>
            </div>
            <button type="submit" class="btn btn-outline-primary"><i class="bi bi-calendar-plus"></i> Gia hạn</button>
            <span class="text-muted small pb-2">Còn ${soLanGiaHanConLai} lần gia hạn.</span>
          </c:if>
          <c:if test="${coTheHuy}">
            <a href="${pageContext.request.contextPath}/phieumuon?action=huy&maPm=${phieuMuon.maPm}"
               class="btn btn-outline-danger ms-auto"
               onclick="return confirm('Hủy toàn bộ phiếu mượn #${phieuMuon.maPm}? Toàn bộ sách trong phiếu sẽ được hoàn lại kho. Hành động này chỉ dùng khi lập nhầm.');">
               <i class="bi bi-x-circle"></i> Hủy toàn bộ phiếu mượn
            </a>
          </c:if>
        </div>
      </form>
    </div>
  </div>
</c:if>

<div class="card">
  <div class="card-body table-responsive">
    <table class="table table-hover align-middle">
      <thead>
        <tr><th>Sách</th><th class="text-center">Số lượng</th><th>Ngày trả thực tế</th><th class="text-end">Tiền phạt</th><th class="text-center">Trạng thái</th><th class="text-center">Thao tác</th></tr>
      </thead>
      <tbody>
        <c:forEach var="ct" items="${phieuMuon.chiTietList}">
          <tr>
            <td>${ct.tenSach}</td>
            <td class="text-center">${ct.soLuong}</td>
            <td>${ct.ngayTraThucTe}</td>
            <td class="text-end">
              <c:choose>
                <c:when test="${ct.daTra == 1}">
                  <fmt:formatNumber value="${ct.tienPhat}" type="number"/> đ
                </c:when>
                <c:when test="${not empty ct.tienPhatUocTinh}">
                  <span class="text-danger" title="Số tiền phạt đang phát sinh tính đến hôm nay do trả trễ. Đây là số ƯỚC TÍNH, chỉ được chốt chính thức khi ghi nhận trả sách.">
                    ≈ <fmt:formatNumber value="${ct.tienPhatUocTinh}" type="number"/> đ <i class="bi bi-info-circle"></i>
                  </span>
                </c:when>
                <c:otherwise>
                  <span class="text-muted">—</span>
                </c:otherwise>
              </c:choose>
            </td>
            <td class="text-center">
              <c:choose>
                <c:when test="${ct.daTra == 1}"><span class="badge bg-success">Đã trả</span></c:when>
                <c:when test="${phieuMuon.trangThai == 'DA_HUY'}"><span class="badge bg-secondary">Đã hủy</span></c:when>
                <c:otherwise><span class="badge bg-warning text-dark">Chưa trả</span></c:otherwise>
              </c:choose>
            </td>
            <td class="text-center">
              <c:if test="${ct.daTra == 0 && phieuMuon.trangThai != 'DA_HUY'}">
                <a href="${pageContext.request.contextPath}/phieumuon?action=traSach&maCt=${ct.maCt}&maPm=${phieuMuon.maPm}"
                   class="btn btn-sm btn-success" onclick="return confirm('Xác nhận độc giả đã trả sách này?');">
                   <i class="bi bi-check2-circle"></i> Ghi nhận trả
                </a>
              </c:if>
            </td>
          </tr>
        </c:forEach>
      </tbody>
    </table>
  </div>
</div>

<div class="mt-3">
  <a href="${pageContext.request.contextPath}/phieumuon" class="btn btn-outline-secondary"><i class="bi bi-arrow-left"></i> Quay lại danh sách</a>
</div>

<%@ include file="../common/footer.jsp" %>

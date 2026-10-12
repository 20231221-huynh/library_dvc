<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Danh sách đen - Thư Viện DVC" scope="request"/>
<%@ include file="../common/header.jsp" %>

<div class="d-flex justify-content-between align-items-center mb-3">
  <h4><i class="bi bi-slash-circle text-danger"></i> Danh sách đen - Độc giả chưa trả sách</h4>
  <a href="${pageContext.request.contextPath}/docgia" class="btn btn-outline-secondary">
    <i class="bi bi-arrow-left"></i> Quay lại danh sách độc giả
  </a>
</div>

<div class="alert alert-warning">
  <i class="bi bi-exclamation-triangle"></i>
  Danh sách dưới đây liệt kê các độc giả đang có phiếu mượn <strong>quá hạn</strong> (đã phát sinh tiền phạt theo số ngày trễ) nhưng
  <strong>vẫn chưa trả sách</strong>. Có thể khóa tài khoản (đưa vào danh sách đen) để tạm ngưng cho mượn thêm cho đến khi độc giả trả sách và nộp phạt đầy đủ.
</div>

<div class="card">
  <div class="card-body table-responsive">
    <table class="table table-hover align-middle">
      <thead>
        <tr>
          <th>#</th>
          <th>Họ tên</th>
          <th>SĐT</th>
          <th>Email</th>
          <th class="text-center">Số phiếu quá hạn</th>
          <th class="text-center" title="Số ngày trễ = hôm nay − hạn trả (ngày_hẹn_trả), cộng dồn theo từng phiếu quá hạn của độc giả này.">
            Tổng số ngày trễ <i class="bi bi-info-circle"></i>
          </th>
          <th class="text-end" title="Ước tính = tổng (số ngày trễ × số lượng sách chưa trả × đơn giá phạt/ngày) trên các phiếu quá hạn. Số tiền thật sẽ được chốt khi độc giả trả sách.">
            Tiền phạt ước tính <i class="bi bi-info-circle"></i>
          </th>
          <th>Trạng thái</th>
          <th class="text-center">Thao tác</th>
        </tr>
      </thead>
      <tbody>
        <c:forEach var="row" items="${danhSachDen}">
          <tr>
            <td>${row[0]}</td>
            <td>${row[1]}</td>
            <td>${row[2]}</td>
            <td>${row[3]}</td>
            <td class="text-center"><span class="badge bg-danger">${row[5]}</span></td>
            <td class="text-center">${row[6]}</td>
            <td class="text-end"><fmt:formatNumber value="${row[7]}" type="number"/> đ</td>
            <td>
              <c:choose>
                <c:when test="${row[4] == 1}"><span class="badge bg-success">Hoạt động</span></c:when>
                <c:otherwise><span class="badge bg-secondary">Đã khóa</span></c:otherwise>
              </c:choose>
            </td>
            <td class="text-center">
              <a href="${pageContext.request.contextPath}/phieumuon?keyword=${row[1]}&filter=QUA_HAN"
                 class="btn btn-sm btn-outline-primary" title="Xem phiếu mượn quá hạn">
                <i class="bi bi-eye"></i>
              </a>
              <c:choose>
                <c:when test="${row[4] == 1}">
                  <a href="${pageContext.request.contextPath}/docgia?action=khoa&id=${row[0]}"
                     class="btn btn-sm btn-danger" onclick="return confirm('Khóa tài khoản độc giả này (đưa vào danh sách đen)?');">
                    <i class="bi bi-lock"></i> Khóa
                  </a>
                </c:when>
                <c:otherwise>
                  <a href="${pageContext.request.contextPath}/docgia?action=moKhoa&id=${row[0]}"
                     class="btn btn-sm btn-outline-success" onclick="return confirm('Mở khóa tài khoản độc giả này?');">
                    <i class="bi bi-unlock"></i> Mở khóa
                  </a>
                </c:otherwise>
              </c:choose>
            </td>
          </tr>
        </c:forEach>
        <c:if test="${empty danhSachDen}">
          <tr><td colspan="9" class="text-center text-muted py-4">
            <i class="bi bi-emoji-smile"></i> Không có độc giả nào vi phạm quá hạn chưa trả sách.
          </td></tr>
        </c:if>
      </tbody>
    </table>
  </div>
</div>

<%@ include file="../common/footer.jsp" %>

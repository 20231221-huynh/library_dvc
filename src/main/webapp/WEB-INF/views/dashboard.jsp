<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Trang chủ - Thư Viện DVC" scope="request"/>
<%@ include file="common/header.jsp" %>

<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.4/dist/chart.umd.min.js"></script>

<h4 class="mb-3"><i class="bi bi-speedometer2"></i> Tổng quan hệ thống</h4>


<div class="row g-2 mb-2 align-items-stretch">
  <div class="col-lg-5">
    <div class="card h-100">
      <div class="card-body py-2 px-3">
        <form method="get" action="${pageContext.request.contextPath}/dashboard" id="formKhoangThoiGian">
          <!-- Dat nhan va o chon TREN CUNG 1 DONG NGANG thay vi nhan rieng 1 dong (truoc day nhan dai
               bi xuong dong lam the cao len va thua rat nhieu khoang trang ben phai). -->
          <div class="d-flex align-items-center gap-2 flex-wrap">
            <label class="form-label mb-0 small text-nowrap" for="selectKhoang">
              <i class="bi bi-calendar-range"></i> Khoảng thời gian
            </label>
            <select name="khoang" id="selectKhoang" class="form-select form-select-sm" style="max-width:180px;"
                    onchange="toggleTuyChon(); if(this.value!=='tuychon'){document.getElementById('formKhoangThoiGian').submit();}">
              <option value="tatca" ${khoang == 'tatca' ? 'selected' : ''}>Tất cả</option>
              <option value="7ngay" ${khoang == '7ngay' ? 'selected' : ''}>7 ngày qua</option>
              <option value="30ngay" ${khoang == '30ngay' ? 'selected' : ''}>30 ngày qua</option>
              <option value="thang" ${khoang == 'thang' ? 'selected' : ''}>Tháng này</option>
              <option value="nam" ${khoang == 'nam' ? 'selected' : ''}>Năm nay</option>
              <option value="tuychon" ${khoang == 'tuychon' ? 'selected' : ''}>Tùy chọn...</option>
            </select>
            <span class="small text-muted" id="nhanPhamVi">
              <c:choose>
                <c:when test="${not empty tuNgay and not empty denNgay}">${tuNgay} → ${denNgay}</c:when>
                <c:otherwise>Toàn bộ dữ liệu</c:otherwise>
              </c:choose>
            </span>
          </div>
          <!-- Khoi chon ngay tuy chon chi hien khi chon "Tuy chon...", nen mac dinh khong chiem cho -->
          <div class="row g-2 mt-1" id="divTuyChon" style="${khoang == 'tuychon' ? '' : 'display:none;'}">
            <div class="col-5">
              <input type="date" name="tuNgay" class="form-control form-control-sm" value="${tuNgay}" aria-label="Từ ngày">
            </div>
            <div class="col-5">
              <input type="date" name="denNgay" class="form-control form-control-sm" value="${denNgay}" aria-label="Đến ngày">
            </div>
            <div class="col-2">
              <button type="submit" class="btn btn-sm btn-primary w-100"><i class="bi bi-funnel"></i></button>
            </div>
          </div>
        </form>
      </div>
    </div>
  </div>
  <div class="col-lg-7">
    <div class="card h-100">
      <div class="card-body py-2 px-3">
        <h6 class="card-title mb-2 small text-uppercase text-muted"><i class="bi bi-lightning-charge"></i> Thao tác nhanh</h6>
        <div class="d-flex flex-wrap gap-2">
          <a href="${pageContext.request.contextPath}/phieumuon?action=add" class="btn btn-sm btn-primary"><i class="bi bi-plus-circle"></i> Lập phiếu mượn</a>
          <a href="${pageContext.request.contextPath}/sach?action=add" class="btn btn-sm btn-outline-secondary"><i class="bi bi-plus-circle"></i> Thêm sách</a>
          <a href="${pageContext.request.contextPath}/docgia?action=add" class="btn btn-sm btn-outline-secondary"><i class="bi bi-plus-circle"></i> Thêm độc giả</a>
          <a href="${pageContext.request.contextPath}/phieumuon?filter=QUA_HAN" class="btn btn-sm btn-outline-danger"><i class="bi bi-exclamation-triangle"></i> Phiếu quá hạn</a>
          <a href="${pageContext.request.contextPath}/thongke" class="btn btn-sm btn-outline-info"><i class="bi bi-bar-chart"></i> Thống kê</a>
        </div>
      </div>
    </div>
  </div>
</div>
<script>
  function toggleTuyChon() {
    var isTuyChon = document.getElementById('selectKhoang').value === 'tuychon';
    document.getElementById('divTuyChon').style.display = isTuyChon ? '' : 'none';
    document.getElementById('nhanPhamVi').style.display = isTuyChon ? 'none' : '';
  }
</script>

<!-- Hang 1: chi so hoat dong phieu muon theo khoang thoi gian da chon o tren -->
<div class="row g-2 mb-2">
  <div class="col-6 col-md-3">
    <div class="stat-card bg-stat-1">
      <div class="d-flex justify-content-between align-items-center">
        <div><p class="mb-0">Phiếu đang mượn</p><h3 class="mb-0">${soPhieuDangMuon}</h3></div>
        <i class="bi bi-arrow-left-right" style="font-size:1.5rem;opacity:.7;"></i>
      </div>
    </div>
  </div>
  <div class="col-6 col-md-3">
    <div class="stat-card bg-stat-4">
      <div class="d-flex justify-content-between align-items-center">
        <div><p class="mb-0">Phiếu quá hạn</p><h3 class="mb-0">${soPhieuQuaHan}</h3></div>
        <i class="bi bi-exclamation-triangle" style="font-size:1.5rem;opacity:.7;"></i>
      </div>
    </div>
  </div>
  <div class="col-6 col-md-3">
    <div class="stat-card bg-stat-2">
      <div class="d-flex justify-content-between align-items-center">
        <div><p class="mb-0">Phiếu đã trả</p><h3 class="mb-0">${soPhieuDaTra}</h3></div>
        <i class="bi bi-check-circle" style="font-size:1.5rem;opacity:.7;"></i>
      </div>
    </div>
  </div>
  <div class="col-6 col-md-3">
    <div class="stat-card bg-stat-5">
      <div class="d-flex justify-content-between align-items-center">
        <div><p class="mb-0">Phiếu đã hủy</p><h3 class="mb-0">${soPhieuDaHuy}</h3></div>
        <i class="bi bi-x-circle" style="font-size:1.5rem;opacity:.7;"></i>
      </div>
    </div>
  </div>
</div>

<!-- Hang 2: chi so ton kho/danh muc hien tai - KHONG phu thuoc bo loc thoi gian o tren. Nhan nho gan
     vao tieu de thay vi ca 1 dong text rieng, giup tiet kiem chieu cao trang. -->
<div class="row row-cols-2 row-cols-md-3 row-cols-lg-5 g-2 mb-3">
  <div class="col">
    <div class="card mini-stat">
      <div class="mini-stat-icon bg-primary bg-opacity-10 text-primary"><i class="bi bi-journal-bookmark"></i></div>
      <div><p>Tổng đầu sách</p><h4>${tongSoSach}</h4></div>
    </div>
  </div>
  <div class="col">
    <div class="card mini-stat">
      <div class="mini-stat-icon bg-success bg-opacity-10 text-success"><i class="bi bi-people"></i></div>
      <div><p>Tổng độc giả</p><h4>${tongSoDocGia}</h4></div>
    </div>
  </div>
  <div class="col">
    <div class="card mini-stat">
      <div class="mini-stat-icon bg-info bg-opacity-10 text-info"><i class="bi bi-tags"></i></div>
      <div><p>Thể loại</p><h4>${tongSoTheLoai}</h4></div>
    </div>
  </div>
  <div class="col">
    <div class="card mini-stat">
      <div class="mini-stat-icon bg-warning bg-opacity-10 text-warning"><i class="bi bi-person-lines-fill"></i></div>
      <div><p>Tác giả</p><h4>${tongSoTacGia}</h4></div>
    </div>
  </div>
  <c:if test="${sessionScope.LOGGED_IN_USER.vaiTro == 'ADMIN'}">
    <div class="col">
      <div class="card mini-stat">
        <div class="mini-stat-icon bg-dark bg-opacity-10 text-dark"><i class="bi bi-person-gear"></i></div>
        <div><p>Tài khoản NV</p><h4>${tongSoTaiKhoan}</h4></div>
      </div>
    </div>
  </c:if>
</div>

<!-- Phieu qua han (trang thai hien tai, KHONG phu thuoc bo loc thoi gian o tren) + phieu moi lap
     THEO KHOANG THOI GIAN DA CHON (loc theo ngay lap phieu) -->
<div class="row g-2 mb-3">
  <div class="col-lg-6">
    <div class="card h-100">
      <div class="card-body">
        <div class="d-flex justify-content-between align-items-center mb-2">
          <h6 class="card-title mb-0"><i class="bi bi-alarm text-danger"></i> Phiếu quá hạn cần xử lý</h6>
          <a href="${pageContext.request.contextPath}/phieumuon?filter=QUA_HAN" class="small">Xem tất cả</a>
        </div>
        <div class="table-responsive">
          <table class="table table-sm table-hover align-middle mb-0">
            <thead><tr><th>Mã PM</th><th>Độc giả</th><th>Hạn trả</th><th class="text-center">Thao tác</th></tr></thead>
            <tbody>
              <c:forEach var="pm" items="${dsPhieuQuaHanGanDay}">
                <tr>
                  <td>#${pm.maPm}</td>
                  <td>${pm.tenDocGia}</td>
                  <td><span class="text-danger">${pm.ngayHenTra}</span></td>
                  <td class="text-center">
                    <a href="${pageContext.request.contextPath}/phieumuon?action=detail&id=${pm.maPm}" class="btn btn-sm btn-outline-primary"><i class="bi bi-eye"></i></a>
                  </td>
                </tr>
              </c:forEach>
              <c:if test="${empty dsPhieuQuaHanGanDay}">
                <tr><td colspan="4" class="text-center text-muted py-3">Không có phiếu nào đang quá hạn. 🎉</td></tr>
              </c:if>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
  <div class="col-lg-6">
    <div class="card h-100">
      <div class="card-body">
        <div class="d-flex justify-content-between align-items-center mb-2">
          <h6 class="card-title mb-0"><i class="bi bi-clock-history"></i> Phiếu mượn mới lập</h6>
          <a href="${pageContext.request.contextPath}/phieumuon" class="small">Xem tất cả</a>
        </div>
        <div class="table-responsive">
          <table class="table table-sm table-hover align-middle mb-0">
            <thead><tr><th>Mã PM</th><th>Độc giả</th><th>Ngày mượn</th><th>Trạng thái</th></tr></thead>
            <tbody>
              <c:forEach var="pm" items="${dsPhieuMoiLap}">
                <tr>
                  <td>#${pm.maPm}</td>
                  <td>${pm.tenDocGia}</td>
                  <td>${pm.ngayMuon}</td>
                  <td>
                    <c:choose>
                      <c:when test="${pm.trangThai == 'DANG_MUON'}"><span class="badge bg-primary">Đang mượn</span></c:when>
                      <c:when test="${pm.trangThai == 'QUA_HAN'}"><span class="badge bg-danger">Quá hạn</span></c:when>
                      <c:when test="${pm.trangThai == 'DA_HUY'}"><span class="badge bg-secondary">Đã hủy</span></c:when>
                      <c:otherwise><span class="badge bg-success">Đã trả</span></c:otherwise>
                    </c:choose>
                  </td>
                </tr>
              </c:forEach>
              <c:if test="${empty dsPhieuMoiLap}">
                <tr><td colspan="4" class="text-center text-muted py-3">Không có phiếu mượn nào trong khoảng đã chọn.</td></tr>
              </c:if>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</div>

<!-- Bieu do co cau kho sach + canh bao sach sap het (khong phu thuoc thoi gian) -->
<div class="row g-2 mb-3">
  <div class="col-lg-6">
    <div class="card h-100">
      <div class="card-body">
        <h6 class="card-title"><i class="bi bi-bar-chart"></i> Số lượng sách theo thể loại</h6>
        <canvas id="chartTheLoai" height="200"></canvas>
      </div>
    </div>
  </div>
  <div class="col-lg-6">
    <div class="card h-100">
      <div class="card-body">
        <div class="d-flex justify-content-between align-items-center mb-2">
          <h6 class="card-title mb-0"><i class="bi bi-exclamation-triangle text-warning"></i> Sách sắp hết (còn ≤ 3 cuốn)</h6>
          <a href="${pageContext.request.contextPath}/sach" class="small">Xem tất cả sách</a>
        </div>
        <div class="table-responsive">
          <table class="table table-sm table-hover align-middle mb-0">
            <thead><tr><th>Tên sách</th><th>Thể loại</th><th class="text-center">Còn lại</th><th class="text-center">Sửa</th></tr></thead>
            <tbody>
              <c:forEach var="s" items="${dsSachSapHet}">
                <tr>
                  <td>${s.tenSach}</td>
                  <td>${s.tenTheLoai}</td>
                  <td class="text-center"><span class="badge ${s.soLuongCon == 0 ? 'bg-danger' : 'bg-warning text-dark'}">${s.soLuongCon}</span></td>
                  <td class="text-center">
                    <a href="${pageContext.request.contextPath}/sach?action=edit&id=${s.maSach}" class="btn btn-sm btn-outline-warning"><i class="bi bi-pencil"></i></a>
                  </td>
                </tr>
              </c:forEach>
              <c:if test="${empty dsSachSapHet}">
                <tr><td colspan="4" class="text-center text-muted py-3">Không có sách nào sắp hết hàng.</td></tr>
              </c:if>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</div>

<script>
(function() {
  var nhanTheLoai = ${theLoaiLabelsJson};
  var soLuongTheLoai = ${theLoaiValuesJson};

  new Chart(document.getElementById('chartTheLoai'), {
    type: 'bar',
    data: {
      labels: nhanTheLoai,
      datasets: [{ label: 'Số đầu sách', data: soLuongTheLoai, backgroundColor: '#4e73df' }]
    },
    options: {
      responsive: true,
      plugins: { legend: { display: false } },
      scales: { y: { beginAtZero: true, ticks: { precision: 0 } } }
    }
  });
})();
</script>

<%@ include file="common/footer.jsp" %>

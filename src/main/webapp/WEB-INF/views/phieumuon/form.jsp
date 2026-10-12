<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Lập phiếu mượn - Thư Viện DVC" scope="request"/>
<%@ include file="../common/header.jsp" %>

<h4 class="mb-3"><i class="bi bi-plus-circle"></i> Lập phiếu mượn mới</h4>

<div class="card">
  <div class="card-body">
    <form method="post" action="${pageContext.request.contextPath}/phieumuon" id="frmPhieuMuon">
      <div class="row g-3 mb-3">
        <div class="col-md-4">
          <label class="form-label">Độc giả <span class="text-danger">*</span></label>
          <select name="maDg" id="selMaDg" class="form-select" required>
            <option value="">-- Chọn độc giả --</option>
            <c:forEach var="dg" items="${docGiaList}">
              <c:if test="${dg.trangThai == 1}">
                <option value="${dg.maDg}">${dg.hoTen} - ${dg.sdt}</option>
              </c:if>
            </c:forEach>
          </select>
        </div>
        <div class="col-md-4">
          <label class="form-label">Ngày mượn <span class="text-danger">*</span></label>
          <input type="date" name="ngayMuon" id="ngayMuon" class="form-control" value="${ngayMuonMacDinh}" required>
        </div>
        <div class="col-md-4">
          <label class="form-label d-flex justify-content-between align-items-center">
            <span>Ngày hẹn trả <span class="text-danger">*</span></span>
            <a href="#" id="btnResetHenTra" class="small">Mặc định 2 tuần</a>
          </label>
          <input type="date" name="ngayHenTra" id="ngayHenTra" class="form-control" value="${ngayHenTraMacDinh}" required>
          <div class="form-text">Mặc định 14 ngày kể từ ngày mượn — có thể sửa thành ngày khác nếu cần.</div>
        </div>
        <div class="col-12">
          <label class="form-label">Ghi chú</label>
          <input type="text" name="ghiChu" class="form-control" maxlength="255" placeholder="Ghi chú (không bắt buộc)">
        </div>
      </div>

      <hr>
      <div class="d-flex justify-content-between align-items-center mb-2">
        <label class="form-label mb-0 fw-bold">Danh sách sách mượn</label>
        <button type="button" class="btn btn-sm btn-outline-primary" id="btnAddRow"><i class="bi bi-plus"></i> Thêm dòng</button>
      </div>

      <table class="table" id="tblSach">
        <thead><tr><th style="width:60%">Sách</th><th style="width:20%">Số lượng</th><th></th></tr></thead>
        <tbody>
          <tr>
            <td>
              <select name="maSach" class="form-select select-sach" required>
                <option value="">-- Chọn sách --</option>
                <c:forEach var="s" items="${sachList}">
                  <c:if test="${s.trangThai == 1 && s.soLuongCon > 0}">
                    <option value="${s.maSach}">${s.tenSach} (còn ${s.soLuongCon})</option>
                  </c:if>
                </c:forEach>
              </select>
            </td>
            <td><input type="number" name="soLuongMuon" class="form-control" min="1" value="1" required></td>
            <td><button type="button" class="btn btn-sm btn-outline-danger btnRemoveRow"><i class="bi bi-x"></i></button></td>
          </tr>
        </tbody>
      </table>

      <!-- Mau dong an (khong hien thi) dung de nhan ban khi bam "Them dong" - luon la <select> "sach" thuan,
           chua bi Tom Select "bien hinh" (Tom Select chen them the div bao ngoai select goc nen khong the
           nhan ban truc tiep tu mot dong da duoc khoi tao Tom Select). -->
      <template id="rowTemplate">
        <tr>
          <td>
            <select name="maSach" class="form-select select-sach" required>
              <option value="">-- Chọn sách --</option>
              <c:forEach var="s" items="${sachList}">
                <c:if test="${s.trangThai == 1 && s.soLuongCon > 0}">
                  <option value="${s.maSach}">${s.tenSach} (còn ${s.soLuongCon})</option>
                </c:if>
              </c:forEach>
            </select>
          </td>
          <td><input type="number" name="soLuongMuon" class="form-control" min="1" value="1" required></td>
          <td><button type="button" class="btn btn-sm btn-outline-danger btnRemoveRow"><i class="bi bi-x"></i></button></td>
        </tr>
      </template>

      <div class="mt-4">
        <button type="submit" class="btn btn-primary"><i class="bi bi-save"></i> Lập phiếu mượn</button>
        <a href="${pageContext.request.contextPath}/phieumuon" class="btn btn-outline-secondary">Hủy</a>
      </div>
    </form>
  </div>
</div>

<script>
// Bien o chon Doc gia thanh combobox co the go de tim kiem
new TomSelect('#selMaDg', {
  create: false,
  allowEmptyOption: true,
  placeholder: '-- Chọn độc giả --',
  sortField: { field: 'text', direction: 'asc' }
});

// So ngay muon mac dinh (lay tu Constants.SO_NGAY_MUON_MAC_DINH ben server, khong hardcode lai o JS)
var SO_NGAY_MAC_DINH = ${soNgayMuonMacDinh};

function tinhNgayHenTraMacDinh(ngayMuonStr) {
  var d = new Date(ngayMuonStr + 'T00:00:00');
  if (isNaN(d.getTime())) return null;
  d.setDate(d.getDate() + SO_NGAY_MAC_DINH);
  return d.toISOString().slice(0, 10);
}

var inpNgayMuon = document.getElementById('ngayMuon');
var inpNgayHenTra = document.getElementById('ngayHenTra');

// Khi doi "Ngay muon", tu dong tinh lai "Ngay hen tra" = ngay muon + 14 ngay (nhan vien van co the
// sua tay lai ngay hen tra sau do neu muon mot thoi han khac).
inpNgayMuon.addEventListener('change', function () {
  var macDinh = tinhNgayHenTraMacDinh(this.value);
  if (macDinh) inpNgayHenTra.value = macDinh;
});

// Nut "Mac dinh 2 tuan": dat lai Ngay hen tra ve dung 14 ngay ke tu Ngay muon hien tai,
// dung khi nhan vien da lo sua Ngay hen tra sang gia tri khac va muon quay lai mac dinh.
document.getElementById('btnResetHenTra').addEventListener('click', function (e) {
  e.preventDefault();
  var macDinh = tinhNgayHenTraMacDinh(inpNgayMuon.value);
  if (macDinh) inpNgayHenTra.value = macDinh;
});

// Kiem tra truoc khi submit: Ngay hen tra khong duoc truoc Ngay muon (server cung kiem tra lai,
// day chi la lop kiem tra som o phia trinh duyet de bao loi ngay, khong doi round-trip len server).
document.getElementById('frmPhieuMuon').addEventListener('submit', function (e) {
  if (inpNgayHenTra.value < inpNgayMuon.value) {
    e.preventDefault();
    alert('Ngày hẹn trả không được trước ngày mượn.');
    inpNgayHenTra.focus();
  }
});

// Bien 1 the <select name="maSach"> thanh combobox tim kiem duoc, tra ve instance Tom Select vua tao
function initSachSelect(selectEl) {
  return new TomSelect(selectEl, {
    create: false,
    allowEmptyOption: true,
    placeholder: '-- Chọn sách --',
    sortField: { field: 'text', direction: 'asc' }
  });
}

// Khoi tao Tom Select cho dong sach co san luc dau
document.querySelectorAll('#tblSach select.select-sach').forEach(initSachSelect);

// Them dong moi: nhan ban tu <template> (con nguyen la <select> thuan), gan vao bang, roi moi khoi tao Tom Select
document.getElementById('btnAddRow').addEventListener('click', function () {
  var tpl = document.getElementById('rowTemplate');
  var newRow = tpl.content.firstElementChild.cloneNode(true);
  document.querySelector('#tblSach tbody').appendChild(newRow);
  initSachSelect(newRow.querySelector('select.select-sach'));
});

// Xoa dong: neu select trong dong do da la Tom Select thi huy instance truoc de tranh ri bo nho, roi moi xoa dong
document.querySelector('#tblSach tbody').addEventListener('click', function (e) {
  var btn = e.target.closest('.btnRemoveRow');
  if (!btn) return;
  var tbody = document.querySelector('#tblSach tbody');
  if (tbody.rows.length > 1) {
    var tr = btn.closest('tr');
    var sel = tr.querySelector('select.select-sach');
    if (sel && sel.tomselect) sel.tomselect.destroy();
    tr.remove();
  }
});
</script>

<%@ include file="../common/footer.jsp" %>

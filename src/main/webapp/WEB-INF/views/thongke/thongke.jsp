<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Thống kê - Thư Viện DVC" scope="request"/>
<%@ include file="../common/header.jsp" %>

<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.4/dist/chart.umd.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/chartjs-plugin-datalabels@2.2.0/dist/chartjs-plugin-datalabels.min.js"></script>

<!-- Tieu de va bo loc khoang thoi gian dat ngang hang nhau -->
<div class="d-flex flex-wrap justify-content-between align-items-center mb-3 gap-2">
  <h4 class="mb-0"><i class="bi bi-bar-chart"></i> Thống kê - Báo cáo</h4>

  <!-- Bo loc khoang thoi gian: thay doi khoang se cap nhat lai cac chi so/bieu do lien quan den phieu muon -->
  <form method="get" action="${pageContext.request.contextPath}/thongke" class="row g-2 align-items-end" id="formKhoangThoiGian">
    <div class="col-auto">
      <label class="form-label mb-1"><i class="bi bi-calendar-range"></i> Khoảng thời gian</label>
      <select name="khoang" id="selectKhoang" class="form-select" onchange="toggleTuyChon(); document.getElementById('formKhoangThoiGian').submit();">
        <option value="tatca" ${khoang == 'tatca' ? 'selected' : ''}>Tất cả</option>
        <option value="7ngay" ${khoang == '7ngay' ? 'selected' : ''}>7 ngày qua</option>
        <option value="30ngay" ${khoang == '30ngay' ? 'selected' : ''}>30 ngày qua</option>
        <option value="thang" ${khoang == 'thang' ? 'selected' : ''}>Tháng này</option>
        <option value="nam" ${khoang == 'nam' ? 'selected' : ''}>Năm nay</option>
        <option value="tuychon" ${khoang == 'tuychon' ? 'selected' : ''}>Tùy chọn...</option>
      </select>
    </div>
    <div class="col-auto" id="divTuNgay" style="${khoang == 'tuychon' ? '' : 'display:none;'}">
      <label class="form-label mb-1">Từ ngày</label>
      <input type="date" name="tuNgay" class="form-control" value="${tuNgay}">
    </div>
    <div class="col-auto" id="divDenNgay" style="${khoang == 'tuychon' ? '' : 'display:none;'}">
      <label class="form-label mb-1">Đến ngày</label>
      <input type="date" name="denNgay" class="form-control" value="${denNgay}">
    </div>
    <div class="col-auto" id="divApDung" style="${khoang == 'tuychon' ? '' : 'display:none;'}">
      <button type="submit" class="btn btn-primary"><i class="bi bi-funnel"></i> Áp dụng</button>
    </div>
  </form>
</div>
<c:if test="${not empty tuNgay or not empty denNgay}">
  <div class="form-text mb-4">
    Đang xem số liệu phiếu mượn từ <strong>${empty tuNgay ? 'trước đến nay' : tuNgay}</strong>
    đến <strong>${empty denNgay ? 'hiện tại' : denNgay}</strong>.
  </div>
</c:if>
<script>
  function toggleTuyChon() {
    var isTuyChon = document.getElementById('selectKhoang').value === 'tuychon';
    var disp = isTuyChon ? '' : 'none';
    document.getElementById('divTuNgay').style.display = disp;
    document.getElementById('divDenNgay').style.display = disp;
    document.getElementById('divApDung').style.display = disp;
  }
</script>

<div class="row g-3 mb-4">
  <div class="col-md-3">
    <div class="stat-card bg-stat-1"><p class="mb-1">Phiếu đang mượn</p><h3>${soPhieuDangMuon}</h3></div>
  </div>
  <div class="col-md-3">
    <div class="stat-card bg-stat-4"><p class="mb-1">Phiếu quá hạn</p><h3>${soPhieuQuaHan}</h3></div>
  </div>
  <div class="col-md-3">
    <div class="stat-card bg-stat-2"><p class="mb-1">Phiếu đã trả</p><h3>${soPhieuDaTra}</h3></div>
  </div>
  <div class="col-md-3">
    <div class="stat-card bg-stat-5"><p class="mb-1">Phiếu đã hủy</p><h3>${soPhieuDaHuy}</h3></div>
  </div>
</div>

<!-- Hang bieu do 1 (quan trong nhat, dua len tren cung): 2 bieu do "% Sach theo the loai" va
     "% Trang thai phieu muon" kich thuoc bang nhau, chia deu 2 nua trang -->
<div class="row g-3 mb-4">
  <div class="col-md-6">
    <div class="card h-100">
      <div class="card-body">
        <h6 class="card-title text-center"><i class="bi bi-pie-chart"></i> % Sách theo thể loại</h6>
        <canvas id="chartTyLeTheLoai" height="200"></canvas>
      </div>
    </div>
  </div>
  <div class="col-md-6">
    <div class="card h-100">
      <div class="card-body">
        <h6 class="card-title text-center"><i class="bi bi-pie-chart"></i> % Trạng thái phiếu mượn</h6>
        <canvas id="chartTrangThaiPhieu" height="200"></canvas>
      </div>
    </div>
  </div>
</div>

<!-- Hang bieu do 2: bieu do "% Sach theo trang thai" (dang luu hanh) chuyen xuong day, cung hang voi 2 bieu do Top 5 -->
<div class="row g-3">
  <div class="col-md-6 col-lg-4">
    <div class="card h-100">
      <div class="card-body">
        <h6 class="card-title text-center"><i class="bi bi-pie-chart"></i> % Sách theo trạng thái</h6>
        <canvas id="chartTyLeSach"></canvas>
      </div>
    </div>
  </div>
  <div class="col-md-6 col-lg-4">
    <div class="card h-100">
      <div class="card-body">
        <h6 class="card-title"><i class="bi bi-person-check"></i> Top 5 độc giả mượn nhiều nhất</h6>
        <div class="chart-container-cot">
          <canvas id="chartTopDocGia"></canvas>
        </div>
      </div>
    </div>
  </div>
  <div class="col-md-6 col-lg-4">
    <div class="card h-100">
      <div class="card-body">
        <h6 class="card-title"><i class="bi bi-book"></i> Top 5 sách mượn nhiều nhất</h6>
        <div class="chart-container-cot">
          <canvas id="chartTopSachMuon"></canvas>
        </div>
      </div>
    </div>
  </div>
</div>

<style>
  /* Chieu cao co dinh cho vung chua bieu do cot, tranh truong hop nhan truc X (ten doc gia/sach)
     bi day dai xuong lam tran ra ngoai card. maintainAspectRatio:false o duoi se dua vao chieu cao nay. */
  .chart-container-cot {
    position: relative;
    height: 280px;
    width: 100%;
  }
</style>

<script>
(function() {
  // Dang ky plugin hien thi nhan tren bieu do (Chart.js v4 khong tu dong bat plugin cho tung chart,
  // nen se chi bat "datalabels" rieng cho cac bieu do tron ben duoi; bieu do cot tat plugin nay di).
  Chart.register(ChartDataLabels);

  var mauSac = ['#4e73df', '#1cc88a', '#f6c23e', '#e74a3b', '#36b9cc', '#858796', '#fd7e14', '#6610f2'];

  // Sinh mot bang mau voi so luong tuy y, moi mau cach nhau theo "golden angle" (137.508 do)
  // de dam bao cac mau lien tiep luon khac biet ro rang, du bieu do co bao nhieu phan (the loai) di nua.
  function taoBangMau(soLuong) {
    var mau = [];
    for (var i = 0; i < soLuong; i++) {
      var hue = Math.round((i * 137.508) % 360);
      mau.push('hsl(' + hue + ', 68%, 55%)');
    }
    return mau;
  }

  // Ham tinh % tren tong de hien thi ngay tren tung phan cua bieu do tron.
  function nhanPhanTram(value, context) {
    var duLieu = context.chart.data.datasets[0].data;
    var tong = duLieu.reduce(function(a, b) { return a + b; }, 0);
    if (tong <= 0) return '0%';
    return (value * 100 / tong).toFixed(1).replace(/\.0$/, '') + '%';
  }

  var tuyChonBieuDoTron = {
    responsive: true,
    plugins: {
      legend: { position: 'bottom', labels: { boxWidth: 12 } },
      datalabels: {
        color: '#fff',
        font: { weight: 'bold' },
        formatter: nhanPhanTram
      }
    }
  };

  var tuyChonBieuDoCot = {
    responsive: true,
    // Tat maintainAspectRatio de bieu do bam theo chieu cao co dinh cua .chart-container-cot
    // thay vi tu keo dai theo ty le mac dinh (nguyen nhan lam bieu do/nhan truc X bi tran ra ngoai card).
    maintainAspectRatio: false,
    layout: { padding: { bottom: 4 } },
    plugins: { legend: { display: false }, datalabels: { display: false } },
    scales: {
      x: {
        ticks: {
          autoSkip: false,
          maxRotation: 40,
          minRotation: 40,
          font: { size: 10 }
        }
      },
      y: {
        beginAtZero: true,
        // "grace" chua them 10% khoang trong phia tren cot cao nhat, tranh truong hop cot
        // cham sat/tran len duong vien tren cua khung bieu do (nhu khi gia tri lon nhat la so tron).
        grace: '10%',
        ticks: { precision: 0 }
      }
    }
  };

  // Du lieu do servlet sinh san duoi dang JSON hop le (da escape ky tu dac biet/tieng Viet).
  var nhanTheLoai = ${theLoaiLabelsJson};
  var soLuongTheLoai = ${theLoaiValuesJson};
  var nhanSach = ${sachLabelsJson};
  var soLuongSach = ${sachValuesJson};
  var nhanDocGia = ${docGiaLabelsJson};
  var soLuongDocGia = ${docGiaValuesJson};
  var nhanSachMuon = ${sachMuonLabelsJson};
  var soLuongSachMuon = ${sachMuonValuesJson};

  new Chart(document.getElementById('chartTopDocGia'), {
    type: 'bar',
    data: {
      labels: nhanDocGia,
      datasets: [{ label: 'Số phiếu mượn', data: soLuongDocGia, backgroundColor: '#1cc88a' }]
    },
    options: tuyChonBieuDoCot
  });

  new Chart(document.getElementById('chartTopSachMuon'), {
    type: 'bar',
    data: {
      labels: nhanSachMuon,
      datasets: [{ label: 'Số lượt mượn', data: soLuongSachMuon, backgroundColor: '#fd7e14' }]
    },
    options: tuyChonBieuDoCot
  });

  new Chart(document.getElementById('chartTyLeTheLoai'), {
    type: 'pie',
    // Moi the loai duoc gan 1 mau rieng bang taoBangMau(), khong con bi lap/trung mau
    // nhu truoc day khi so the loai vuot qua do dai mang mauSac co dinh.
    // radius: '80%' de bieu do tron nho lai 1 chut so voi khung chua, tranh bi to qua kho nhin.
    data: { labels: nhanTheLoai, datasets: [{ data: soLuongTheLoai, backgroundColor: taoBangMau(nhanTheLoai.length), radius: '80%' }] },
    options: tuyChonBieuDoTron
  });

  new Chart(document.getElementById('chartTyLeSach'), {
    type: 'pie',
    // Dong bo radius voi bieu do "% Sach theo the loai" ben canh de kich thuoc hinh tron tuong xung, hai hoa.
    data: { labels: nhanSach, datasets: [{ data: soLuongSach, backgroundColor: ['#1cc88a', '#e74a3b', '#f6c23e'], radius: '80%' }] },
    options: tuyChonBieuDoTron
  });

  // Trang thai phieu muon (dang muon / da tra / qua han / da huy) theo khoang thoi gian da chon o tren
  var nhanPhieu = ['Đang mượn', 'Đã trả', 'Quá hạn', 'Đã hủy'];
  var soLuongPhieu = [${soPhieuDangMuon}, ${soPhieuDaTra}, ${soPhieuQuaHan}, ${soPhieuDaHuy}];
  var mauPhieu = ['#4e73df', '#1cc88a', '#e74a3b', '#858796'];

  new Chart(document.getElementById('chartTrangThaiPhieu'), {
    type: 'pie',
    // Dong bo radius voi 2 bieu do tron con lai trong cung hang de kich thuoc hinh tron tuong xung, hai hoa.
    data: { labels: nhanPhieu, datasets: [{ data: soLuongPhieu, backgroundColor: mauPhieu, radius: '80%' }] },
    options: tuyChonBieuDoTron
  });
})();
</script>

<%@ include file="../common/footer.jsp" %>

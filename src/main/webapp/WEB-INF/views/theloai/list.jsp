<%@ page contentType="text/html;charset=UTF-8" %> <!--khai báo/cấu hình cho JSP -->
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Quản lý Thể loại - Thư Viện DVC" scope="request"/>
<%@ include file="../common/header.jsp" %>

<h4 class="mb-3"><i class="bi bi-tags"></i> Quản lý Thể loại</h4>

<div class="row g-3">
    <!--Form bên trái -->
    <div class="col-md-4">
        <div class="card">
            <div class="card-body">
                <h6>${empty theLoai ? 'Thêm thể loại mới' : 'Sửa thể loại'}</h6>
                <form method="post" action="${pageContext.request.contextPath}/theloai"> <!--gửi đến servlet /theloai-->
                    <input type="hidden" name="action" value="${empty theLoai ? 'insert' : 'update'}">
                    <c:if test="${not empty theLoai}">
                        <input type="hidden" name="maTl" value="${theLoai.maTl}">
                    </c:if>
                    <div class="mb-2">
                        <label class="form-label">Tên thể loại <span class="text-danger">*</span></label>
                        <input type="text" name="tenTheLoai" class="form-control" maxlength="100" required value="${theLoai.tenTheLoai}">
                    </div>
                    <div class="mb-2">
                        <label class="form-label">Mô tả</label>
                        <textarea name="moTa" class="form-control" rows="2" maxlength="255">${theLoai.moTa}</textarea> <!--nội dung nhiều dòng-->
                    </div>
                    <button type="submit" class="btn btn-primary btn-sm"><i class="bi bi-save"></i> Lưu</button>
                    <c:if test="${not empty theLoai}">
                        <a href="${pageContext.request.contextPath}/theloai" class="btn btn-outline-secondary btn-sm">Hủy</a>
                    </c:if>
                </form>
            </div>
        </div>
    </div>
    <!-- Form bên phải -->                
    <div class="col-md-8">
        <div class="card">
            <div class="card-body table-responsive">
                <table class="table table-hover align-middle">
                    <thead><tr><th>#</th><th>Tên thể loại</th><th>Mô tả</th><th class="text-center">Số lượng sách</th><th class="text-center">Thao tác</th></tr></thead>
                    <tbody>
                        <!--Lặp qua từng phần tử trong theLoaiList, và mỗi phần tử được đặt tên tạm là tl-->
                        <c:forEach var="tl" items="${theLoaiList}">
                            <tr>
                                <td>${tl.maTl}</td><td>${tl.tenTheLoai}</td><td>${tl.moTa}</td>
                                <td class="text-center">
                                    <span class="badge ${tl.soLuongSach == 0 ? 'bg-secondary' : 'bg-info text-dark'}">${tl.soLuongSach}</span>
                                </td>
                                <td class="text-center">
                                    <a href="${pageContext.request.contextPath}/theloai?action=edit&id=${tl.maTl}" class="btn btn-sm btn-outline-warning"><i class="bi bi-pencil"></i></a>
                                    <a href="${pageContext.request.contextPath}/theloai?action=delete&id=${tl.maTl}" class="btn btn-sm btn-outline-danger"
                                       onclick="return confirm('Xóa thể loại này?');"><i class="bi bi-trash"></i></a>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty theLoaiList}"><tr><td colspan="5" class="text-center text-muted py-3">Chưa có thể loại nào.</td></tr></c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

<%@ include file="../common/footer.jsp" %>

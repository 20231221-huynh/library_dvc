-- =========================================================
-- CSDL: THU VIEN DVC - Quan ly thu vien
-- =========================================================
DROP DATABASE IF EXISTS library_dvc;
CREATE DATABASE library_dvc CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE library_dvc;

-- Bang tai khoan (Admin / Thu thu)
CREATE TABLE taikhoan (
    ma_tk INT AUTO_INCREMENT PRIMARY KEY,
    ten_dang_nhap VARCHAR(50) NOT NULL UNIQUE,
    mat_khau VARCHAR(255) NOT NULL,
    ho_ten VARCHAR(100) NOT NULL,
    vai_tro ENUM('ADMIN','THUTHU') NOT NULL DEFAULT 'THUTHU',
    trang_thai TINYINT NOT NULL DEFAULT 1,  -- 1: hoat dong, 0: khoa
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Bang the loai
CREATE TABLE theloai (
    ma_tl INT AUTO_INCREMENT PRIMARY KEY,
    ten_theloai VARCHAR(100) NOT NULL,
    mo_ta VARCHAR(255),
    UNIQUE KEY uq_theloai_ten (ten_theloai)
);

-- Bang tac gia
CREATE TABLE tacgia (
    ma_tg INT AUTO_INCREMENT PRIMARY KEY,
    ten_tacgia VARCHAR(100) NOT NULL,
    quoc_tich VARCHAR(50),
    UNIQUE KEY uq_tacgia_ten_quoctich (ten_tacgia, quoc_tich)
);

-- Bang sach
CREATE TABLE sach (
    ma_sach INT AUTO_INCREMENT PRIMARY KEY,
    ten_sach VARCHAR(200) NOT NULL,
    ma_tg INT,
    ma_tl INT,
    nha_xuat_ban VARCHAR(150),
    nam_xb INT,
    so_luong INT NOT NULL DEFAULT 0,
    so_luong_con INT NOT NULL DEFAULT 0,
    don_gia DECIMAL(10,2) DEFAULT 0,
    mo_ta VARCHAR(500),
    hinh_anh VARCHAR(255), -- luu trong /assets/uploads/sach/
    trang_thai TINYINT NOT NULL DEFAULT 1, -- 1: dang luu hanh, 0: ngung luu hanh
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ma_tg) REFERENCES tacgia(ma_tg) ON DELETE SET NULL,
    FOREIGN KEY (ma_tl) REFERENCES theloai(ma_tl) ON DELETE SET NULL,
    UNIQUE KEY uq_sach_ten_tacgia (ten_sach, ma_tg)
);

-- Bang doc gia
CREATE TABLE docgia (
    ma_dg INT AUTO_INCREMENT PRIMARY KEY,
    ho_ten VARCHAR(100) NOT NULL,
    ngay_sinh DATE,
    gioi_tinh VARCHAR(10),
    dia_chi VARCHAR(255),
    sdt VARCHAR(15),
    email VARCHAR(100),
    ngay_dang_ky DATE DEFAULT (CURRENT_DATE),
    trang_thai TINYINT NOT NULL DEFAULT 1,  -- 1: hoat dong, 0: khoa
    UNIQUE KEY uq_docgia_sdt (sdt),
    UNIQUE KEY uq_docgia_email (email)
);

-- Bang phieu muon
CREATE TABLE phieumuon (
    ma_pm INT AUTO_INCREMENT PRIMARY KEY,
    ma_dg INT NOT NULL,
    ma_tk INT NOT NULL,             -- nhan vien lap phieu
    ngay_muon DATE NOT NULL DEFAULT (CURRENT_DATE),
    ngay_hen_tra DATE NOT NULL,
    trang_thai ENUM('DANG_MUON','DA_TRA','QUA_HAN','DA_HUY') NOT NULL DEFAULT 'DANG_MUON',
    so_lan_gia_han INT NOT NULL DEFAULT 0, -- so lan da gia han ngay hen tra, toi da Constants.SO_LAN_GIA_HAN_TOI_DA
    ghi_chu VARCHAR(255),
    FOREIGN KEY (ma_dg) REFERENCES docgia(ma_dg),
    FOREIGN KEY (ma_tk) REFERENCES taikhoan(ma_tk)
);

-- Bang chi tiet phieu muon
CREATE TABLE chitietphieumuon (
    ma_ct INT AUTO_INCREMENT PRIMARY KEY,
    ma_pm INT NOT NULL,
    ma_sach INT NOT NULL,
    so_luong INT NOT NULL DEFAULT 1,
    ngay_tra_thucte DATE NULL,
    tien_phat DECIMAL(10,2) DEFAULT 0,
    da_tra TINYINT NOT NULL DEFAULT 0,
    FOREIGN KEY (ma_pm) REFERENCES phieumuon(ma_pm) ON DELETE CASCADE,
    FOREIGN KEY (ma_sach) REFERENCES sach(ma_sach)
);

-- DU LIEU MAU
-- =========================================================

INSERT INTO taikhoan (ten_dang_nhap, mat_khau, ho_ten, vai_tro) VALUES
('admin', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Quản Trị Viên', 'ADMIN'),
('thuthu1', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'Nguyễn Văn Thủ Thư', 'THUTHU');

-- Danh muc the loai theo huong THU VIEN TRUONG DAI HOC: giao trinh theo khoi kien thuc + mang sach
-- tham khao/ky nang di kem, thay cho cac the loai chung chung cua thu vien cong cong truoc day.
INSERT INTO theloai (ten_theloai, mo_ta) VALUES
(N'Giáo trình - Đại cương', N'Giáo trình các môn đại cương: Triết học, Kinh tế, Toán, Pháp luật...'),
(N'Công nghệ thông tin - Khoa học máy tính', N'Giáo trình và sách chuyên ngành CNTT, lập trình, khoa học máy tính'),
(N'Kinh tế - Quản trị kinh doanh', N'Sách và giáo trình về kinh tế, tài chính, quản trị, kế toán'),
(N'Kỹ thuật - Công nghệ', N'Sách kỹ thuật, công nghệ ứng dụng cho các ngành kỹ thuật'),
(N'Khoa học tự nhiên (Toán - Lý - Hóa)', N'Giáo trình Toán, Vật lý, Hóa học đại cương và chuyên ngành'),
(N'Ngoại ngữ', N'Sách học và tham khảo tiếng Anh, tiếng Nhật, tiếng Trung...'),
(N'Luật - Chính trị - Pháp luật đại cương', N'Giáo trình pháp luật, chính trị, tư tưởng, triết học'),
(N'Kỹ năng mềm - Hướng nghiệp', N'Sách phát triển kỹ năng mềm, định hướng nghề nghiệp cho sinh viên'),
(N'Văn học - Xã hội nhân văn', N'Sách văn học, xã hội nhân văn phục vụ đọc mở rộng'),
(N'Luận văn - Đồ án - Nghiên cứu khoa học', N'Tài liệu hướng dẫn nghiên cứu khoa học, viết luận văn, đồ án tốt nghiệp');

-- Tac gia: giu lai cac tac gia sach ky nang/van hoc pho bien, bo sung them giang vien bien soan
-- giao trinh (ten mang tinh minh hoa, dai dien cho khoi giao trinh noi bo cua truong).
INSERT INTO tacgia (ten_tacgia, quoc_tich) VALUES
(N'Nguyễn Nhật Ánh', N'Việt Nam'),
(N'Robert C. Martin', N'Hoa Kỳ'),
(N'Dale Carnegie', N'Hoa Kỳ'),
(N'Tô Hoài', N'Việt Nam'),
(N'Paulo Coelho', N'Brazil'),
(N'Haruki Murakami', N'Nhật Bản'),
(N'Steve McConnell', N'Hoa Kỳ'),
(N'Napoleon Hill', N'Hoa Kỳ'),
(N'Yuval Noah Harari', N'Israel'),
(N'PGS.TS. Trần Văn Bình', N'Việt Nam'),
(N'TS. Lê Thị Hồng', N'Việt Nam'),
(N'GS.TS. Phạm Minh Đức', N'Việt Nam'),
(N'TS. Ngô Quang Huy', N'Việt Nam'),
(N'ThS. Vũ Thị Lan Anh', N'Việt Nam'),
(N'TS. Hoàng Văn Sơn', N'Việt Nam');

-- Sach: mo rong thanh danh muc thien ve GIAO TRINH DAI HOC (dai cuong, CNTT, kinh te, ky thuat,
-- khoa hoc tu nhien, phap luat, nghien cuu khoa hoc...), van giu mot so dau sach tham khao/ky nang/
-- van hoc pho bien de phuc vu doc mo rong.
-- ma_sach duoc gan tu 1 den 40 theo dung thu tu chen ben duoi (dung lam khoa ngoai o cac phieu muon).
INSERT INTO sach (ten_sach, ma_tg, ma_tl, nha_xuat_ban, nam_xb, so_luong, so_luong_con, don_gia, mo_ta) VALUES
(N'Cho Tôi Xin Một Vé Đi Tuổi Thơ', 1, 9, N'NXB Trẻ', 2010, 10, 9, 60000, N'Tác phẩm nổi tiếng của Nguyễn Nhật Ánh'),                          -- 1
(N'Clean Code', 2, 2, N'Prentice Hall', 2008, 5, 5, 250000, N'Sách về viết mã nguồn sạch'),                                                    -- 2
(N'Đắc Nhân Tâm', 3, 8, N'NXB Tổng Hợp', 1936, 8, 8, 90000, N'Sách kỹ năng giao tiếp kinh điển'),                                              -- 3
(N'Dế Mèn Phiêu Lưu Ký', 4, 9, N'NXB Kim Đồng', 1941, 6, 5, 45000, N'Tác phẩm thiếu nhi nổi tiếng'),                                           -- 4
(N'Nhà Giả Kim', 5, 9, N'NXB Hội Nhà Văn', 2013, 12, 11, 79000, N'Cuốn sách bán chạy chỉ sau Kinh Thánh'),                                     -- 5
(N'Rừng Na Uy', 6, 9, N'NXB Hội Nhà Văn', 2008, 7, 7, 110000, N'Tiểu thuyết nổi tiếng nhất của Haruki Murakami'),                              -- 6
(N'Code Complete', 7, 2, N'Microsoft Press', 2004, 4, 4, 320000, N'Cẩm nang toàn tập về xây dựng phần mềm'),                                   -- 7
(N'Nghĩ Giàu Làm Giàu', 8, 8, N'NXB Trẻ', 2011, 15, 15, 115000, N'Nguyên tắc làm giàu kinh điển mọi thời đại'),                                -- 8
(N'Sapiens: Lược Sử Loài Người', 9, 9, N'NXB Tri Thức', 2017, 10, 10, 230000, N'Hành trình tiến hóa của loài người'),                          -- 9
(N'Clean Architecture', 2, 2, N'Prentice Hall', 2017, 6, 6, 290000, N'Cấu trúc và thiết kế phần mềm chuẩn mực từ Robert C. Martin'),            -- 10
(N'The Clean Coder', 2, 2, N'Prentice Hall', 2011, 5, 5, 270000, N'Hành trang và đạo đức nghề nghiệp dành cho lập trình viên chuyên nghiệp'),   -- 11
(N'Quẳng Gánh Lo Đi Và Vui Sống', 3, 8, N'NXB Tổng Hợp', 1948, 12, 12, 88000, N'Bí quyết xua tan lo âu để sống trọn vẹn từng ngày'),            -- 12
(N'Homo Deus: Lược Sử Tương Lai', 9, 9, N'NXB Tri Thức', 2018, 9, 9, 250000, N'Dự đoán về tương lai của loài người trong kỷ nguyên công nghệ'),-- 13
(N'21 Bài Học Cho Thế Kỷ 21', 9, 9, N'NXB Tri Thức', 2019, 11, 11, 240000, N'Giải đáp các thách thức lớn nhất của nhân loại hiện đại'),        -- 14
(N'Lập Trình Hướng Đối Tượng Với Java', 13, 2, N'NXB Giáo Dục', 2020, 15, 15, 135000, N'Giáo trình nền tảng về OOP và ngôn ngữ Java'),         -- 15
(N'Cấu Trúc Dữ Liệu Và Giải Thuật', 13, 2, N'NXB Thông Tin Và Truyền Thông', 2019, 10, 9, 160000, N'Giáo trình nền tảng về thuật toán'),       -- 16
(N'Tuần Làm Việc 4 Giờ', 8, 8, N'NXB Lao Động', 2018, 8, 8, 145000, N'Tối ưu hóa thời gian và xây dựng nguồn thu nhập tự động'),                -- 17
(N'Khởi Nghiệp Tinh Gọn', 8, 3, N'NXB Thế Giới', 2016, 10, 10, 155000, N'Mô hình phát triển sản phẩm sáng tạo cho doanh nghiệp mới'),          -- 18
(N'Tâm Lý Học Về Tiền', 3, 8, N'NXB Dân Trí', 2021, 18, 18, 129000, N'Bản chất tâm lý đằng sau những quyết định tài chính'),                   -- 19
(N'English Grammar In Use', 14, 6, N'Cambridge University Press', 2019, 25, 25, 280000, N'Ngữ pháp tiếng Anh toàn tập từ cơ bản đến nâng cao'),-- 20
(N'Giáo Trình Kinh Tế Vi Mô (Microeconomics)', 10, 1, N'NXB Đại Học Kinh Tế Quốc Dân', 2021, 20, 19, 95000, N'Giáo trình đại cương môn Kinh tế vi mô'),         -- 21
(N'Giáo Trình Kinh Tế Vĩ Mô (Macroeconomics)', 10, 1, N'NXB Đại Học Kinh Tế Quốc Dân', 2021, 20, 19, 95000, N'Giáo trình đại cương môn Kinh tế vĩ mô'),         -- 22
(N'Giáo Trình Triết Học Mác - Lênin', 12, 1, N'NXB Chính Trị Quốc Gia', 2021, 25, 25, 65000, N'Giáo trình lý luận chính trị bắt buộc'),        -- 23
(N'Giáo Trình Pháp Luật Đại Cương', 12, 7, N'NXB Tư Pháp', 2020, 4, 3, 60000, N'Giáo trình đại cương môn Pháp luật đại cương'),                -- 24
(N'Giáo Trình Toán Cao Cấp A1', 11, 5, N'NXB Giáo Dục', 2019, 20, 20, 70000, N'Giáo trình Toán cao cấp phần Giải tích'),                       -- 25
(N'Giáo Trình Toán Cao Cấp A2', 11, 5, N'NXB Giáo Dục', 2019, 18, 18, 70000, N'Giáo trình Toán cao cấp phần Đại số tuyến tính'),               -- 26
(N'Giáo Trình Xác Suất Thống Kê', 11, 5, N'NXB Giáo Dục', 2020, 15, 14, 75000, N'Giáo trình Xác suất thống kê ứng dụng'),                      -- 27
(N'Giáo Trình Vật Lý Đại Cương', 15, 5, N'NXB Đại Học Quốc Gia', 2018, 12, 11, 85000, N'Giáo trình Vật lý đại cương A1-A2'),                   -- 28
(N'Giáo Trình Hóa Học Đại Cương', 15, 5, N'NXB Đại Học Quốc Gia', 2018, 12, 11, 80000, N'Giáo trình Hóa học đại cương'),                       -- 29
(N'Nhập Môn Cơ Sở Dữ Liệu', 13, 2, N'NXB Bách Khoa Hà Nội', 2020, 14, 13, 110000, N'Giáo trình nhập môn hệ cơ sở dữ liệu quan hệ'),            -- 30
(N'Mạng Máy Tính Căn Bản', 13, 2, N'NXB Bách Khoa Hà Nội', 2019, 12, 11, 105000, N'Giáo trình nguyên lý và kiến trúc mạng máy tính'),          -- 31
(N'Hệ Điều Hành', 13, 2, N'NXB Bách Khoa Hà Nội', 2019, 10, 10, 115000, N'Giáo trình nguyên lý hệ điều hành'),                                 -- 32
(N'Kỹ Thuật Lập Trình C++', 13, 2, N'NXB Bách Khoa Hà Nội', 2018, 10, 10, 100000, N'Giáo trình kỹ thuật lập trình với C++'),                   -- 33
(N'Nhập Môn Trí Tuệ Nhân Tạo', 13, 2, N'NXB Đại Học Quốc Gia', 2022, 3, 2, 140000, N'Giáo trình nhập môn AI và học máy'),                      -- 34
(N'Giáo Trình Tiếng Anh Chuyên Ngành CNTT', 14, 6, N'NXB Bách Khoa Hà Nội', 2021, 10, 10, 90000, N'Tiếng Anh chuyên ngành Công nghệ thông tin'),-- 35
(N'Kỹ Năng Thuyết Trình Và Làm Việc Nhóm', 3, 8, N'NXB Đại Học Quốc Gia', 2020, 9, 8, 65000, N'Rèn luyện kỹ năng mềm cho sinh viên'),          -- 36
(N'Phương Pháp Nghiên Cứu Khoa Học', 12, 10, N'NXB Đại Học Quốc Gia', 2019, 6, 5, 75000, N'Hướng dẫn phương pháp luận nghiên cứu khoa học'),   -- 37
(N'Hướng Dẫn Viết Luận Văn Tốt Nghiệp', 12, 10, N'NXB Đại Học Quốc Gia', 2021, 3, 2, 55000, N'Cẩm nang trình bày và bảo vệ luận văn/đồ án'),   -- 38
(N'Giáo Trình Quản Trị Học', 10, 3, N'NXB Đại Học Kinh Tế Quốc Dân', 2020, 10, 10, 90000, N'Giáo trình các nguyên lý quản trị học'),           -- 39
(N'Nguyên Lý Kế Toán', 10, 3, N'NXB Tài Chính', 2021, 10, 9, 95000, N'Giáo trình nhập môn nguyên lý kế toán'),                               -- 40
-- Bo sung 20 dau sach de KHONG CON THE LOAI NAO TRONG (dac biet the loai "Ky thuat - Cong nghe" truoc
-- do chua co dau sach nao), dong thoi lam day them cho cac the loai it sach (Ngoai ngu, Luat - Chinh
-- tri, Luan van - Do an - NCKH, Kinh te - QTKD, Giao trinh - Dai cuong, KHTN).
(N'Cơ Kỹ Thuật Đại Cương', 15, 4, N'NXB Khoa Học Kỹ Thuật', 2020, 10, 10, 90000, N'Giáo trình cơ học kỹ thuật cho khối ngành kỹ thuật'),                    -- 41
(N'Vẽ Kỹ Thuật Cơ Khí', 15, 4, N'NXB Khoa Học Kỹ Thuật', 2019, 12, 12, 85000, N'Giáo trình vẽ kỹ thuật và tiêu chuẩn bản vẽ cơ khí'),                       -- 42
(N'Kỹ Thuật Điện - Điện Tử Căn Bản', 13, 4, N'NXB Bách Khoa Hà Nội', 2020, 10, 10, 105000, N'Giáo trình nhập môn mạch điện và linh kiện điện tử'),          -- 43
(N'Nguyên Lý Tự Động Hóa', 13, 4, N'NXB Khoa Học Kỹ Thuật', 2021, 8, 8, 120000, N'Giáo trình nhập môn hệ thống điều khiển tự động'),                        -- 44
(N'Vật Liệu Học Đại Cương', 15, 4, N'NXB Khoa Học Kỹ Thuật', 2019, 9, 9, 95000, N'Giáo trình tính chất và ứng dụng của vật liệu kỹ thuật'),                 -- 45
(N'Giáo Trình Tư Tưởng Hồ Chí Minh', 12, 7, N'NXB Chính Trị Quốc Gia', 2021, 20, 20, 60000, N'Giáo trình lý luận chính trị bắt buộc'),                      -- 46
(N'Giáo Trình Chủ Nghĩa Xã Hội Khoa Học', 12, 7, N'NXB Chính Trị Quốc Gia', 2021, 18, 18, 60000, N'Giáo trình lý luận chính trị bắt buộc'),                 -- 47
(N'Giáo Trình Pháp Luật Kinh Tế', 12, 7, N'NXB Tư Pháp', 2020, 10, 10, 75000, N'Giáo trình pháp luật áp dụng trong hoạt động kinh tế, doanh nghiệp'),       -- 48
(N'IELTS Writing Task 2 - Chiến Lược Đạt Điểm Cao', 14, 6, N'NXB Đại Học Quốc Gia', 2022, 12, 12, 165000, N'Chiến lược và mẫu câu viết luận IELTS band 7+'),-- 49
(N'Từ Vựng Tiếng Anh Học Thuật', 14, 6, N'Cambridge University Press', 2020, 15, 15, 220000, N'Hệ thống từ vựng học thuật phục vụ đọc, viết nghiên cứu'), -- 50
(N'Giáo Trình Tiếng Nhật Sơ Cấp', 14, 6, N'NXB Đại Học Quốc Gia', 2019, 10, 10, 130000, N'Giáo trình tiếng Nhật trình độ sơ cấp N5-N4'),                    -- 51
(N'Cẩm Nang Trích Dẫn Và Chống Đạo Văn', 12, 10, N'NXB Đại Học Quốc Gia', 2022, 8, 8, 45000, N'Hướng dẫn trích dẫn nguồn và tránh đạo văn trong nghiên cứu'),-- 52
(N'Phương Pháp Viết Bài Báo Khoa Học', 12, 10, N'NXB Đại Học Quốc Gia', 2021, 7, 7, 60000, N'Hướng dẫn cấu trúc và cách viết bài báo khoa học'),            -- 53
(N'Hướng Dẫn Bảo Vệ Đồ Án Tốt Nghiệp', 12, 10, N'NXB Đại Học Quốc Gia', 2022, 6, 6, 50000, N'Kỹ năng chuẩn bị slide và trả lời phản biện đồ án tốt nghiệp'),-- 54
(N'Giáo Trình Marketing Căn Bản', 10, 3, N'NXB Đại Học Kinh Tế Quốc Dân', 2020, 15, 15, 90000, N'Giáo trình nguyên lý marketing căn bản'),                  -- 55
(N'Giáo Trình Quản Trị Nhân Lực', 10, 3, N'NXB Đại Học Kinh Tế Quốc Dân', 2021, 12, 12, 90000, N'Giáo trình quản trị nguồn nhân lực trong doanh nghiệp'),  -- 56
(N'Giáo Trình Tài Chính Doanh Nghiệp', 10, 3, N'NXB Tài Chính', 2020, 12, 12, 95000, N'Giáo trình nguyên lý tài chính doanh nghiệp'),                       -- 57
(N'Giáo Trình Lịch Sử Đảng Cộng Sản Việt Nam', 12, 1, N'NXB Chính Trị Quốc Gia', 2021, 20, 20, 60000, N'Giáo trình lý luận chính trị bắt buộc'),            -- 58
(N'Giáo Trình Kinh Tế Chính Trị Mác - Lênin', 10, 1, N'NXB Chính Trị Quốc Gia', 2021, 18, 18, 60000, N'Giáo trình lý luận chính trị bắt buộc'),             -- 59
(N'Giáo Trình Giải Tích Hàm Nhiều Biến', 11, 5, N'NXB Giáo Dục', 2020, 10, 10, 75000, N'Giáo trình giải tích nâng cao cho sinh viên khối kỹ thuật');       -- 60

-- Doc gia: sinh vien (dia chi mang phong cach ky tuc xa / khu vuc quanh truong)
INSERT INTO docgia (ho_ten, ngay_sinh, gioi_tinh, dia_chi, sdt, email) VALUES
(N'Trần Thị B', '2003-05-12', N'Nữ', N'KTX Khu B, Hà Nội', '0912345678', 'tranthib@gmail.com'),                          -- 1
(N'Lê Văn C', '2002-08-20', N'Nam', N'Ký túc xá B7, Hải Phòng', '0987654321', 'levanc@gmail.com'),                        -- 2
(N'Nguyễn Hoàng Anh', '2001-03-15', N'Nam', N'123 Cầu Giấy, Hà Nội', '0981122334', 'hoanganh.nguyen@gmail.com'),          -- 3
(N'Phạm Thị Minh Châu', '1999-11-20', N'Nữ', N'45 Lê Lợi, Quận 1, TP.HCM', '0903456789', 'chou.pham99@gmail.com'),        -- 4
(N'Đặng Quốc Hoàng', '2004-01-10', N'Nam', N'88 Trần Phú, Đà Nẵng', '0918765432', 'hoangdq2004@gmail.com'),               -- 5
(N'Vũ Phương Thảo', '2002-09-05', N'Nữ', N'KTX Mễ Trì, Hà Nội', '0978999888', 'thaovp.nuce@gmail.com'),                   -- 6
(N'Bùi Minh Tuấn', '2000-07-25', N'Nam', N'56 Nguyễn Văn Linh, Hải Phòng', '0934112233', 'tuanbm2000@gmail.com'),         -- 7
(N'Đỗ Khánh Linh', '2003-12-18', N'Nữ', N'102 Điện Biên Phủ, Huế', '0965443322', 'khanhlinh.do@gmail.com'),               -- 8
(N'Lê Hoàng Nam', '1998-04-30', N'Nam', N'78 Nguyễn Huệ, Cần Thơ', '0942556677', 'namlh.backend@gmail.com'),              -- 9
(N'Trịnh Thu Trang', '2005-06-14', N'Nữ', N'15 Quang Trung, Hà Đông, Hà Nội', '0911009988', 'trangtrinh05@gmail.com'),    -- 10
(N'Ngô Thanh Sơn', '2003-02-14', N'Nam', N'KTX Khu B, Đại Học Quốc Gia', '0977001122', 'sonnt.dhqg@gmail.com'),           -- 11
(N'Hoàng Thị Yến', '2004-05-09', N'Nữ', N'KTX Khu A, Đại Học Quốc Gia', '0966112233', 'yenht.dhqg@gmail.com'),            -- 12
(N'Phan Đức Anh', '2001-10-30', N'Nam', N'20 Tạ Quang Bửu, Hà Nội', '0988223344', 'anhpd.bku@gmail.com'),                 -- 13
(N'Trương Gia Bảo', '2002-12-01', N'Nam', N'Ký túc xá B7, Bách Khoa Hà Nội', '0933445566', 'baotg.hust@gmail.com'),       -- 14
(N'Lâm Ngọc Diệp', '2003-07-18', N'Nữ', N'15 Lê Thanh Nghị, Hà Nội', '0944556677', 'diepln@gmail.com'),                  -- 15
(N'Đinh Công Chiến', '2000-01-22', N'Nam', N'KTX Mễ Trì, Hà Nội', '0955667788', 'chiendc@gmail.com');                     -- 16

-- Phieu muon: du lieu trai dai Thang 7 - Thang 9/2026 (khop voi ngay he thong hien tai la 2026-09-15),
-- bao phu du 4 trang thai (DANG_MUON, DA_TRA, QUA_HAN, DA_HUY) va nhieu doc gia/dau sach de cac trang
-- Trang chu, Thong ke, Danh sach den co du lieu thuc te de hien thi.
-- ma_pm duoc gan tu 1 den 19 theo dung thu tu chen (dung lam khoa ngoai o chitietphieumuon ben duoi).
INSERT INTO phieumuon (ma_dg, ma_tk, ngay_muon, ngay_hen_tra, trang_thai, ghi_chu) VALUES
(1, 2, '2026-09-01', '2026-09-15', 'DA_TRA',   N'Đã trả đúng hạn'),                            -- 1
(2, 2, '2026-09-10', '2026-09-24', 'DANG_MUON',N'Mượn đọc phục vụ làm đồ án'),                  -- 2
(3, 2, '2026-08-27', '2026-09-10', 'QUA_HAN',  N'Đã quá hạn trả, đã nhắn tin nhắc nhở'),        -- 3
(4, 1, '2026-08-15', '2026-08-29', 'DA_TRA',   N'Mượn tham khảo ôn thi giữa kỳ'),               -- 4
(5, 2, '2026-09-05', '2026-09-19', 'DANG_MUON',N'Mượn giáo trình học kỳ mới'),                  -- 5
(6, 2, '2026-08-20', '2026-09-03', 'QUA_HAN',  N'Quá hạn, độc giả đang đi thực tập xa'),        -- 6
(7, 1, '2026-09-08', '2026-09-22', 'DANG_MUON',N'Mượn giáo trình phục vụ đồ án môn học'),       -- 7
(8, 2, '2026-08-01', '2026-08-15', 'DA_TRA',   N'Trả trễ 5 ngày, đã nộp phạt'),                 -- 8
(9, 2, '2026-09-12', '2026-09-26', 'DANG_MUON',N'Mượn tham khảo học phần Pháp luật'),           -- 9
(10,1, '2026-07-20', '2026-08-03', 'DA_HUY',   N'Lập nhầm phiếu, đã hủy'),                      -- 10
(11,2, '2026-09-02', '2026-09-16', 'DANG_MUON',N'Mượn tham khảo môn Trí tuệ nhân tạo'),         -- 11
(12,2, '2026-08-25', '2026-09-08', 'QUA_HAN',  N'Quá hạn, chưa liên hệ được độc giả'),          -- 12
(13,1, '2026-09-11', '2026-09-25', 'DANG_MUON',N'Mượn phục vụ buổi seminar kỹ năng mềm'),       -- 13
(14,2, '2026-08-10', '2026-08-24', 'DA_TRA',   N'Đã trả đúng hạn'),                             -- 14
(15,2, '2026-09-06', '2026-09-20', 'DANG_MUON',N'Mượn tham khảo phương pháp NCKH'),             -- 15
(16,1, '2026-08-05', '2026-08-19', 'QUA_HAN',  N'Quá hạn lâu ngày, cần liên hệ gấp'),           -- 16
(1, 2, '2026-08-01', '2026-08-15', 'DA_TRA',   N'Trả trễ 3 ngày, đã nộp phạt'),                 -- 17
(2, 1, '2026-09-13', '2026-09-27', 'DANG_MUON',N'Mượn giáo trình học kỳ mới'),                  -- 18
(9, 2, '2026-09-01', '2026-09-15', 'DA_HUY',   N'Độc giả đổi ý, hủy mượn');                     -- 19

-- Chi tiet phieu muon: tien_phat cua cac dong CHUA TRA (da_tra = 0) luon de 0 - he thong CHI tinh va
-- CHOT tien phat that su tai thoi diem ghi nhan tra sach (xem PhieuMuonDAO.traSach). Voi sach dang
-- qua han nhung chua tra, man hinh Chi tiet phieu muon / Danh sach den se tu UOC TINH tien phat theo
-- cong thuc so_ngay_tre * 5.000d * so_luong (Constants.TIEN_PHAT_MOI_NGAY), khong doc gia tri cot nay.
INSERT INTO chitietphieumuon (ma_pm, ma_sach, so_luong, ngay_tra_thucte, tien_phat, da_tra) VALUES
(1, 2, 1, '2026-09-14', 0, 1),      -- PM1: Clean Code - trả sớm hơn hạn 1 ngày
(1, 3, 1, '2026-09-14', 0, 1),      -- PM1: Đắc Nhân Tâm
(2, 1, 1, NULL, 0, 0),              -- PM2: Cho Tôi Xin Một Vé Đi Tuổi Thơ (đang mượn)
(2, 5, 1, NULL, 0, 0),              -- PM2: Nhà Giả Kim (đang mượn)
(3, 4, 1, NULL, 0, 0),              -- PM3: Dế Mèn Phiêu Lưu Ký (quá hạn ~5 ngày)
(4, 25,1, '2026-08-28', 0, 1),      -- PM4: Giáo Trình Toán Cao Cấp A1 - trả đúng hạn
(5, 21,1, NULL, 0, 0),              -- PM5: Giáo Trình Kinh Tế Vi Mô (đang mượn)
(5, 22,1, NULL, 0, 0),              -- PM5: Giáo Trình Kinh Tế Vĩ Mô (đang mượn)
(6, 16,1, NULL, 0, 0),              -- PM6: Cấu Trúc Dữ Liệu Và Giải Thuật (quá hạn ~12 ngày)
(7, 30,1, NULL, 0, 0),              -- PM7: Nhập Môn Cơ Sở Dữ Liệu (đang mượn)
(7, 31,1, NULL, 0, 0),              -- PM7: Mạng Máy Tính Căn Bản (đang mượn)
(8, 3, 1, '2026-08-20', 25000, 1),  -- PM8: Đắc Nhân Tâm - trả trễ 5 ngày (5*5.000)
(9, 24,1, NULL, 0, 0),              -- PM9: Giáo Trình Pháp Luật Đại Cương (đang mượn)
(10,20,1, NULL, 0, 0),              -- PM10: English Grammar In Use (đã hủy, không phát sinh phạt)
(11,34,1, NULL, 0, 0),              -- PM11: Nhập Môn Trí Tuệ Nhân Tạo (đang mượn)
(12,28,1, NULL, 0, 0),              -- PM12: Giáo Trình Vật Lý Đại Cương (quá hạn ~7 ngày)
(12,29,1, NULL, 0, 0),              -- PM12: Giáo Trình Hóa Học Đại Cương (quá hạn ~7 ngày)
(13,36,1, NULL, 0, 0),              -- PM13: Kỹ Năng Thuyết Trình Và Làm Việc Nhóm (đang mượn)
(14,32,1, '2026-08-23', 0, 1),      -- PM14: Hệ Điều Hành - trả đúng hạn
(14,33,1, '2026-08-23', 0, 1),      -- PM14: Kỹ Thuật Lập Trình C++ - trả đúng hạn
(15,37,1, NULL, 0, 0),              -- PM15: Phương Pháp Nghiên Cứu Khoa Học (đang mượn)
(16,38,1, NULL, 0, 0),              -- PM16: Hướng Dẫn Viết Luận Văn Tốt Nghiệp (quá hạn ~27 ngày)
(17,39,1, '2026-08-18', 15000, 1),  -- PM17: Giáo Trình Quản Trị Học - trả trễ 3 ngày (3*5.000)
(18,40,1, NULL, 0, 0),              -- PM18: Nguyên Lý Kế Toán (đang mượn)
(18,27,1, NULL, 0, 0),              -- PM18: Giáo Trình Xác Suất Thống Kê (đang mượn)
(19,40,1, NULL, 0, 0);              -- PM19: Nguyên Lý Kế Toán (đã hủy, không phát sinh phạt)
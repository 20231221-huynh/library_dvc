-- =========================================================
-- MIGRATION: Bo sung rang buoc UNIQUE de chong trung lap du lieu
-- Dung cho database library_dvc DA TON TAI SAN (khong xoa du lieu).
-- Neu ban dung file library_dvc.sql moi de tao lai CSDL tu dau
-- thi KHONG can chay file nay (cac rang buoc da co san trong do).
--
-- LUU Y QUAN TRONG: neu du lieu hien tai dang bi trung lap
-- (vi du 2 tac gia cung ten + cung quoc tich, 2 sach cung ten
-- + cung tac gia, the loai trung ten, doc gia trung sdt/email),
-- cac lenh ALTER TABLE ben duoi se BAO LOI va khong them duoc
-- rang buoc. Hay chay cac truy van kiem tra & don dep du lieu
-- trung lap (xem phan cuoi file) TRUOC khi chay migration nay.
-- =========================================================
USE library_dvc;

-- 1. The loai: khong duoc trung ten
ALTER TABLE theloai
    ADD UNIQUE KEY uq_theloai_ten (ten_theloai);

-- 2. Tac gia: khong duoc trung (ten_tacgia, quoc_tich)
ALTER TABLE tacgia
    ADD UNIQUE KEY uq_tacgia_ten_quoctich (ten_tacgia, quoc_tich);

-- 3. Sach: khong duoc trung (ten_sach, ma_tg)
ALTER TABLE sach
    ADD UNIQUE KEY uq_sach_ten_tacgia (ten_sach, ma_tg);

-- 4. Doc gia: khong duoc trung so dien thoai / email
--    (MySQL cho phep nhieu gia tri NULL trong cot UNIQUE nen doc gia
--     chua nhap sdt/email van them/sua binh thuong).
ALTER TABLE docgia
    ADD UNIQUE KEY uq_docgia_sdt (sdt);

ALTER TABLE docgia
    ADD UNIQUE KEY uq_docgia_email (email);

-- =========================================================
-- (Tuy chon) TRUY VAN KIEM TRA DU LIEU TRUNG LAP CO SAN
-- Chay truoc migration neu nghi ngo co du lieu trung, roi tu
-- quyet dinh giu ban ghi nao / xoa ban ghi nao truoc khi ALTER.
-- =========================================================

-- The loai trung ten:
-- SELECT ten_theloai, COUNT(*) FROM theloai GROUP BY ten_theloai HAVING COUNT(*) > 1;

-- Tac gia trung ten + quoc tich:
-- SELECT ten_tacgia, quoc_tich, COUNT(*) FROM tacgia GROUP BY ten_tacgia, quoc_tich HAVING COUNT(*) > 1;

-- Sach trung ten + tac gia:
-- SELECT ten_sach, ma_tg, COUNT(*) FROM sach GROUP BY ten_sach, ma_tg HAVING COUNT(*) > 1;

-- Doc gia trung so dien thoai:
-- SELECT sdt, COUNT(*) FROM docgia WHERE sdt IS NOT NULL AND sdt <> '' GROUP BY sdt HAVING COUNT(*) > 1;

-- Doc gia trung email:
-- SELECT email, COUNT(*) FROM docgia WHERE email IS NOT NULL AND email <> '' GROUP BY email HAVING COUNT(*) > 1;

-- =========================================================
-- MIGRATION: Bo sung trang thai "DA_HUY" va cot dem so lan gia han cho phieu muon
-- Dung cho database library_dvc DA TON TAI SAN (khong xoa du lieu).
-- Neu ban dung file library_dvc.sql moi de tao lai CSDL tu dau thi KHONG can chay file nay
-- (cac thay doi da co san trong do).
--
-- Phuc vu 2 nghiep vu moi:
--   1) Gia han ngay hen tra (chi ap dung cho phieu chua tra het, gioi han so lan gia han)
--   2) Huy toan bo phieu muon (chi khi CHUA co dong sach nao duoc tra), hoan tra lai ton kho
--      va chuyen trang thai sang DA_HUY thay vi xoa han ban ghi (giu lai lich su/audit trail).
-- =========================================================
USE library_dvc;

-- 1. Them gia tri 'DA_HUY' vao ENUM trang_thai cua bang phieumuon.
--    MySQL khong ho tro ALTER TYPE cho ENUM nen phai dung MODIFY COLUMN voi danh sach gia tri day du.
ALTER TABLE phieumuon
    MODIFY trang_thai ENUM('DANG_MUON','DA_TRA','QUA_HAN','DA_HUY') NOT NULL DEFAULT 'DANG_MUON';

-- 2. Them cot dem so lan da gia han ngay hen tra (mac dinh 0 cho tat ca cac phieu da co san).
ALTER TABLE phieumuon
    ADD COLUMN so_lan_gia_han INT NOT NULL DEFAULT 0 AFTER trang_thai;

package com.dvc.library.model;

import java.time.LocalDate;
import java.util.List;

public class PhieuMuon {
    private int maPm;
    private int maDg;
    private int maTk;
    private String tenDocGia;
    private String tenNhanVien;
    private LocalDate ngayMuon;
    private LocalDate ngayHenTra;
    private String trangThai; // DANG_MUON | DA_TRA | QUA_HAN | DA_HUY
    private int soLanGiaHan;
    private String ghiChu;
    private List<ChiTietPhieuMuon> chiTietList;

    public PhieuMuon() {}

    public int getMaPm() { return maPm; }
    public void setMaPm(int maPm) { this.maPm = maPm; }
    public int getMaDg() { return maDg; }
    public void setMaDg(int maDg) { this.maDg = maDg; }
    public int getMaTk() { return maTk; }
    public void setMaTk(int maTk) { this.maTk = maTk; }
    public String getTenDocGia() { return tenDocGia; }
    public void setTenDocGia(String tenDocGia) { this.tenDocGia = tenDocGia; }
    public String getTenNhanVien() { return tenNhanVien; }
    public void setTenNhanVien(String tenNhanVien) { this.tenNhanVien = tenNhanVien; }
    public LocalDate getNgayMuon() { return ngayMuon; }
    public void setNgayMuon(LocalDate ngayMuon) { this.ngayMuon = ngayMuon; }
    public LocalDate getNgayHenTra() { return ngayHenTra; }
    public void setNgayHenTra(LocalDate ngayHenTra) { this.ngayHenTra = ngayHenTra; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public int getSoLanGiaHan() { return soLanGiaHan; }
    public void setSoLanGiaHan(int soLanGiaHan) { this.soLanGiaHan = soLanGiaHan; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    public List<ChiTietPhieuMuon> getChiTietList() { return chiTietList; }
    public void setChiTietList(List<ChiTietPhieuMuon> chiTietList) { this.chiTietList = chiTietList; }
}

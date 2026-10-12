package com.dvc.library.model;

import java.math.BigDecimal;

public class Sach {
    private int maSach;
    private String tenSach;
    private int maTg;
    private int maTl;
    private String tenTacGia;
    private String tenTheLoai;
    private String nhaXuatBan;
    private int namXb;
    private int soLuong;
    private int soLuongCon;
    private BigDecimal donGia;
    private String moTa;
    private String hinhAnh; // ten file hinh anh bia sach
    private int trangThai;

    public Sach() {}

    public int getMaSach() { return maSach; }
    public void setMaSach(int maSach) { this.maSach = maSach; }
    public String getTenSach() { return tenSach; }
    public void setTenSach(String tenSach) { this.tenSach = tenSach; }
    public int getMaTg() { return maTg; }
    public void setMaTg(int maTg) { this.maTg = maTg; }
    public int getMaTl() { return maTl; }
    public void setMaTl(int maTl) { this.maTl = maTl; }
    public String getTenTacGia() { return tenTacGia; }
    public void setTenTacGia(String tenTacGia) { this.tenTacGia = tenTacGia; }
    public String getTenTheLoai() { return tenTheLoai; }
    public void setTenTheLoai(String tenTheLoai) { this.tenTheLoai = tenTheLoai; }
    public String getNhaXuatBan() { return nhaXuatBan; }
    public void setNhaXuatBan(String nhaXuatBan) { this.nhaXuatBan = nhaXuatBan; }
    public int getNamXb() { return namXb; }
    public void setNamXb(int namXb) { this.namXb = namXb; }
    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
    public int getSoLuongCon() { return soLuongCon; }
    public void setSoLuongCon(int soLuongCon) { this.soLuongCon = soLuongCon; }
    public BigDecimal getDonGia() { return donGia; }
    public void setDonGia(BigDecimal donGia) { this.donGia = donGia; }
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
    public String getHinhAnh() { return hinhAnh; }
    public void setHinhAnh(String hinhAnh) { this.hinhAnh = hinhAnh; }
    public int getTrangThai() { return trangThai; }
    public void setTrangThai(int trangThai) { this.trangThai = trangThai; }
}

package com.dvc.library.model;

import java.time.LocalDateTime;

public class TaiKhoan {
    private int maTk;
    private String tenDangNhap;
    private String matKhau;
    private String hoTen;
    private String vaiTro; // ADMIN | THUTHU
    private int trangThai; // 1: hoat dong, 0: khoa
    private LocalDateTime ngayTao;

    public TaiKhoan() {}

    public TaiKhoan(int maTk, String tenDangNhap, String matKhau, String hoTen, String vaiTro, int trangThai) {
        this.maTk = maTk;
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.hoTen = hoTen;
        this.vaiTro = vaiTro;
        this.trangThai = trangThai;
    }

    public int getMaTk() { return maTk; }
    public void setMaTk(int maTk) { this.maTk = maTk; }
    public String getTenDangNhap() { return tenDangNhap; }
    public void setTenDangNhap(String tenDangNhap) { this.tenDangNhap = tenDangNhap; }
    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public String getVaiTro() { return vaiTro; }
    public void setVaiTro(String vaiTro) { this.vaiTro = vaiTro; }
    public int getTrangThai() { return trangThai; }
    public void setTrangThai(int trangThai) { this.trangThai = trangThai; }
    public LocalDateTime getNgayTao() { return ngayTao; }
    public void setNgayTao(LocalDateTime ngayTao) { this.ngayTao = ngayTao; }

    public boolean isAdmin() { return "ADMIN".equalsIgnoreCase(vaiTro); }
}

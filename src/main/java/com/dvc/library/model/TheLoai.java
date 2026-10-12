package com.dvc.library.model;

public class TheLoai {
    private int maTl;
    private String tenTheLoai;
    private String moTa;
   
    private int soLuongSach;

    public TheLoai() {}
    public TheLoai(int maTl, String tenTheLoai, String moTa) {
        this.maTl = maTl; this.tenTheLoai = tenTheLoai; this.moTa = moTa;
    }

    public int getMaTl() { return maTl; }
    public void setMaTl(int maTl) { this.maTl = maTl; }
    public String getTenTheLoai() { return tenTheLoai; }
    public void setTenTheLoai(String tenTheLoai) { this.tenTheLoai = tenTheLoai; }
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
    public int getSoLuongSach() { return soLuongSach; }
    public void setSoLuongSach(int soLuongSach) { this.soLuongSach = soLuongSach; }
}

package com.dvc.library.model;

public class TacGia {
    private int maTg;
    private String tenTacGia;
    private String quocTich;

    public TacGia() {}
    public TacGia(int maTg, String tenTacGia, String quocTich) {
        this.maTg = maTg; this.tenTacGia = tenTacGia; this.quocTich = quocTich;
    }

    public int getMaTg() { return maTg; }
    public void setMaTg(int maTg) { this.maTg = maTg; }
    public String getTenTacGia() { return tenTacGia; }
    public void setTenTacGia(String tenTacGia) { this.tenTacGia = tenTacGia; }
    public String getQuocTich() { return quocTich; }
    public void setQuocTich(String quocTich) { this.quocTich = quocTich; }
}

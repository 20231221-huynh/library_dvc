package com.dvc.library.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ChiTietPhieuMuon {
    private int maCt;
    private int maPm;
    private int maSach;
    private String tenSach;
    private int soLuong;
    private LocalDate ngayTraThucTe;
    private BigDecimal tienPhat;
    private int daTra;
    
    private BigDecimal tienPhatUocTinh;

    public ChiTietPhieuMuon() {}

    public int getMaCt() { return maCt; }
    public void setMaCt(int maCt) { this.maCt = maCt; }
    public int getMaPm() { return maPm; }
    public void setMaPm(int maPm) { this.maPm = maPm; }
    public int getMaSach() { return maSach; }
    public void setMaSach(int maSach) { this.maSach = maSach; }
    public String getTenSach() { return tenSach; }
    public void setTenSach(String tenSach) { this.tenSach = tenSach; }
    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
    public LocalDate getNgayTraThucTe() { return ngayTraThucTe; }
    public void setNgayTraThucTe(LocalDate ngayTraThucTe) { this.ngayTraThucTe = ngayTraThucTe; }
    public BigDecimal getTienPhat() { return tienPhat; }
    public void setTienPhat(BigDecimal tienPhat) { this.tienPhat = tienPhat; }
    public int getDaTra() { return daTra; }
    public void setDaTra(int daTra) { this.daTra = daTra; }
    public BigDecimal getTienPhatUocTinh() { return tienPhatUocTinh; }
    public void setTienPhatUocTinh(BigDecimal tienPhatUocTinh) { this.tienPhatUocTinh = tienPhatUocTinh; }
}

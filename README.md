# THU VIEN DVC - He thong Quan ly Thu vien (Java Servlet/JSP)

Do an mon hoc: Cong nghe Java
Cong nghe: Jakarta Servlet 6.0 (jakarta.*), JSP, JSTL, CDI (Weld), MySQL (JDBC thuan), Bootstrap 5, Maven

## 1. Yeu cau moi truong

- JDK 11+
- **Apache Tomcat 10.1.x** (ho tro Jakarta Servlet 6.0 / namespace `jakarta.*`)
  - **QUAN TRONG**: du an nay dung namespace `jakarta.*` nen **KHONG chay duoc tren Tomcat 9.x tro xuong**
    (Tomcat 9 chi ho tro `javax.*` - Servlet 4.0). Bat buoc phai dung Tomcat 10.1.x tro len.
  - Tomcat 10.0.x (Servlet 5.0) cung dung duoc ve mat ky thuat nhung da EOL (het ho tro), nen dung 10.1.x.
- MySQL 8.x
- Maven 3.6+
- IDE: Eclipse / IntelliJ IDEA / VS Code (co plugin Java EE / Jakarta EE, nho cau hinh server la Tomcat 10.1.x)

## 2. Cai dat CSDL

1. Mo MySQL, chay script: `database/library_dvc.sql`
   ```
   mysql -u root -p < database/library_dvc.sql
   ```
2. Script se tu tao database `library_dvc`, cac bang (bao gom cot `hinh_anh` trong bang `sach`), va du lieu mau.

   **Luu y**: Neu ban da tao CSDL `library_dvc` tu truoc (phien ban chua co cot `hinh_anh`) va khong muon mat du lieu da nhap,
   KHONG chay lai `library_dvc.sql` (vi se xoa va tao lai tu dau). Thay vao do chi can chay script migration:
   ```
   mysql -u root -p < database/migration_add_hinhanh.sql
   ```

3. Kiem tra / sua thong tin ket noi trong file:
   `src/main/resources/db.properties`
   ```
   db.url=jdbc:mysql://localhost:3306/library_dvc?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Ho_Chi_Minh
   db.username=root
   db.password=          <-- sua thanh mat khau MySQL cua ban
   ```

## 3. Build va chay du an

### Cach 1: Dung Maven CLI
```bash
mvn clean package
```
File `library-dvc.war` se duoc tao trong thu muc `target/`.
Copy file nay vao thu muc `webapps` cua Tomcat, khoi dong Tomcat, sau do truy cap:
```
http://localhost:8080/library-dvc/
```

### Cach 2: Dung IDE (Eclipse/IntelliJ)
1. Import du an dang "Existing Maven Project".
2. Cau hinh Tomcat Server **phien ban 10.1.x** trong IDE (khong dung Tomcat 9.x vi se loi ClassNotFoundException
   do IDE/server tim class `javax.servlet.*` nhung code va thu vien dung `jakarta.servlet.*`), gan du an vao server.
3. Run on Server.

## 3.1. Ghi chu ve viec nang cap tu javax.* len jakarta.*

Du an da duoc chuyen doi hoan toan tu namespace `javax.servlet.*` (Tomcat 9 / Java EE 8) sang
`jakarta.servlet.*` (Tomcat 10.1.x / Jakarta EE 10), bao gom:

- `pom.xml`: doi dependency `javax.servlet:javax.servlet-api` → `jakarta.servlet:jakarta.servlet-api:6.0.0`;
  doi `javax.servlet:jstl:1.2` → `jakarta.servlet.jsp.jstl:jakarta.servlet.jsp.jstl-api:3.0.0` +
  `org.glassfish.web:jakarta.servlet.jsp.jstl:3.0.1`.
- Toan bo cac lop Servlet/Filter: doi import `javax.servlet.*` → `jakarta.servlet.*`.
- `web.xml`: doi khai bao namespace sang `https://jakarta.ee/xml/ns/jakartaee`, schema `web-app_6_0.xsd`, `version="6.0"`.
- Cac trang JSP **khong can sua** vi hai URI taglib JSTL cu (`http://java.sun.com/jsp/jstl/core`,
  `.../fmt`) van duoc dac ta Jakarta Tags 3.0 tiep tuc ho tro nguoc (backward-compatible), du thu vien JSTL
  ben duoi da la ban Jakarta.

**Neu deploy len mot server Tomcat 9.x co san (khong nang cap duoc)**: bat buoc phai revert lai toan bo cac
thay doi tren ve `javax.*` (khong the chay song song hai namespace tren cung mot Tomcat).

## 3.2. Ghi chu ve viec them CDI (Contexts and Dependency Injection) bang Weld

Du an da duoc bo sung CDI (dung Weld - CDI Reference Implementation) de quan ly vong doi va tu dong
"tiem" (inject) cac doi tuong DAO vao Servlet, thay vi tung Servlet tu `new` rieng mot ban DAO cho minh.

**Vi sao can them thu vien rieng (Weld) thay vi dung CDI co san?**
Tomcat la mot Servlet Container thuan (chi hien thuc dac ta Servlet/JSP), KHONG phai la mot Application
Server day du theo chuan Jakarta EE (khac voi WildFly, Payara, GlassFish, Open Liberty... - cac server nay
co san CDI container tich hop khong can cai them gi). Do do tren Tomcat phai tu nhung them mot thu vien
hien thuc CDI vao ung dung - o day dung **Weld** (ban `weld-servlet-shaded`), la CDI Reference
Implementation chinh thuc, duoc chinh Jakarta EE dua ra lam bo dac ta tham chieu.

**Cac thay doi cu the:**

1. `pom.xml`: them dependency `org.jboss.weld.servlet:weld-servlet-shaded:5.1.5.Final`
   (phien ban 5.x tuong ung Tomcat 10.1.x / Jakarta EE 10; neu nang Tomcat 11.x sau nay phai doi sang Weld 6.x).
2. `WEB-INF/beans.xml` (file moi): su hien dien cua file nay bao cho container biet day la mot
   "CDI bean archive" va kich hoat CDI quet toan bo ung dung. Dung `bean-discovery-mode="annotated"`
   (mac dinh) - nghia la chi cac class co gan san mot "bean-defining annotation" (vi du `@ApplicationScoped`)
   moi duoc CDI tu dong nhan la bean va quan ly.
3. `web.xml`: dang ky `<listener>org.jboss.weld.environment.servlet.Listener</listener>` - day la thanh
   phan cua Weld chiu trach nhiem khoi dong CDI container khi ung dung start, va thuc hien injection vao
   cac Servlet/Filter ma Tomcat tao ra (theo dac ta tich hop CDI - Servlet Container).
4. Tat ca cac lop DAO (`SachDAO`, `TheLoaiDAO`, `TacGiaDAO`, `DocGiaDAO`, `PhieuMuonDAO`, `TaiKhoanDAO`)
   duoc gan `@ApplicationScoped` - nghia la CDI chi tao **DUY NHAT MOT the hien** cho moi lop DAO, dung
   chung cho toan bo ung dung trong suot vong doi cua no (thay vi moi Servlet tu tao rieng mot ban).
   Day la an toan vi cac DAO khong luu tru bat ky trang thai (state) nao giua cac lan goi - moi phuong
   thuc tu mo va dong rieng mot `Connection` JDBC cuc bo, khong chia se du lieu giua cac request.
5. Tat ca cac lop Servlet: doi field DAO tu kieu
   ```java
   private final SachDAO sachDAO = new SachDAO();
   ```
   sang
   ```java
   @Inject
   private SachDAO sachDAO;
   ```
   CDI (thong qua Weld) se tu dong gan gia tri cho field nay ngay sau khi Servlet duoc Tomcat khoi tao,
   truoc khi bat ky request nao duoc xu ly.
6. Rieng `PhieuMuonDAO` cung duoc chuyen sang `@Inject private SachDAO sachDAO;` (thay vi tu `new`) vi
   chinh no cung can dung lai `SachDAO` de tang/giam so luong sach ton kho trong luc xu ly giao dich
   muon/tra sach - the hien viec CDI co the inject xuyen suot nhieu tang (DAO inject vao DAO), khong chi
   gioi han o tang Servlet.

**Loi ich mang lai:**
- Giam trung lap: khong con moi Servlet tu viet `new XxxDAO()` rai rac o nhieu noi.
- De kiem thu (unit test) hon trong tuong lai: co the thay the DAO that bang mot DAO gia (mock) khi
  can viet test cho Servlet, ma khong phai sua code Servlet.
- Dung mot the hien DAO duy nhat toan he thong (ApplicationScoped) giup tiet kiem bo nho so voi viec
  moi Servlet tu tao rieng mot ban giong het nhau.
- Mo duong de ap dung them cac tinh nang khac cua CDI trong tuong lai neu can (vi du: `@RequestScoped`
  cho bean luu du lieu rieng theo tung request, CDI Events de giao tiep long-coupling giua cac module,
  Interceptor/`@Transactional`-style de tach logic xu ly giao dich ra khoi DAO...).

## 4. Tai khoan mac dinh (du lieu mau)

| Ten dang nhap | Mat khau | Vai tro          |
|----------------|----------|-------------------|
| admin          | 123456   | Quan tri vien (ADMIN) |
| thuthu1        | 123456   | Thu thu (THUTHU)  |

## 5. Cac chuc nang chinh

- Dang nhap / Dang xuat, phan quyen ADMIN va THUTHU
- Quan ly Sach (them / sua / xoa / tim kiem theo ten, the loai, tac gia), **co the tai anh bia sach len khi them/sua sach**
- Quan ly The loai sach
- Quan ly Tac gia
- Quan ly Doc gia (them / sua / xoa / tim kiem)
- Lap phieu muon sach (nhieu dau sach / 1 phieu), tra sach, tinh tien phat tra tre tu dong
- Tu dong cap nhat trang thai "Qua han" cho phieu muon qua ngay hen tra
- Thong ke: Top sach muon nhieu nhat, Top doc gia muon nhieu nhat, so lieu tong quan
- Quan ly tai khoan nhan vien (chi danh cho ADMIN)

## 5.1. Ghi chu ve tinh nang tai anh bia sach

- Anh bia duoc luu vat ly trong thu muc `src/main/webapp/assets/uploads/sach` cua ung dung dang chay
  (Tomcat tu tao thu muc nay neu chua co).
- Dinh dang cho phep: JPG, JPEG, PNG, GIF, WEBP; dung luong toi da 5MB/anh.
- **Luu y quan trong**: neu deploy dang WAR (khong phai "exploded"/thu muc giai nen), khi Tomcat redeploy lai
  (build lai project) thu muc anh vua upload co the bi xoa vi WAR duoc giai nen lai tu dau. Day la gioi han
  binh thuong cua cach luu file don gian nay (phu hop pham vi do an mon hoc). Neu muon anh ton tai vinh vien
  qua nhieu lan deploy, co the sua `DBHelper`/`SachServlet` de luu anh ra mot thu muc ben ngoai project
  (vi du dung mot duong dan tuyet doi tren o dia, cau hinh qua file properties) thay vi trong `webapp/assets/uploads`.

## 6. Cau truc thu muc du an

Xem chi tiet trong tai lieu thiet ke (file docx dinh kem).

```
library-dvc/
 |- pom.xml
 |- database/
 |   `- library_dvc.sql
 |- src/main/java/com/dvc/library/
 |   |- model/       (thuc the: Sach, DocGia, TheLoai, TacGia, PhieuMuon, ChiTietPhieuMuon, TaiKhoan)
 |   |- dao/         (lop truy xuat du lieu JDBC)
 |   |- servlet/     (dieu khien - Controller)
 |   |- filter/      (AuthFilter, AdminFilter)
 |   `- util/        (DBConnection, PasswordUtil, Constants)
 |- src/main/resources/
 |   `- db.properties
 `- src/main/webapp/
     |- assets/css/style.css
     |- index.jsp
     `- WEB-INF/
         |- web.xml
         `- views/    (cac trang JSP, to chuc theo module)
```

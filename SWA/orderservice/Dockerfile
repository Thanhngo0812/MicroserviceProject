# ----------------------------------------------------
# TẦNG 1: "BUILDER" (Nhà xưởng để build)
# ----------------------------------------------------
FROM maven:3-eclipse-temurin-17-alpine AS builder

# Đặt thư mục làm việc
WORKDIR /app

# ---- LỚP 1: TỐI ƯU CACHE ----
# 1. Copy tất cả các file pom.xml (Giống hệt như cũ)
COPY pom.xml .
COPY order-domain/pom.xml order-domain/
COPY order-domain/order-domain-core/pom.xml order-domain/order-domain-core/
COPY order-application-service/pom.xml order-application-service/
COPY order-dataaccess/pom.xml order-dataaccess/
COPY order-container/pom.xml order-container/
COPY order-messaging/pom.xml order-messaging/
# (Thêm các module khác nếu có)

# 2. Chạy lệnh "build giả" (Đây là mánh khóe)
# Chúng ta chạy "install" nhưng skip toàn bộ việc biên dịch code.
# - Dmaven.main.skip=true: Bỏ qua biên dịch code main
# - Dmaven.test.skip=true: Bỏ qua biên dịch code test
# - DskipTests: Bỏ qua chạy test
# Loại bỏ Dmaven.main.skip=true để cho phép plugin repackage chạy qua

RUN mvn clean dependency:go-offline
# ----- Lệnh này sẽ: -----
# 1. Tải TẤT CẢ thư viện bên ngoài (Spring, Kafka,...) về -> OK, Đã cache!
# 2. Build các file .jar "rỗng" (chỉ có pom) cho các module nội bộ.
# 3. Cài đặt (install) các file .jar rỗng này vào kho .m2.
# 4. Khi build 'order-application-service' (rỗng), nó sẽ tìm 'order-domain-core'
#    và tìm thấy file .jar rỗng trong kho .m2 -> OK, Build thành công!

# ---- LỚP 2: BUILD THẬT ----
# 3. Copy toàn bộ source code (file .java, .properties...)
#    Lớp này sẽ bị "hủy cache" khi bạn sửa code.
COPY . .

# 4. Chạy lệnh "build thật"
# Lần này, tất cả thư viện bên ngoài đã được cache từ LỚP 1.
# Nó sẽ chỉ tốn thời gian biên dịch code của BẠN (rất nhanh).
RUN mvn clean install -DskipTests

# ----------------------------------------------------
# TẦNG 2: "RUNTIME" (Container chạy production)
# (Phần này giữ nguyên như cũ)
# ----------------------------------------------------
FROM eclipse-temurin:17-jre-alpine
RUN addgroup -g 1000 appgroup && adduser -u 1000 -G appgroup -s /bin/sh -D appuser

# Đặt thư mục làm việc
WORKDIR /app

# Copy file .jar (thành phẩm) từ tầng "builder" sang
COPY --from=builder /app/order-container/target/order-container-0.0.1-SNAPSHOT.jar app.jar

# Đổi chủ sở hữu file sang user mới
RUN chown -R appuser:appgroup /app

# Chuyển sang user mới
USER appuser

# Expose port (Giả sử app của bạn chạy port 8080)
EXPOSE 8080

# Lệnh khởi động ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]
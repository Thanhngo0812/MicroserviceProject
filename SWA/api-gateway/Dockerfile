# ----------------------------------------------------
# TẦNG 1: BUILDER (Tối ưu Cache Dependencies)
# ----------------------------------------------------
FROM maven:3.8.5-openjdk-17-slim AS builder
WORKDIR /app

# 1. Chỉ copy file pom.xml trước
COPY pom.xml .

# 2. Tải toàn bộ thư viện về (.m2 cache)
# Lệnh này sẽ chạy rất lâu lần đầu, nhưng cực nhanh các lần sau nếu pom.xml không đổi
RUN mvn dependency:go-offline -B

# 3. Copy source code
COPY src ./src

# 4. Build ra file .jar (Skip test để build nhanh hơn)
RUN mvn clean package -DskipTests

# ----------------------------------------------------
# TẦNG 2: RUNTIME (Bảo mật & Nhẹ)
# ----------------------------------------------------
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Tạo user non-root để chạy app (Bảo mật hơn)
RUN addgroup -g 1000 appgroup && adduser -u 1000 -G appgroup -s /bin/sh -D appuser

# Copy file jar từ bước builder sang (Đường dẫn target/*.jar)
COPY --from=builder /app/target/*.jar app.jar

# Đổi quyền sở hữu file cho user mới
RUN chown -R appuser:appgroup /app

# Chuyển sang user non-root
USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
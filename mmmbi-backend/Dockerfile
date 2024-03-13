FROM openjdk:8-jdk-alpine
LABEL maintainer="mmm2233 <2350617910@qq.com>"

# 设置时区
#RUN apk add --no-cache tzdata && \
#    cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
#    echo "Asia/Shanghai" > /etc/timezone && \
#    apk del tzdata
# 创建工作目录
RUN mkdir -p /app/mmmbi

# 将所有jar文件添加到对应模块的目录中 => 需要注意的是 , TODO 构建的时候是以dockerfile所在的目录开始的
COPY target/mmmbi-backend.jar .

# 暴露端口号
EXPOSE 8101

# 运行所有jar文件
CMD ["sh", "-c", "java -jar mmmbi-backend.jar --spring.profiles.active=prod"]

## Docker 镜像构建
#FROM maven:3.8.1-jdk-8-slim as builder
#
## Copy local code to the container image.
#WORKDIR /app
#COPY pom.xml .
#COPY src ./src
#
## Build a release artifact.
#RUN mvn package -DskipTests
#
## Run the web service on container startup.
#CMD ["java","-jar","/app/target/mmmbi-backend-0.0.1-SNAPSHOT.jar","--spring.profiles.active=prod"]
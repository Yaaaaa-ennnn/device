# 设备认证 Demo

设备先注册拿密钥，之后改/删时带密钥。Spring Boot 4.2.0 / Java 21 / 内存存储（重启清空）/ 端口 8081。

## 启动

```bash
java -jar target/device-0.0.1-SNAPSHOT.jar
# 或 ./mvnw spring-boot:run
```

## 接口

| 方法 | 路径 | 鉴权 | 说明 |
| --- | --- | --- | --- |
| POST | /device/register | 免 | 自带 deviceId + deviceName，只返回 secret |
| GET | /device/list | 免 | 所有设备的 deviceId + deviceName |
| GET | /device/{id} | 免 | 按 id 直接查 |
| PUT | /device/{id} | 密钥 | 改名，只能改自己 |
| DELETE | /device/{id} | 密钥 | 删除，只能删自己 |

## 规则

1. 注册时 id 由客户端给，重复返回 `409 deviceId already exists`。
2. 注册只回 `{"secret":"..."}`，不返回 id。
3. 查询（列表、按 id）不需要任何密钥。
4. 改和删要带 `X-Device-Id` + `X-Device-Secret`，且只能操作自己。
5. 存储在本机内存 Map，重启全空。

## 快速试

```bash
curl -X POST http://localhost:8082/device/register \
  -H "Content-Type: application/json" \
  -d '{"deviceId":"dev-A1","deviceName":"sensor-A"}'
# {"secret":"...."}

curl http://localhost:8082/device/list
curl http://localhost:8082/device/dev-A1

curl -X PUT http://localhost:8082/device/dev-A1 \
  -H "Content-Type: application/json" \
  -H "X-Device-Id: dev-A1" -H "X-Device-Secret: ...." \
  -d '{"deviceName":"sensor-A-renamed"}'
```

## 自测

服务起来后 `bash src/main/java/com/individual/device/verify.sh`，全部 PASS 即正常。

接口细节见 `API接口文档.md`。

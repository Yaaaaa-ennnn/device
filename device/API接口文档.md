# 设备身份认证 Demo —— 流程与接口文档

版本：0.0.1-SNAPSHOT ｜ 端口：`8082` ｜ 数据存内存（重启即清空）

---

## 一、接口清单

| # | 方法 | 路径 | 要不要密码 | 说明 |
| --- | --- | --- | --- | --- |
| 1 | POST | `/device/register` | 否 | 客户端自带 `deviceId` + `deviceName` 注册，返回 `secret` |
| 2 | GET | `/device/list` | 否 | 查全部设备的 id + name |
| 3 | GET | `/device/{id}` | 否 | 按 id 查单台 |
| 4 | PUT | `/device/{id}` | 是 | 改名，只能改自己 |
| 5 | DELETE | `/device/{id}` | 是 | 删除，只能删自己 |

规则一句话：**注册拿密码 → 查询随便查 → 改/删必须带密码，且只能动自己那台。**

---

## 二、密码放哪里（重点）

- 密码 = 注册接口返回的 `secret`（32 位十六进制），**只在注册响应里出现一次**，之后任何接口都不再返回，必须自己存下来。
- 调用 PUT / DELETE 时，把密码放在**请求头（Header）**里，两个头缺一不可：

```
X-Device-Id: dev-A1            <- 自己的设备 id（和路径里的 {id} 必须一致）
X-Device-Secret: 0b6f1d8c...   <- 注册拿到的 secret 原样放这里
```

- 不放在 body、不放在 URL 参数里；body 里只放 `deviceName`。
- GET 类接口（查询）**不需要也不会校验**这两个头。
- 注册接口本身不需要任何头，只提交 body。

---

## 三、状态码与错误信息

错误响应统一格式：`{"code": 401, "msg": "missing auth header"}`，HTTP 状态码与 `code` 相同。

| HTTP | msg | 触发条件 |
| --- | --- | --- |
| 200 | — | 成功（注册返回 `{"secret":"..."}`；改/删返回 `{"code":200,"msg":"updated/deleted: 设备id"}`） |
| 400 | `deviceId and deviceName required` | 注册时缺字段/空值 |
| 400 | `deviceName required` | 改名时名称空 |
| 401 | `missing auth header` | 改/删时缺 `X-Device-Id` 或 `X-Device-Secret` |
| 401 | `invalid device or secret` | 设备不存在，或 secret 不匹配（含设备已删后继续用旧密钥） |
| 403 | `cannot operate other device` | 改/删时路径 `{id}` ≠ 请求头 `X-Device-Id` |
| 404 | `device not found` | 查询的设备不存在 |
| 409 | `deviceId already exists` | 注册时 id 已被占用 |

判定顺序（改/删）：先看头有没有（401 missing）→ 再看 id+secret 对不对（401 invalid）→ 再看路径 id 是不是自己（403）。

---

## 四、完整调用流程

```bash
BASE=http://localhost:8082

# 1) 注册：id 自己定，只交 id + name，响应只回 secret（不回 id）
curl -s -X POST $BASE/device/register \
  -H "Content-Type: application/json" \
  -d '{"deviceId":"dev-A1","deviceName":"sensor-A"}'
# -> {"secret":"0b6f1d8c4a7e2f9305d1c8b64e7a2f51"}
SECRET=0b6f1d8c4a7e2f9305d1c8b64e7a2f51   # 存好，后面靠它

# 2) 查全部 / 查单个：不用密码
curl -s $BASE/device/list
# -> [{"deviceId":"dev-A1","deviceName":"sensor-A"}]
curl -s $BASE/device/dev-A1
# -> {"deviceId":"dev-A1","deviceName":"sensor-A"}

# 3) 改名：密码放请求头，body 只放新名字
curl -s -X PUT $BASE/device/dev-A1 \
  -H "Content-Type: application/json" \
  -H "X-Device-Id: dev-A1" \
  -H "X-Device-Secret: $SECRET" \
  -d '{"deviceName":"sensor-A-renamed"}'
# -> {"code":200,"msg":"updated: dev-A1"}

# 4) 越权示范：头是自己的 id，路径却是别人的 -> 403
curl -s -X PUT $BASE/device/dev-B1 \
  -H "Content-Type: application/json" \
  -H "X-Device-Id: dev-A1" -H "X-Device-Secret: $SECRET" \
  -d '{"deviceName":"hacked"}'
# -> {"code":403,"msg":"cannot operate other device"}

# 5) 删自己：同样两个头 -> 200，之后这把密钥立即失效
curl -s -X DELETE $BASE/device/dev-A1 \
  -H "X-Device-Id: dev-A1" -H "X-Device-Secret: $SECRET"
# -> {"code":200,"msg":"deleted: dev-A1"}

# 6) 删完再用旧密钥 -> 401
curl -s -X PUT $BASE/device/dev-A1 \
  -H "Content-Type: application/json" \
  -H "X-Device-Id: dev-A1" -H "X-Device-Secret: $SECRET" \
  -d '{"deviceName":"again"}'
# -> {"code":401,"msg":"invalid device or secret"}
```

## 五、客户端实现要点

1. 注册后把 `deviceId` 和 `secret` 一起持久化（文件/配置/本地存储）；进程重启后服务端设备已丢，需重新注册。
2. 发修改/删除请求时，先把 `X-Device-Id`、`X-Device-Secret` 两个头塞进去，再发请求。
3. 收到 401：密钥没存对或已失效，只能重新注册。
4. 收到 409：id 撞了，换 id 重试。

## 六、环境配置

环境：JDK 21、Maven 3.6+（项目自带 `mvnw` / `mvnw.cmd`，不必装 Maven）；无需数据库、无需 Redis。

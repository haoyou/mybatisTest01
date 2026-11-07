# mybatisTest01 开发文档

> 一个 Spring Boot + MyBatis 的用户 / 角色权限管理示例，包含 JWT 无状态认证、MyBatis 插件自动填充、统一返回体。
>
> 最后更新：2026-11-08

---

## 1. 项目简介

本项目实现了一套最小可用的 **RBAC（基于角色的访问控制）** 后端：

- 用户注册 / 登录，密码 BCrypt 加密后入库
- 登录成功签发 JWT，后续请求由过滤器校验，服务端不保存会话
- 权限来自 `user_role` → `role` 关联，映射为 `ROLE_<角色名大写>`
- 接口可用 `@PreAuthorize` 做角色级控制
- 统一的 `CommonResult` 返回体 + 401/403 的 JSON 错误处理

它同时是一个练手项目，用来演示：MyBatis XML 映射、自定义注解 + 拦截器自动填充主键与时间、雪花 ID、JWT 工具类。

---

## 2. 技术栈

| 类别 | 选型 | 版本 |
|---|---|---|
| 语言 | Java | 1.8 |
| 框架 | Spring Boot | 2.6.13 |
| Web | spring-boot-starter-web | 随 Boot |
| 持久层 | MyBatis（mybatis-spring-boot-starter） | 2.2.2 |
| 安全 | spring-boot-starter-security | 随 Boot |
| Token | JJWT（jjwt） | 0.9.0 |
| 数据库 | MySQL 8（mysql-connector-j） | 8.0.31 |
| 分页 | pagehelper-spring-boot-starter | 1.4.1（当前未使用） |
| 文档 | springfox-boot-starter（Swagger 3） | 3.0.0 |
| 工具 | Lombok / Hutool | 1.18.x / 4.5.7 |

---

## 3. 目录结构

```
mybatisTest01/
├── pom.xml
├── docs/
│   └── DEVELOPMENT.md              # 本文档
└── src/main/
    ├── java/com/example/mybatistest01/
    │   ├── MybatisTest01Application.java   # 启动类
    │   ├── annotation/                     # @IdKeyGenerator @CreateTime @UpdateTime
    │   ├── common/api/                     # CommonResult / ResultCode / IErrorCode / CommonPage
    │   ├── config/
    │   │   ├── SecurityConfig.java         # 无状态安全配置 + 白名单
    │   │   ├── MybatisConfig.java          # 注册 MyBatis 拦截器
    │   │   └── Swagger2Config.java         # Swagger 3 配置
    │   ├── controller/                     # Authority / User / Role / UserRole
    │   ├── dto/AuthorityDto.java           # UserDetails 适配层
    │   ├── entity/                         # User / Role / UserRole
    │   ├── idworker/IdWorker.java          # 雪花算法
    │   ├── interceptor/CustomInterCeptor.java  # MyBatis 自动填充插件
    │   ├── mappers/                        # Mapper 接口（@Mapper）
    │   ├── security/                       # JWT 过滤器 + 401/403 处理器
    │   ├── service/  service/impl/         # 业务层
    │   ├── utils/JwtTokenUtil.java         # JWT 生成 / 解析 / 校验
    │   └── vo/invo/                        # 入参对象
    └── resources/
        ├── application.yml                 # 数据源 + MyBatis + JWT
        ├── application.properties          # 端口 + mapper-locations
        └── com/example/mybatistest01/mappers/*.xml   # 与 Mapper 接口同包
```

---

## 4. 快速开始

### 4.1 环境要求

- JDK 8
- Maven 3.6+
- MySQL 8（本项目开发库：`192.168.152.128:3306/mybatis_test01`）

### 4.2 环境变量（**必读**）

敏感信息不写在配置文件里，启动前必须提供：

| 变量 | 是否必需 | 说明 |
|---|---|---|
| `DB_USER` | 可选 | 缺省 `root`，**实际部署请用专用账号**（见 4.3.1） |
| `DB_PASSWORD` | **必需** | 缺失则启动失败（fail fast） |
| `JWT_SECRET` | **必需** | JWT 签名密钥，建议 ≥64 字符随机串；缺失则启动失败 |

设置方式（Windows）：

```powershell
setx DB_USER "app_user"
setx DB_PASSWORD "你的数据库密码"
setx JWT_SECRET "用随机串，例如 48 字节 Base64"
```

或在 IDEA 的 `Run → Edit Configurations → Environment variables` 里填写（存于 `.idea/`，已被 gitignore）。

> 修改 `JWT_SECRET` 会让已签发的所有 token 立即失效，客户端需要重新登录。

### 4.3 数据库初始化

```sql
CREATE DATABASE IF NOT EXISTS mybatis_test01
  DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE mybatis_test01;

CREATE TABLE `user` (
  id          BIGINT       NOT NULL,
  password    VARCHAR(128) NOT NULL,
  mobile      VARCHAR(32)  DEFAULT NULL,
  id_card     VARCHAR(32)  DEFAULT NULL,
  address     VARCHAR(255) DEFAULT NULL,
  birthday    DATETIME     DEFAULT NULL,
  sex         VARCHAR(8)   DEFAULT NULL,
  username    VARCHAR(64)  NOT NULL,
  status      VARCHAR(16)  DEFAULT NULL,
  create_by   VARCHAR(64)  DEFAULT NULL,
  create_time DATETIME     DEFAULT NULL,
  update_by   VARCHAR(64)  DEFAULT NULL,
  update_time DATETIME     DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `role` (
  id          BIGINT      NOT NULL,
  role_name   VARCHAR(64) NOT NULL,
  create_by   VARCHAR(64) DEFAULT NULL,
  create_time DATETIME    DEFAULT NULL,
  update_by   VARCHAR(64) DEFAULT NULL,
  update_time DATETIME    DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_role_name (role_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `user_role` (
  id          BIGINT      NOT NULL,
  role_id     BIGINT      NOT NULL,
  user_id     BIGINT      NOT NULL,
  create_by   VARCHAR(64) DEFAULT NULL,
  create_time DATETIME    DEFAULT NULL,
  update_by   VARCHAR(64) DEFAULT NULL,
  update_time DATETIME    DEFAULT NULL,
  PRIMARY KEY (id),
  KEY idx_user_role_user (user_id),
  KEY idx_user_role_role (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

主键由雪花算法在插入时生成，**不需要**自增。

### 4.3.1 专用数据库账号（不要用 root 连库）

应用只需要 DML 权限，建账号时只授权 `mybatis_test01` 这一个库：

```sql
CREATE USER IF NOT EXISTS 'app_user'@'%' IDENTIFIED BY '换成强密码';
GRANT SELECT, INSERT, UPDATE, DELETE ON mybatis_test01.* TO 'app_user'@'%';
FLUSH PRIVILEGES;
```

验证：

```sql
-- 能查
SELECT COUNT(*) FROM mybatis_test01.`user`;
-- 不能建表（应当报 CREATE command denied）
CREATE TABLE mybatis_test01.should_fail (id INT);
```

> MySQL 8 默认启用 `validate_password`，密码需同时包含大写、小写、数字和符号，否则 `CREATE USER` 会报 `Your password does not satisfy the current policy requirements`。

### 4.4 启动与验证

```bash
# 启动（IDEA 里直接 Run MybatisTest01Application，或命令行）
mvn spring-boot:run
```

```bash
# 1) 公开接口
curl "http://127.0.0.1:8080/hello?name=abc"
# -> Hello abc

# 2) 注册
curl -X POST http://127.0.0.1:8080/publicrs/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"123456","address":"上海","mobile":"13800000000","idCard":"110101199001010011","sex":"F"}'

# 3) 登录拿 token
curl -X POST http://127.0.0.1:8080/publicrs/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"123456"}'

# 4) 带 token 访问（/whoami 任意登录用户可访问）
curl http://127.0.0.1:8080/whoami -H "Authorization: Bearer <token>"

# 5) 无 token 访问受保护接口
curl -i http://127.0.0.1:8080/user
# -> 401 {"code":401,"message":"暂未登录或token已经过期","data":null}
```

### 4.5 运行测试

```bash
mvn test          # 或在 IDEA 里直接 Run SecuritySmokeTest
```

`SecuritySmokeTest` 用 MockMvc + Mock Service 覆盖 6 条路径（公开接口、无 token 401、无效 token 401、普通用户 403、ADMIN 200、登录返回 token），**不需要连接数据库**，改鉴权逻辑后应保持全绿。

---

## 5. 配置说明

`application.yml`（主要配置，密码相关全部走环境变量）：

```yaml
server:
  port: 8080

spring:
  # springfox 3 依赖 AntPathMatcher；Boot 2.6 默认的 PathPatternParser 会让 Swagger 接口列表变空
  mvc:
    pathmatch:
      matching-strategy: ant_path_matcher
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: 'jdbc:mysql://192.168.152.128:3306/mybatis_test01?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true'
    username: ${DB_USER:root}
    password: ${DB_PASSWORD}

mybatis:
  mapper-locations: classpath:com/example/mybatistest01/mappers/*.xml
  type-aliases-package: com.example.mybatistest01.entity
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl   # 控制台打印 SQL

jwt:
  tokenHeader: Authorization   # 读取 token 的请求头
  secret: ${JWT_SECRET}        # 签名密钥
  expiration: 604800           # 过期秒数（7 天）
  tokenHead: Bearer            # token 前缀
```

> 原 `application.properties` 已合并进 `application.yml` 并删除，配置只有一处。

---

## 6. 认证与授权

### 6.1 请求流程

```
Client
  │  Authorization: Bearer <jwt>
  ▼
SecurityContextPersistenceFilter
  ▼
JwtAuthenticationTokenFilter        ← 本项目新增
  │  1. 取 header，去掉 Bearer 前缀
  │  2. 解析出 username（失败则跳过，不抛异常）
  │  3. UserDetailsService 查库（user + user_role + role）
  │  4. 校验签名与过期时间
  │  5. 写入 SecurityContext（含 ROLE_* 权限）
  ▼
FilterSecurityInterceptor           ← 路径白名单匹配，未登录 → EntryPoint(401)
  ▼
Controller                          ← @PreAuthorize 校验角色，不足 → AccessDeniedHandler(403)
```

### 6.2 白名单（无需认证）

```java
"/", "/hello", "/publicrs/auth/**", "/publicrs/user/test",
"/swagger-ui.html", "/swagger-ui/**", "/swagger-resources/**",
"/v2/api-docs", "/v3/api-docs/**", "/webjars/**", "/doc.html", "/favicon.ico"
```

其余请求一律要求认证（`anyRequest().authenticated()`），会话策略为 `STATELESS`，`formLogin` / `httpBasic` 全部关闭。

> `publicrs` 前缀是历史遗留命名，**不代表公开**：只有 `/publicrs/auth/**`（登录注册）和 `/publicrs/user/test`（演示）真正免认证。角色与用户-角色管理已迁移到 `/admin/**`，要求 ADMIN。

### 6.3 错误返回

| 场景 | 状态码 | 响应 |
|---|---|---|
| 未携带 token / token 无效或过期 | 401 | `{"code":401,"message":"暂未登录或token已经过期","data":null}` |
| 已登录但角色不足 | 403 | `{"code":403,"message":"没有相关权限","data":null}` |

分别由 `RestAuthenticationEntryPoint` 与 `RestAccessDeniedHandler` 输出。

### 6.4 角色来源

```
user_role.user_id ──► role.id ──► role.role_name ──► "ROLE_" + 大写
```

实现见 `UserDetailsServiceImpl#loadAuthorities`。因此数据库里存 `admin` 即可命中 `@PreAuthorize("hasRole('ADMIN')")`（`hasRole` 会自动补 `ROLE_` 前缀）。

### 6.5 当前角色控制的接口

`GET /user` 上的 `@PreAuthorize("hasRole('ADMIN')")`。

### 6.6 自测接口

`GET /whoami`：返回当前 token 对应的用户名与权限列表，用来排查"为什么 403"。

```json
{"code":200,"message":"操作成功","data":{"username":"alice","authorities":["ROLE_ADMIN"]}}
```

---

## 7. 接口清单

前缀说明：`/publicrs/auth/**` 与 `/publicrs/user/test` 免 token；其余均需 `Authorization: Bearer <token>`，标注 **ROLE_ADMIN** 的还要求该角色。

| 方法 | 路径 | 鉴权 | 入参 | 返回 |
|---|---|---|---|---|
| GET | `/hello` | 公开 | `name`(query, 可选) | 纯文本 `Hello xxx` |
| POST | `/publicrs/auth/register` | 公开 | `RegisterUserInvo`(JSON) | `CommonResult` |
| POST | `/publicrs/auth/login` | 公开 | `LoginUserInvo`(JSON) | `CommonResult<{token, tokenHead}>` |
| GET | `/whoami` | 登录 | - | `CommonResult<{username, authorities}>` |
| GET | `/user` | **ROLE_ADMIN** | - | `List<User>`（不含 password） |
| GET | `/user/page` | **ROLE_ADMIN** | `pageNum`(默认1)、`pageSize`(默认10，上限100) | `CommonResult<CommonPage<User>>` |
| POST | `/user/insert` | **ROLE_ADMIN** | `User`(JSON) | `CommonResult` |
| POST | `/publicrs/user/test` | 公开 | - | `publicrs method` |
| POST | `/admin/role/save` | **ROLE_ADMIN** | `{roleName}` | `CommonResult` |
| POST | `/admin/role/search` | **ROLE_ADMIN** | `{roleId}` | `CommonResult<Role>` |
| POST | `/admin/role/searchAll` | **ROLE_ADMIN** | - | `CommonResult<List<Role>>` |
| POST | `/admin/userRole/save` | **ROLE_ADMIN** | `{userId, roleId}` | `CommonResult` |
| POST | `/admin/userRole/search` | **ROLE_ADMIN** | `{userId}` | `CommonResult<List<UserRole>>` |

入参对象字段：

| 对象 | 字段 | 校验 |
|---|---|---|
| `RegisterUserInvo` | address, password, mobile, idCard, username | 均 `@NotBlank` |
| | birthday, sex | 可选 |
| `LoginUserInvo` | username, password | 均 `@NotBlank` |
| `RoleInvo` | roleId, roleName | 业务层判空 |
| `UserRoleInvo` | userId, roleId | 业务层判空 |

统一返回体：

```json
{"code": 200, "message": "操作成功", "data": {}}
```

`code` 取值见 `ResultCode`：200 成功 / 401 未登录 / 403 无权限 / 404 参数校验失败 / 500 失败。

---

## 8. 数据模型

```
user (id, username, password, mobile, id_card, address, birthday, sex, status, 审计字段)
role (id, role_name, 审计字段)
user_role (id, user_id, role_id, 审计字段)
```

- `user.username` 唯一
- `role.role_name` 唯一
- `user_role` 无唯一约束，重复绑定需业务层判重（`UserRoleController` 已做）

审计字段：`create_by / create_time / update_by / update_time`，由拦截器自动填充。

---

## 9. 框架机制

### 9.1 MyBatis 自动填充插件

`CustomInterCeptor` 拦截 `Executor.update`，按实体字段上的注解填充：

| 注解 | 生效时机 | 作用 |
|---|---|---|
| `@IdKeyGenerator` | INSERT | 用雪花算法生成 `long` 主键 |
| `@CreateTime` | INSERT | 写入当前时间 |
| `@UpdateTime` | UPDATE | 刷新为当前时间 |

`IdWorker` 的 workerId / datacenterId 在 `CustomInterCeptor` 中固定为 `(19, 1, 1)`，单机部署没问题；**多实例部署需要按机器分配**，否则可能生成重复 ID。

### 9.2 统一返回体

`CommonResult<T>` 提供 `success/failed/validateFailed/unauthorized/forbidden` 工厂方法；`ResultCode` 是错误码枚举。

### 9.3 密码

统一使用 `BCryptPasswordEncoder(10)`，`User.password` 标了 `@JsonProperty(access = WRITE_ONLY)`，**可以接收入参，但永远不会序列化输出**。

---

## 10. 开发约定

**新增一个受保护接口：**

```java
@GetMapping("/orders")
@PreAuthorize("hasRole('ADMIN')")     // 需要角色时加；不加则"登录即可"
public CommonResult<List<Order>> list() { ... }
```

新增接口默认就在认证保护范围内，无需改 `SecurityConfig`；只有需要**公开**的路径才要加进白名单。

**新增 Mapper：**

1. 在 `mappers/` 下写 `@Mapper` 接口
2. 在 `resources/com/example/mybatistest01/mappers/` 下写同名 XML
3. 手写列清单时**必须**用 `resultMap`（或给 `application.yml` 打开 `map-underscore-to-camel-case`），否则 `id_card` 这类下划线列会映射丢失

**SQL 日志**：已开 `StdOutImpl`，控制台可见完整 SQL 与参数。

---

## 11. 已知问题与技术债

| 优先级 | 问题 | 位置 | 说明 |
|---|---|---|---|
| 低 | `mappers` 目录名拼写（应为 `mapper`） | 全项目 | 与 `mapper-locations` 一致，改名需同步 |

---

## 12. 版本历史（git）

按时间顺序（哈希会随历史重写而变，这里只列主题）：

| # | 主题 | 内容 |
|---|---|---|
| 1 | `baseline` | 原始代码，含旧的数据库配置与内存用户 |
| 2 | `feat(security)` | 接入 JWT 过滤器、从数据库装载权限、修复 `getUserList` 映射、屏蔽密码输出 |
| 3 | `chore(config)` | 数据源账号密码改走环境变量 |
| 4 | `feat(security)` | `GET /user` 要求 `ROLE_ADMIN` |
| 5 | `chore` | 停止跟踪生成的评审报告，并清洗历史里的明文凭据 |
| 6 | `chore(config)` | JWT 签名密钥改走环境变量（并轮换密钥） |
| 7 | `fix(swagger)` | 修正 path-match 策略与 SecurityContext 范围，Swagger 恢复可用 |
| 8 | `docs` | 本文档 |
| 9 | `feat(security)` | 角色 / 用户-角色接口收紧为 ADMIN，白名单只留认证入口 |
| 10 | `feat(security)` | 数据库改用只授权单库的专用账号；`/user/insert` 收紧为 ADMIN 并补校验与密码编码 |
| 11 | `chore` | 删除空的 `testStream` 与其接口；`application.properties` 合并进 `application.yml` |
| 12 | `test` | 新增 `SecuritySmokeTest`，固化 401 / 403 / 200 三条鉴权路径 |
| 13 | `refactor` | 角色/绑定接口迁到 `/admin/**`；`/user/page` 用 PageHelper 实现分页，修复 SQL 末尾分号与 BeanUtils 原始类型拷贝两个 500 |

---

## 13. 常见问题排查

| 现象 | 原因 | 处理 |
|---|---|---|
| 启动报 `Could not resolve placeholder 'DB_PASSWORD'` | 环境变量没生效 | IDEA 需重启以继承 `setx` 的变量，或在运行配置里手填 |
| 启动报 `Could not resolve placeholder 'JWT_SECRET'` | 同上 | 同上 |
| 所有接口 500，日志 `Communications link failure` | 数据库不可达 | 检查 IP / 端口 / 安全组 / 账号密码 |
| 登录 500，日志 `Unknown column 'xxx'` | 库表结构与实体不一致 | 对照第 4.3 节建表语句 |
| 登录成功但访问接口 401 | token 未带或格式不对 | 必须 `Authorization: Bearer <token>`，注意 Bearer 后的空格 |
| 访问接口 403 | 用户没有对应角色 | 看 `/whoami` 的 authorities；补 `user_role` 绑定后**重新登录** |
| 改了角色没生效 | 权限在登录时装载 | 重新登录获取新 token |
| 分页接口 500，日志 `syntax error ... near 'LIMIT'` | Mapper SQL 末尾带了分号，PageHelper 追加 `LIMIT` 后变成 `from user; LIMIT ?` | 去掉语句结尾的 `;` |
| 接口 500，日志只有 `java.lang.IllegalArgumentException: null` | `BeanUtils.copyProperties` 把 null 拷给**原始类型**字段（如 `long roleId`） | 改用 setter 逐字段赋值 |

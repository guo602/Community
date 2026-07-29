# Community 论坛

基于 Spring Boot 的社区论坛 Web 应用，参考牛客网社区项目实现。支持帖子浏览、用户注册、邮件激活等功能。

## 技术栈

| 类别 | 技术 |
|------|------|
| 后端框架 | Spring Boot 3.3 |
| 语言 | Java 17 |
| 持久层 | MyBatis + MySQL |
| 模板引擎 | Thymeleaf |
| 邮件 | Spring Mail |
| 前端 | Bootstrap 4 + 原生 JS |

## 功能概览

- **首页**：分页展示帖子列表，关联发帖用户信息
- **用户注册**：账号 / 密码 / 邮箱校验，MD5 + 盐值加密存储
- **邮件激活**：注册成功后发送 HTML 激活邮件（Thymeleaf 模板）
- **静态页面**：登录、私信、个人主页、帖子详情等页面模板（部分功能待后端接入）

## 项目结构

```
src/main/java/com/nowcoder/community/
├── CommunityApplication.java    # 启动类
├── config/                      # Spring 配置
├── controller/                  # 控制器
│   ├── HomeController.java      # 首页
│   ├── LoginController.java     # 注册
│   └── AlphaController.java     # Spring MVC 学习示例
├── dao/                         # MyBatis Mapper 接口
├── entity/                      # 实体类
├── service/                     # 业务逻辑
└── util/                        # 工具类（MD5、邮件发送等）

src/main/resources/
├── mapper/                      # MyBatis XML 映射
├── static/                      # 静态资源（CSS / JS / 图片）
├── templates/                   # Thymeleaf 页面模板
└── application.properties       # 应用配置（本地，已 gitignore）
```

## 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+

## 快速开始

### 1. 克隆项目

```bash
git clone <repository-url>
cd community
```

### 2. 创建数据库

在 MySQL 中创建数据库并导入表结构：

```sql
CREATE DATABASE community DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE community;

CREATE TABLE user (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    username        VARCHAR(50)  NOT NULL UNIQUE,
    password        VARCHAR(128) NOT NULL,
    salt            VARCHAR(50)  NOT NULL,
    email           VARCHAR(100) NOT NULL UNIQUE,
    type            INT          NOT NULL DEFAULT 0 COMMENT '0-普通用户; 1-管理员',
    status          INT          NOT NULL DEFAULT 0 COMMENT '0-未激活; 1-已激活',
    activation_code VARCHAR(100) NOT NULL,
    header_url      VARCHAR(200) NOT NULL DEFAULT '',
    create_time     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE discuss_post (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    user_id       INT          NOT NULL,
    title         VARCHAR(100) NOT NULL,
    content       TEXT         NOT NULL,
    type          INT          NOT NULL DEFAULT 0 COMMENT '0-普通; 1-置顶',
    status        INT          NOT NULL DEFAULT 0 COMMENT '0-正常; 1-精华; 2-拉黑',
    create_time   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    comment_count INT          NOT NULL DEFAULT 0,
    score         DOUBLE       NOT NULL DEFAULT 0
);
```

### 3. 配置应用

项目根目录下的 `application.properties` 已被 `.gitignore` 忽略，需自行创建。可参考 `application.properties.example`：


然后修改其中的数据库连接、邮件账号等配置。

### 4. 启动应用

```bash
mvn spring-boot:run
```

或在 IDE 中运行 `CommunityApplication`。

### 5. 访问

默认地址：[http://localhost:8090/community/index](http://localhost:8090/community/index)

> 应用配置了 `server.servlet.context-path=/community`，所有 URL 均需带上此前缀。

## 主要接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/community/index` | 首页（帖子列表） |
| GET | `/community/register` | 注册页面 |
| POST | `/community/register` | 提交注册 |
| GET | `/community/alpha/*` | Spring MVC 学习示例接口 |

## 配置说明

| 配置项 | 说明 | 示例 |
|--------|------|------|
| `server.port` | 服务端口 | `8091` |
| `server.servlet.context-path` | 上下文路径 | `/community` |
| `spring.datasource.*` | MySQL 连接信息 | — |
| `spring.mail.*` | SMTP 邮件服务 | — |
| `community.path.domain` | 站点域名（用于邮件链接） | `http://localhost:8091` |
| `community.headurl.library` | 默认头像库地址 | `http://images.nowcoder.com/head` |

## 运行测试

```bash
mvn test
```

邮件相关测试（`MailTests`）需要配置有效的 SMTP 账号后才会通过。

## 开发说明

- Thymeleaf 模板缓存已关闭（`spring.thymeleaf.cache=false`），修改页面后刷新即可生效
- 静态资源路径需使用 Thymeleaf URL 表达式，例如 `th:href="@{/img/icon.png}"`，以正确拼接 context-path
- 日志输出目录：`log/community/`

## License

本项目仅供学习交流使用。

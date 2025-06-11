## 一、安全性隐患
在本次Web应用安全性分析中，我们从四个关键方面系统地识别并评估了当前系统可能存在的安全隐患。
### 1. 密码管理风险
- **明文存储**  
    当前系统在登录与修改密码时直接使用 `rawPassword.equals(storedPassword)` 进行比较，表明数据库中保存的密码为明文。一旦数据库被泄露，攻击者可立即获取所有用户密码，进而对外部系统进行撞库攻击。此外，明文存储还会导致内部运维人员在排查问题时无意中获取用户密码，带来更高的内部联系人威胁。
- **使用密码作为 JWT 签名密钥**  
    在 `TokenUtils.createToken(data, sign)` 中，直接用用户的明文密码 `sign` 作为 HMAC-SHA256 的密钥。这种设计会将密码暴露给 JWT 库与日志系统，任何能够截获或打印出签名过程的调用栈，都可能泄露密码。同时，攻击者只要获取到任意一个用户的签名密钥，就可伪造该用户的 JWT。
- **默认弱密码策略**  
    系统为未提供密码的新用户赋予固定默认密码（如 `"123456"`）。这种做法使得所有新注册或被重置的账户初始密码都相同，攻击者只需对大量账号进行一次暴力猜测即可批量登录成功，极大地降低了密码强度。此外，若未强制用户首次登录后修改该弱密码，风险将长期存在。
### 2. 认证与授权漏洞
- **无标准化框架支撑**  
    由于自研鉴权流程绕过了 Spring Security 或类似成熟框架，缺失了许多开箱即用的安全防护功能，包括：
    - **CSRF 防护**（跨站请求伪造令牌）
    - **会话固定保护**（Session Fixation）
    - **安全响应头**（如 HSTS、XSS 保护头）
    - **审计日志**（登录失败、密码修改历史）
    - **自动限流**（防止暴力破解）  
        自研方案不仅重复造轮子，还容易在边缘场景中产生疏漏。
        
- **角色与权限硬编码**  
    在生成 JWT 时，将用户角色以简单字符串拼接（例如 `"123-USER"`），后续在业务代码中也以 `if ("USER".equals(role))…` 方式判断。此方式难以支持：
    - **动态授权**：无法基于接口、资源、上下文做细粒度控制
    - **权限继承与变更**：角色或权限调整需要修改代码并重新部署
    - **审计与回溯**：无法准确追踪某个权限操作是由哪个角色、哪条策略触发
    
- **缺乏多因子/验证码防护**  
    登录接口既无图形验证码，也无短信/邮件验证码，面对脚本化的自动化登录或字典攻击毫无防备。攻击者可通过分布式脚本对大量账号盲目尝试，从而绕过单因素密码保护。
### 3. 令牌设计与校验缺陷
- **敏感信息暴露**  
    将明文的 `"{userId}-{role}"` 放在 JWT 的 audience（受众）字段中，任何人都能通过 Base64 解码轻易获取用户身份、角色信息。这样不仅泄露了内部结构，也让权限枚举攻击更为简单。

- **依赖数据库查询进行校验**  
    `JWTInterceptor` 在每次请求时，先解析出用户 ID，然后立即向数据库查询该用户的当前密码，再用它来验证签名。此做法带来两大问题：
    1. **性能瓶颈**：高并发环境下，每次请求都需一次数据库往返，显著增加后端负载。
    2. **安全风险**：若用户密码被注入或篡改，拦截器可能在模拟攻击时被利用，引发注入链式攻击。

- **无统一密钥管理**  
    不同用户使用不同密码作为签名密钥，导致无法集中化管理或轮换密钥；当发现签名密钥泄露时，也难以在全局层面快速作废并替换，迫使团队频繁修改用户密码或手动清理。
### 4. 输入输出验证漏洞
- **缺失输入校验**  
    各接口对 `username`、`password`、`email`、`resume` 等参数仅做空值判断，未限制长度、字符集、格式或正则校验。攻击者可通过构造超长字符串、嵌入 SQL 语句、JSON 注入 payload 等方式，诱发 SQL 注入、参数污染、拒绝服务等漏洞。
    
- **无输出编码**  
    系统将用户输入（例如简历文本、评论）未经任何编码即直接返回或渲染到前端模板，缺乏统一的 HTML/JSON 转义。攻击者可轻松注入 `<script>`、`<img onerror>` 等 XSS 载荷，进而盗取 Session、伪造请求或植入钓鱼页面。
    
- **CSRF 防护缺失**  
    所有状态变更操作（如修改密码、简历投递、用户注销）都无 CSRF Token 验证，任意站点的脚本或钓鱼链接都可在用户已登录状态下发起恶意请求，实现跨站请求伪造。
    
- **文件上传安全隐患**  
    简历上传仅基于文件后缀和大小做校验，未检测文件内容或区分存储目录。攻击者可能上传包含恶意网页脚本或可执行文件，利用未隔离的存储路径进行远程代码执行或客户端脚本注入。
    
- **安全头与 HTTPS 强制缺失**  
    系统未配置关键安全响应头：
    - `Strict-Transport-Security`（HSTS）：防止 SSL 剥离攻击
    - `X-Frame-Options`：防止点击劫持
    - `X-Content-Type-Options`：防止 MIME 混淆
    - `Content-Security-Policy`：限制资源加载来源  
        同时未在应用层或反向代理层强制将所有流量重定向到 HTTPS，易受中间人攻击。
## 二、安全性防护策略
为提升Web应用的安全性，我们引入了 **Spring Security 框架** 来替代原有的手动认证与密码管理方式。通过集成 Spring Security，我们实现了 **密码的加密存储** 和 **标准化的认证授权机制**。在用户注册或更新密码时，系统使用 `BCryptPasswordEncoder` 对密码进行加密存储，有效防止明文泄露和暴力破解攻击。在认证流程中，我们利用 Spring Security 的 `AuthenticationManager` 统一管理用户名与密码的验证，结合 JWT 实现无状态会话管理，同时基于用户角色进行细粒度权限控制。这不仅提高了系统的安全性和可维护性，也为后续扩展如多因子认证、权限注解等功能打下了坚实基础。
### 1. 密文存储密码
#### 哈希密码
在传统的 Web 应用中，直接存储用户密码的明文是一种非常危险的做法。一旦数据库泄露，所有用户的密码都将暴露无遗，这可能导致严重的个人信息泄露和账户盗用问题。为了避免这种风险，我们需要对密码进行**哈希处理**。
**密码哈希**是将密码通过一个不可逆的数学函数转换成一串固定长度的字符串（哈希值）。即使攻击者获得了哈希值，也无法轻易地反推出原始密码。
#### BCryptPasswordEncoder 的优势
Spring Security 提供的 **BCryptPasswordEncoder** 是一个强大且广泛推荐的密码哈希算法，它具备以下优点：
- **加盐（Salting）**：BCrypt 会为每个密码自动生成一个**随机的盐值**。这个盐值与密码一起进行哈希，然后将盐值和哈希值一起存储。这意味着即使两个用户设置了相同的密码，它们的哈希值也会不同，从而有效防止了**彩虹表攻击**。彩虹表是一种预先计算好的哈希值与密码对照表，通过加盐可以使这种攻击失效。
- **自适应性（Adaptive）**：BCrypt 算法是计算密集型的，这意味着哈希一个密码需要消耗一定的时间和计算资源。通过调整算法的**强度参数**，可以控制哈希过程的计算成本。当硬件性能提升时，可以提高强度参数，增加破解密码的难度，从而抵御**暴力破解攻击**。
- **不可逆性**：BCrypt 算法是单向的，无法从哈希值反向推导出原始密码。

#### BCryptPasswordEncoder 存储和校验密码
##### 1. 声明 PasswordEncoder Bean
在 `SecurityConfig.java` 配置类中，声明了一个 `BCryptPasswordEncoder` 的 Bean：
```java
// SecurityConfig.java
@Configuration
public class SecurityConfig {

  /**
   * BCrypt 默认 strength=10，可以根据安全/性能要求调整
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
```
##### 2. 在 UserService 中使用 PasswordEncoder
`UserService.java` 是处理用户相关业务逻辑的核心服务，将 `PasswordEncoder` 注入到其中，并在关键业务方法中使用它：
###### 新增用户时哈希密码
在 `add` 方法中，用户注册时提供的原始密码不再直接存储，而是通过 `passwordEncoder.encode(rawPwd)` 方法进行哈希处理后存储到数据库中。
```java
  /**
   * 新增用户：对密码进行哈希
   */
  public void add(User user) {
    // ... 其他校验逻辑 ...
    String rawPwd = ObjectUtil.isEmpty(user.getPassword())
                    ? Constants.USER_DEFAULT_PASSWORD
                    : user.getPassword();
    user.setPassword(passwordEncoder.encode(rawPwd)); // 对密码进行哈希
    // ... 其他业务逻辑 ...
    userMapper.insert(user);
  }
```

###### 登录时校验密码
在 `login` 方法中，当用户尝试登录时，你不再直接比较用户输入的密码与数据库中存储的哈希值。而是使用 `passwordEncoder.matches(account.getPassword(), dbUser.getPassword())` 方法。这个方法会：
1. 从数据库中提取存储的哈希值和盐值。
2. 使用用户输入的原始密码和提取的盐值，重新计算出一个哈希值。
3. 将计算出的哈希值与数据库中存储的哈希值进行比较。
只有当两者匹配时，才认为密码正确。
```java
  /**
   * 登录：用 BCrypt 的 matches() 校验
   */
  public User login(Account account) {
    // ... 其他校验逻辑 ...
    // raw = 前端密码，encoded = db 中哈希
    if (!passwordEncoder.matches(account.getPassword(), dbUser.getPassword())) {
      throw new CustomException(ResultCodeEnum.USER_ACCOUNT_ERROR);
    }
    // ... 生成 token 等逻辑 ...
    return dbUser;
  }
```
###### 修改密码时先校验旧密码，再哈希新密码
在 `updatePassword` 方法中，为了确保用户有权修改密码，首先会使用 `passwordEncoder.matches()` 方法校验用户提供的旧密码是否正确。通过校验后，再对新密码进行哈希处理并更新到数据库。
```java
  /**
   * 修改密码：先校验旧密码，再哈希新密码
   */
  public void updatePassword(Account account) {
    // ... 其他校验逻辑 ...
    if (!passwordEncoder.matches(account.getPassword(), dbUser.getPassword())) {
      throw new CustomException(ResultCodeEnum.PARAM_PASSWORD_ERROR);
    }
    // 对 newPassword 进行哈希后存储
    dbUser.setPassword(passwordEncoder.encode(account.getNewPassword()));
    userMapper.updateById(dbUser);
  }
```

通过引入 Spring Security 的 `BCryptPasswordEncoder`，Web 应用程序实现了更安全的密码管理策略。这种改造将：
- **保护用户隐私**：即使数据库泄露，原始密码也不会被暴露。
- **抵御攻击**：有效防御彩虹表攻击和暴力破解攻击。
- **提升应用信誉**：向用户展示了对数据安全的重视。
### 2. 认证授权功能
Spring Security 是一个功能强大的框架，它提供了全面的安全服务，包括认证、授权、防止常见的安全漏洞（如 CSRF）等。通过集成 JWT，我们能够构建一个无状态的、可扩展的认证系统。

Spring Security的优势：
- **标准化与成熟度：** Spring Security 是一个经过时间考验、社区活跃的成熟框架，其设计遵循业界最佳实践，能够处理许多复杂的安全场景。
- **安全性增强：** 框架内置了多种安全机制，如密码编码、防止会话固定攻击等，帮助你避免常见的安全漏洞。
- **易于扩展：** Spring Security 的模块化设计使得集成其他认证方式（如 OAuth2、LDAP、SSO）变得非常容易，为未来的扩展预留了空间。
- **声明式安全：** 可以通过注解或配置轻松地在方法或请求级别定义访问权限，使权限管理更加清晰和集中。
#### 1. 添加 Maven 依赖
首先，我们需要在项目的 `pom.xml` 文件中引入 Spring Security 和 JWT 相关的 Maven 依赖。
添加两个核心模块：
- `spring-boot-starter-security`：提供认证与授权的核心功能。
- `java-jwt`：用于 JWT 的生成与解析。
#### 2. 定义用户服务与密码加密器
 - 用户信息加载服务（`UserDetailsService`）
	- 实现方式：查询数据库 → 封装角色 → 返回对象
	- 作用：Spring 会在登录验证和鉴权过程中使用此接口加载用户信息。
- 密码加密器（`PasswordEncoder`）
	- Spring Security 会自动调用该加密器来校验登录密码是否匹配。
	- 使用 `BCryptPasswordEncoder`，具备不可逆、带盐值、防暴力破解等优点。
#### 3.  编写 JWT 工具类（只负责生成与解析）
原始代码中 JWT 的创建、验证和业务耦合太深，现在需要将其剥离成纯工具类：
- `generateToken()`：生成包含用户名与角色的 JWT。
- `getUsername()`：从 token 中提取用户名。
- `validateToken()`：验证 token 是否有效与过期。
工具类不再负责查数据库，只处理字符串签名与解析，保持通用性和低耦合。
#### 4. 编写 JWT 过滤器
为了在每次请求中自动识别当前用户，需要一个全局过滤器，完成以下工作：
- 拦截所有请求
- 从请求头中提取 `Authorization: Bearer xxx` 形式的 JWT
- 解析 token 获取用户名
- 加载数据库中的用户信息
- 验证 token 是否匹配该用户
- 将用户信息注入 `SecurityContext`，供后续权限判断使用
此过滤器只做一件事：**认证上下文注入**，且只做一次，避免重复数据库查询。
#### 5. 配置 Spring Security 主配置类
需要创建一个继承 `WebSecurityConfigurerAdapter` 的类，完成以下配置：
-  设置用户服务和密码加密器：让 Spring Security 知道如何加载用户和校验密码。
- 配置安全策略
	- 禁用 CSRF（前后端分离场景常见做法）
	- 设置无状态会话管理（不使用 session，纯靠 token）
	- 定义公开接口（如 `/login`, `/register`）允许匿名访问
	- 设置基于角色的访问控制（如 `/admin/**` 仅限 ADMIN）
	- 注入你写的 JWT 过滤器，并放在标准认证过滤器之前
#### 6. 修改登录接口
不再手动校验用户名和密码，而是委托给 `AuthenticationManager`：
- 构造认证请求对象（用户名 + 密码）
- 调用 `authenticate()` 方法，如果校验失败会自动抛异常
- 校验通过后，从返回的 `Authentication` 中获取用户信息
- 生成 JWT 并返回给前端
这是认证逻辑的核心切换点，从手工代码 → 框架统一认证流程。
#### 7. 在业务逻辑中获取当前登录用户
原本可能通过解析 JWT 或数据库查询来获取当前用户信息，现在只需：
- 从 `SecurityContextHolder.getContext()` 获取当前认证信息
- 取出其中的 `UserDetails` 或用户名
- 如需完整用户实体，再查询数据库即可
这样做的好处是：**认证信息已经统一管理，无需每次手动解析 Token**。

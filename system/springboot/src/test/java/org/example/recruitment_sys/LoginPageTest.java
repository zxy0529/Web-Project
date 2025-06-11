package org.example.recruitment_sys;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import static org.junit.jupiter.api.Assertions.*;

class LoginPageTest {

    private WebDriver driver;

    @BeforeEach
    void setUp() {
        // 设置 ChromeDriver 的路径
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver"); // 这里替换为 chromedriver 的实际路径

        // 创建一个新的 ChromeOptions 实例
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");  // 无头模式，不显示浏览器窗口
        options.addArguments("--disable-gpu");  // 禁用GPU硬件加速

        // 创建新的 WebDriver 实例
        driver = new ChromeDriver(options);
    }

    @Test
    void testLoginPage() {
        // 打开登录页面
        driver.get("http://localhost:7070/login");

        // 查找用户名、密码和角色选择输入框，并进行输入
        driver.findElement(By.cssSelector("input[placeholder='请输入账号']")).sendKeys("admin");
        driver.findElement(By.cssSelector("input[placeholder='请输入密码']")).sendKeys("admin");
        driver.findElement(By.cssSelector("button[type='primary']")).click();

        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/manager/home"), "登录成功，跳转到后台主页");

        String pageTitle = driver.getTitle();
        assertTrue(pageTitle.contains("欢迎"), "验证登录成功");
    }

    @AfterEach
    void tearDown() {
        // 关闭浏览器
        if (driver != null) {
            driver.quit();
        }
    }
}

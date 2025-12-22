package ui.pages;

import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class LoginPage {
    private final Page page;

    public LoginPage(Page page) {
        this.page = page;
    }

    public void open() {
        page.navigate("https://automationexercise.com/login");
        assertThat(page.locator("text=Login to your account")).isVisible();
    }

    public void login(String email, String password) {
        page.locator("input[data-qa='login-email']").fill(email);
        page.locator("input[data-qa='login-password']").fill(password);
        page.locator("button[data-qa='login-button']").click();
    }
}

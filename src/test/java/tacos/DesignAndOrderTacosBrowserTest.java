package tacos;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Slf4j
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class DesignAndOrderTacosBrowserTest {

    private static HtmlUnitDriver browser;

    @LocalServerPort
    private int port;

    @Autowired
    TestRestTemplate restTemplate;

    @BeforeAll
    public static void setup() {
        browser = new HtmlUnitDriver();
        browser.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    @AfterAll
    public static void closeBrowser() {
        browser.quit();
    }

    @Test
    public void testDesignATacoPage() throws Exception {
        browser.get(homePageUrl());
        clickDesignATaco();
        assertLandedOnLoginPage();
        doRegistration("testuser", "testpassword");
        assertLandedOnLoginPage();
        doLogin("testuser", "testpassword");
        assertDesignPageElements();
        buildAndSubmitATaco("Basic Taco", "FLTO", "GRBF", "CHED", "TMTO", "SLSA");
        clickBuildAnotherTaco();
        buildAndSubmitATaco("Another Taco", "COTO", "CARN", "JACK", "LETC", "SRCR");
        fillInAndSubmitOrderForm();
        assertEquals(homePageUrl(), browser.getCurrentUrl());
        doLogout();
    }

    @Test
    public void testDesignATacoPage_EmptyOrderInfo() throws Exception {
        browser.get(homePageUrl());
        clickDesignATaco();
        assertLandedOnLoginPage();
        doRegistration("testuser2", "testpassword");
        doLogin("testuser2", "testpassword");
        assertDesignPageElements();
        buildAndSubmitATaco("Basic Taco", "FLTO", "GRBF", "CHED", "TMTO", "SLSA");
        submitEmptyOrderForm();
        fillInAndSubmitOrderForm();
        assertEquals(homePageUrl(), browser.getCurrentUrl());
        doLogout();
    }

    @Test
    public void testDesignATacoPage_InvalidOrderInfo() throws Exception {
        browser.get(homePageUrl());
        clickDesignATaco();
        assertLandedOnLoginPage();
        doRegistration("testuser3", "testpassword");
        doLogin("testuser3", "testpassword");
        assertDesignPageElements();
        buildAndSubmitATaco("Basic Taco", "FLTO", "GRBF", "CHED", "TMTO", "SLSA");
        submitInvalidOrderForm();
        fillInAndSubmitOrderForm();
        assertEquals(homePageUrl(), browser.getCurrentUrl());
        doLogout();
    }

    private void doLogin(String username, String password) {
        browser.findElementByCssSelector("input#username").sendKeys(username);
        browser.findElementByCssSelector("input#password").sendKeys(password);
        browser.findElementByCssSelector("form#loginForm").submit();
    }

    private void doLogout() {
        browser.findElementByCssSelector("form#logoutForm").submit();
    }

    private void doRegistration(String username, String password) {
        browser.findElementByLinkText("here").click();
        assertEquals(registrationPageUrl(), browser.getCurrentUrl());

        browser.findElementByName("username").sendKeys(username);
        browser.findElementByName("password").sendKeys(password);
        browser.findElementByName("confirm").sendKeys(password);
        browser.findElementByName("fullname").sendKeys("Test McTest");
        browser.findElementByName("street").sendKeys("1234 Test Street");
        browser.findElementByName("city").sendKeys("Testville");
        browser.findElementByName("state").sendKeys("TX");
        browser.findElementByName("zip").sendKeys("12345");
        browser.findElementByName("phone").sendKeys("123-123-1234");
        browser.findElementByCssSelector("form#registerForm").submit();
    }

    private void assertLandedOnLoginPage() {
        assertEquals(loginPageUrl(), browser.getCurrentUrl());
    }

    private void submitInvalidOrderForm() {
        assertTrue(browser.getCurrentUrl().startsWith(orderDetailsPageUrl()));
        fillField("input#deliveryName", "I");
        fillField("input#deliveryStreet", "1");
        fillField("input#deliveryCity", "F");
        fillField("input#deliveryState", "C");
        fillField("input#deliveryZip", "8");
        fillField("input#ccNumber", "1234432112344322");
        fillField("input#ccExpiration", "14/91");
        fillField("input#ccCVV", "1234");
        browser.findElementByCssSelector("form#orderForm").submit();

        assertEquals(orderDetailsPageUrl(), browser.getCurrentUrl());

        List<String> validationErrors = getValidationErrorTexts();
        assertEquals(5, validationErrors.size());
        assertTrue(validationErrors.contains("Please correct the problems below and resubmit."));
        assertTrue(validationErrors.contains("Must be two characters"));
        assertTrue(validationErrors.contains("Not a valid credit card number"));
        assertTrue(validationErrors.contains("Must be formatted MM/YY"));
        assertTrue(validationErrors.contains("Invalid CVV"));
    }

    private void submitEmptyOrderForm() {
        assertEquals(currentOrderDetailsPageUrl(), browser.getCurrentUrl());
        browser.findElementByCssSelector("form#orderForm").submit();

        assertEquals(orderDetailsPageUrl(), browser.getCurrentUrl());

        List<String> validationErrors = getValidationErrorTexts();
        assertEquals(9, validationErrors.size());
        assertTrue(validationErrors.contains("Please correct the problems below and resubmit."));
        assertTrue(validationErrors.contains("Delivery name is required"));
        assertTrue(validationErrors.contains("Street is required"));
        assertTrue(validationErrors.contains("City is required"));
        assertTrue(validationErrors.contains("Must be two characters"));
        assertTrue(validationErrors.contains("Zip code is required"));
        assertTrue(validationErrors.contains("Not a valid credit card number"));
        assertTrue(validationErrors.contains("Must be formatted MM/YY"));
        assertTrue(validationErrors.contains("Invalid CVV"));
    }

    private List<String> getValidationErrorTexts() {
        List<WebElement> elements = browser.findElementsByClassName("validationError");
        return elements.stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    private void fillInAndSubmitOrderForm() {
        assertTrue(browser.getCurrentUrl().startsWith(orderDetailsPageUrl()));
        fillField("input#deliveryName", "Ima Hungry");
        fillField("input#deliveryStreet", "1234 Culinary Blvd.");
        fillField("input#deliveryCity", "Foodsville");
        fillField("input#deliveryState", "CO");
        fillField("input#deliveryZip", "81019");
        fillField("input#ccNumber", "4111111111111111");
        fillField("input#ccExpiration", "10/19");
        fillField("input#ccCVV", "123");
        browser.findElementByCssSelector("form").submit();
    }

    private void fillField(String fieldName, String value) {
        WebElement field = browser.findElementByCssSelector(fieldName);
        field.clear();
        field.sendKeys(value);
    }

    private void clickBuildAnotherTaco() {
        assertTrue(browser.getCurrentUrl().startsWith(orderDetailsPageUrl()));
        browser.findElementByCssSelector("a[id='another']").click();
    }

    private void buildAndSubmitATaco(String name, String... ingredients) {
        assertDesignPageElements();
        
        for (String ingredient : ingredients) {
            browser.findElementByCssSelector(String.format("input[value=%s]", ingredient)).click();
        }
        browser.findElementByCssSelector("input#name").sendKeys(name);
        browser.findElementByCssSelector("form#tacoForm").submit();
    }

    private void clickDesignATaco() {
        assertEquals(homePageUrl(), browser.getCurrentUrl());
        browser.findElementByCssSelector("a[id='design']").click();
    }

    private void assertDesignPageElements() {
        assertEquals(designPageUrl(), browser.getCurrentUrl());
        List<WebElement> ingredientGroups = browser.findElementsByClassName("ingredient-group");
        assertEquals(5, ingredientGroups.size());

        WebElement wrapGroup = browser.findElementByCssSelector("div.ingredient-group#wraps");
        List<WebElement> wraps = wrapGroup.findElements(By.tagName("div"));
        assertEquals(2, wraps.size());
        assertIngredient(wrapGroup, 0, "COTO", "Corn Tortilla");
        assertIngredient(wrapGroup, 1, "FLTO", "Flour Tortilla");

        WebElement proteinGroup = browser.findElementByCssSelector("div.ingredient-group#proteins");
        List<WebElement> proteins = proteinGroup.findElements(By.tagName("div"));
        assertEquals(2, proteins.size());
        assertIngredient(proteinGroup, 0, "CARN", "Carnitas");
        assertIngredient(proteinGroup, 1, "GRBF", "Ground Beef");

        WebElement cheeseGroup = browser.findElementByCssSelector("div.ingredient-group#cheeses");
        List<WebElement> cheeses = proteinGroup.findElements(By.tagName("div"));
        assertEquals(2, cheeses.size());
        assertIngredient(cheeseGroup, 0, "CHED", "Cheddar");
        assertIngredient(cheeseGroup, 1, "JACK", "Monterrey Jack");

        WebElement veggieGroup = browser.findElementByCssSelector("div.ingredient-group#veggies");
        List<WebElement> veggies = proteinGroup.findElements(By.tagName("div"));
        assertEquals(2, veggies.size());
        assertIngredient(veggieGroup, 0, "LETC", "Lettuce");
        assertIngredient(veggieGroup, 1, "TMTO", "Diced Tomatoes");

        WebElement sauceGroup = browser.findElementByCssSelector("div.ingredient-group#sauces");
        List<WebElement> sauces = proteinGroup.findElements(By.tagName("div"));
        assertEquals(2, sauces.size());
        assertIngredient(sauceGroup, 0, "SLSA", "Salsa");
        assertIngredient(sauceGroup, 1, "SRCR", "Sour Cream");
    }

    private void assertIngredient(WebElement ingredientGroup, int idx, String id, String name) {
        List<WebElement> ingredients = ingredientGroup.findElements(By.tagName("div"));
        WebElement ingredient = ingredients.get(idx);
        assertEquals(id, ingredient.findElement(By.tagName("input")).getAttribute("value"));
        assertEquals(name, ingredient.findElement(By.tagName("span")).getText());
    }

    private String registrationPageUrl() {
        return homePageUrl() + "register";
    }

    private String loginPageUrl() {
        return homePageUrl() + "login";
    }

    private String currentOrderDetailsPageUrl() {
        return homePageUrl() + "orders/current";
    }

    private String orderDetailsPageUrl() {
        return homePageUrl() + "orders";
    }

    private String designPageUrl() {
        return homePageUrl() + "design";
    }

    private String homePageUrl() {
        return "http://localhost:" + port + "/";
    }

}

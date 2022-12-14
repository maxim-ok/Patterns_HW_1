import com.codeborne.selenide.Condition;
import com.codeborne.selenide.logevents.SelenideLogger;
import data.ClientInfo;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import org.openqa.selenium.Keys;
import utils.DataGenerator;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.*;

public class PatternsTest {

    public String generateDate(int days) {
        return LocalDate.now().plusDays(days).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }


    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    void setup() {
        open("http://localhost:9999/");
    }


    @Test
    @DisplayName("Позитивный кейс на бронирование даты доставки")
    void inputPositive() {

        String planningDate = generateDate(3);

        ClientInfo clientInfo = DataGenerator.InfoFilling.clientInfo("ru");

        $x("//span[@data-test-id='city']//input").setValue(clientInfo.getCity());
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $x("//span[@data-test-id='date']//input").val(planningDate);
        $x("//span[@data-test-id='name']//input").val(clientInfo.getName());
        $x("//span[@data-test-id='phone']//input").val(clientInfo.getPhone());
        $x("//label[@data-test-id='agreement']").click();
        $x("//*[contains(text(), 'Запланировать')]").click();
        $(".notification__title").shouldHave(text("Успешно!"));
        $(".notification__content").shouldHave(text("Встреча успешно запланирована на " + planningDate));

    }


    @Test
    @DisplayName("Позитивный кейс на перебронирование даты доставки")
    void rebookingDate() {

        String planningDate = generateDate(3);
        String rebookingDate = generateDate(4);

        ClientInfo clientInfo = DataGenerator.InfoFilling.clientInfo("ru");

        $("[data-test-id=city] input").setValue(clientInfo.getCity());
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(planningDate);
        $("[data-test-id=name] input").setValue(clientInfo.getName());
        $("[data-test-id=phone] input").setValue(clientInfo.getPhone());
        $("[data-test-id=agreement] span").click();
        $x("//*[contains(text(), 'Запланировать')]").click();
        $("[data-test-id=success-notification]").shouldBe(visible);
        $(".notification__title").shouldHave(text("Успешно!"));
        $(".notification__content").shouldHave(text("Встреча успешно запланирована на " + planningDate));
        $("[data-test-id=success-notification] button").click();
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(rebookingDate);
        $x("//*[contains(text(), 'Запланировать')]").click();
        $("[data-test-id=replan-notification]").shouldBe(visible);
        $$(".notification__title").findBy(text("Необходимо подтверждение")).should(exist);
        $$(".notification__content").findBy(text("У вас уже запланирована встреча на другую дату. Перепланировать?")).should(exist);
        $("[data-test-id=replan-notification] button").click();
        $("[data-test-id=success-notification]").shouldBe(visible);
        $(".notification__title").shouldHave(text("Успешно!"));
        $(".notification__content").shouldHave(text("Встреча успешно запланирована на " + rebookingDate));

    }


}

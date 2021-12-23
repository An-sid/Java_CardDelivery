package ru.netology;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CardDeliveryTest {

    @Test
    void whenFormIsCorrectlyFilledIn() {
        open("http://localhost:9999");
        $$("input").get(0).setValue("Казань");
        var currentDate = $$("input").get(1).getValue();
        $$("input").get(2).setValue("Алексей");
        $$("input").get(3).setValue("+79993332211");
        $(".checkbox").click();
        $$("button").get(1).click();
        $("[data-test-id=notification]").shouldBe(visible, Duration.ofSeconds(12)).shouldHave(
                exactText("Успешно! \n Встреча успешно забронирована на " + currentDate));
    }

    @Test
    void whenFormIsEmpty() {
        open("http://localhost:9999");
        $$("button").get(1).click();
        $("[data-test-id=city]").shouldHave(exactText("Поле обязательно для заполнения"));
        $(".notification").shouldBe(hidden, Duration.ofSeconds(12));
    }

    @Test
    void whenDateIsEmpty() {
        open("http://localhost:9999");
        $("[data-test-id=city] input").setValue("Москва");
        $("[data-test-id=date] input").sendKeys(Keys.SHIFT, Keys.HOME, Keys.DELETE);
        $$("button").get(1).click();
        $("[data-test-id=date]").shouldHave(exactText("Неверно введена дата"));
        $(".notification").shouldBe(hidden, Duration.ofSeconds(12));
    }

    @Test
    void whenPhoneIsEmpty() {
        open("http://localhost:9999");
        $("[data-test-id=city] input").setValue("Москва");
        $("[data-test-id=name] input").setValue("Алексей");
        $$("button").get(1).click();
        $("[data-test-id=phone]").shouldHave(text("Поле обязательно для заполнения"));
        $(".notification").shouldBe(hidden, Duration.ofSeconds(12));
    }

    @Test
    void whenCheckboxIsSkipped() {
        open("http://localhost:9999");
        $$("input").get(0).setValue("Казань");
        $$("input").get(2).setValue("Алексей");
        $$("input").get(3).setValue("+79993332211");
        $$("button").get(1).click();
        var color = $(".checkbox__text").getCssValue("color");
        assertEquals("rgba(255, 92, 92, 1)", color);
        $(".notification").shouldBe(hidden, Duration.ofSeconds(12));

    }

    @Test
    void whenCityIsIncorrect() {
        open("http://localhost:9999");
        $("[data-test-id=city] input").setValue("None");
        $$("button").get(1).click();
        $("[data-test-id=city]").shouldHave(exactText("Доставка в выбранный город недоступна"));
        $(".notification").shouldBe(hidden, Duration.ofSeconds(12));
    }

    @Test
    void whenDateIsLessThan3Days() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.uuuu");
        LocalDate today = LocalDate.now();
        open("http://localhost:9999");
        $("[data-test-id=city] input").setValue("Саранск");
        $("[data-test-id=date] input").sendKeys(Keys.SHIFT, Keys.HOME, Keys.DELETE);
        $("[data-test-id=date] input").setValue(dtf.format(today));
        $$("button").get(1).click();
        $("[data-test-id=date]").shouldHave(exactText("Заказ на выбранную дату невозможен"));
        $(".notification").shouldBe(hidden, Duration.ofSeconds(12));
    }

    @Test
    void whenNameIsIncorrect() {
        open("http://localhost:9999");
        $("[data-test-id=city] input").setValue("Владивосток");
        $("[data-test-id=name] input").setValue("None");
        $$("button").get(1).click();
        $("[data-test-id=name]").shouldHave(text(
                "Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы."));
        $(".notification").shouldBe(hidden, Duration.ofSeconds(12));
    }

    @Test
    void whenPhoneIsIncorrect() {
        open("http://localhost:9999");
        $("[data-test-id=city] input").setValue("Москва");
        $("[data-test-id=name] input").setValue("Алексей");
        $("[data-test-id=phone] input").setValue("None");
        $$("button").get(1).click();
        $("[data-test-id=phone]").shouldHave(text(
                "Телефон указан неверно. Должно быть 11 цифр, например, +79012345678."));
        $(".notification").shouldBe(hidden, Duration.ofSeconds(12));
    }

}

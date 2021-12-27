package ru.netology;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CardDeliveryTest {

    @BeforeEach
    void setUp() {
        open("http://localhost:9999");
    }

    @Test
    void whenFormIsCorrectlyFilledIn() {
        String meetDate = LocalDate.now().plusDays(3).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        $("[data-test-id=city] input").setValue("Казань");
        $("[data-test-id='date'] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(meetDate);
        $("[data-test-id=name] input").setValue("Алексей");
        $("[data-test-id=phone] input").setValue("+79993332211");
        $(".checkbox").click();
        $("[class*=grid-col] button").click();
        $("[data-test-id=notification]").shouldBe(visible, Duration.ofSeconds(12)).shouldHave(
                exactText("Успешно! \n Встреча успешно забронирована на " + meetDate));
    }

    @Test
    void whenFormIsEmpty() {
        $("[class*=grid-col] button").click();
        $("[data-test-id=city].input_invalid .input__sub").shouldHave(exactText("Поле обязательно для заполнения"));
        $(".notification").shouldBe(hidden, Duration.ofSeconds(12));
    }

    @Test
    void whenDateIsEmpty() {
        $("[data-test-id=city] input").setValue("Москва");
        $("[data-test-id=date] input").sendKeys(Keys.SHIFT, Keys.HOME, Keys.DELETE);
        $("[class*=grid-col] button").click();
        $("[data-test-id=date] .input_invalid .input__sub").shouldHave(exactText("Неверно введена дата"));
        $(".notification").shouldBe(hidden, Duration.ofSeconds(12));
    }

    @Test
    void whenPhoneIsEmpty() {
        $("[data-test-id=city] input").setValue("Москва");
        $("[data-test-id=name] input").setValue("Алексей");
        $("[class*=grid-col] button").click();
        $("[data-test-id=phone].input_invalid .input__sub").shouldHave(exactText("Поле обязательно для заполнения"));
        $(".notification").shouldBe(hidden, Duration.ofSeconds(12));
    }

    @Test
    void whenCheckboxIsSkipped() {
        $("[data-test-id=city] input").setValue("Казань");
        $("[data-test-id=name] input").setValue("Алексей");
        $("[data-test-id=phone] input").setValue("+79993332211");
        $("[class*=grid-col] button").click();
        $(".checkbox").shouldHave(cssClass("input_invalid"));
        $(".notification").shouldBe(hidden, Duration.ofSeconds(12));
    }

    @Test
    void whenCityIsIncorrect() {
        $("[data-test-id=city] input").setValue("None");
        $("[class*=grid-col] button").click();
        $("[data-test-id=city].input_invalid .input__sub").shouldHave(exactText("Доставка в выбранный город недоступна"));
        $(".notification").shouldBe(hidden, Duration.ofSeconds(12));
    }

    @Test
    void whenDateIsLessThan3Days() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.uuuu");
        LocalDate today = LocalDate.now();
        $("[data-test-id=city] input").setValue("Саранск");
        $("[data-test-id=date] input").sendKeys(Keys.SHIFT, Keys.HOME, Keys.DELETE);
        $("[data-test-id=date] input").setValue(dtf.format(today));
        $("[class*=grid-col] button").click();
        $("[data-test-id=date] .input_invalid .input__sub").shouldHave(exactText("Заказ на выбранную дату невозможен"));
        $(".notification").shouldBe(hidden, Duration.ofSeconds(12));
    }

    @Test
    void whenNameIsIncorrect() {
        $("[data-test-id=city] input").setValue("Владивосток");
        $("[data-test-id=name] input").setValue("None");
        $("[class*=grid-col] button").click();
        $("[data-test-id=name].input_invalid .input__sub").shouldHave(exactText(
                "Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы."));
        $(".notification").shouldBe(hidden, Duration.ofSeconds(12));
    }

    @Test
    void whenPhoneIsIncorrect() {
        $("[data-test-id=city] input").setValue("Москва");
        $("[data-test-id=name] input").setValue("Алексей");
        $("[data-test-id=phone] input").setValue("None");
        $("[class*=grid-col] button").click();
        $("[data-test-id=phone].input_invalid .input__sub").shouldHave(exactText(
                "Телефон указан неверно. Должно быть 11 цифр, например, +79012345678."));
        $(".notification").shouldBe(hidden, Duration.ofSeconds(12));
    }

}

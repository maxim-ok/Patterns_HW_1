package utils;

import com.github.javafaker.Faker;
import data.ClientInfo;
import lombok.experimental.UtilityClass;

import java.util.Locale;

@UtilityClass
public class DataGenerator {

    public static class InfoFilling {
        public static ClientInfo clientInfo(String locale) {
            Faker faker = new Faker(new Locale(locale));
            return new ClientInfo(faker.address().cityName(), faker.name().firstName(), faker.numerify("+79#########"));
        }

    }

}

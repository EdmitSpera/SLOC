import com.github.javafaker.Faker;
import com.github.javafaker.PhoneNumber;
import org.junit.Test;

import java.util.Locale;

public class FakerTests {
    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            Faker faker = new Faker(Locale.CHINA);

            String name = faker.funnyName().name();
            System.out.println("中文名：" + name);

            PhoneNumber phoneNumber = faker.phoneNumber();
            String mobileNumber = phoneNumber.cellPhone();
            System.out.println("手机号：" + mobileNumber);

            String email = faker.internet().emailAddress();
            System.out.println("电子邮箱：" + email);
            System.out.println();
        }
    }
}

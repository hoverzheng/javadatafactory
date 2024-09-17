import com.github.javafaker.Faker;

import java.util.Locale;

public class testGenField {

    public static void main(String[] args) {

        Faker faker = new Faker(new Locale("zh", "CN"));

        String fldStr = "name";
        String v = generateField(fldStr, faker);

        System.out.println(v);
    }

    private static String generateField(String field, Faker faker) {
        try {
            switch (field) {
                case "name":
                    return faker.name().fullName(); // 生成随机名字
                case "address":
                    return faker.address().fullAddress(); // 生成随机地址
                case "phoneNumber":
                    return faker.phoneNumber().phoneNumber(); // 生成随机电话号码
                // 添加更多字段的对应方法
                default:
                    return "Invalid field";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}

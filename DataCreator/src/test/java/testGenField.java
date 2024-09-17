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
                    return faker.name().fullName(); // �����������
                case "address":
                    return faker.address().fullAddress(); // ���������ַ
                case "phoneNumber":
                    return faker.phoneNumber().phoneNumber(); // ��������绰����
                // ��Ӹ����ֶεĶ�Ӧ����
                default:
                    return "Invalid field";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}

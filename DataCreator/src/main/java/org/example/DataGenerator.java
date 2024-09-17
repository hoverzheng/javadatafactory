package org.example;

import com.github.javafaker.Faker;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class DataGenerator {
    private static final Faker faker = new Faker(Locale.CHINA);
    private static final AtomicInteger idCounter = new AtomicInteger(1); // 自增ID计数器
    public static String generateData(String fields, int rowCount, String delimiter) {
        String[] fieldArray = fields.split(",");
        return IntStream.range(0, rowCount)
                .mapToObj(i -> generateRow(fieldArray, delimiter))
                .collect(Collectors.joining("\n"));
    }

    private static String generateRow(String[] fieldArray, String delimiter) {
        int id = idCounter.getAndIncrement(); // 获取并自增ID
        return IntStream.range(0, fieldArray.length + 1) // +1是为了包括ID列
                .mapToObj(i -> i == 0 ? String.valueOf(id) : generateField(fieldArray[i - 1].trim())) // 第一个位置放ID，其余位置放生成的字段值
                .collect(Collectors.joining(delimiter));
    }

    private static String generateField(String field) {
        try {
            switch (field) {
                case "name":
                    return faker.name().fullName();
                case "address":
                    return faker.address().fullAddress();
                case "phoneNumber":
                    return faker.phoneNumber().phoneNumber();
                case "ssn":
                    return faker.idNumber().ssnValid();
                case "email":
                    return faker.internet().emailAddress();
                case "ip":
                    return faker.internet().ipV4Address();
                case "mac":
                    return faker.internet().macAddress();
                case "url":
                    return faker.internet().url();
                case "creditcard":
                    return faker.business().creditCardNumber();
                // 添加更多字段的对应方法
                default:
                    return "Invalid field";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void generateFile(String fields, int rowCount, String delimiter, int threadId) {
        String data = generateData(fields, rowCount, delimiter);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("data_file_" + threadId + ".txt"))) {
            writer.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length < 4) {
            System.out.println("usage: java -jar DataGenerator.jar <fields> <rowCount> <delimiter> <threadCount>");
            return;
        }

        String fields = args[0]; // 字段名
        int rowCount = Integer.parseInt(args[1]); // 行数
        String delimiter = args[2]; // 分隔符
        int threadCount = Integer.parseInt(args[3]); // 线程数


        Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> generateFile(fields, rowCount, delimiter, threadId));
            threads[i].start();
        }

        // 等待所有线程完成
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("数据文件生成完成.");
    }
}

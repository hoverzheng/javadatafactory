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

    public static void generateDataInBatches(String fields, int rowCount, String delimiter, int threadId, int batchSize) {
        String[] fieldArray = fields.split(",");
        int fullBatches = rowCount / batchSize;
        int remainingRows = rowCount % batchSize;

        for (int i = 0; i < fullBatches; i++) {
            String data = generateData(fieldArray, batchSize, delimiter);
            writeFile("data_file_" + threadId + ".txt", data);
            System.out.printf("Thread %d: Finished writing batch %d/%d\n", threadId, i + 1, fullBatches);
        }

        if (remainingRows > 0) {
            String data = generateData(fieldArray, remainingRows, delimiter);
            writeFile("data_file_" + threadId + ".txt", data);
            System.out.printf("Thread %d: Finished writing final batch\n", threadId);
        }
    }

    private static String generateData(String[] fieldArray, int rowCount, String delimiter) {
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

    private static void writeFile(String fileName, String data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) { // 文件追加模式
            writer.write(data);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length < 5) {
            System.out.println("usage: java -jar DataGenerator.jar <fields> <rowCount> <delimiter> <threadCount> <batchSize>");
            return;
        }

        String fields = args[0]; // 字段名
        int rowCount = Integer.parseInt(args[1]); // 行数
        String delimiter = args[2]; // 分隔符
        int threadCount = Integer.parseInt(args[3]); // 线程数
        int batchSize = Integer.parseInt(args[4]); // 批次大小

        Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> generateDataInBatches(fields, rowCount, delimiter, threadId, batchSize));
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

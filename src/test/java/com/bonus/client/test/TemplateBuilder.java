package com.bonus.client.test;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class TemplateBuilder {

    public static void main(String[] args) {
        File javaFile1 = new File("src/test/java/com/bonus/client/test/BonusPactTestTemplate.java");

        File javaFile2 = new File("src/test/java/com/bonus/client/test/BonusPactTestTemplate2.java");
        try {
            String data = FileUtils.readFileToString(javaFile1);
            data = data.replace("PROVIDER-SERVICE", "bonus-service");
            data = data.replace("CONSUMER-SERVICE", "bonus-consumer");
            FileUtils.writeStringToFile(javaFile2, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

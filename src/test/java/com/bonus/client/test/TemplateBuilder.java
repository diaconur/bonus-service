package com.bonus.client.test;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class TemplateBuilder {


    static String providerName = "bonus-service";
    static String consumerName = "bonus-consumer";

    static String sourceClassName = "PactClassTemplate";
    static String destinationClassName = "PactTestGenerated";


    public static void main(String[] args) {
        File javaFile1 = new File("src/test/java/com/bonus/client/test/" + sourceClassName + ".java");

        File javaFile2 = new File("src/test/java/com/bonus/client/test/" + destinationClassName + ".java");
        try {
            String data = FileUtils.readFileToString(javaFile1);
            data = data.replace("PROVIDER_SERVICE", providerName);
            data = data.replace("CONSUMER_SERVICE", consumerName);
            data = data.replace(sourceClassName, destinationClassName);
            FileUtils.writeStringToFile(javaFile2, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package com.returnonintelligence.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Props {

    private static final String PROPERTY_FILE_NAME = "config.xml";
    private static String inputFolderPath;
    private static String outputFolderPath;
    private static String processedFolderPath;
    private static String processingType;
    private static int poolSize;

    public static void loadProps() {
        Properties properties = new Properties();
        try {
            properties.loadFromXML(new FileInputStream(PROPERTY_FILE_NAME));
        } catch (IOException e) {
            e.printStackTrace();
        }
        inputFolderPath = properties.getProperty("inputFolderPath");
        outputFolderPath = properties.getProperty("outputFolderPath");
        processedFolderPath = properties.getProperty("processedFolderPath");
        processingType = properties.getProperty("processingType");
        poolSize = Integer.parseInt(properties.getProperty("poolSize"));
    }

    public static String getInputFolderPath() {
        return inputFolderPath;
    }

    public static String getOutputFolderPath() {
        return outputFolderPath;
    }

    public static String getProcessedFolderPath() {
        return processedFolderPath;
    }

    public static String getProcessingType() {
        return processingType;
    }

    public static int getPoolSize() {
        return poolSize;
    }
}

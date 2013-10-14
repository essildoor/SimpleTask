package com.returnonintelligence.util;

import java.io.*;

/**
 * Counts lines number from all *.java files in src and sub directories
 */
public class CodeLinesCounter {

    private int codeLinesNumber;

    public int getCodeLinesNumber() {
        return codeLinesNumber;
    }

    /**
     * Recursively analyzes specified folder and all sub folders
     * @param folderPath
     */
    public void count(File folderPath) {
        File[] fileList = folderPath.listFiles();
        for (File f : fileList) {
            if (f.isFile()) {
                codeLinesNumber += countInFile(f);
            }
            if (f.isDirectory()) {
                count(f);
            }
        }
    }
    /**
     * Counts lines number in a specified file
     * @param f - file to be processed
     * @return lines number
     */
    private int countInFile(File f) {
        int linesNumber = 0;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(f));
            while (reader.readLine() != null) {
                linesNumber++;
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return linesNumber;
    }

    public static void main(String[] args) {
        String srcDirPath = "D:\\!_java_idea_projects\\SimpleTask\\src\\";
        CodeLinesCounter counter = new CodeLinesCounter();
        counter.count(new File(srcDirPath));
        System.out.println(counter.getCodeLinesNumber() + " code lines found.");
    }
}

package com.returnonintelligence.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ToExitKeyListener extends Thread {

    private Thread scanner;
    private Thread handler;

    public ToExitKeyListener(Thread scanner, Thread handler) {
        this.scanner = scanner;
        this.handler = handler;
    }

    @Override
    public void run() {
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        try {
            bf.readLine();
            System.out.println("stopped");
            scanner.interrupt();
            handler.interrupt();
            System.out.println("scanner and handler has been interrupted");
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

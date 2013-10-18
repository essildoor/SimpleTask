package com.returnonintelligence;

import com.returnonintelligence.util.FolderScanner;
import com.returnonintelligence.util.IncomingFileHandler;
import com.returnonintelligence.util.Props;
import com.returnonintelligence.util.ToExitKeyListener;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {

    public static void main(String[] args) {
        BlockingQueue<String> sharedQueue = new LinkedBlockingQueue<String>();
        Props.loadProps();
        FolderScanner folderScanner = new FolderScanner(sharedQueue);
        IncomingFileHandler incomingFileHandler =
                new IncomingFileHandler(Props.getPoolSize(), sharedQueue);
        ToExitKeyListener toExitKeyListener =
                new ToExitKeyListener(folderScanner, incomingFileHandler);
        toExitKeyListener.start();
        folderScanner.start();
        incomingFileHandler.start();
    }
}

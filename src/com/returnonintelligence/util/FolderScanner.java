package com.returnonintelligence.util;


import java.io.File;
import java.util.*;
import java.util.concurrent.BlockingQueue;

public class FolderScanner extends Thread {

    private BlockingQueue<String> sharedQueue;

    public FolderScanner(BlockingQueue<String> sharedQueue) {
        this.sharedQueue = sharedQueue;
    }

    @Override
    public void run() {
        String inputFolder = Props.getInputFolderPath();
        //set of files obtained during current folder scan
        Set<String> toSubmit = new HashSet<String>();
        //set of files which were submitted to processing
        List<String> submitted = new ArrayList<String>();
        while (true) {

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                System.out.println("Folder scanner has been interrupted!");
            }
            //scans folder and refreshes toSubmit set
            String[] fileList = new File(inputFolder).list();
            toSubmit.clear();
            //if there is no files clears submitted set (if some files were in the folder but
            // were submitted previously)
            if (fileList.length == 0) {
                submitted.clear();
            } else {
                Collections.addAll(toSubmit, fileList);
                //puts file to queue for submit if 'submitted' set does not have it
                for (String f : toSubmit) {
                    if (!submitted.contains(f)) {
                        try {
                            sharedQueue.put(f);
                            submitted.add(f);
                            Thread.yield();
                        } catch (InterruptedException e) {
                            System.out.println("Folder scanner has been interrupted!");
                        }
                    }
                }
            }
            //if files were submitted to processing, but there appears new files with the same names
            //and scanner did not do the new cycle of scan, then clears these file names from
            //submitted set
            fileList = new File(inputFolder).list();
            if (fileList.length != 0) {
                for (String aFileList : fileList) {
                    if (submitted.contains(aFileList))
                        submitted.remove(aFileList);
                }
            }
        }
    }
}

package com.company;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Класс "потребитель" загружающий файлы
 */
class FileLoaderThread implements Runnable {

    final private BlockingQueue<FileURL> queue;

    public FileLoaderThread(BlockingQueue<FileURL> queue) {
        this.queue = queue;
    }

    private long downloadWithJava7IO(String fileURL, List<String> localFilename){

        Path source = Paths.get(localFilename.get(0));
        try {
            try (InputStream in = new URL(fileURL).openStream()) {
                Files.copy(in, Paths.get(localFilename.get(0)), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                System.out.println("Ошибка загрузки файла  " + fileURL);
            }

            if(localFilename.size() > 1){
                for (int i = 1; i<localFilename.size(); ++i){
                    Path destination= Paths.get(localFilename.get(i));
                    FileUtils.copyFile(source.toFile(), destination.toFile());
                }
            }

            return source.toFile().length();

        } catch (IOException e) {
            System.out.println("Ошибка загрузки файла  " + fileURL);
            return 0;
        }
    }

    private void download(String fileURL, List<String> localFilename) throws IOException {

        String sourceFilename = localFilename.get(0).substring(localFilename.get(0).lastIndexOf('/')+1);

        URL url = new URL(fileURL);
        synchronized (EventBus.getDefault()) {
            EventBus.getDefault().post(new MessageEvent("Загружается файл: " + sourceFilename));
        }

        long startTime = System.currentTimeMillis();

        long length = downloadWithJava7IO(fileURL, localFilename);
        if(length == 0) return;

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;

        long countFiles = 0;

        synchronized (EventBus.getDefault()) {
            for(String filename : localFilename){
                EventBus.getDefault().post(new MessageEvent("Файл " + filename.substring(filename.lastIndexOf('/')+1) + " загружен: " + length + " за " + elapsedTime));
                ++countFiles;
            }
            EventBus.getDefault().post(new StatisticDataEvent(length, elapsedTime, countFiles));
        }
    }

    public void run() {
        while (!queue.isEmpty()) {
            try {
                FileURL fileURL = queue.take();
                download(fileURL.getFileURL(), fileURL.getLocalFilename());
            } catch (Exception e) {
                System.out.println("Thread Running error " + e.getMessage());
            }
        }
    }
}
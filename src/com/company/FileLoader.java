//Модуль загрузки файлов в несколько потоков


package com.company;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.*;

/**
 * Класс URL загружаемого файла
 */
class FileURL {
    private final String fileURL;
    private final String localFilename;

    public String getFileURL() {
        return fileURL;
    }
    public String getLocalFilename() {
        return localFilename;
    }

    public FileURL(String fileURL, String localFilename) {
        this.fileURL = fileURL;
        this.localFilename = localFilename;
    }
}

/**
 * Класс загрузки файлов в несколько потоков
 */
public class FileLoader implements Subscriber<Event>{
    private int totalTime;
    private int totalFileSize;

    public int getTotalTime() {
        return totalTime;
    }

    public int getTotalFileSize() {
        return totalFileSize;
    }

    public FileLoader() {
        final BlockingQueue<FileURL> queue = new ArrayBlockingQueue<>(11);

        EventBus.getDefault().register(this, MessageEvent.class);
        EventBus.getDefault().register(this, StatisticDataEvent.class);

        //TODO Load From File
        try {
            queue.put(new FileURL("http://meteoweb.ru/xls/ms2008-05.zip", "/tmp/ms2008-05.zip"));
            queue.put(new FileURL("http://meteoweb.ru/xls/ms2008-04.zip", "/tmp/ms2008-04.zip"));
            queue.put(new FileURL("http://meteoweb.ru/xls/ms2008-03.zip", "/tmp/ms2008-03.zip"));
            queue.put(new FileURL("http://meteoweb.ru/xls/ms2008-02.zip", "/tmp/ms2008-02.zip"));
            queue.put(new FileURL("http://meteoweb.ru/xls/ms2008-01.zip", "/tmp/ms2008-01.zip"));
            queue.put(new FileURL("http://meteoweb.ru/xls/ms2007-51.zip", "/tmp/ms2007-51.zip"));
            queue.put(new FileURL("http://meteoweb.ru/xls/ms2007-50.zip", "/tmp/ms2007-50.zip"));
            queue.put(new FileURL("http://meteoweb.ru/xls/ms2007-09.zip", "/tmp/ms2007-09.zip"));
            queue.put(new FileURL("http://meteoweb.ru/xls/ms2007-08.zip", "/tmp/ms2007-08.zip"));
            queue.put(new FileURL("http://meteoweb.ru/xls/ms2007-06.zip", "/tmp/ms2007-06.zip"));
            queue.put(new FileURL("http://meteoweb.ru/xls/ms2007-04.zip", "/tmp/ms2007-04.zip"));

            ExecutorService exec = Executors.newCachedThreadPool();

            exec.submit(new Customer(queue));
            exec.submit(new Customer(queue));
            exec.submit(new Customer(queue));

            exec.shutdown();

            //TODO Terminate exec
            exec.awaitTermination(1, TimeUnit.DAYS);
        } catch (Exception e) {
            System.out.println("Start download error " + e.getMessage());
        }
    }

    @Override
    public void onEvent(Event event) {
        if(event instanceof MessageEvent)
            System.out.println(((MessageEvent)event).getMessage());
        if( event instanceof StatisticDataEvent){
            totalFileSize += ((StatisticDataEvent)event).getSize();
        }
    }
}
/**
 * Класс "потребитель" загружающий файлы
 */
class Customer implements Runnable {

    final private BlockingQueue<FileURL> queue;

    public Customer(BlockingQueue<FileURL> queue) {
        this.queue = queue;
    }

    private void downloadWithJavaNIO(String fileURL, String localFilename) throws IOException {

        URL url = new URL(fileURL);
        synchronized (EventBus.getDefault()) {
            EventBus.getDefault().post(new MessageEvent("File " + localFilename + " is loading..."));
        }

        long startTime = System.currentTimeMillis();

        try (ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
             //TODO Save To File List
             FileOutputStream fileOutputStream = new FileOutputStream(localFilename);
             FileChannel fileChannel = fileOutputStream.getChannel()) {
             fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        } catch (IOException e) {
            System.out.println("Load " + localFilename + " error " + e.getMessage());
            return;
        }

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;

        File f = new File(localFilename);
        long length = f.length();

        synchronized (EventBus.getDefault()) {
            EventBus.getDefault().post(new MessageEvent("File " + localFilename + " loaded. Elapsed time " + elapsedTime
                    + ". File size " + length));
            EventBus.getDefault().post(new StatisticDataEvent(length, elapsedTime));
        }
    }

    public void run() {
        while (!queue.isEmpty()) {
            try {
                FileURL fileURL = queue.take();
                downloadWithJavaNIO(fileURL.getFileURL(), fileURL.getLocalFilename());
            } catch (Exception e) {
                System.out.println("Thread Running error " + e.getMessage());
            }
        }
    }
}



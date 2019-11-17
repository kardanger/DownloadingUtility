//Модуль загрузки файлов в несколько потоков


package com.company;

import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Класс загрузки файлов в несколько потоков
 */
public class FileLoader implements Subscriber<Event>{
    private int totalTime;
    private int totalFileSize;
    private CollectionLinks links;

    public int getTotalTime() {
        return totalTime;
    }

    public int getTotalFileSize() {
        return totalFileSize;
    }

    public FileLoader(CollectionLinks links, int numThreads) {
        this.links = links;
        final BlockingQueue<FileURL> queue = new ArrayBlockingQueue<>(11);

        EventBus.getDefault().register(this, MessageEvent.class);
        EventBus.getDefault().register(this, StatisticDataEvent.class);

        //TODO Load From File
        try {
/*            queue.put(new FileURL("http://meteoweb.ru/xls/ms2008-05.zip", "/tmp/ms2008-05.zip"));
            queue.put(new FileURL("http://meteoweb.ru/xls/ms2008-04.zip", "/tmp/ms2008-04.zip"));
            queue.put(new FileURL("http://meteoweb.ru/xls/ms2008-03.zip", "/tmp/ms2008-03.zip"));
            queue.put(new FileURL("http://meteoweb.ru/xls/ms2008-02.zip", "/tmp/ms2008-02.zip"));
            queue.put(new FileURL("http://meteoweb.ru/xls/ms2008-01.zip", "/tmp/ms2008-01.zip"));
            queue.put(new FileURL("http://meteoweb.ru/xls/ms2007-51.zip", "/tmp/ms2007-51.zip"));
            queue.put(new FileURL("http://meteoweb.ru/xls/ms2007-50.zip", "/tmp/ms2007-50.zip"));
            queue.put(new FileURL("http://meteoweb.ru/xls/ms2007-09.zip", "/tmp/ms2007-09.zip"));
            queue.put(new FileURL("http://meteoweb.ru/xls/ms2007-08.zip", "/tmp/ms2007-08.zip"));
            queue.put(new FileURL("http://meteoweb.ru/xls/ms2007-06.zip", "/tmp/ms2007-06.zip"));
            queue.put(new FileURL("http://meteoweb.ru/xls/ms2007-04.zip", "/tmp/ms2007-04.zip"));*/

            for(Map.Entry<String, List<String>> entry: links.getURLs()){
                queue.put(CollectionLinks.getURL(entry));
            }

            ThreadPoolExecutor exec = (ThreadPoolExecutor) Executors.newFixedThreadPool(numThreads);

            for(int i =0; i < numThreads; ++i){
                exec.submit(new Customer(queue));
            }

            exec.shutdown();

            while (exec.getActiveCount()!=0){
            }

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

    private void downloadWithJavaNIO(String fileURL, List<String> localFilename) throws IOException {

        URL url = new URL(fileURL);
        synchronized (EventBus.getDefault()) {
            EventBus.getDefault().post(new MessageEvent("File " + localFilename.get(0) + " is loading..."));
        }

        long startTime = System.currentTimeMillis();

        int CONNECT_TIMEOUT = 10000;
        int READ_TIMEOUT = 10000;

        File source= new File(localFilename.get(0));

        try {
            FileUtils.copyURLToFile(new URL(fileURL), source, CONNECT_TIMEOUT, READ_TIMEOUT);

            if(localFilename.size() > 1){
                for (int i = 1; i<localFilename.size(); ++i){
                    File destination = new File(localFilename.get(i));
                    FileUtils.copyFile(source, destination);
                }
            }

        } catch (IOException e) {
            System.out.println("Load " + localFilename + " error " + e.getMessage());
            return;
        }

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        long length = source.length();

        synchronized (EventBus.getDefault()) {
            for(String filename : localFilename){
                EventBus.getDefault().post(new MessageEvent("File " + filename + " loaded. Elapsed time " + elapsedTime
                        + ". File size " + length));
            }
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



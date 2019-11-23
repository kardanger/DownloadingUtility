//Модуль загрузки файлов в несколько потоков


package com.company;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Класс загрузки файлов в несколько потоков
 */
public class FileLoader implements Subscriber<Event>{
    private int totalTime;
    private int totalFileSize;
    private int totalCountFiles;
    private int loadedFileSize;
    private CollectionLinks links;

    public int getLoadedFileSize() {
        return loadedFileSize;
    }

    public int getTotalCountFiles() {
        return totalCountFiles;
    }

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

        try {
            for(Map.Entry<String, List<String>> entry: links.getURLs()){
                queue.put(CollectionLinks.getURL(entry));
            }

            ThreadPoolExecutor exec = (ThreadPoolExecutor) Executors.newFixedThreadPool(numThreads);
            for(int i =0; i < numThreads; ++i){
                exec.submit(new FileLoaderThread(queue));
            }

            exec.shutdown();
            exec.awaitTermination(1, TimeUnit.DAYS);
        } catch (Exception e) {
            System.out.println("Start download error " + e.getMessage());
        }
    }

    @Override
    public synchronized void onEvent(Event event) {
        if(event instanceof MessageEvent)
            System.out.println(((MessageEvent)event).getMessage());
        if( event instanceof StatisticDataEvent){
            loadedFileSize += ((StatisticDataEvent)event).getSize();
            totalFileSize += ((StatisticDataEvent)event).getSize() * ((StatisticDataEvent)event).getCountFiles();
            totalCountFiles += ((StatisticDataEvent)event).getCountFiles();
        }
    }
}




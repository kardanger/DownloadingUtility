package com.company;

import java.util.*;

//Модуль передачи сообщений подписчикам

/**
 * Интерфейс классов событий
 */
interface Event{

}
/**
 * Класс события передачи сообщения
 */
class MessageEvent implements Event {

    public MessageEvent(String message){
        this.message = message;
    }

    public String getMessage(){
        return message;
    }

    private final String message;
}
/**
 * Класс события передачи статитистики загрузки файла
 */
class StatisticDataEvent implements Event{

    public StatisticDataEvent(long size, long time){
        this.size = size;
        this.time = time;
    }

    public long getTime(){
        return time;
    }

    public long getSize(){
        return size;
    }

    private final long time;
    private final long size;
}
/**
 * Интерфейс класса "слушателя" событий
 */
interface Subscriber<T> {
    void onEvent(T event);
}
/**
 * Класс реализующий передачу событий подписчикам
 */
public class EventBus<T> {

    private static volatile EventBus instance;
    private final Map<Class, List<Subscriber> > subscribers;

    private EventBus() {
        instance = null;
        subscribers = new HashMap<>();
    }

    public static EventBus getDefault() {
        if (instance == null) {
            instance = new EventBus();
        }
        return instance;
    }

    void post(Event event){
        if((subscribers.isEmpty())||(!subscribers.containsKey(event.getClass()))) return;
        List<Subscriber>  eventSubscribers =  subscribers.get(event.getClass());
        for( Subscriber subscriber : eventSubscribers){
            subscriber.onEvent(event);
        }
    }

    public  void register(Subscriber subscriber, Class aClass) {
        List<Subscriber> list;

        if(subscribers.containsKey(aClass))  list = subscribers.get(aClass);
        else{
            list = new  LinkedList<>();
        }
        list.add(subscriber);
        subscribers.put(aClass, list);
    }
}


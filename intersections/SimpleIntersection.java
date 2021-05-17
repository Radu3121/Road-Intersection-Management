package com.apd.tema2.intersections;

import com.apd.tema2.Main;
import com.apd.tema2.entities.Intersection;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleIntersection implements Intersection {
    // Define your variables here.

    //task1
    public static Semaphore simpleSemaphore = new Semaphore(Main.carsNo);

    //task2
    public static int maxNumberOfCarsSimpleNRoundabout;
    public static int timeInSimpleNRoundabout;
    public static Semaphore simpleNRoundaboutSemaphore;

    //task3
    public static List<Semaphore> simpleStrict1Semaphores;
    public static int numberOfDirections;
    public static int timeInSimpleStrict1;

    //task4
    public static List<Semaphore> strictXSemaphores;
    public static int numberOfDirectionsStrictX;
    public static int timeInStrictX;
    public static int xCarsInStrictX;
    public static CyclicBarrier strictXBarrier;
    public static CyclicBarrier allOutBarrier;

    //task5
    public static List<Semaphore> maxXSemaphores;
    public static int numberOfDirectionsMaxX;
    public static int xCarsInMaxX;
    public static int timeInMaxX;

    //task6
    public static volatile AtomicInteger highPriorityInside;
    public static ArrayBlockingQueue<Integer> lowQueue;
    public static final String sync = new String("syncString");;
    public static final String lowQueueSync = new String("lowQueueSync");

    //task7
    public static final String syncPedestrians = new String("syncPedestrians");
    public static String[] outMessage;

    //task8
    public static Semaphore passing0Semaphore;
    public static Semaphore passing1Semaphore;
    public static CyclicBarrier passed0Barrier;
    public static CyclicBarrier passed1Barrier;
    public static int X;

    //task10
    public static CyclicBarrier trainBarrier;
    public static ArrayBlockingQueue<Integer> laneZero;
    public static ArrayBlockingQueue<Integer> laneOne;
    public static int passed;
    public static final String laneZeroSync = new String("laneZeroSync");
    public static final String laneOneSync = new String("laneOneSync");
}

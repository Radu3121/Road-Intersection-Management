package com.apd.tema2.factory;

import com.apd.tema2.Main;
import com.apd.tema2.entities.Pedestrians;
import com.apd.tema2.entities.ReaderHandler;
import com.apd.tema2.intersections.SimpleIntersection;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Returneaza sub forma unor clase anonime implementari pentru metoda de citire din fisier.
 */
public class ReaderHandlerFactory {

    public static ReaderHandler getHandler(String handlerType) {
        // simple semaphore intersection
        // max random N cars roundabout (s time to exit each of them)
        // roundabout with exactly one car from each lane simultaneously
        // roundabout with exactly X cars from each lane simultaneously
        // roundabout with at most X cars from each lane simultaneously
        // entering a road without any priority
        // crosswalk activated on at least a number of people (s time to finish all of them)
        // road in maintenance - 1 lane 2 ways, X cars at a time
        // road in maintenance - N lanes 2 ways, X cars at a time
        // railroad blockage for T seconds for all the cars
        // unmarked intersection
        // cars racing
        return switch (handlerType) {
            case "simple_semaphore" -> new ReaderHandler() {
                @Override
                public void handle(final String handlerType, final BufferedReader br) {
                    // Exemplu de utilizare:
                    // Main.intersection = IntersectionFactory.getIntersection("simpleIntersection");
                }
            };
            case "simple_n_roundabout" -> new ReaderHandler() {
                @Override
                public void handle(final String handlerType, final BufferedReader br) throws IOException {
                    // To parse input line use:
                    // String[] line = br.readLine().split(" ");
                    String[] line = br.readLine().split(" ");
                    SimpleIntersection.maxNumberOfCarsSimpleNRoundabout = Integer.parseInt(line[0]);
                    //System.out.println(SimpleIntersection.maxNumberOfCarsSimpleNRoundabout);
                    SimpleIntersection.timeInSimpleNRoundabout = Integer.parseInt(line[1]);
                    SimpleIntersection.simpleNRoundaboutSemaphore = new Semaphore(SimpleIntersection.maxNumberOfCarsSimpleNRoundabout);
                }
            };
            case "simple_strict_1_car_roundabout" -> new ReaderHandler() {
                @Override
                public void handle(final String handlerType, final BufferedReader br) throws IOException {
                    String[] line = br.readLine().split(" ");
                    SimpleIntersection.numberOfDirections = Integer.parseInt(line[0]);
                    SimpleIntersection.timeInSimpleStrict1 = Integer.parseInt(line[1]);
                    SimpleIntersection.simpleStrict1Semaphores = Collections.synchronizedList(new ArrayList<>(SimpleIntersection.numberOfDirections));
                    for (int i = 0; i < SimpleIntersection.numberOfDirections; i++) {
                        SimpleIntersection.simpleStrict1Semaphores.add(new Semaphore(1));
                    }
                }
            };
            case "simple_strict_x_car_roundabout" -> new ReaderHandler() {
                @Override
                public void handle(final String handlerType, final BufferedReader br) throws IOException {
                    String[] line = br.readLine().split(" ");
                    SimpleIntersection.numberOfDirectionsStrictX = Integer.parseInt(line[0]);
                    SimpleIntersection.timeInStrictX = Integer.parseInt(line[1]);
                    SimpleIntersection.xCarsInStrictX = Integer.parseInt(line[2]);
                    SimpleIntersection.strictXSemaphores = Collections.synchronizedList(new ArrayList<>(SimpleIntersection.numberOfDirectionsStrictX));
                    for (int i = 0; i < SimpleIntersection.numberOfDirectionsStrictX; i++) {
                        SimpleIntersection.strictXSemaphores.add(new Semaphore(SimpleIntersection.xCarsInStrictX));
                    }
                    SimpleIntersection.strictXBarrier = new CyclicBarrier(Main.carsNo);
                    SimpleIntersection.allOutBarrier = new CyclicBarrier(SimpleIntersection.xCarsInStrictX * SimpleIntersection.numberOfDirectionsStrictX);
                }
            };
            case "simple_max_x_car_roundabout" -> new ReaderHandler() {
                @Override
                public void handle(final String handlerType, final BufferedReader br) throws IOException {
                    String[] line = br.readLine().split(" ");
                    SimpleIntersection.numberOfDirectionsMaxX = Integer.parseInt(line[0]);
                    SimpleIntersection.timeInMaxX = Integer.parseInt(line[1]);
                    SimpleIntersection.xCarsInMaxX = Integer.parseInt(line[2]);
                    SimpleIntersection.maxXSemaphores = Collections.synchronizedList(new ArrayList<>(SimpleIntersection.numberOfDirectionsMaxX));
                    for (int i = 0; i < SimpleIntersection.numberOfDirectionsMaxX; i++) {
                        SimpleIntersection.maxXSemaphores.add(new Semaphore(SimpleIntersection.xCarsInMaxX));
                    }

                }
            };
            case "priority_intersection" -> new ReaderHandler() {
                @Override
                public void handle(final String handlerType, final BufferedReader br) throws IOException {
                    String[] line = br.readLine().split(" ");
                    SimpleIntersection.highPriorityInside = new AtomicInteger(0);
                    //SimpleIntersection.syncList = Collections.synchronizedList(new ArrayList<>(Integer.parseInt(line[1])));
                   // SimpleIntersection.syncQueue = new SynchronousQueue<>();
                    SimpleIntersection.lowQueue = new ArrayBlockingQueue<>(Integer.parseInt(line[1]));
                }
            };
            case "crosswalk" -> new ReaderHandler() {
                @Override
                public void handle(final String handlerType, final BufferedReader br) throws IOException {
                    String[] line = br.readLine().split(" ");
                    Main.pedestrians = new Pedestrians(Integer.parseInt(line[0]), Integer.parseInt(line[1]));
                    SimpleIntersection.outMessage = new String[Main.carsNo];
                    for (int i = 0; i < Main.carsNo; i++) {
                        SimpleIntersection.outMessage[i] = "";
                    }
                    //System.out.println(Arrays.toString(SimpleIntersection.outMessage));
                }
            };
            case "simple_maintenance" -> new ReaderHandler() {
                @Override
                public void handle(final String handlerType, final BufferedReader br) throws IOException {
                    String[] line = br.readLine().split(" ");
                    SimpleIntersection.X = Integer.parseInt(line[0]);
                    SimpleIntersection.passing0Semaphore = new Semaphore(SimpleIntersection.X);
                    SimpleIntersection.passing1Semaphore = new Semaphore(0);
                    SimpleIntersection.passed0Barrier = new CyclicBarrier(SimpleIntersection.X);
                    SimpleIntersection.passed1Barrier = new CyclicBarrier(SimpleIntersection.X);
                }
            };
            case "complex_maintenance" -> new ReaderHandler() {
                @Override
                public void handle(final String handlerType, final BufferedReader br) throws IOException {
                    
                }
            };
            case "railroad" -> new ReaderHandler() {
                @Override
                public void handle(final String handlerType, final BufferedReader br) throws IOException {
                    SimpleIntersection.trainBarrier = new CyclicBarrier(Main.carsNo);
                    SimpleIntersection.laneOne = new ArrayBlockingQueue<Integer>(Main.carsNo);
                    SimpleIntersection.laneZero = new ArrayBlockingQueue<Integer>(Main.carsNo);
                    SimpleIntersection.passed = 0;
                }
            };
            default -> null;
        };
    }

}

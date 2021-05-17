package com.apd.tema2.factory;

import com.apd.tema2.Main;
import com.apd.tema2.entities.Car;
import com.apd.tema2.entities.IntersectionHandler;
import com.apd.tema2.intersections.SimpleIntersection;
import com.apd.tema2.utils.Constants;

import java.util.concurrent.BrokenBarrierException;

import static java.lang.Thread.sleep;

/**
 * Clasa Factory ce returneaza implementari ale InterfaceHandler sub forma unor
 * clase anonime.
 */
public class IntersectionHandlerFactory {

    public static IntersectionHandler getHandler(String handlerType) {
        // simple semaphore intersection
        // max random N cars roundabout (s time to exit each of them)
        // roundabout with exactly one car from each lane simultaneously
        // roundabout with exactly X cars from each lane simultaneously
        // roundabout with at most X cars from each lane simultaneously
        // entering a road without any priority
        // crosswalk activated on at least a number of people (s time to finish all of
        // them)
        // road in maintenance - 2 ways 1 lane each, X cars at a time
        // road in maintenance - 1 way, M out of N lanes are blocked, X cars at a time
        // railroad blockage for s seconds for all the cars
        // unmarked intersection
        // cars racing
        return switch (handlerType) {
            case "simple_semaphore" -> new IntersectionHandler() {
                @Override
                public void handle(Car car) {
                    // Semafor pentru controlarea numarului de masini care trebuie sa intre in intersectie
                    try {
                        SimpleIntersection.simpleSemaphore.acquire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Car " + car.getId() + " has reached the semaphore, now waiting...");
                    try {
                        sleep(car.getWaitingTime());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    SimpleIntersection.simpleSemaphore.release();
                    System.out.println("Car " + car.getId() + " has waited enough, now driving...");
                }
            };
            case "simple_n_roundabout" -> new IntersectionHandler() {
                @Override
                public void handle(Car car) {


                    System.out.println("Car " + car.getId() + " has reached the roundabout, now waiting...");

                    // Semafor pentru controlarea numarului de masini care trebuie sa se afle in intersectie la un
                    // moment dat
                    // Fiecare masina care incearca sa intre in intersectia va incerca sa faca mai intai acquire pe
                    // pe semafor
                    try {
                        SimpleIntersection.simpleNRoundaboutSemaphore.acquire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    System.out.println("Car " + car.getId() + " has entered the roundabout");

                    // Se asteapta timpul precizat in fisierul de intrare
                    try {
                        sleep(SimpleIntersection.timeInSimpleNRoundabout);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Se afiseaza mesajul de iesire din intersectie
                    System.out.println("Car " + car.getId() + " has exited the roundabout after " +
                            SimpleIntersection.timeInSimpleNRoundabout / 1000 + " seconds");

                    // Se da release pe semafor pentru a lasa urmatoarea masina sa intre
                    SimpleIntersection.simpleNRoundaboutSemaphore.release();
                }
            };
            case "simple_strict_1_car_roundabout" -> new IntersectionHandler() {
                @Override
                public void handle(Car car) {

                    System.out.println("Car " + car.getId() + " has reached the roundabout");

                    // Masinile incearca sa faca acquire pe semnaforul corespunzator directiei lor
                    try {
                        SimpleIntersection.simpleStrict1Semaphores.get(car.getStartDirection()).acquire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    System.out.println("Car " + car.getId() + " has entered the roundabout from lane " +
                            car.getStartDirection());

                    // Se asteapta timpul precizat in fisierul de intrare
                    try {
                        sleep(SimpleIntersection.timeInSimpleStrict1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    System.out.println("Car " + car.getId() + " has exited the roundabout after " +
                            SimpleIntersection.timeInSimpleStrict1 / 1000 + " seconds");

                    // Se face release pe semaforul corespunzator unei directii pe care nu se mai afla nici o masina
                    SimpleIntersection.simpleStrict1Semaphores.get(car.getStartDirection()).release();
                }
            };
            case "simple_strict_x_car_roundabout" -> new IntersectionHandler() {
                @Override
                public void handle(Car car) {

                    System.out.println("Car " + car.getId() + " has reached the roundabout, now waiting...");

                    // Se asteapta la bariera ca toate masinile sa ajunga la intrarea in intersectie
                    try {
                        SimpleIntersection.strictXBarrier.await();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        e.printStackTrace();
                    }

                    // Se face acquire pe semaforul conform directiei de mers
                    try {
                        SimpleIntersection.strictXSemaphores.get(car.getStartDirection()).acquire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    System.out.println("Car " + car.getId() + " was selected to enter the roundabout from lane "
                            + car.getStartDirection());

                    // Se asteapta la bariera pentru ca toate masinile de pe toate directiile sa fie selectate pentru a
                    // intra in intersectie, iar apoi se intra in intersectie
                    try {
                        SimpleIntersection.allOutBarrier.await();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        e.printStackTrace();
                    }

                    // Mesaj de intrare in intersectie
                    System.out.println("Car " + car.getId() + " has entered the roundabout from lane "
                            + car.getStartDirection());

                    // Se asteapta timpul precizat in fisierul de intrare
                    try {
                        sleep(SimpleIntersection.timeInStrictX);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Se asteapta la bariera pentru ca toate masinile sa poata afisa faptul ca au intrat in
                    // intersectie fara a se intercala cu mesajul de iesire din intersectie
                    try {
                        SimpleIntersection.allOutBarrier.await();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        e.printStackTrace();
                    }

                    // Mesaj de iesire din intersectie
                    System.out.println("Car " + car.getId() + " has exited the roundabout after "
                            + SimpleIntersection.timeInStrictX / 1000 + " seconds");

                    // Se asteapta ca toate masinile din intersectie sa afiseze faptul ca au iesit din aceasta
                    try {
                        SimpleIntersection.allOutBarrier.await();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        e.printStackTrace();
                    }

                    // Se face release pe semaforul corespunzator directiei masinii care a iesit
                    SimpleIntersection.strictXSemaphores.get(car.getStartDirection()).release();

                }
            };
            case "simple_max_x_car_roundabout" -> new IntersectionHandler() {
                @Override
                public void handle(Car car) {
                    // Get your Intersection instance

                    try {
                        sleep(car.getWaitingTime());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } // NU MODIFICATI

                    // Continuati de aici

                    System.out.println("Car " + car.getId() + " has reached the roundabout from lane "
                            + car.getStartDirection());

                    // Masinile incearca sa faca acquire pe semaforul asociat directiei lor
                    try {
                        SimpleIntersection.maxXSemaphores.get(car.getStartDirection()).acquire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    System.out.println("Car " + car.getId() + " has entered the roundabout from lane "
                            + car.getStartDirection());

                    // Se asteapta timpul dat din fisierul de intrare
                    try {
                        sleep(SimpleIntersection.timeInMaxX);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    System.out.println("Car " + car.getId() + " has exited the roundabout after "
                            + SimpleIntersection.timeInMaxX / 1000  + " seconds");

                    // Se face release pe semaforul asociat directiei de mers a masinilor care ies din intersectie
                    SimpleIntersection.maxXSemaphores.get(car.getStartDirection()).release();
                }
            };
            case "priority_intersection" -> new IntersectionHandler() {
                @Override
                public void handle(Car car) {
                    // Get your Intersection instance

                    try {
                        sleep(car.getWaitingTime());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } // NU MODIFICATI

                    // Continuati de aici

                    // if asociat masinilor cu prioritate scazuta
                    if (car.getPriority() == 1) {

                        System.out.println("Car " + car.getId() + " with low priority is trying to enter the "
                                + "intersection...");

                        // Masinile cu prioritate scazuta sunt introduse intr-o coada de tipul
                        // ArrayBlockingQueue<Integer> pentru a putea fi scoase mai tarziu in ordinea in care au ajuns
                        // in intersectie
                        try {
                            SimpleIntersection.lowQueue.put(car.getId());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        // Daca exista o masina (thread) cu prioritate inalta in intersectie masinile (thread-urile)
                        // cu prioritate scazuta intra in sleep
                        while (SimpleIntersection.highPriorityInside.get() != 0) {
                            synchronized (SimpleIntersection.sync) {
                                try {
                                    SimpleIntersection.sync.wait();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        // Pentru a putea introduce in intersectie masinile in ordniea in care au ajuns in fata ei
                        // am incercat sa folosesc take(), dar afisajul nu se facea neaparat in mod ordonat, deoarece,
                        // desi take() permite ca un singur thread sa ia un element din coada afisajul era facut
                        // de toate threadurile, pentru a le ordona a fost nevoie de synchronized
                        synchronized (SimpleIntersection.lowQueueSync) {
                            try {

                                System.out.println("Car " + SimpleIntersection.lowQueue.take()
                                        + " with low priority has entered the intersection");

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    // if asociat masinilor cu prioritate inalta
                    if (car.getPriority() > 1) {
                        // se incrementeaza numarul de masini cu prioritate din intersectie
                        SimpleIntersection.highPriorityInside.incrementAndGet();

                        System.out.println("Car " + car.getId() +" with high priority has entered the intersection");

                        // Se asteapta timpul cerut
                        try {
                            sleep(Constants.PRIORITY_INTERSECTION_PASSING);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        System.out.println("Car " + car.getId() + " with high priority has exited the intersection");
                        // Se decrementeaza numarul de masini cu prioritate din intersectie
                        SimpleIntersection.highPriorityInside.decrementAndGet();

                        // Daca numarul de masini cu prioritate din intersectie este 0 atunci toate thread-urile sunt
                        // notificate pentru a putea sa iasa din sleep
                        if (SimpleIntersection.highPriorityInside.get() == 0) {
                            synchronized (SimpleIntersection.sync) {
                                SimpleIntersection.sync.notifyAll();
                            }
                        }
                    }


                }
            };
            case "crosswalk" -> new IntersectionHandler() {
                @Override
                public void handle(Car car) {
                    // Se verifica daca au terminat de venit pietoni
                    while (!Main.pedestrians.isFinished()) {
                        // Se verifica daca pietoni trebuie sa treaca, daca da masinile intra in sleep pana cand
                        // pietoni traverseaza
                        while (Main.pedestrians.isPass()) {
                            synchronized (SimpleIntersection.syncPedestrians) {
                                try {
                                    // Se verifica mesajul dat anterior de masini, daca este green atunci se va afisa
                                    // red
                                    if (!SimpleIntersection.outMessage[car.getId()].equals("Car " + car.getId() + " has now red light")) {
                                        System.out.println("Car " + car.getId() + " has now red light");
                                        SimpleIntersection.outMessage[car.getId()] = "Car " + car.getId() + " has now red light";
                                    }
                                    // Se pun in wait() masinile, dupa afisarea culorii
                                    SimpleIntersection.syncPedestrians.wait();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        //  Se verifica mesajul dat anterior de masini, daca este red atunci se va afisa green
                        if (!SimpleIntersection.outMessage[car.getId()].equals("Car " + car.getId() + " has now green light")) {
                            System.out.println("Car " + car.getId() + " has now green light");
                            SimpleIntersection.outMessage[car.getId()] = "Car " + car.getId() + " has now green light";
                        }
                    }
                }
            };
            case "simple_maintenance" -> new IntersectionHandler() {
                @Override
                public void handle(Car car) {

                    System.out.println("Car " + car.getId() + " from side number " + car.getStartDirection()
                            + " has reached the bottleneck");

                    // Se incepe prin a permite masinilor de pe directia 0 sa treaca si se face acquire pe semaforul
                    // directiei 0
                    if (car.getStartDirection() == 0) {
                        try {
                            SimpleIntersection.passing0Semaphore.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        System.out.println("Car " + car.getId() + " from side number " + car.getStartDirection()
                                + " has passed the bottleneck");

                        // Se asteapta la bariera pentru ca toate thread-urile sa faca release in aproximativ
                        // acelasi timp la token-urile necesare trecerii masinilor de pe directia 1
                        try {
                            SimpleIntersection.passed0Barrier.await();
                        } catch (InterruptedException | BrokenBarrierException e) {
                            e.printStackTrace();
                        }
                        SimpleIntersection.passing1Semaphore.release();
                    }

                    // Se permite trecerea masinilor de pe directia 1 daca au trecut inainte masini de pe directia 0
                    // care au produs token-uri pentru semaforul directiei 1
                    if (car.getStartDirection() == 1) {
                        try {
                            SimpleIntersection.passing1Semaphore.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        System.out.println("Car " + car.getId() + " from side number " + car.getStartDirection()
                                + " has passed the bottleneck");

                        // Se asteapta la bariera pentru ca toate thread-urile sa faca release in aproximativ
                        // acelasi timp la token-urile necesare trecerii masinilor de pe directia 0
                        try {
                            SimpleIntersection.passed1Barrier.await();
                        } catch (InterruptedException | BrokenBarrierException e) {
                            e.printStackTrace();
                        }
                        SimpleIntersection.passing0Semaphore.release();
                    }
                }
            };
            case "complex_maintenance" -> new IntersectionHandler() {
                @Override
                public void handle(Car car) {
                    
                }
            };
            case "railroad" -> new IntersectionHandler() {
                @Override
                public void handle(Car car) {

                    System.out.println("Car " + car.getId() + " from side number " + car.getStartDirection()
                            + " has stopped by the railroad");

                    // Se adauga in coada laneZero masinile care vin din directia 0 si laneOne cele care vin din
                    // directia 1
                    if (car.getStartDirection() == 0) {
                        try {
                            SimpleIntersection.laneZero.put(car.getId());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            SimpleIntersection.laneOne.put(car.getId());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    // Se asteapta la bariera pana cand trenul trece
                    try {
                        SimpleIntersection.trainBarrier.await();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        e.printStackTrace();
                    }

                    // Se afiseaza o singura data faptul ca trenul a trecut de catre thread-ul care a ajuns primul
                    // la instructiunea de afisaj
                    if (SimpleIntersection.passed == 0) {
                        SimpleIntersection.passed++;
                        System.out.println("The train has passed, cars can now proceed");
                    }

                    // Se afiseaza in ordniea sosirii masinile de pe directia 0
                    if (car.getStartDirection() == 0) {
                        synchronized (SimpleIntersection.laneZeroSync) {
                            try {

                                System.out.println("Car " + SimpleIntersection.laneZero.take()
                                        + " from side number 0 has started driving");

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    // Se afiseaza in ordniea sosirii masinile de pe directia 1
                    } else {
                        synchronized (SimpleIntersection.laneOneSync) {
                            try {

                                System.out.println("Car " + SimpleIntersection.laneOne.take()
                                        + " from side number 1 has started driving");

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            };
            default -> null;
        };
    }
}

import java.util.*;

public class Airport {
    /** Основная очередь грузов. */
    private Queue<Cargo> mainCargoQueue;

    /** Очередь грузов, для которых на данный момент нет самолётов. */
    private Queue<Cargo> garageCargoQueue;

    /** Очередь доступных самолётов. */
    private Queue<Plane> planeQueue;

    public Airport() {
        mainCargoQueue = new ArrayDeque<>();
        garageCargoQueue = new ArrayDeque<>();
        planeQueue = new ArrayDeque<>();
    }

    public void startWork() throws InterruptedException {
        Timer timer = new Timer();
        TimerTask cargoGenerator = new CargoGenerator(mainCargoQueue);
        timer.schedule(cargoGenerator, 0, 1000);
        TimerTask planeGenerator = new PlaneGenerator(planeQueue);
        timer.schedule(planeGenerator, 0, 1000);
        while (true) {
            Thread.sleep(1000);
            System.out.println(Arrays.toString(mainCargoQueue.toArray()));
            System.out.println(Arrays.toString(planeQueue.toArray()));
        }
    }
}

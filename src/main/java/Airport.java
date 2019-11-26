import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Airport {
    /** Основная очередь грузов. */
    private volatile Queue<Cargo> mainCargoQueue;

    /** Очередь грузов, для которых на не нашлось самолёта. */
    private volatile Queue<Cargo> garageCargoQueue;

    /** Очередь доступных самолётов. */
    private volatile List<Plane> planeQueue;

    public Airport() {
        mainCargoQueue = new ArrayDeque<>();
        garageCargoQueue = new ArrayDeque<>();
        planeQueue = new ArrayList<>();
    }

    public synchronized void startWork() throws InterruptedException {
        Executor executor = Executors.newFixedThreadPool(3);
        Timer timer = new Timer();
        TimerTask cargoGenerator = new CargoGenerator(mainCargoQueue);
        timer.schedule(cargoGenerator, 0, 1000);
        TimerTask planeGenerator = new PlaneGenerator(planeQueue);
        timer.schedule(planeGenerator, 0, 20000);
        while (true) {
            executor.execute(new LoadMaster(mainCargoQueue, garageCargoQueue, planeQueue));
            System.out.println(Arrays.toString(garageCargoQueue.toArray()));
            System.out.println(Arrays.toString(mainCargoQueue.toArray()));
            System.out.println(Arrays.toString(planeQueue.toArray()));
            Thread.sleep(2000);
        }
    }
}

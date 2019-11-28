import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class Airport {
    /** Основная очередь грузов. */
    private volatile Queue<Cargo> mainCargoQueue;

    /** Очередь грузов, для которых на не нашлось самолёта. */
    private volatile Queue<Cargo> garageCargoQueue;

    /** Очередь доступных самолётов. */
    private volatile List<Plane> planeQueue;

    private Semaphore semaphore = new Semaphore(1);

    /** Конструктор без параметров. */
    public Airport() {
        mainCargoQueue = new ArrayDeque<>();
        garageCargoQueue = new ArrayDeque<>();
        planeQueue = new ArrayList<>();
    }

    public void startWork() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(Settings.LOAD_MASTER_COUNT);
        Timer timer = new Timer();
        TimerTask cargoGenerator = new CargoGenerator(semaphore, mainCargoQueue);
        timer.schedule(cargoGenerator, 0, Settings.CARGO_GENERATING_PERIOD);
        TimerTask planeGenerator = new PlaneGenerator(semaphore, planeQueue);
        timer.schedule(planeGenerator, 0, Settings.PLANE_GENERATING_PERIOD);
        while (true) {
            executorService.execute(new LoadMaster(semaphore, mainCargoQueue, garageCargoQueue, planeQueue));
            semaphore.acquire();
            System.out.println(this + " garageQueue: "    + Arrays.toString(garageCargoQueue.toArray()));
            System.out.println(this + " mainCargoQueue: " + Arrays.toString(mainCargoQueue.toArray()));
            System.out.println(this + " planeQueue: "     + Arrays.toString(planeQueue.toArray()));
            semaphore.release();
            Thread.sleep(Settings.LOAD_MASTERS_CALL_PERIOD);
        }
    }
}

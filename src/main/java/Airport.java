import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Airport {
    /** Основная очередь грузов. */
    private volatile Queue<Cargo> mainCargoQueue;

    /** Очередь грузов, для которых на не нашлось самолёта. */
    private volatile Queue<Cargo> garageCargoQueue;

    /** Очередь доступных самолётов. */
    private volatile List<Plane> planeQueue;

    /** Конструктор без параметров. */
    public Airport() {
        mainCargoQueue = new ArrayDeque<>();
        garageCargoQueue = new ArrayDeque<>();
        planeQueue = new ArrayList<>();
    }

    public void startWork() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(Settings.LOAD_MASTER_COUNT);
        Timer timer = new Timer();
        TimerTask cargoGenerator = new CargoGenerator(mainCargoQueue);
        timer.schedule(cargoGenerator, 0, Settings.CARGO_GENERATING_PERIOD);
        TimerTask planeGenerator = new PlaneGenerator(planeQueue);
        timer.schedule(planeGenerator, 0, Settings.PLANE_GENERATING_PERIOD);
        while (true) {
            executorService.execute(new LoadMaster(mainCargoQueue, garageCargoQueue, planeQueue));
            synchronized (garageCargoQueue) {
                System.out.println(this + " garageQueue: "    + Arrays.toString(garageCargoQueue.toArray()));
            }
            synchronized (mainCargoQueue) {
                System.out.println(this + " mainCargoQueue: " + Arrays.toString(mainCargoQueue.toArray()));
            }
            synchronized (planeQueue) {
                System.out.println(this + " planeQueue: "     + Arrays.toString(planeQueue.toArray()));
            }
            Thread.sleep(Settings.LOAD_MASTERS_CALL_PERIOD);
        }
    }
}

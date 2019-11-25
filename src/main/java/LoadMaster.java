import java.util.List;
import java.util.Queue;

public class LoadMaster implements Runnable {

    /** Основная очередь грузов. */
    private final Queue<Cargo> mainCargoQueue;

    /** Очередь грузов, для которых на не нашлось самолёта. */
    private final Queue<Cargo> garageCargoQueue;

    /** Очередь доступных самолётов. */
    private final List<Plane> planeQueue;

    public LoadMaster(Queue<Cargo> mainCargoQueue, Queue<Cargo> garageCargoQueue, List<Plane> planeQueue) {
        this.mainCargoQueue = mainCargoQueue;
        this.garageCargoQueue = garageCargoQueue;
        this.planeQueue = planeQueue;
    }

    @Override
    public synchronized void run() {
        System.out.println("Thread " + this);
        Cargo cargo = null;
        Plane plane = null;
        if(!garageCargoQueue.isEmpty()) {
            cargo = garageCargoQueue.poll();
        }
        else if(!mainCargoQueue.isEmpty()) {
            cargo = mainCargoQueue.poll();
        }
        if(cargo != null) {
            for (int i = 0; i < planeQueue.size(); i++) {
                System.out.println("==================================================");
                System.out.println(cargo);
                plane = planeQueue.get(i);
                System.out.println(plane);
                if(plane.canTransferCargo(cargo)) {
                    plane.putCargo(cargo);
                    System.out.println(plane);
                    System.out.println("==================================================");
                    break;
                }

            }
        }
        if(plane != null && plane.isReady()) {
            planeQueue.remove(plane);
        }
    }
}

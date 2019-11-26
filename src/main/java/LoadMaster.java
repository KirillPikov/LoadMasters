import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.util.Pair;

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
        Pair<Boolean, Cargo> resultPut = null;
        if(!garageCargoQueue.isEmpty()) {
            cargo = garageCargoQueue.poll();
        }
        else if(!mainCargoQueue.isEmpty()) {
            cargo = mainCargoQueue.poll();
        }
        if(cargo != null) {
            for (int i = 0; i < planeQueue.size(); i++) {
                plane = planeQueue.get(i);
                if(plane.canTransferCargo(cargo)) {
                    System.out.println("OK!K!");
                    resultPut = plane.putCargo(cargo);
                    System.out.println(resultPut);
                    break;
                }

            }
        }
        System.out.println(resultPut);
        if(resultPut != null && !resultPut.getKey()) {
            garageCargoQueue.add(resultPut.getValue());
        }
        if(plane != null && !plane.isReady()) {
            System.out.println("ADD!");
            planeQueue.add(plane);
        }
    }
}

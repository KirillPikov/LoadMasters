import java.util.Queue;
import java.util.Random;
import java.util.TimerTask;

/** Генератор самолётов. */
public class PlaneGenerator extends TimerTask {

    /** Очередь самолётов. */
    private Queue<Plane> planeQueue;

    /**
     * Конструктор с параметром.
     * @param planeQueue очередь грузов, в которую будем генерировать новые.
     */
    public PlaneGenerator(Queue<Plane> planeQueue) {
        this.planeQueue = planeQueue;
    }

    /**  Метод, выполняющий генерацю самолёта. */
    @Override
    public void run() {
        Destination destination = Destination.getRandomDestination();
        Plane plane = new Plane(destination);
        int cabinCount = new Random().nextInt() % Settings.MAX_CABIN_COUNT + 1;
        for (int i = 0; i < cabinCount; i++) {
            plane.addCabin(CabinGenerator.generateCabin());
        }
        planeQueue.add(plane);
    }
}

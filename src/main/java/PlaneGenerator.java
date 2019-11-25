import java.util.List;
import java.util.Random;
import java.util.TimerTask;

/** Генератор самолётов. */
public class PlaneGenerator extends TimerTask {

    /** Очередь самолётов. */
    private List<Plane> planeQueue;

    /**
     * Конструктор с параметром.
     * @param planeQueue очередь грузов, в которую будем генерировать новые.
     */
    public PlaneGenerator(List<Plane> planeQueue) {
        this.planeQueue = planeQueue;
    }

    /**  Метод, выполняющий генерацю самолёта. */
    @Override
    public void run() {
        Destination destination = Destination.getRandomDestination();
        Plane plane = new Plane(destination);
        int cabinCount = Math.abs(new Random().nextInt()) % Settings.MAX_CABIN_COUNT + 1;
        for (int i = 0; i < cabinCount; i++) {
            plane.addCabin(CabinGenerator.generateCabin());
        }
        synchronized (planeQueue) {
            planeQueue.add(plane);
        }
    }
}

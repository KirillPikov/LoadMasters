import java.util.List;
import java.util.Random;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

/** Генератор самолётов. */
public class PlaneGenerator extends TimerTask {

    /** Семафор. */
    private final Semaphore semaphore;

    /** Очередь самолётов. */
    private final List<Plane> planeQueue;

    /**
     * Конструктор с параметром.
     * @param semaphore семафор.
     * @param planeQueue очередь грузов, в которую будем генерировать новые.
     */
    public PlaneGenerator(Semaphore semaphore, List<Plane> planeQueue) {
        this.semaphore = semaphore;
        this.planeQueue = planeQueue;
    }

    /**  Метод, выполняющий генерацю самолёта. */
    @Override
    public void run() {
        System.out.println(this + " Начало задачи генератора самолётов ");
        Destination destination = Destination.getRandomDestination();
        Plane plane = new Plane(destination);
        int cabinCount = Math.abs(new Random().nextInt()) % Settings.MAX_CABIN_COUNT + 1;
        for (int i = 0; i < cabinCount; i++) {
            plane.addCabin(CabinGenerator.generateCabin());
        }
        try {
            semaphore.acquire();
            planeQueue.add(plane);
            System.out.println(this + " Добавили новый самолёт ");
            System.out.println(this + " Возобновили потоки ");
            semaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

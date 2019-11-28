import java.util.Queue;
import java.util.Random;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

/** Генератор грузов. */
public class CargoGenerator extends TimerTask {

    /* Семафор */
    private final Semaphore semaphore;

    /** Очередь грузов. */
    private final Queue<Cargo> cargoQueue;

    /**
     * Конструктор с параметром.
     * @param semaphore семафор.
     * @param cargoQueue очередь грузов, в которую будем генерировать новые.
     */
    public CargoGenerator(Semaphore semaphore, Queue<Cargo> cargoQueue) {
        this.semaphore = semaphore;
        this.cargoQueue = cargoQueue;
    }

    /** Метод, выполняющий генерацю груза и его добавление в очередь грузов. */
    @Override
    public void run() {
        System.out.println(this + " Начало задачи генератора груза ");
        int volume = Math.abs(new Random().nextInt()) % Settings.MAX_CARGO_VOLUME + Settings.MIN_CARGO_VOLUME;
        CargoType cargoType = CargoType.getRandomCargoType();
        Destination destination = Destination.getRandomDestination();
        try {
            semaphore.acquire();
            cargoQueue.add(
                    new Cargo(volume, cargoType, destination)
            );
            System.out.println(this + " Добавили новый груз ");
            System.out.println(this + " Возобновили потоки ");
            semaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

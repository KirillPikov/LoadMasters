import java.util.Queue;
import java.util.Random;
import java.util.TimerTask;

/** Генератор грузов. */
public class CargoGenerator extends TimerTask {

    /** Очередь грузов. */
    private final Queue<Cargo> cargoQueue;

    /**
     * Конструктор с параметром.
     * @param cargoQueue очередь грузов, в которую будем генерировать новые.
     */
    public CargoGenerator(Queue<Cargo> cargoQueue) {
        this.cargoQueue = cargoQueue;
    }

    /** Метод, выполняющий генерацю груза и его добавление в очередь грузов. */
    @Override
    public void run() {
        System.out.println(this + " Начало задачи генератора груза ");
        int volume = Math.abs(new Random().nextInt()) % Settings.MAX_CARGO_VOLUME + Settings.MIN_CARGO_VOLUME;
        CargoType cargoType = CargoType.getRandomCargoType();
        Destination destination = Destination.getRandomDestination();
        synchronized (cargoQueue) {
            cargoQueue.add(
                    new Cargo(volume, cargoType, destination)
            );
            System.out.println(this + " Добавили новый груз ");
            cargoQueue.notify();
            System.out.println(this + " Возобновили потоки ");
        }
    }
}

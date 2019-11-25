import java.util.Queue;
import java.util.Random;
import java.util.TimerTask;

/** Генератор грузов. */
public class CargoGenerator extends TimerTask {

    /** Очередь грузов. */
    private Queue<Cargo> cargoQueue;

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
        int volume = Math.abs(new Random().nextInt()) % Settings.MAX_CARGO_VOLUME + Settings.MIN_CARGO_VOLUME;
        CargoType cargoType = CargoType.getRandomCargoType();
        Destination destination = Destination.getRandomDestination();
        cargoQueue.add(
            new Cargo(volume, cargoType, destination)
        );
    }
}

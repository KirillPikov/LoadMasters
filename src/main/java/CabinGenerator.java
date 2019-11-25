import java.util.Random;

/** Генератор отсеков самолёта. */
public class CabinGenerator {

    /** Пустой приватный конструктор. */
    private CabinGenerator() {

    }

    /**
     * Метод генерации отсека.
     * @return сгенерированный отсек.
     */
    public static Cabin generateCabin() {
        int volume = Math.abs(new Random().nextInt()) % Settings.MAX_CABIN_VOLUME + Settings.MIN_CABIN_VOLUME;
        CargoType cargoType = CargoType.getRandomCargoType();
        return new Cabin(volume, cargoType);
    }
}

/**
 * Класс настроек приложения.
 */
public class Settings {
    /**
     * Пустой приватный конструктор.
     */
    private Settings() {
    }

    /** Значение загруженности самолёта, необходимой для отправления самолёта. */
    public static final double CONGESTION = 0.8;

    /** Количество потоков погрузчиков (lodd-masters). */
    public static final double LOAD_MASTER_COUNT = 5;

    /** Минимальный объём, который может занимать груз. */
    public static final int MIN_CARGO_VOLUME = 1;

    /** Максимальный объём, который может занимать груз. */
    public static final int MAX_CARGO_VOLUME = 10;

    /** Минимальный объём кабины. */
    public static final int MIN_CABIN_VOLUME = 5;

    /** Максимальный объём кабины. */
    public static final int MAX_CABIN_VOLUME = 30;

    /** Количество типов грузов в {@link CargoType}. */
    public static final int MAX_CABIN_COUNT = 4;

    /** Количество мест назначений в {@link Destination}. */
    public static final int DESTINATION_COUNT = 5;
}

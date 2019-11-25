/**
 * Груз.
 */
public class Cargo {

    /** Объём груза. */
    private final int volume;

    /** Тип груза. */
    private final CargoType type;

    /** Место назначения груза. */
    private final Destination destination;

    /**
     * Конструктор с параметрами.
     * @param volume {@link #volume}
     * @param type {@link #type}
     */
    public Cargo(int volume, CargoType type, Destination destination) {
        this.volume = volume;
        this.type = type;
        this.destination = destination;
    }

    public int getVolume() {
        return volume;
    }

    public CargoType getType() {
        return type;
    }

    public Destination getDestination() {
        return destination;
    }

    @Override
    public String toString() {
        return "Cargo{" +
                "volume=" + volume +
                ", type=" + type +
                ", destination=" + destination +
                '}';
    }
}

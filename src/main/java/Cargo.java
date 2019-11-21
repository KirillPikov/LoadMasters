/**
 * Груз.
 */
public class Cargo {
    /**
     * Объём груза.
     */
    private final int volume;
    /**
     * Тип груза.
     */
    private final CargoType type;

    /**
     * Конструктор с параметрами.
     * @param volume {@link #volume}
     * @param type {@link #type}
     */
    public Cargo(int volume, CargoType type) {
        this.volume = volume;
        this.type = type;
    }

    public int getVolume() {
        return volume;
    }

    public CargoType getType() {
        return type;
    }
}

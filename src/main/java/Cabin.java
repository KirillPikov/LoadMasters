import java.util.HashSet;
import java.util.Set;

/**
 * Отсек для хранения груза.
 */
public class Cabin {
    /**
     * Фактический объём отсека.
     */
    private final int maxVolume;
    /**
     * Свободный объём отсека.
     */
    private final int freeVolume;
    /**
     * Тип какого груза должен перевозить.
     */
    private final CargoType type;
    /**
     * Множество грузов, хранящихся в отсеке.
     */
    private final Set<Cargo> cargoSet;

    /**
     * Конструктор с параметрами.
     * @param maxVolume {@link #maxVolume}
     * @param freeVolume {@link #freeVolume}
     * @param type {@link #type}
     */
    public Cabin(int maxVolume, int freeVolume, CargoType type) {
        this.maxVolume = maxVolume;
        this.freeVolume = freeVolume;
        this.type = type;
        this.cargoSet = new HashSet<>();
    }

    public int getFreeVolume() {
        return freeVolume;
    }

    public CargoType getType() {
        return type;
    }
}

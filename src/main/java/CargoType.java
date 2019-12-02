import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/** Тип груза. */
public enum CargoType {

    /** Живой. */
    LIVE,

    /** Скоропортящийся. */
    PERISHABLE,

    /** Опасный. */
    DANGEROUS,

    /** Обычный. */
    ORDINARY;

    private static final List<CargoType> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();

    public static CargoType getRandomCargoType()  {
        return VALUES.get(RANDOM.nextInt(SIZE));
    }
}

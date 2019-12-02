import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/** Место назначения грузов и самолётов. */
public enum Destination {

    /** Анапа. */
    ANAPA,

    /** Сургут. */
    SYRGYT,

    /** Сочи. */
    SOCHI,

    /** Москва. */
    MOSKOW,

    /** * Ульяновск. */
    YLSK;

    private static final List<Destination> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();

    public static Destination getRandomDestination()  {
        return VALUES.get(RANDOM.nextInt(SIZE));
    }
}

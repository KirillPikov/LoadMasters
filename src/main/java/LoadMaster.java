import javafx.util.Pair;

import java.util.concurrent.Callable;

public class LoadMaster implements Callable<Pair<Boolean, Cargo>> {

    /** Груз, который будем пытаться положить в самолёт. */
    Cargo cargo;

    /** Самолёт, в который пытаемся положить груз. */
    Plane plane;

    public LoadMaster(Cargo cargo, Plane plane) {
    }

    @Override
    public Pair<Boolean, Cargo> call() throws Exception {
        return null;
    }
}

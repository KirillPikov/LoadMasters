import javafx.util.Pair;

import java.util.HashSet;
import java.util.Set;

/**
 * Отсек для хранения груза.
 */
public class Cabin {

    /** Фактический объём отсека. */
    private final int maxVolume;

    /** Свободный объём отсека. */
    private final int freeVolume;

    /** Тип какого груза должен перевозить. */
    private final CargoType type;

    /** Множество грузов, хранящихся в отсеке. */
    private final Set<Cargo> cargoSet;

    /**
     * Конструктор с параметрами.
     * @param maxVolume {@link #maxVolume}
     * @param type {@link #type}
     */
    public Cabin(int maxVolume, CargoType type) {
        this.maxVolume = maxVolume;
        this.freeVolume = this.maxVolume;
        this.type = type;
        this.cargoSet = new HashSet<>();
    }

    /**
     * Метод, который выполняет попытку положить груз в отсек.
     * @param cargo груз, который хотим положить.
     * @return 3 случая пары:
     * <li></li> ключ пары true - положили без замены.
     * <li></li> ключ пары false - если значение пары равно null, то груз не положили.
     * <li></li> ключ пары false - если значение пары отлично от null, то положили с заменой,
     * при этом значени пары и есть груз, на который заменили.
     */
    public Pair<Boolean, Cargo> putCargo(Cargo cargo) {
        /* Груз, на который можем заменить передаваемый */
        Cargo removedCargo = null;
        /* Если в отсеке есть место */
        if(cargo.getVolume() <= freeVolume) {
            /* Но тип груза не соответствует типу отсека */
            if(cargo.getType() != this.getType()) {
                /* Проверяем, можем ли по правилам задачи перевозить груз этого типа в отсеке этого типа */
                if(cargo.getType() != CargoType.LIVE && cargo.getType() != CargoType.PERISHABLE &&
                        this.getType() != CargoType.DANGEROUS && this.getType() != CargoType.ORDINARY) {
                    cargoSet.add(cargo);
                }
            } else /* В случае, если есть место и типы совпадают, просто добавляем груз */ {
                cargoSet.add(cargo);
            }
        } else if(cargo.getType() == this.getType()) /* В случае, если места нет, но типы совпадают */ {
            /* Пробегаемся по всем хранящимся грузам */
            for (Cargo currentCargo : cargoSet) {
                /* Если находим груз, который не совпадает с типом отсека */
                if (currentCargo.getType() != this.getType()) {
                    /* Проверяем, что если мы уберём несовпадающий груз,
                     у нас хватит места добавить новый, т.е. есть ли смысл убирать? */
                    if (cargo.getVolume() <= freeVolume - currentCargo.getVolume()) {
                        removedCargo = currentCargo;
                        cargoSet.remove(removedCargo);
                        cargoSet.add(cargo);
                        break;
                    }
                }
            }
        }
        Pair result = removedCargo == null ? new Pair(true, cargo) : new Pair(false, removedCargo);
        return result;
    }

    public int getMaxVolume() {
        return maxVolume;
    }

    public int getFreeVolume() {
        return freeVolume;
    }

    public CargoType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Cabin{" +
                "maxVolume=" + maxVolume +
                ", freeVolume=" + freeVolume +
                ", type=" + type +
                ", cargoSet=" + cargoSet +
                '}';
    }
}

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Самолёт.
 */
public class Plane {

    /** Список отсеков самолёта. */
    private final List<Cabin> cabins;

    /** Место назначения самолёта. */
    private final Destination destination;

    /** Общий объём всех отсеков. */
    private int maxVolume;

    /** Типы грузов, который может перевозить самолёт. */
    private Set<CargoType> cargoTypesSet;

    /**
     * Конструктор с параметрами.
     * @param destination {@link #destination}
     */
    public Plane(Destination destination) {
        this.destination = destination;
        this.cabins = new ArrayList<>();
        this.maxVolume = 0;
        this.cargoTypesSet = new HashSet<>();
    }

    /**
     * Добавляет отсек в самолёт.
     * @param cabin добавляемый отсек.
     */
    public void addCabin(Cabin cabin) {
        cabins.add(cabin);
        maxVolume += cabin.getMaxVolume();
        if(!cargoTypesSet.contains(cabin.getType())) {
            cargoTypesSet.add(cabin.getType());
        }
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
        boolean canTransfer = false;
        Pair<Boolean, Cargo> putResult = null;
        /* Пробегаемся по всем отсекам */
        for (Cabin cabin : cabins) {
            /* И пробуем туда положить груз */
            putResult = cabin.putCargo(cargo);
            System.out.println("PUT REZULT = " + putResult);
            /* Определяем положился он в отсек или нет */
            if(putResult.getKey()) /* В этом случае однозначно положили без замены */ {
                canTransfer = true;
                break;
            } else if(putResult.getValue() != null) /* В этом случае положили с заменой */ {
                break;
            }
        }
        return canTransfer ? new Pair<>(true, cargo) : putResult;
    }

    /**
     * Проверяет, тип отсеков самолёта совместим с грузом, и совпадаютли места назначения.
     * @param cargo груз, который хотим перевезти.
     * @return Вернут результат условия "Если место назначения груза совпадает с местом отправки И
     * можем перевозить груз такого типа по условиям задачи".
     */
    public boolean canTransferCargo(Cargo cargo) {
        boolean canTransferType = false;
        /* Проверяем, можем ли по правилам задачи перевозить груз этого типа в отсеке этого типа */
        if(!cargoTypesSet.contains(cargo.getType())) {
            if(cargo.getType() != CargoType.LIVE && cargo.getType() != CargoType.PERISHABLE) {
                canTransferType = true;
            }
        } else {
            canTransferType = true;
        }
        /* Вернут результат условия "Если место назначения груза совпадает с местом отправки И можем перевозить" */
        return this.destination == cargo.getDestination() && canTransferType;
    }

    /**
     * Проверяет загруженность самолёта.
     * @return если самолёт загружен более чем на {@link Settings#CONGESTION} вернёт true.
     */
    public boolean isReady() {
        double currentFreeVolume = 0;
        for (Cabin cabin: cabins) {
            currentFreeVolume += cabin.getFreeVolume();
        }
        double freeVaolumeProc = currentFreeVolume / maxVolume;
        System.out.println(1.0 - freeVaolumeProc + " >= " + Settings.CONGESTION);
        return 1.0 - freeVaolumeProc >= Settings.CONGESTION;
    }

    @Override
    public String toString() {
        return "Plane{" +
                "cabins=" + cabins +
                ", destination=" + destination +
                ", maxVolume=" + maxVolume +
                ", cargoTypesSet=" + cargoTypesSet +
                '}';
    }
}

import javafx.util.Pair;

import java.util.List;
import java.util.Queue;

public class LoadMaster implements Runnable {

    /** Основная очередь грузов. */
    private final Queue<Cargo> mainCargoQueue;

    /** Очередь грузов, для которых на не нашлось самолёта. */
    private final Queue<Cargo> garageCargoQueue;

    /** Очередь доступных самолётов. */
    private final List<Plane> planeList;

    /* Результат работы load-master'а */
    private Pair<Boolean, Cargo> resultPut;

    /**
     * Конструктор с параметрами.
     * @param mainCargoQueue {@link #mainCargoQueue}
     * @param garageCargoQueue {@link #garageCargoQueue}
     * @param planeList {@link #planeList}
     */
    public LoadMaster(Queue<Cargo> mainCargoQueue, Queue<Cargo> garageCargoQueue, List<Plane> planeList) {
        this.mainCargoQueue = mainCargoQueue;
        this.garageCargoQueue = garageCargoQueue;
        this.planeList = planeList;
        this.resultPut = null;
    }

    /** Метод запуска задачи LoadMaster. */
    @Override
    public void run() {
        System.out.println(this + " Начало нового выполнения LoadMaster ");
        Cargo cargo = null;
        synchronized (garageCargoQueue) {
            /* Если есть грузы из склада ожидания */
            if (!garageCargoQueue.isEmpty()) {
                System.out.println(this + " Взял груз из доп склада");
                cargo = garageCargoQueue.poll();
            }
        }
        if(cargo != null) {
            /* Пытаемся его положить на один из самолётов */
            resultPut = this.tryPutCargo(cargo);
            /* Если результат попытки неудачный, возвращаем груз обратно */
            if (cargo != null && resultPut == null) {
                synchronized (garageCargoQueue) {
                    garageCargoQueue.add(cargo);
                }
            }
        }
        /* В случае, если положить не получилось */
        if(resultPut == null || (!resultPut.getKey() && resultPut.getValue() == null)) {
            System.out.println(this + " На доп складе нет груза");
            synchronized (mainCargoQueue) {
                /* Если нет груза на основном складе */
                if (mainCargoQueue.isEmpty()) {
                    try {
                        System.out.println(this + " Основной склад пуст. Перехожу в ожидание.");
                        /* Переходим в режим ожидания */
                        mainCargoQueue.wait();
                        System.out.println(this + " Дождался нового груза на основном складе");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                /* Забираем груз с основного склада */
                cargo = mainCargoQueue.poll();
            }
            System.out.println(this + " Получил груз с основного склада");
            resultPut = tryPutCargo(cargo);
        }
        System.out.println(this + " Резултат загрузки груза " + resultPut);
        /* Если груз положили с заменой */
        if(resultPut != null && !resultPut.getKey() && resultPut.getValue() != null) {
            System.out.println(this + " Груз добавился с заменой " + resultPut);
            /* Замену помещаем обратно на основной склад */
            synchronized (mainCargoQueue) {
                mainCargoQueue.add(resultPut.getValue());
            }
        }
        /* Если груз был изъят со склада, но не был погружен в какой-либо самолёт */
        if(resultPut == null && cargo != null) {
            System.out.println(this + " Груз не получилось добавить, возвращаем его ");
            synchronized (garageCargoQueue) {
                /* Помещаем его на склад ожидания */
                garageCargoQueue.add(cargo);
            }
        }
    }

    /**
     * Метод выполняющий попытку положить груз в какой-либо самолёт.
     * @param cargo груз, который пытаемся положить.
     * @return 3 случая пары:
     * <li></li> ключ пары true - положили без замены.
     * <li></li> ключ пары false - если значение пары равно null, то груз не положили.
     * <li></li> ключ пары false - если значение пары отлично от null, то положили с заменой,
     * при этом значени пары и есть груз, на который заменили.
     */
    private Pair<Boolean, Cargo> tryPutCargo(Cargo cargo) {
        Plane plane = null;
        Pair<Boolean, Cargo> resultPut = null;
        synchronized (planeList) {
            /* Проверка на наличие самолётов */
            if(planeList.isEmpty()) {
                try {
                    System.out.println(this + " Очередь самолётов пуста, перехожу в ожидание");
                    /* В случае, если самолётов нет, переходим в ожидание */
                    planeList.wait();
                    System.out.println(this + " Дождался нового самолёта");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            /* Пробегаемся по всем самолётам */
            for (int i = 0; i < planeList.size(); i++) {
                plane = planeList.get(i);
                /* Может ли самолёт по правилам задачи перевозить груз такого типа и назначения? */
                if(plane.canTransferCargo(cargo)) {
                    System.out.println(this + " Можем транспортировать ");
                    /* Помещаем груз в самолёт */
                    resultPut = plane.putCargo(cargo);
                    break;
                }
            }
        }
        /* В случае, если мы добавили груз и нашли самолёт, при этом он уже заполнен */
        if(resultPut != null && resultPut.getValue() != null && plane != null && plane.isReady()) {
            System.out.println(this + " Самолёт готов к отправке, мест нет! " + plane);
            synchronized (planeList) {
                /* Удаляем самолёт из очереди */
                planeList.remove(plane);
            }
        }
        return resultPut;
    }
}

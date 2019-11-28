import javafx.util.Pair;

import java.util.List;
import java.util.Queue;

public class LoadMaster implements Runnable {

    /** Основная очередь грузов. */
    private final Queue<Cargo> mainCargoQueue;

    /** Очередь грузов, для которых на не нашлось самолёта. */
    private final Queue<Cargo> garageCargoQueue;

    /** Очередь доступных самолётов. */
    private final List<Plane> planeQueue;

    /**
     * Конструктор с параметрами.
     * @param mainCargoQueue {@link #mainCargoQueue}
     * @param garageCargoQueue {@link #garageCargoQueue}
     * @param planeQueue {@link #planeQueue}
     */
    public LoadMaster(Queue<Cargo> mainCargoQueue, Queue<Cargo> garageCargoQueue, List<Plane> planeQueue) {
        this.mainCargoQueue = mainCargoQueue;
        this.garageCargoQueue = garageCargoQueue;
        this.planeQueue = planeQueue;
    }

    /** Метод запуска задачи LoadMaster. */
    @Override
    public void run() {
        System.out.println(this + " Начало нового выполнения LoadMaster ");
        Cargo cargo = null;
        Pair<Boolean, Cargo> resultPut = null;
        synchronized (garageCargoQueue) {
            /* Если есть грузы из склада ожидания */
            if(!garageCargoQueue.isEmpty()) {
                System.out.println(this + " Взял груз из доп склада");
                cargo = garageCargoQueue.poll();
                /* Пытаемся его положить на один из самолётов */
                resultPut = this.tryPutCargo(cargo);
                /* Если результат попытки неудачный, возвращаем груз обратно */
                if(cargo != null && resultPut == null) {
                    garageCargoQueue.add(cargo);
                }
            }
            /* В случае, если положить не получилось */
            if(resultPut == null || (!resultPut.getKey() && resultPut.getValue() == null)) {
                synchronized (mainCargoQueue) {
                    System.out.println(this + " На доп складе нет груза");
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
                    System.out.println(this + " Получил груз с основного склада");
                }
            }
        }

        System.out.println(this + " Резултат загрузки груза " + resultPut);
        /* Если груз положили с заменой */
        if(resultPut != null && !resultPut.getKey()) {
            synchronized (mainCargoQueue) {
                if (resultPut.getValue() != null) {
                    System.out.println(this + " Груз добавился с заменой " + resultPut);
                    /* Замену помещаем обратно на основной склад */
                    mainCargoQueue.add(resultPut.getValue());
                }
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
        synchronized (planeQueue) {
            /* Проверка на наличие самолётов */
            if(planeQueue.isEmpty()) {
                try {
                    System.out.println(this + " Очередь самолётов пуста, перехожу в ожидание");
                    /* В случае, если самолётов нет, переходим в ожидание */
                    planeQueue.wait();
                    System.out.println(this + " Дождался нового самолёта");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            /* Пробегаемся по всем самолётам */
            for (int i = 0; i < planeQueue.size(); i++) {
                plane = planeQueue.get(i);
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
        if(resultPut != null && plane != null && plane.isReady()) {
            System.out.println(this + " Самолёт готов к отправке, мест нет! " + plane);
            synchronized (planeQueue) {
                /* Удаляем самолёт из очереди */
                planeQueue.remove(plane);
            }
        }
        return resultPut;
    }
}

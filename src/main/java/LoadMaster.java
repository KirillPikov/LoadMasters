import javafx.util.Pair;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class LoadMaster implements Runnable {

    /** Семафор. */
    private final Semaphore semaphore;

    /** Основная очередь грузов. */
    private final Queue<Cargo> mainCargoQueue;

    /** Очередь грузов, для которых на не нашлось самолёта. */
    private final Queue<Cargo> garageCargoQueue;

    /** Очередь доступных самолётов. */
    private final List<Plane> planeList;

    /** Результат попытки погрузить груз на самолёт. */
    private Pair<Boolean, Cargo> resultPut;

    /**
     * Конструктор с параметрами.
     * @param mainCargoQueue {@link #mainCargoQueue}
     * @param garageCargoQueue {@link #garageCargoQueue}
     * @param planeList {@link #planeList}
     */
    public LoadMaster(Semaphore semaphore, Queue<Cargo> mainCargoQueue, Queue<Cargo> garageCargoQueue, List<Plane> planeList) {
        this.semaphore = semaphore;
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
        try {
            semaphore.acquire();
            /* Если есть грузы из склада ожидания */
            if (!garageCargoQueue.isEmpty()) {
                System.out.println(this + " Взял груз из доп склада");
                cargo = garageCargoQueue.poll();
            }
            semaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(cargo != null) {
            /* Пытаемся его положить на один из самолётов */
            try {
                resultPut = this.tryPutCargo(cargo);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            /* Если результат попытки неудачный, возвращаем груз обратно */
            if (cargo != null && resultPut == null) {
                try {
                    semaphore.acquire();
                    garageCargoQueue.add(cargo);
                    semaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        /* В случае, если положить не получилось */
        if(resultPut == null || (!resultPut.getKey() && resultPut.getValue() == null)) {
            System.out.println(this + " На доп складе нет груза");
            try {
                semaphore.acquire();
                /* Если нет груза на основном складе */
                if (mainCargoQueue.isEmpty()) {
                    System.out.println(this + " Основной склад пуст. Перехожу в ожидание.");
                    semaphore.release();
                    System.out.println(this + " Дождался нового груза на основном складе");
                }
                /* Забираем груз с основного склада */
                cargo = mainCargoQueue.poll();
                semaphore.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(this + " Получил груз с основного склада");
            try {
                resultPut = tryPutCargo(cargo);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(this + " Резултат загрузки груза " + resultPut);
        /* Если груз положили с заменой */
        if(resultPut != null && !resultPut.getKey() && resultPut.getValue() != null) {
            System.out.println(this + " Груз добавился с заменой " + resultPut);
            /* Замену помещаем обратно на основной склад */
            try {
                semaphore.acquire();
                synchronized (mainCargoQueue) {
                    mainCargoQueue.add(resultPut.getValue());
                }
                semaphore.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        /* Если груз был изъят со склада, но не был погружен в какой-либо самолёт */
        if(resultPut == null && cargo != null) {
            System.out.println(this + " Груз не получилось добавить, возвращаем его ");
            try {
                semaphore.acquire();
                synchronized (garageCargoQueue) {
                    garageCargoQueue.add(cargo);
                }
                semaphore.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
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
    private Pair<Boolean, Cargo> tryPutCargo(Cargo cargo) throws InterruptedException {
        Plane plane = null;
        Pair<Boolean, Cargo> resultPut = null;
            /* Проверка на наличие самолётов */
            if(planeList.isEmpty()) {
                semaphore.acquire();
                System.out.println(this + " Очередь самолётов пуста, перехожу в ожидание");
                /* В случае, если самолётов нет, переходим в ожидание */
                synchronized (planeList) {
                    semaphore.release();
                    planeList.wait();
                }
                System.out.println("=========================================================================================" + plane);
                System.out.println(this + " Дождался нового самолёта");
            }
            semaphore.acquire();
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
        semaphore.release();
        /* В случае, если мы добавили груз и нашли самолёт, при этом он уже заполнен */
        if(resultPut != null && plane != null && plane.isReady()) {
            System.out.println(this + " Самолёт готов к отправке, мест нет! " + plane);
            semaphore.acquire();
                /* Удаляем самолёт из очереди */
                planeList.remove(plane);
            semaphore.release();
        }
        return resultPut;
    }
}

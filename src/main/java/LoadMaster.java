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

    public LoadMaster(Queue<Cargo> mainCargoQueue, Queue<Cargo> garageCargoQueue, List<Plane> planeQueue) {
        this.mainCargoQueue = mainCargoQueue;
        this.garageCargoQueue = garageCargoQueue;
        this.planeQueue = planeQueue;
    }

    @Override
    public void run() {
        System.out.println(this + " Начало нового выполнения LoadMaster ");
        Cargo cargo = null;
        Pair<Boolean, Cargo> resultPut = null;

        synchronized (garageCargoQueue) {
            if(!garageCargoQueue.isEmpty()) {
                System.out.println(this + " Взял груз из доп склада");
                cargo = garageCargoQueue.poll();
                if(cargo != null) {
                    resultPut = this.tryPutCargo(cargo);
                }
            }
            if(resultPut == null || (!resultPut.getKey() && resultPut.getValue() == null)) {
                if(cargo != null && resultPut == null) {
                    garageCargoQueue.add(cargo);
                }
                synchronized (mainCargoQueue) {
                    System.out.println(this + " На доп складе нет груза");
                    if (mainCargoQueue.isEmpty()) {
                        try {
                            System.out.println(this + " Основной склад пуст. Перехожу в ожидание.");
                            mainCargoQueue.wait();
                            System.out.println(this + " Дождался нового груза на основном складе");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    cargo = mainCargoQueue.poll();
                    System.out.println(this + " Получил груз с основного склада");
                }
            }
        }

        System.out.println(this + " Резултат загрузки груза " + resultPut);
        if(resultPut != null && !resultPut.getKey()) {
            synchronized (garageCargoQueue) {
                if (resultPut.getValue() != null) {
                    System.out.println(this + " Груз добавился с заменой " + resultPut);
                    garageCargoQueue.add(resultPut.getValue());
                }
            }
        }
        if(resultPut == null && cargo != null) {
            System.out.println(this + " Груз не получилось добавить, возвращаем его ");
            synchronized (garageCargoQueue) {
                garageCargoQueue.add(cargo);
            }
        }
    }

    private Pair<Boolean, Cargo> tryPutCargo(Cargo cargo) {
        Plane plane = null;
        Pair<Boolean, Cargo> resultPut = null;
        synchronized (planeQueue) {
            if(planeQueue.isEmpty()) {
                try {
                    System.out.println(this + " Очередь самолётов пуста, перехожу в ожидание");
                    planeQueue.wait();
                    System.out.println(this + " Дождался нового самолёта");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            for (int i = 0; i < planeQueue.size(); i++) {
                plane = planeQueue.get(i);
                if(plane.canTransferCargo(cargo)) {
                    System.out.println(this + " Можем транспортировать ");
                    resultPut = plane.putCargo(cargo);
                    break;
                }
            }
        }
        if(resultPut != null && plane != null && plane.isReady()) {
            System.out.println(this + " Самолёт готов к отправке, мест нет! " + plane);
            synchronized (planeQueue) {
                planeQueue.remove(plane);
            }
        }
        return resultPut;
    }

}

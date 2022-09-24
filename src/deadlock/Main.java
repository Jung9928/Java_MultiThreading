package deadlock;

import java.util.Random;

/**
 * 각 스레드의 locking 순서를 동일하게 유지하면 데드락을 방지할 수 있다.
 * 락킹을 해제하는 순서는 각 스레드가 동일하지 않아도 된다.
 * 즉, 순환 종속성(순환 대기)를 해결하는게 핵심!!
 *
 * 다만, 방대한 크기의 어플리케이션일 경우, 다양한 곳에서 순환 대기 발생 코드가 있을 수 있으므로
 * 데드락을 감지할 수 있는 방어 코드(watchDog)를 작성하도록 하자!
 */

public class Main {

    public static void main(String[] args) {
        Intersaction intersaction = new Intersaction();
        Thread trainAThread = new Thread(new TrainA(intersaction));
        Thread trainBThread = new Thread(new TrainB(intersaction));

        trainAThread.start();
        trainBThread.start();
    }

    public static class TrainA implements Runnable {
        private Intersaction intersaction;
        private Random random = new Random();

        public TrainA(Intersaction intersaction) {
            this.intersaction = intersaction;
        }

        @Override
        public void run() {
            while(true) {
                long sleepingTime = random.nextInt(5);

                try {
                    Thread.sleep(sleepingTime);
                } catch (InterruptedException e) {

                }
                intersaction.takeRoadA();
            }
        }
    }

    public static class TrainB implements Runnable {
        private Intersaction intersaction;
        private Random random = new Random();

        public TrainB(Intersaction intersaction) {
            this.intersaction = intersaction;
        }

        @Override
        public void run() {
            while(true) {
                long sleepingTime = random.nextInt(5);

                try {
                    Thread.sleep(sleepingTime);
                } catch (InterruptedException e) {

                }
                intersaction.takeRoadB();
            }
        }
    }

    public static class Intersaction {

        private Object roadA = new Object();
        private Object roadB = new Object();

        public void takeRoadA() {
            synchronized (roadA) {
                System.out.println("Road A is locked by thread " + Thread.currentThread().getName());

                synchronized (roadB) {
                    System.out.println("Train is passing through road A");

                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {

                    }
                }
            }
        }

        public void takeRoadB() {
            synchronized (roadA) {
                System.out.println("Road B is locked by thread " + Thread.currentThread().getName());

                synchronized (roadB) {
                    System.out.println("Train is passing through road B");

                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {

                    }
                }
            }
        }
    }
}

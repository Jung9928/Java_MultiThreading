package join_method2;

import java.math.BigInteger;
import java.time.chrono.ThaiBuddhistEra;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.FormatterClosedException;
import java.util.List;

/**
 * main 스레드는 이미 모든 작업을 수행 후 종료됐지만
 * 너무 큰 값을 처리하는 스레드는 계속 RUNNING 중이다.
 * 스레드를 인터럽트 하지 않았기 때문에 앱도 여전히 실행 중일 것임. -> 앱이 여전히 실행되는 것은 문제가 됨.
 */

public class Main {

    public static void main(String[] args) throws InterruptedException {
        // 만약 어떤 한 값이 너무 크다면?? ex) 100000000L
        List<Long> inputNumbers = Arrays.asList(100000000L, 3435L, 35435L, 2324L, 4656L, 23L, 2435L, 5566L);

        List<FactorialThread> threads = new ArrayList<>();

        for(long inputNumber : inputNumbers) {
            threads.add(new FactorialThread(inputNumber));
        }

        // 팩토리얼 연산을 시작
        for(Thread thread : threads) {
            // 모든 팩토리얼 연산 스레드를 Daemon 스레드로 설정하여
            // 연산이 종료되지 않은 스레드가 있더라도 main 메소드가 종료되면 앱을 종료.
            thread.setDaemon(true);
            thread.start();
        }

        // join 메소드를 사용하여 팩토리얼 연산을 끝낼 때까지 main 메소드를 대기
        // 모든 스레드의 join 메소드는 스레드가 종료되어야 반환
        for(Thread thread : threads) {
            // 한 스레드의 팩토리얼 계산시간을 2초가 넘지 않게 제한.
            // 2초가 넘어도 스레드가 종료되지 않으면 join 메소드 리턴.
            thread.join(2000);
        }

        for(int i=0; i<inputNumbers.size(); i++) {
            // 팩토리얼 연산을 수행할 각 스레드들
            FactorialThread factorialThread = threads.get(i);

            // main 스레드가 결과 확인
            if(factorialThread.isFinished()) {
                System.out.println("Factorial of " + inputNumbers.get(i) + " is " + factorialThread.getResult());
            }
            else {
                System.out.println("The caculate for " + inputNumbers.get(i) + " is still in progress");
            }
        }
    }

    public static class FactorialThread extends Thread {
        private long inputNumber;
        private BigInteger result = BigInteger.ZERO;
        private boolean isFinished = false;

        public FactorialThread(long inputNumber) {this.inputNumber = inputNumber;}

        @Override
        public void run() {
            this.result = factorial(inputNumber);
            this.isFinished = true;
        }

        public BigInteger factorial(long n) {
            BigInteger tempResult = BigInteger.ONE;

            for(long i = n; i > 0; i--) {
                tempResult = tempResult.multiply(new BigInteger(Long.toString(i)));
            }
            return tempResult;
        }

        public boolean isFinished() {
            return isFinished;
        }

        public BigInteger getResult() {
            return result;
        }
    }
}


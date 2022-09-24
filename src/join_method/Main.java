package join_method;

import java.math.BigInteger;
import java.time.chrono.ThaiBuddhistEra;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.FormatterClosedException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        List<Long> inputNumbers = Arrays.asList(0L, 3435L, 35435L, 2324L, 4656L, 23L, 2435L, 5566L);

        List<FactorialThread> threads = new ArrayList<>();

        for(long inputNumber : inputNumbers) {
            threads.add(new FactorialThread(inputNumber));
        }

        // 팩토리얼 연산을 시작
        for(Thread thread : threads) {
            thread.start();
        }

        // join 메소드를 사용하여 팩토리얼 연산을 끝낼 때까지 main 메소드를 대기
        // 모든 스레드의 join 메소드는 스레드가 종료되어야 반환
        for(Thread thread : threads) {
            thread.join();
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

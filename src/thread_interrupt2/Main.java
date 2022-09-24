package thread_interrupt2;

import java.math.BigInteger;

public class Main {

    public static void main(String[] args) {
        Thread thread = new Thread(new LongComputationTask(new BigInteger("20000"), new BigInteger("100")));

        thread.start();

        /**
            만약, base와 power가 너무 큰 값이면 계산하는데 너무 오래걸린다.
            그래서 interrupt를 호출해서 종료하려고 하지만 그래도 역부족이다.
            인터럽트 시그널은 전달됐지만 이를 처리할 메소드나 로직이 없기 때문.
            그리고 여전히 LongComputationTask 스레드는 동작하는 상황이다.
            이 경우, 코드 내에서 시간이 오래 걸리는 구간을 찾아서 수정하는게 맞다.
         */
        thread.interrupt();
    }

    private static class LongComputationTask implements Runnable {

        private BigInteger base;
        private BigInteger power;

        public LongComputationTask(BigInteger base, BigInteger power) {
            this.base = base;
            this.power = power;
        }

        @Override
        public void run() {
            System.out.println(base + "*" + power + " = " + pow(base, power) );
        }

        private BigInteger pow(BigInteger base, BigInteger power) {
            BigInteger result = BigInteger.ONE;

            // 이 구간이 가장 시간이 많이 소모되는 곳. 즉, 연산 작업이 많음
            // 이 for문에 인터럽트가 걸렸는지 체크하도록 하자.
            for(BigInteger i = BigInteger.ZERO; i.compareTo(power) != 0; i = i.add(BigInteger.ONE)) {
                if(Thread.currentThread().isInterrupted()) {
                    System.out.println("인터럽트로 인해 계산 중단.");
                    return BigInteger.ZERO;
                }
                result = result.multiply(base);
            }

            return result;
        }
    }
}

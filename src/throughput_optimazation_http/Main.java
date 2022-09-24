package throughput_optimazation_http;


import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Executable;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 쓰레드 풀링을 사용해서 작업을 처리할 때마다
   작업 처리에 필요한 쓰레드를 생성해야하는 오버헤드를 감수하지않음으로써
   태스크 처리 성능을 향상시키자.

 * Description
 * - HTTP request를 처리하는 HTTP Server 구현
 * - 구현 후, Apache Jmeter로 어플리케이션 성능 측정
 *
 * 1) 클라이언트가 HTTP 요청 URL을 통해 단어를 전송하면
 *    단어 등장 횟수를 센 후, 사용자에게 등장 횟수를 return
 *
 * - 결과적으로, CPU 코어 수와 같게 쓰레드를 생성해서 작업하면 처리 성능이 늘어나는 것을 확인할 수 있음.
 * - 하이퍼쓰레딩을 감안해서 쓰레드를 더 증가시켜도 성능이 향상되지만 비약적이진 않음
 * - 어느순간부터는 처리량이 향상되지 않음.
 */
public class Main {

    private static final String INPUT_FILE = "resources/throughput/war_and_peace.txt";
    private static final int NUMBER_OF_THREADS = 4;

    public static void main(String[] args) throws IOException {
        String text = new String(Files.readAllBytes(Paths.get(INPUT_FILE)));
        startServer(text);
    }

    public static void startServer(String text) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/search", new WordCountHandler(text));
        Executor executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        server.setExecutor(executor);
        server.start();
    }

    private static class WordCountHandler implements HttpHandler {
        private String text;

        public WordCountHandler(String text) {
            this.text = text;
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String query = httpExchange.getRequestURI().getQuery();
            String[] keyValue = query.split("=");
            String action = keyValue[0];
            String word = keyValue[1];

            if(!action.equals("word")) {
                httpExchange.sendResponseHeaders(400, 0);
                return;
            }

            long count = countWord(word);

            byte[] response = Long.toString(count).getBytes();
            httpExchange.sendResponseHeaders(200, response.length);
            OutputStream outputStream = httpExchange.getResponseBody();
            outputStream.write(response);
            outputStream.close();
        }

        private long countWord(String word) {
            long count = 0;
            int index = 0;

            while(index >= 0) {
                index = text.indexOf(word, index);

                if(index >= 0) {
                    count++;
                    index++;
                }
            }
            return count;
        }
    }
}

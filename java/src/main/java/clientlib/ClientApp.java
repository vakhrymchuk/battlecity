package clientlib;

import java.util.List;

public class ClientApp {

    private static final List<String> URLs = List.of(
            // сюда копировать URL из браузера, который открывается при просмотре игры после регистрации
            "http://192.168.8.34/codenjoy-contest/board/player/o3pj6zxnmufzsbtdc5qw?code=7516293637678207099&gameName=battlecity"
    );

    public static void main(String[] args) {
        try {
            Thread[] threads = new Thread[URLs.size()];
            for (int i = 0; i < threads.length; i++) {
                String url = URLs.get(i);
                threads[i] = new Thread(() -> WebSocketRunner.run(url, new SampleSolver()));
                threads[i].start();
            }
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

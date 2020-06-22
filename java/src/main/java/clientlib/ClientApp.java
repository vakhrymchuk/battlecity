package clientlib;

import java.util.List;

public class ClientApp {

    private static final List<String> URLs = List.of(
            // сюда копировать URL из браузера, который открывается при просмотре игры после регистрации
            "http://battlecity.godeltech.com/codenjoy-contest/board/player/wbl3y72q6aurt4ujd783?code=5304663248276302044&gameName=battlecity"
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

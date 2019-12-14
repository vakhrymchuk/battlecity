package clientlib;



public class ClientApp {

    private final static String URL = "http://localhost:8080/codenjoy-contest/board/player/demo15@codenjoy.com?code=3186282887493133449";

    public static void main(String[] args) {
        try {
            WebSocketRunner.run(URL, new SampleSolver());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

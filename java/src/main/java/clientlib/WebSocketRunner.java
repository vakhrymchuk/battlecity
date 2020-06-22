package clientlib;


import clientlib.model.Message;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketClient;
import org.eclipse.jetty.websocket.WebSocketClientFactory;

import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebSocketRunner {
    private static final boolean PRINT_TO_CONSOLE = false;
    private static final Map<String, WebSocketRunner> CLIENTS = new ConcurrentHashMap<>();
    private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
    private final Solver solver;
    private WebSocket.Connection connection;
    private WebSocketClientFactory factory;
    private Runnable onClose;

    private WebSocketRunner(Solver solver) {
        this.solver = solver;
    }

    public static WebSocketRunner run(String url, Solver solver) {
        String serverLocation = url.replace("http", "ws")
                                   .replace("board/player/", "ws?user=")
                                   .replace("?code=", "&code=");

        try {
            if (CLIENTS.containsKey(serverLocation)) {
                return CLIENTS.get(serverLocation);
            }
            final WebSocketRunner client = new WebSocketRunner(solver);
            client.start(serverLocation);
            Runtime.getRuntime().addShutdownHook(new Thread(client::stop));

            CLIENTS.put(serverLocation, client);
            return client;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void print(String message) {
        if (PRINT_TO_CONSOLE) {
            System.out.println(sdf.format(new Date()) + " " + Thread.currentThread().getId() + " " + message);
        }
    }

    private void stop() {
        try {
            connection.close();
            factory.stop();
        } catch (Exception e) {
            print(e);
        }
    }

    private void start(final String server) throws Exception {
        final Pattern urlPattern = Pattern.compile("^board=(.*)$");

        factory = new WebSocketClientFactory();
        factory.start();

        final WebSocketClient client = factory.newWebSocketClient();
        client.setMaxTextMessageSize(100000);
        connectLoop(server, urlPattern, client);
    }

    private void connectLoop(String server, Pattern urlPattern, WebSocketClient client) {
        while (true) {
            try {
                onClose = null;
                tryToConnect(server, urlPattern, client);
                onClose = () -> {
                    printReconnect();
                    connectLoop(server, urlPattern, client);
                };
                break;
            } catch (Exception e) {
                print(e);
                printReconnect();
            }
        }
    }

    private void printReconnect() {
        print("Waiting before reconnect...");
        sleep(5000);
    }

    private void tryToConnect(String server, final Pattern urlPattern, WebSocketClient client) throws Exception {
        URI uri = new URI(server);
        print(String.format("Connecting '%s'...", uri));

        if (connection != null) {
            connection.close();
        }

        connection = client.open(uri, new WebSocket.OnTextMessage() {
            public void onOpen(Connection connection) {
                print("Opened connection " + connection.toString());
            }

            public void onClose(int closeCode, String message) {
                if (onClose != null) {
                    new Thread(onClose).start();
                }
                print("Closed with message: '" + message + "' and code: " + closeCode);
            }

            public void onMessage(String data) {
                String answer = "";
                try {
                    print("========================= Received data for step");
                    Matcher matcher = urlPattern.matcher(data);
                    if (!matcher.matches()) {
                        throw new RuntimeException("Error parsing data: " + data);
                    }
                    Message message = new Gson().fromJson(matcher.group(1), Message.class);
                    print(message.getLayers().get(0));
                    solver.applyMessage(message);
                    answer = solver.move();
                } catch (Exception e) {
                    print(e);
                }

                try {
                    print("Sending step: " + answer);
                    connection.sendMessage(answer);
                } catch (IOException e) {
                    print(e);
                }
            }
        }).get(5000, TimeUnit.MILLISECONDS);
    }

    private void sleep(int mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {
            print(e);
            Thread.currentThread().interrupt();
        }
    }

    private void print(Exception e) {
        if (PRINT_TO_CONSOLE) {
            System.out.println(sdf.format(new Date()) + " " + e.getMessage());
            e.printStackTrace(System.out);
        }
    }
}

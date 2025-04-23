import com.google.gson.Gson;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadedJettyServer {
    private static final int CORE_POOL_SIZE = 5;
    private static final int MAX_POOL_SIZE = 10;
    private static final int QUEUE_CAPACITY = 100;
    private static final long KEEP_ALIVE_TIME = 60L;

    private static final ThreadPoolExecutor executorService = new ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAX_POOL_SIZE,
            KEEP_ALIVE_TIME,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(QUEUE_CAPACITY),
            new CustomThreadFactory("ServerWorker"),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    private static final Gson gson = new Gson();
    private static final ConcurrentHashMap<Integer, UserData> userDataMap = new ConcurrentHashMap<>();
    private static final AtomicInteger userIdCounter = new AtomicInteger(0);

    static class CustomThreadFactory implements ThreadFactory {
        private final String namePrefix;
        private final AtomicInteger threadNumber = new AtomicInteger(1);

        CustomThreadFactory(String namePrefix) {
            this.namePrefix = namePrefix;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, namePrefix + "-" + threadNumber.getAndIncrement());
            thread.setDaemon(false);
            return thread;
        }
    }

    public static void main(String[] args) throws Exception {
        // 샘플 데이터 추가
        addSampleData();

        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8080);
        server.addConnector(connector);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        // API 엔드포인트 등록
        context.addServlet(new ServletHolder(new GetUsersServlet()), "/api/users");
        context.addServlet(new ServletHolder(new AddUserServlet()), "/api/users/add");
        context.addServlet(new ServletHolder(new GetUserByIdServlet()), "/api/users/*");

        server.setHandler(context);
        server.start();
        System.out.println("서버가 시작되었습니다. http://localhost:8080/");
        
        // 쓰레드 모니터링 시작
        startThreadMonitoring();
        
        server.join();
    }

    private static void startThreadMonitoring() {
        new Thread(() -> {
            while (true) {
                System.out.println("\n=== 쓰레드 풀 상태 ===");
                System.out.println("활성 쓰레드 수: " + executorService.getActiveCount());
                System.out.println("현재 풀 크기: " + executorService.getPoolSize());
                System.out.println("대기 작업 수: " + executorService.getQueue().size());
                System.out.println("==================\n");

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }, "MonitorThread").start();
    }

    private static void addSampleData() {
        userDataMap.put(1, new UserData(1, "김철수", "kim@example.com", "안녕하세요"));
        userDataMap.put(2, new UserData(2, "이영희", "lee@example.com", "반갑습니다"));
        userDataMap.put(3, new UserData(3, "박지민", "park@example.com", "좋은 하루되세요"));
        userIdCounter.set(3);
    }

    static class GetUsersServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) {
            CompletableFuture.runAsync(() -> {
                try {
                    response.setContentType("application/json;charset=utf-8");
                    response.setStatus(HttpServletResponse.SC_OK);
                    String jsonResponse = gson.toJson(new ArrayList<>(userDataMap.values()));
                    response.getWriter().println(jsonResponse);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }, executorService);
        }
    }

    static class GetUserByIdServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) {
            CompletableFuture.runAsync(() -> {
                try {
                    String pathInfo = request.getPathInfo();
                    int userId = Integer.parseInt(pathInfo.substring(1));
                    
                    response.setContentType("application/json;charset=utf-8");
                    UserData userData = userDataMap.get(userId);
                    
                    if (userData != null) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        response.getWriter().println(gson.toJson(userData));
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        response.getWriter().println("{\"error\": \"사용자를 찾을 수 없습니다.\"}");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, executorService);
        }
    }

    static class AddUserServlet extends HttpServlet {
        @Override
        protected void doPost(HttpServletRequest request, HttpServletResponse response) {
            CompletableFuture.runAsync(() -> {
                try {
                    StringBuilder buffer = new StringBuilder();
                    String line;
                    while ((line = request.getReader().readLine()) != null) {
                        buffer.append(line);
                    }

                    UserData newUser = gson.fromJson(buffer.toString(), UserData.class);
                    newUser.setId(userIdCounter.incrementAndGet());
                    userDataMap.put(newUser.getId(), newUser);

                    response.setContentType("application/json;charset=utf-8");
                    response.setStatus(HttpServletResponse.SC_CREATED);
                    response.getWriter().println(gson.toJson(newUser));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, executorService);
        }
    }
} 
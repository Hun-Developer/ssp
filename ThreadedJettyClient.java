import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpHeader;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadedJettyClient {
    private static final Gson gson = new Gson();
    private static final ExecutorService executorService = Executors.newFixedThreadPool(5);
    private static final String BASE_URL = "http://localhost:8080";

    public static void main(String[] args) throws Exception {
        HttpClient httpClient = new HttpClient();
        httpClient.start();

        try {
            // 여러 요청을 비동기적으로 실행
            executorService.submit(() -> getAllUsers(httpClient));
            executorService.submit(() -> getUserById(httpClient, 1));
            executorService.submit(() -> addNewUser(httpClient));
            
            // 모든 요청이 완료될 때까지 대기
            executorService.shutdown();
            executorService.awaitTermination(10, TimeUnit.SECONDS);
            
        } finally {
            httpClient.stop();
        }
    }

    private static void getAllUsers(HttpClient client) {
        try {
            System.out.println("모든 사용자 조회 중...");
            ContentResponse response = client.GET(BASE_URL + "/api/users");
            
            Type userListType = new TypeToken<List<UserData>>(){}.getType();
            List<UserData> users = gson.fromJson(response.getContentAsString(), userListType);
            
            System.out.println("=== 전체 사용자 목록 ===");
            users.forEach(System.out::println);
            System.out.println("=====================");
        } catch (Exception e) {
            System.err.println("사용자 목록 조회 중 오류 발생: " + e.getMessage());
        }
    }

    private static void getUserById(HttpClient client, int userId) {
        try {
            System.out.println(userId + "번 사용자 조회 중...");
            ContentResponse response = client.GET(BASE_URL + "/api/users/" + userId);
            
            if (response.getStatus() == 200) {
                UserData user = gson.fromJson(response.getContentAsString(), UserData.class);
                System.out.println("=== 조회된 사용자 정보 ===");
                System.out.println(user);
                System.out.println("=====================");
            } else {
                System.out.println("사용자를 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            System.err.println("사용자 조회 중 오류 발생: " + e.getMessage());
        }
    }

    private static void addNewUser(HttpClient client) {
        try {
            System.out.println("새 사용자 추가 중...");
            UserData newUser = new UserData(0, "홍길동", "hong@example.com", "새로운 사용자입니다");
            String jsonUser = gson.toJson(newUser);

            ContentResponse response = client.POST(BASE_URL + "/api/users/add")
                    .header(HttpHeader.CONTENT_TYPE, "application/json")
                    .content(new StringContentProvider(jsonUser))
                    .send();

            if (response.getStatus() == 201) {
                UserData createdUser = gson.fromJson(response.getContentAsString(), UserData.class);
                System.out.println("=== 추가된 사용자 정보 ===");
                System.out.println(createdUser);
                System.out.println("=====================");
            } else {
                System.out.println("사용자 추가 실패");
            }
        } catch (Exception e) {
            System.err.println("사용자 추가 중 오류 발생: " + e.getMessage());
        }
    }
} 
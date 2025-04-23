using System;
using System.Collections.Generic;
using System.Net.Http;
using System.Text;
using System.Text.Json;
using System.Threading.Tasks;

public class ThreadedClient
{
    private static readonly HttpClient httpClient = new HttpClient();
    private static readonly string BASE_URL = "http://localhost:8080";

    public static async Task Main(string[] args)
    {
        try
        {
            // 여러 요청을 비동기적으로 실행
            var tasks = new List<Task>
            {
                GetAllUsersAsync(),
                GetUserByIdAsync(1),
                AddNewUserAsync()
            };

            await Task.WhenAll(tasks);
        }
        catch (Exception ex)
        {
            Console.WriteLine($"오류 발생: {ex.Message}");
        }
    }

    private static async Task GetAllUsersAsync()
    {
        try
        {
            Console.WriteLine("모든 사용자 조회 중...");
            var response = await httpClient.GetAsync($"{BASE_URL}/api/users");
            response.EnsureSuccessStatusCode();

            var jsonResponse = await response.Content.ReadAsStringAsync();
            var users = JsonSerializer.Deserialize<List<UserData>>(jsonResponse);

            Console.WriteLine("=== 전체 사용자 목록 ===");
            foreach (var user in users)
            {
                Console.WriteLine(user);
            }
            Console.WriteLine("=====================");
        }
        catch (Exception ex)
        {
            Console.WriteLine($"사용자 목록 조회 중 오류 발생: {ex.Message}");
        }
    }

    private static async Task GetUserByIdAsync(int userId)
    {
        try
        {
            Console.WriteLine($"{userId}번 사용자 조회 중...");
            var response = await httpClient.GetAsync($"{BASE_URL}/api/users/{userId}");

            if (response.IsSuccessStatusCode)
            {
                var jsonResponse = await response.Content.ReadAsStringAsync();
                var user = JsonSerializer.Deserialize<UserData>(jsonResponse);

                Console.WriteLine("=== 조회된 사용자 정보 ===");
                Console.WriteLine(user);
                Console.WriteLine("=====================");
            }
            else
            {
                Console.WriteLine("사용자를 찾을 수 없습니다.");
            }
        }
        catch (Exception ex)
        {
            Console.WriteLine($"사용자 조회 중 오류 발생: {ex.Message}");
        }
    }

    private static async Task AddNewUserAsync()
    {
        try
        {
            Console.WriteLine("새 사용자 추가 중...");
            var newUser = new UserData(0, "홍길동", "hong@example.com", "새로운 사용자입니다");
            var jsonUser = JsonSerializer.Serialize(newUser);
            var content = new StringContent(jsonUser, Encoding.UTF8, "application/json");

            var response = await httpClient.PostAsync($"{BASE_URL}/api/users/add", content);

            if (response.IsSuccessStatusCode)
            {
                var jsonResponse = await response.Content.ReadAsStringAsync();
                var createdUser = JsonSerializer.Deserialize<UserData>(jsonResponse);

                Console.WriteLine("=== 추가된 사용자 정보 ===");
                Console.WriteLine(createdUser);
                Console.WriteLine("=====================");
            }
            else
            {
                Console.WriteLine("사용자 추가 실패");
            }
        }
        catch (Exception ex)
        {
            Console.WriteLine($"사용자 추가 중 오류 발생: {ex.Message}");
        }
    }
} 
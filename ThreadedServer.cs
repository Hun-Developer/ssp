using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.IO;
using System.Net;
using System.Text;
using System.Text.Json;
using System.Threading;
using System.Threading.Tasks;

public class ThreadedServer
{
    private static readonly int CORE_POOL_SIZE = 5;
    private static readonly int MAX_POOL_SIZE = 10;
    private static readonly int QUEUE_CAPACITY = 100;
    private static readonly TimeSpan KEEP_ALIVE_TIME = TimeSpan.FromSeconds(60);

    private static readonly ConcurrentDictionary<int, UserData> userDataMap = new ConcurrentDictionary<int, UserData>();
    private static int userIdCounter = 0;
    private static readonly object counterLock = new object();

    public static async Task Main(string[] args)
    {
        // 샘플 데이터 추가
        AddSampleData();

        // HTTP 서버 시작
        var listener = new HttpListener();
        listener.Prefixes.Add("http://localhost:8080/");
        listener.Start();
        Console.WriteLine("서버가 시작되었습니다. http://localhost:8080/");

        // 쓰레드 모니터링 시작
        StartThreadMonitoring();

        // 요청 처리
        while (true)
        {
            var context = await listener.GetContextAsync();
            _ = ProcessRequestAsync(context);
        }
    }

    private static async Task ProcessRequestAsync(HttpListenerContext context)
    {
        try
        {
            var request = context.Request;
            var response = context.Response;

            if (request.HttpMethod == "GET")
            {
                if (request.Url.AbsolutePath == "/api/users")
                {
                    await GetAllUsersAsync(response);
                }
                else if (request.Url.AbsolutePath.StartsWith("/api/users/"))
                {
                    var userId = int.Parse(request.Url.AbsolutePath.Split('/')[3]);
                    await GetUserByIdAsync(response, userId);
                }
            }
            else if (request.HttpMethod == "POST" && request.Url.AbsolutePath == "/api/users/add")
            {
                await AddUserAsync(request, response);
            }
            else
            {
                response.StatusCode = (int)HttpStatusCode.NotFound;
            }
        }
        catch (Exception ex)
        {
            Console.WriteLine($"요청 처리 중 오류 발생: {ex.Message}");
            context.Response.StatusCode = (int)HttpStatusCode.InternalServerError;
        }
        finally
        {
            context.Response.Close();
        }
    }

    private static async Task GetAllUsersAsync(HttpListenerResponse response)
    {
        response.ContentType = "application/json;charset=utf-8";
        response.StatusCode = (int)HttpStatusCode.OK;
        
        var users = new List<UserData>(userDataMap.Values);
        var jsonResponse = JsonSerializer.Serialize(users);
        
        var buffer = Encoding.UTF8.GetBytes(jsonResponse);
        await response.OutputStream.WriteAsync(buffer, 0, buffer.Length);
    }

    private static async Task GetUserByIdAsync(HttpListenerResponse response, int userId)
    {
        response.ContentType = "application/json;charset=utf-8";
        
        if (userDataMap.TryGetValue(userId, out var userData))
        {
            response.StatusCode = (int)HttpStatusCode.OK;
            var jsonResponse = JsonSerializer.Serialize(userData);
            var buffer = Encoding.UTF8.GetBytes(jsonResponse);
            await response.OutputStream.WriteAsync(buffer, 0, buffer.Length);
        }
        else
        {
            response.StatusCode = (int)HttpStatusCode.NotFound;
            var errorResponse = JsonSerializer.Serialize(new { error = "사용자를 찾을 수 없습니다." });
            var buffer = Encoding.UTF8.GetBytes(errorResponse);
            await response.OutputStream.WriteAsync(buffer, 0, buffer.Length);
        }
    }

    private static async Task AddUserAsync(HttpListenerRequest request, HttpListenerResponse response)
    {
        using var reader = new StreamReader(request.InputStream, request.ContentEncoding);
        var jsonBody = await reader.ReadToEndAsync();
        var newUser = JsonSerializer.Deserialize<UserData>(jsonBody);

        lock (counterLock)
        {
            newUser.Id = ++userIdCounter;
        }

        userDataMap.TryAdd(newUser.Id, newUser);

        response.ContentType = "application/json;charset=utf-8";
        response.StatusCode = (int)HttpStatusCode.Created;
        
        var jsonResponse = JsonSerializer.Serialize(newUser);
        var buffer = Encoding.UTF8.GetBytes(jsonResponse);
        await response.OutputStream.WriteAsync(buffer, 0, buffer.Length);
    }

    private static void AddSampleData()
    {
        userDataMap.TryAdd(1, new UserData(1, "김철수", "kim@example.com", "안녕하세요"));
        userDataMap.TryAdd(2, new UserData(2, "이영희", "lee@example.com", "반갑습니다"));
        userDataMap.TryAdd(3, new UserData(3, "박지민", "park@example.com", "좋은 하루되세요"));
        userIdCounter = 3;
    }

    private static void StartThreadMonitoring()
    {
        new Thread(() =>
        {
            while (true)
            {
                Console.WriteLine("\n=== 쓰레드 풀 상태 ===");
                Console.WriteLine($"활성 쓰레드 수: {ThreadPool.ThreadCount}");
                Console.WriteLine($"사용 가능한 쓰레드 수: {ThreadPool.GetAvailableThreads(out int workerThreads, out int completionPortThreads)}");
                Console.WriteLine("==================\n");

                Thread.Sleep(5000);
            }
        })
        { IsBackground = true }.Start();
    }
} 
using System;
using System.Threading.Tasks;

class Program
{
    static async Task Main(string[] args)
    {
        Console.WriteLine("=== HTTP 서버/클라이언트 테스트 프로그램 ===");
        Console.WriteLine("1. 서버 시작");
        Console.WriteLine("2. 클라이언트 테스트");
        Console.WriteLine("3. 종료");
        Console.Write("선택하세요 (1-3): ");

        var choice = Console.ReadLine();

        switch (choice)
        {
            case "1":
                await StartServer();
                break;
            case "2":
                await RunClientTests();
                break;
            case "3":
                Console.WriteLine("프로그램을 종료합니다.");
                return;
            default:
                Console.WriteLine("잘못된 선택입니다.");
                break;
        }
    }

    static async Task StartServer()
    {
        Console.WriteLine("\n=== 서버 시작 ===");
        Console.WriteLine("서버를 시작하려면 'start'를 입력하세요.");
        Console.WriteLine("서버를 종료하려면 'stop'을 입력하세요.");

        var serverTask = Task.Run(() => ThreadedServer.Main(new string[0]));

        while (true)
        {
            var command = Console.ReadLine();
            if (command?.ToLower() == "stop")
            {
                // 서버 종료 로직 추가 (필요한 경우)
                Console.WriteLine("서버를 종료합니다...");
                break;
            }
            else if (command?.ToLower() == "start")
            {
                Console.WriteLine("서버가 이미 실행 중입니다.");
            }
        }
    }

    static async Task RunClientTests()
    {
        Console.WriteLine("\n=== 클라이언트 테스트 시작 ===");
        Console.WriteLine("다음 테스트를 실행합니다:");
        Console.WriteLine("1. 모든 사용자 조회");
        Console.WriteLine("2. 특정 사용자 조회");
        Console.WriteLine("3. 새 사용자 추가");
        Console.WriteLine("4. 모든 테스트 실행");

        Console.Write("테스트 번호를 선택하세요 (1-4): ");
        var testChoice = Console.ReadLine();

        try
        {
            switch (testChoice)
            {
                case "1":
                    await ThreadedClient.GetAllUsersAsync();
                    break;
                case "2":
                    Console.Write("조회할 사용자 ID를 입력하세요: ");
                    if (int.TryParse(Console.ReadLine(), out int userId))
                    {
                        await ThreadedClient.GetUserByIdAsync(userId);
                    }
                    else
                    {
                        Console.WriteLine("잘못된 ID입니다.");
                    }
                    break;
                case "3":
                    await ThreadedClient.AddNewUserAsync();
                    break;
                case "4":
                    Console.WriteLine("\n=== 모든 테스트 실행 ===");
                    await RunAllTests();
                    break;
                default:
                    Console.WriteLine("잘못된 선택입니다.");
                    break;
            }
        }
        catch (Exception ex)
        {
            Console.WriteLine($"테스트 실행 중 오류 발생: {ex.Message}");
        }
    }

    static async Task RunAllTests()
    {
        // 모든 사용자 조회
        Console.WriteLine("\n[테스트 1] 모든 사용자 조회");
        await ThreadedClient.GetAllUsersAsync();

        // 특정 사용자 조회
        Console.WriteLine("\n[테스트 2] 특정 사용자 조회");
        await ThreadedClient.GetUserByIdAsync(1);

        // 새 사용자 추가
        Console.WriteLine("\n[테스트 3] 새 사용자 추가");
        await ThreadedClient.AddNewUserAsync();

        // 추가된 사용자 확인
        Console.WriteLine("\n[테스트 4] 추가된 사용자 확인");
        await ThreadedClient.GetAllUsersAsync();
    }
} 
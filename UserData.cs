using System;

public class UserData
{
    public int Id { get; set; }
    public string Name { get; set; }
    public string Email { get; set; }
    public string Message { get; set; }

    public UserData()
    {
    }

    public UserData(int id, string name, string email, string message)
    {
        Id = id;
        Name = name;
        Email = email;
        Message = message;
    }

    public override string ToString()
    {
        return $"UserData{{Id={Id}, Name='{Name}', Email='{Email}', Message='{Message}'}}";
    }
} 
using System;
using System.Threading.Tasks;
using CodeBattleNetLibrary;
using CodeBattleNetLibrary.Models;

namespace CodeBattleNet
{
    internal static class Program
    {
        private static void Main(string[] args)
        {
            // сюда копировать URL из браузера, который открывается при просмотре игры после регистрации
            var url = "http://192.168.8.34/codenjoy-contest/board/player/21b4yiz462hf1lpmewkb?code=1640566413676185557";
            if (args.Length > 0)
            {
                url = args[0];
            }
            IBattlecityRobot robot = new SampleRobot();
            
            new GameClientBattlecity(url, robot);
            Console.Read();
        }
    }
}

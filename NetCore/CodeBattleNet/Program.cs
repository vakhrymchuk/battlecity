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
            var url = "http://192.168.8.34/codenjoy-contest/board/player/21b4yiz462hf1lpmewkb?code=1640566413676185557";
            if (args.Length > 0)
            {
                url = args[0];
            }     
            new GameClientBattlecity(url, createAction);
            Console.Read();
        }

        private static async Task<StepCommands> createAction(StepData stepData)
        {
            //stepData contains info about board configuration at current step
            //stepCommands this is your response. What you robot should do.
            StepCommands stepCommands = new StepCommands();
            stepCommands.CommandTwo = Commands.GO_TOP;
            stepCommands.CommandOne = Commands.FIRE;
            return stepCommands;
        }
    }
}

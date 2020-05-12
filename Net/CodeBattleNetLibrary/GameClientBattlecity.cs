using System;
using System.Collections.Generic;
using System.Threading;
using CodeBattleNetLibrary.GameModels;
using CodeBattleNetLibrary.Models;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using WebSocket4Net;

namespace CodeBattleNetLibrary
{
    public class GameClientBattlecity
    {
        private WebSocket _socket;
        private readonly Func<StepData, StepCommands> _userActionHandler;
        public GameClientBattlecity(string url, Func<StepData, StepCommands> action)
        {
            configureSocket(url);
            _userActionHandler = action;
        }

        private void OnMessageReceivedHandler(string message)
        {
            var stepData = ParseField(message);
            //Console.WriteLine("Received field");
            //Console.Write(stepData.RawLayers);
            
            var userBotResponse = _userActionHandler(stepData);
            var actions = PrintActions(userBotResponse);
            Console.WriteLine("Send actions");
            Console.WriteLine(actions);
            SendActions(actions);
        }

        private string PrintActions(StepCommands stepCommands)
        {
            string response = string.Empty;
            
            switch (stepCommands.CommandOne)
            {
                case Commands.GO_TOP:
                    response += "UP";
                    break;
                case Commands.GO_DOWN:
                    response += "DOWN";
                    break;
                case Commands.GO_LEFT:
                    response += "LEFT";
                    break;
                case Commands.GO_RIGHT:
                    response += "RIGHT";
                    break;
                case Commands.FIRE:
                    response += "ACT";
                    break;  
            }

            if (stepCommands.CommandOne != 0 && stepCommands.CommandTwo != 0)
            {
                response += ",";
            }
            
            switch (stepCommands.CommandTwo)
            {
                case Commands.GO_TOP:
                    response += "UP";
                    break;
                case Commands.GO_DOWN:
                    response += "DOWN";
                    break;
                case Commands.GO_LEFT:
                    response += "LEFT";
                    break;
                case Commands.GO_RIGHT:
                    response += "RIGHT";
                    break;
                case Commands.FIRE:
                    response += "ACT";
                    break;  
            }
            
            return response;
        }
        
        private StepData ParseField(string rawField)
        {
            rawField = rawField.Substring(6);
            var jsonField = JObject.Parse(rawField);
            var playerTank = jsonField["playerTank"].ToObject<Tank>();
            var borders = jsonField["borders"].ToObject<List<Border>>();
            var aiTanks = jsonField["aiTanks"].ToObject<List<Tank>>();
            var constructions = jsonField["constructions"].ToObject<List<Construction>>();
            var enemies = jsonField["enemies"].ToObject<List<Tank>>();
            var bullets = jsonField["bullets"].ToObject<List<Bullet>>();
            var rawLayers = jsonField["layers"].ToString();
            var stepData = new StepData(playerTank ,aiTanks,enemies,constructions,borders,bullets,rawLayers);

            return stepData;
        }
        
        private void SendActions(string commands)
        {
            _socket.Send(commands);
        }
        
        private void configureSocket(string url)
        {
            var serverUrl = url.Replace("http", "ws").Replace("board/player/", "ws?user=").Replace("?code=", "&code=");
            _socket = new WebSocket(serverUrl);
            _socket.MessageReceived += (s, e) => { OnMessageReceivedHandler(e.Message); };
            _socket.Open();
        }
    }
}
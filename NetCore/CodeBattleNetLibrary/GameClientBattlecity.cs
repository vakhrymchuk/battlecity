using System;
using System.Collections.Generic;
using System.Net.Sockets;
using System.Threading;
using System.Threading.Tasks;
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
        private string _serverUrl;
        private readonly Func<StepData, Task<StepCommands>> _userActionHandler;

        public GameClientBattlecity(string url, IBattlecityRobot robot)
        {
            _serverUrl = url.Replace("http", "ws").Replace("board/player/", "ws?user=").Replace("?code=", "&code=");
            _userActionHandler = robot.CreateAction;
            ConfigureSocket();
        }

        private async void OnMessageReceivedHandler(string message)
        {
            var stepData = ParseField(message);

            if (stepData == null)
            {
                return;
            }

            PrintLayersToConsole(stepData.Layers[0]);
            var userBotResponse = await _userActionHandler(stepData);
            var actions = PrintActions(userBotResponse);
            Console.WriteLine("Send actions");
            Console.WriteLine(actions);
            SendActions(actions);
        }

        private void PrintLayersToConsole(string rawLayers)
        {
            Console.WriteLine("Received field");
            rawLayers = rawLayers.Substring(6);
            int rowLength = 0;
            foreach (var symbol in rawLayers)
            {
                if (symbol == '\\')
                {
                    break;
                }

                rowLength++;
            }

            rawLayers = rawLayers.Replace("\\n", "");
            int stringPointer = 0;
            while (stringPointer <= rawLayers.Length)
            {
                if ((stringPointer + rowLength) > rawLayers.Length) break;
                Console.WriteLine(rawLayers.Substring(stringPointer, rowLength));
                stringPointer += rowLength;
            }
        }

        private string PrintActions(StepCommands stepCommands)
        {
            var response = string.Empty;

            if (stepCommands.Fire == Fire.FIRE_BEFORE_ACTION)
            {
                response += "ACT";
                if (stepCommands.Command != 0)
                {
                    response += ",";
                    response += ChooseCommand(stepCommands.Command);
                }

                return response;
            }

            if (stepCommands.Fire == Fire.FIRE_AFTER_ACTION)
            {
                response += ChooseCommand(stepCommands.Command);
                response += ",";
                response += "ACT";
                return response;
            }

            response += ChooseCommand(stepCommands.Command);

            return response;
        }

        private string ChooseCommand(Commands command)
        {
            switch (command)
            {
                case Commands.GO_TOP:
                    return "UP";
                case Commands.GO_DOWN:
                    return "DOWN";
                case Commands.GO_LEFT:
                    return "LEFT";
                case Commands.GO_RIGHT:
                    return "RIGHT";
            }

            return string.Empty;
        }

        private StepData ParseField(string rawField)
        {
            rawField = rawField.Substring(6);
            try
            {
                var stepData = JsonConvert.DeserializeObject<StepData>(rawField);
                return stepData;
            }
            catch (Exception e)
            {
                Console.WriteLine("Can't parse server response");
                Console.WriteLine(e.Message);
                return null;
            }
        }

        private void SendActions(string commands)
        {
            _socket.Send(commands);
        }

        private async void ConfigureSocket()
        {
            Console.WriteLine("Connecting to the server.");
            _socket?.Dispose();
            _socket = new WebSocket(_serverUrl);
            _socket.MessageReceived += (s, e) => { OnMessageReceivedHandler(e.Message); };
            _socket.Closed += (s, e) => { SocketNotConnected(); };
            _socket.Open();
            Thread.Sleep(500);
            if (_socket.State != WebSocketState.Open)
            {
                await SocketNotConnected();
            }
            else
            {
                Console.WriteLine("Connection successful");
            }
        }

        private async Task SocketNotConnected()
        {
            Console.WriteLine("Unable to connect or connection was unexpectedly lost");
            Thread.Sleep(3000);
            Console.WriteLine("Try to reconnect...");
            ConfigureSocket();
        }
    }
}
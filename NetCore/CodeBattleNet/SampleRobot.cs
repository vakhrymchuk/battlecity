using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Threading.Tasks;
using CodeBattleNetLibrary;
using CodeBattleNetLibrary.GameModels;
using CodeBattleNetLibrary.Models;

namespace CodeBattleNet
{
    public class SampleRobot : IBattlecityRobot
    {
        private Random _rnd;
        private StepData _stepData;
        public SampleRobot()
        {
            _rnd = new Random();    
        }
        public async Task<StepCommands> CreateAction(StepData stepData)
        {
            _stepData = stepData;
            var allTanks = _stepData.Enemies;
            allTanks.AddRange(_stepData.AiTanks);
            allTanks = allTanks.Where(t => t.Alive).ToList();
            var stepCommands = new StepCommands();
            switch (_rnd.Next(3))
            {
                case 1:
                    stepCommands.Fire = Fire.FIRE_AFTER_ACTION;
                    break;
                case 2:
                    stepCommands.Fire = Fire.FIRE_BEFORE_ACTION;
                    break;             
            }

            var nearestEnemy = GetNearest(allTanks);
            List<Commands> availableCommands = new List<Commands>();
            
            if (!_stepData.PlayerTank.Alive)
            {
                availableCommands.Add(Commands.GO_RIGHT);  
                availableCommands.Add(Commands.GO_LEFT);  
                availableCommands.Add(Commands.GO_TOP);
                availableCommands.Add(Commands.GO_DOWN); 
            }
            
            if (nearestEnemy.X > _stepData.PlayerTank.X)
            {
                availableCommands.Add(Commands.GO_RIGHT);   
            }
            if (nearestEnemy.X < _stepData.PlayerTank.X)
            {
                availableCommands.Add(Commands.GO_LEFT);   
            }
            if (nearestEnemy.Y > _stepData.PlayerTank.Y)
            {
                availableCommands.Add(Commands.GO_TOP);   
            }
            
            if (nearestEnemy.Y < _stepData.PlayerTank.Y)
            {
                availableCommands.Add(Commands.GO_DOWN);   
            }

            stepCommands.Command =
                availableCommands[_rnd.Next(1,availableCommands.Count)-1];

            return stepCommands;
        }

        private Tank GetNearest(List<Tank> allTanks)
        {
            if (allTanks == null || allTanks.Capacity == 0) {
                return null;
            }
            var min = GetManhattanDistance(_stepData.PlayerTank, allTanks[0]);
            var nearestTank = allTanks[0];
            foreach (var tank in allTanks)
            {
                var distance = GetManhattanDistance(_stepData.PlayerTank, tank);
                if (distance < min)
                {
                    min = distance;
                    nearestTank = tank;
                }
            }

            return nearestTank;
        }

        private int GetManhattanDistance(Tank tank1, Tank tank2)
        {
            return Math.Abs(tank1.X - tank2.X) + Math.Abs(tank1.Y - tank2.Y);
        }
    }
}
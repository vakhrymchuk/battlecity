using System.Collections.Generic;
using CodeBattleNetLibrary.GameModels;
using Newtonsoft.Json;

namespace CodeBattleNetLibrary.Models
{
    public class StepData
    {
        public Tank PlayerTank { get; private set; }  
        public List<Tank> AiTanks { get; private set;}      
        public List<Tank> Enemies { get; private set;}       
        public List<Construction> Constructions { get; private set;}     
        public List<Border> Borders { get; private set;}     
        public List<Bullet> Bullets { get; private set;}     
        public List<string> Layers { get; private set;}

        public StepData(Tank playerTank, List<Tank> aiTanks, List<Tank> enemies, List<Construction> constructions, List<Border> borders, List<Bullet> bullets, List<string> layers)
        {
            PlayerTank = playerTank;
            AiTanks = aiTanks;
            Enemies = enemies;
            Constructions = constructions;
            Borders = borders;
            Bullets = bullets;
            Layers = layers;
        }
    }
}
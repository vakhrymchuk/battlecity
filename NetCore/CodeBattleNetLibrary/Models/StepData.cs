using System.Collections.Generic;
using CodeBattleNetLibrary.GameModels;
using Newtonsoft.Json;

namespace CodeBattleNetLibrary.Models
{
    public class StepData
    {
        public Tank PlayerTank { get; set; }  
        public List<Tank> AiTanks { get; set;}      
        public List<Tank> Enemies { get; set;}       
        public List<Construction> Constructions { get; set;}     
        public List<Border> Borders { get; set;}     
        public List<Bullet> Bullets { get; set;}     
        public List<string> Layers { get; set;}

        public StepData(Tank tank, List<Tank> aiTanks, List<Tank> enemies, List<Construction> constructions, List<Border> borders, List<Bullet> bullets, List<string> layers)
        {
            PlayerTank = tank;
            AiTanks = aiTanks;
            Enemies = enemies;
            Constructions = constructions;
            Borders = borders;
            Bullets = bullets;
            Layers = layers;
        }
        public StepData(){}
    }
}
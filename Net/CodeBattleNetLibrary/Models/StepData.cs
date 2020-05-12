using System.Collections.Generic;
using CodeBattleNetLibrary.GameModels;

namespace CodeBattleNetLibrary.Models
{
    public class StepData
    {
        public Tank PlayerTank { get; }
        public List<Tank> AiTanks { get; }
        public List<Tank> EnemyTanks { get; }
        public List<Construction> Constructions { get; }
        public List<Border> Borders { get; }
        public List<Bullet> Bullets { get; }
        public string RawLayers { get; }

        public StepData(Tank tank, List<Tank> aiTanks, List<Tank> enemyTanks, List<Construction> constructions, List<Border> borders, List<Bullet> bullets, string rawLayers)
        {
            PlayerTank = tank;
            AiTanks = aiTanks;
            EnemyTanks = enemyTanks;
            Constructions = constructions;
            Borders = borders;
            Bullets = bullets;
            RawLayers = rawLayers;
        }
        public StepData(){}
    }
}
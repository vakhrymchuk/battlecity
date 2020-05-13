namespace CodeBattleNetLibrary.GameModels
{
    public class Tank : Point
    {
        private string Direction { get; set;}
        private int FireCoolDown { get; set;}
        private int KilledCounter { get; set;}
        private bool Alive { get; set;}
        
        public Tank(int x, int y, string direction, int fireCoolDown, int killedCounter, bool alive) : base(x, y)
        {
            FireCoolDown = fireCoolDown;
            KilledCounter = killedCounter;
            Alive = alive;
            Direction = direction;
        }
    }
}
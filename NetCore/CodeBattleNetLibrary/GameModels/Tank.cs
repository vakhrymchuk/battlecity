namespace CodeBattleNetLibrary.GameModels
{
    public class Tank : Point
    {
        public string Direction { get; set;}
        public int FireCoolDown { get; set;}
        public int KilledCounter { get; set;}
        public bool Alive { get; set;}
        
        public Tank(int x, int y, string direction, int fireCoolDown, int killedCounter, bool alive) : base(x, y)
        {
            FireCoolDown = fireCoolDown;
            KilledCounter = killedCounter;
            Alive = alive;
            Direction = direction;
        }
    }
}
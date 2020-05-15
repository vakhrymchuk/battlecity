namespace CodeBattleNetLibrary.GameModels
{
    public class Tank : Point
    {
        public string Direction { get; private set;}
        public int FireCoolDown { get; private set;}
        public int KilledCounter { get; private set;}
        public bool Alive { get; private set;}
        
        public Tank(int x, int y, string direction, int fireCoolDown, int killedCounter, bool alive) : base(x, y)
        {
            FireCoolDown = fireCoolDown;
            KilledCounter = killedCounter;
            Alive = alive;
            Direction = direction;
        }
    }
}
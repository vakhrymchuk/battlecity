namespace CodeBattleNetLibrary.GameModels
{
    public class Tank : Point
    {
        private string Direction { get; }
        private int FireCoolDown { get; }
        private int KilledCounter { get; }
        private bool Alive { get; }
        
        public Tank(int x, int y, string direction, int fireCoolDown, int killedCounter, bool alive) : base(x, y)
        {
            FireCoolDown = fireCoolDown;
            KilledCounter = killedCounter;
            Alive = alive;
            Direction = direction;
        }
    }
}
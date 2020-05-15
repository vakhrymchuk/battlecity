namespace CodeBattleNetLibrary.GameModels
{
    public class Bullet : Point
    {
        public string Direction { get; private set;}

        public Bullet(int x, int y, string direction) : base(x, y)
        {
            Direction = direction;
        }
    }
}
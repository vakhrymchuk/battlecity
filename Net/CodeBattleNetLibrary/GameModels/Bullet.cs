namespace CodeBattleNetLibrary.GameModels
{
    public class Bullet : Point
    {
        private string Direction { get; }

        public Bullet(int x, int y, string direction) : base(x, y)
        {
            Direction = direction;
        }
    }
}
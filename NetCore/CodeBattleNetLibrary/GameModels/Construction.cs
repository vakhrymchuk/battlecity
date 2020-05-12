namespace CodeBattleNetLibrary.GameModels
{
    public class Construction : Point
    {
        private int Timer { get; }
        private int Power { get; }
        public Construction(int x, int y, int timer, int power) : base(x, y)
        {
            Timer = timer;
            Power = power;
        }
    }
}
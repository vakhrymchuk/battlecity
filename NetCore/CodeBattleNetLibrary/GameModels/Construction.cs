namespace CodeBattleNetLibrary.GameModels
{
    public class Construction : Point
    {
        public int Timer { get; private set;}
        public int Power { get; private set;}
        public Construction(int x, int y, int timer, int power) : base(x, y)
        {
            Timer = timer;
            Power = power;
        }
    }
}
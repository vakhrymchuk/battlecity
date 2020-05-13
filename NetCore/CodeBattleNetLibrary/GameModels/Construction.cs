namespace CodeBattleNetLibrary.GameModels
{
    public class Construction : Point
    {
        public int Timer { get; set;}
        public int Power { get; set;}
        public Construction(int x, int y, int timer, int power) : base(x, y)
        {
            Timer = timer;
            Power = power;
        }
    }
}
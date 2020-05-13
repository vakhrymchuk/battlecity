namespace CodeBattleNetLibrary.GameModels
{
    public class Construction : Point
    {
        private int Timer { get; set;}
        private int Power { get; set;}
        public Construction(int x, int y, int timer, int power) : base(x, y)
        {
            Timer = timer;
            Power = power;
        }
    }
}
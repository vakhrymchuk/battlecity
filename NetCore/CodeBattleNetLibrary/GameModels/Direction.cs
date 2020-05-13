namespace CodeBattleNetLibrary.GameModels
{
   public struct Direction
   {
      public sbyte dX { get; private set;}
      public sbyte dY { get; private set;}

      public Direction(sbyte dx, sbyte dy)
      {
         dX = dx;
         dY = dy;
      }
   }
}
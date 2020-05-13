namespace CodeBattleNetLibrary.GameModels
{
   public struct Direction
   {
      public sbyte dX { get; set;}
      public sbyte dY { get; set;}

      public Direction(sbyte dx, sbyte dy)
      {
         dX = dx;
         dY = dy;
      }
   }
}
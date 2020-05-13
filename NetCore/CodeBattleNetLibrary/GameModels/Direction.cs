namespace CodeBattleNetLibrary.GameModels
{
   public struct Direction
   {
      private sbyte dX { get; set;}
      private sbyte dY { get; set;}

      public Direction(sbyte dx, sbyte dy)
      {
         dX = dx;
         dY = dy;
      }
   }
}
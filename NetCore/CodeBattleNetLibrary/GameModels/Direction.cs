namespace CodeBattleNetLibrary.GameModels
{
   public struct Direction
   {
      private sbyte dX { get; }
      private sbyte dY { get; }

      public Direction(sbyte dx, sbyte dy)
      {
         dX = dx;
         dY = dy;
      }
   }
}
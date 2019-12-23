using System;
using CodeBattleNetLibrary;

namespace CodeBattleNet
{
    internal static class Program
    {
        private static void Main()
        {
            new GameClientBattlecity("http://localhost:8080/codenjoy-contest/board/player/demo15@codenjoy.com?code=3186282887493133449", createAction());

            Console.Read();
        }

        private static Func<GameClientBattlecity, String> createAction()
        {
            return new Func<GameClientBattlecity, String>((gcb) =>
            {
                var r = new Random();
                FireOrder? fireOrder = null;

                switch (r.Next(3))
                {
                    case 0:
                        fireOrder = FireOrder.FIRE_BEFORE_TURN;
                        break;
                    case 1:
                        fireOrder = FireOrder.FIRE_AFTER_TURN;
                        break;
                }
                switch (r.Next(5))
                {
                    case 0:
                        return ActionPrinter.printAction(fireOrder, Direction.UP);
                    case 1:
                        return ActionPrinter.printAction(fireOrder, Direction.RIGHT);
                    case 2:
                        return ActionPrinter.printAction(fireOrder, Direction.LEFT);
                    case 3:
                        return ActionPrinter.printAction(fireOrder, Direction.DOWN);
                    case 4:
                        return ActionPrinter.printAction(fireOrder, null);
                }
                return ActionPrinter.printAction(null, null);
            });
            
        }
    }
}

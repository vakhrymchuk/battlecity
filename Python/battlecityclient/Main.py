from battlecityclient.BattleCityClient import GameClient
import random
import logging

from battlecityclient.internals.actions import BattlecityAction
from battlecityclient.internals.board import Board

logging.basicConfig(format='%(asctime)s %(levelname)s:%(message)s',
                    level=logging.INFO)

def turn(gcb: Board):
    action_id = random.randint(0, len(BattlecityAction)-1)
    return list(BattlecityAction)[action_id]

def main():
    gcb = GameClient("http://localhost:8080/codenjoy-contest/board/player/demo15@codenjoy.com?code=3186282887493133449")
    gcb.run(turn)

if __name__ == '__main__':
    main()
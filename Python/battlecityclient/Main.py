from battlecityclient.BattleCityClient import GameClient
from battlecityclient.internals.message import *
from battlecityclient.internals.actions import *
import random
import logging


logging.basicConfig(format='%(asctime)s %(levelname)s:%(message)s',
                    level=logging.INFO)


def print_action(fire_order: FireOrder = None, direction: Direction = None):
    if direction is None:
        return {FireOrder.FIRE_AFTER_TURN: 'ACT(1)',
                FireOrder.FIRE_BEFORE_TURN: 'ACT',
                None: 'STOP'}[fire_order]
    if fire_order is None:
        return direction.name

    return fire_order.value.format(direction.name)


def get_nearest(tanks, current_tank):
    if (tanks is None) or (len(tanks) == 0):
        return None
    return min(map(lambda tank: (tank.manhattan_distance(current_tank), tank),
                   tanks),
               key=lambda x: x[0])[1]


def turn(gcb: Message):
    fire_order = random.choice([None, FireOrder.FIRE_AFTER_TURN, FireOrder.FIRE_BEFORE_TURN])
    closest_enemy = get_nearest(list(filter(lambda tank: tank.is_alive(), gcb.ai_tanks + gcb.enemies)), gcb.player_tank)
    diff = gcb.player_tank.diff(closest_enemy)

    available_directions = []
    if diff.get_x() * Direction.LEFT.dx > 0:
        available_directions += [Direction.LEFT]
    elif diff.get_x() * Direction.RIGHT.dx > 0:
        available_directions += [Direction.RIGHT]
    if diff.get_y() * Direction.UP.dy > 0:
        available_directions += [Direction.UP]
    elif diff.get_y() * Direction.DOWN.dy > 0:
        available_directions += [Direction.DOWN]
    return print_action(fire_order, random.choice(available_directions))


def main():
    gcb = GameClient(
        "http://192.168.8.34/codenjoy-contest/board/player/o3pj6zxnmufzsbtdc5qw?code=7516293637678207099&gameName=battlecity")
    gcb.run(turn)


if __name__ == '__main__':
    main()

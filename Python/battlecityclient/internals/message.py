import enum

from .point import Point


class Direction(enum.Enum):
    def __new__(cls, *args, **kwds):
        value = len(cls.__members__) + 1
        obj = object.__new__(cls)
        obj._value_ = value
        return obj

    def __init__(self, dx, dy):
        self.dx = dx
        self.dy = dy

    LEFT = -1, 0
    RIGHT = 1, 0
    UP = 0, 1
    DOWN = 0, -1


class Tank(Point):
    _fire_cool_down: int
    _killed_counter: int
    _alive: bool
    _direction: Direction

    def __init__(self, args):
        super(Tank, self).__init__(args['x'], args['y'])
        self._fire_cool_down = args['fireCoolDown']
        self._killed_counter = args['killedCounter']
        self._alive = args['alive']
        self._direction = Direction[args['direction']]

    def is_alive(self):
        return self._alive

    def get_killed_counter(self):
        return self._killed_counter

    def get_fire_cool_down(self):
        return self._fire_cool_down

    def get_direction(self):
        return self._direction

    def to_string(self):
        return "[{},{},{},{},{},{}]".format(self._x, self._y, self._fire_cool_down, self._killed_counter,
                                            'Alive' if self._alive else 'Dead',
                                            self._direction)


class Bullet(Point):
    def __init__(self, args):
        super(Bullet, self).__init__(args['x'], args['y'])
        self._direction = Direction[args['direction']]

    def get_direction(self):
        return self._direction

    def to_string(self):
        return "[{},{},{}]".format(self._x, self._y, self._direction)


class Construction(Point):
    def __init__(self, args):
        super(Construction, self).__init__(args['x'], args['y'])
        self._timer = args['timer']
        self._power = args['power']

    def get_timer(self):
        return self._timer

    def get_power(self):
        return self._power

    def to_string(self):
        return "[{},{},{},{}]".format(self._x, self._y, self._timer, self._power)


class Message(object):
    def __init__(self, args):
        self.player_tank = Tank(args['playerTank'])
        self.enemies = list(map(Tank, args['enemies']))
        self.ai_tanks = list(map(Tank, args['aiTanks']))
        self.bullets = list(map(Bullet, args['bullets']))
        self.borders = list(map(lambda val: Point(val['x'], val['y']), args['borders']))
        self.constructions = list(map(Construction, args['constructions']))
        self.layers = args['layers']

    def print_board(self):
        print(self.layers[0])

class Point(object):
    """ Describes a point on board."""
    def __init__(self, x=0, y=0):
        self._x = int(x)
        self._y = int(y)

    def __key(self):
        return self._x, self._y

    def __str__(self):
        return self.to_string()

    def __repr__(self):
        return self.to_string()

    def __eq__(self, other_point):
        return self.__key() == other_point.__key()

    def __hash__(self):
        return hash(self.__key())

    def get_x(self):
        return self._x

    def get_y(self):
        return self._y

    def abs_sum(self):
        return abs(self._x) + abs(self._y)

    def diff(self, o):
        return Point(x=self._x - o._x, y=self._y - o._y)

    def manhattan_distance(self, o):
        return self.diff(o).abs_sum()

    def to_string(self):
        return "[{},{}]".format(self._x, self._y)


if __name__ == '__main__':
    raise RuntimeError("This module is not expected to be ran from CLI")

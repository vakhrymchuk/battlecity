from enum import Enum


class FireOrder(Enum):
  FIRE_BEFORE_TURN = 'ACT,{}'
  FIRE_AFTER_TURN = '{},ACT'

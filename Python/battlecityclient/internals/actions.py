from enum import Enum


class BattlecityAction(Enum):
  GO_LEFT = 'left'
  GO_RIGHT = 'right'
  GO_UP = 'up'
  GO_DOWN = 'down'
  FIRE_THEN_RIGHT = 'act,right'
  FIRE_THEN_LEFT = 'act,left'
  FIRE_THEN_UP = 'act,up'
  FIRE_THEN_DOWN = 'act,down'
  RIGHT_THEN_FIRE = 'right,act'
  LEFT_THEN_FIRE = 'left,act'
  UP_THEN_FIRE = 'up,act'
  DOWN_THEN_FIRE = 'down,act'
  FIRE = 'act'
  DELAYED_FIRE = 'act(1)'
  DO_NOTHING = 'stop'

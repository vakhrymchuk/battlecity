namespace CodeBattleNetLibrary.Models
{
    public enum Commands : byte
    {
        GO_LEFT = 1,
        GO_TOP = 2,
        GO_RIGHT = 3,
        GO_DOWN = 4,
    }

    public enum Fire : byte
    {
        FIRE_BEFORE_ACTION = 1,
        FIRE_AFTER_ACTION = 2
    }
    
}
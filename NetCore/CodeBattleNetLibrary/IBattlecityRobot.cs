using System.Threading.Tasks;
using CodeBattleNetLibrary.Models;

namespace CodeBattleNetLibrary
{
    public interface IBattlecityRobot
    {
        Task<StepCommands> CreateAction(StepData stepData);
    }
}
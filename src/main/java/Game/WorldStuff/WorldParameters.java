package Game.WorldStuff;

public class WorldParameters {
    public int map;
    public int maxRound;
    public float startDifficulty;
    public float roundScaling;
    public WorldParameters(int map, int maxRound, float startDifficulty, float roundScaling){
        this.map=map;
        this.maxRound=maxRound;
        this.startDifficulty=startDifficulty;
        this.roundScaling=roundScaling;
    }
}

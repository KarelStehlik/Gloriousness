package Game.WorldStuff;

import Game.Common.Buffs.Buff.StatBuff;
import Game.Common.Buffs.Modifier.Modifier;
import Game.Mobs.MobClasses.TdMob;
import Game.Mobs.MobGeneration.Wave;
import Game.Mobs.MobGeneration.WaveGenerator.MoabGenerator;
import GlobalUse.Log;
import windowStuff.GraphicsOnly.Text.TextModifiers;

import java.util.ArrayList;

public class GameModifiers {
    public ArrayList<Modifier<TdWorld>> mods = new ArrayList<>();
    public ArrayList<Modifier<WorldParameters>> parameterMods = new ArrayList<>();
    public ArrayList<String> texts = new ArrayList<>();

    public GameModifiers() {
        addMod();
    }

    public GameModifiers(GameModifiers mod) {
        this.texts=new ArrayList<>();
        texts.addAll(mod.texts);
        this.mods=new ArrayList<>();
        mods.addAll(mod.mods);
        this.parameterMods=new ArrayList<>();
        parameterMods.addAll(mod.parameterMods);

    }

    private void addMod() {
        int modifier = (int) Math.ceil(Math.random() * 4);
        switch (modifier) {
            case (1) -> addBigMobs();
            case (2) -> addStranded();
            case (3) -> addScaling();
            default -> addMoabMadness();
        }
    }

    private void addBigMobs() {
        addDescription(TextModifiers.titleSize + "BIG "+ TextModifiers.size(50) + " mobs","Increases mob size and mob hp");
        mods.add((world)->Wave.addMobMod((TdMob mob) -> {
            mob.addBuff(new StatBuff<TdMob>(StatBuff.Type.MORE, TdMob.Stats.health, 2));
        }));
        mods.add((world)->Wave.addMobMod((TdMob mob) -> {
            mob.addBuff(new StatBuff<TdMob>(StatBuff.Type.MORE, TdMob.Stats.size, 1.3f));
            mob.updateSize();
        }));
    }

    private void addStranded() {
        addDescription(TextModifiers.RGBcolors(new int[]{200, 200, 20}) + "Stranded", "Halves starting cash and income");
        mods.add((world) -> {
            world.setIncome(world.getIncome() / 2);
            world.setMoney(world.getMoney() / 2);
        });
    }

    private void addScaling() {
        addDescription("Steroids", "Increases round scaling");
        parameterMods.add((param) -> param.roundScaling *= 2);
    }

    private void addMoabMadness() {
        addDescription("Moab madness", "Adds moabs");

        MoabGenerator supplements = new MoabGenerator();
        mods.add((world) -> {
            world.mobSpawner.addWaveEffect(
                    (wave) -> {
                        if(supplements.validFromWave()-5<=wave.waveNum) {
                            wave.add(supplements.generate(wave.waveNum).get(0));
                        }
                    }
            );
        });
        parameterMods.add((param)->{
            if(param.maxRound<=12){
                param.maxRound=12;
            }
        });
    }

    private void addDescription(String title, String description) {
        texts.add(TextModifiers.red+TextModifiers.size(50)+ title + "\n" + TextModifiers.Grey + TextModifiers.normalSize + description);
    }

}

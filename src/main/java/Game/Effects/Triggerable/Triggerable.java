package Game.Effects.Triggerable;

import Game.Turrets.Turret;

public interface Triggerable<T> { //these are effects that can be "activated" by somme object in some way,
                                    // things like whenever something happens this activates mah trap card

    public void trigger(T triggerObj);

}

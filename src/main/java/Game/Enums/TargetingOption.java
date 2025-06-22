package Game.Enums;

import Game.Mobs.TdMob;

import java.util.Comparator;

public enum TargetingOption {
    FIRST(new Comparator<TdMob>() {
        @Override
        public int compare(TdMob o1, TdMob o2) {
            return o1.getProgress().compareTo(o2.getProgress());
        }
    }
        ),
    LAST(new Comparator<TdMob>() {
        @Override
        public int compare(TdMob o1, TdMob o2) {
            return o1.getProgress().compareTo(o2.getProgress());
        }
    }
    ),
    STRONG(new Comparator<TdMob>() {
        @Override
        public int compare(TdMob o1, TdMob o2) {
            return o1.getProgress().compareTo(o2.getProgress());
        }
    }
    );
    private final Comparator<TdMob> comparator;
    public Comparator<TdMob> getComparator(){
        return comparator;
    }
    TargetingOption(Comparator<TdMob> comparator){
        this.comparator=comparator;
    }
}

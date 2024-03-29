package thedrake;

import java.util.List;

public class Troop {
    private final String name;
    private final Offset2D aversPivot;
    private final Offset2D reversPivot;
    private final List<TroopAction> aversActions;
    private final List<TroopAction> reversActions;

    public Troop(String name, Offset2D aversPivot, Offset2D reversPivot,List<TroopAction> aversActions,List<TroopAction> reversActions) {
        this.name = name;
        this.aversPivot = aversPivot;
        this.reversPivot = reversPivot;
        this.aversActions=aversActions;
        this.reversActions=reversActions;
    }
    public Troop(String name, Offset2D pivot,List<TroopAction> aversActions,List<TroopAction> reversActions){
        this(name, pivot, pivot,aversActions,reversActions);

    }
    public Troop(String name,List<TroopAction> aversActions,List<TroopAction> reversActions){
        this(name, new Offset2D(1, 1),aversActions,reversActions);
    }
    public String name(){
        return name;
    }
    public Offset2D pivot(TroopFace face){
        if (face == TroopFace.REVERS){
            return reversPivot; }
        else{
            return aversPivot;}

    }
    public List<TroopAction> actions(TroopFace face){
        if(face==TroopFace.REVERS) {
            return reversActions;
        }
        else
            return aversActions;
    }
}


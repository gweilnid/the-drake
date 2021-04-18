package thedrake;

import java.util.LinkedList;
import java.util.List;

public class TroopTile implements Tile{

    private final Troop troop;
    private final PlayingSide side;
    private final TroopFace face;

    public TroopTile(Troop troop, PlayingSide side, TroopFace face){

        this.troop = troop;
        this.side = side;
        this.face = face;
    }
    public PlayingSide side() {

        return side;
    }
    public TroopFace face(){

        return face;
    }
    public Troop troop(){

        return troop;
    }
    @Override
    public boolean canStepOn() {

        return false;
    }

    @Override
    public boolean hasTroop() {

        return true;
    }

    @Override
    public List<Move> movesFrom(BoardPos pos, GameState state) {
        List<Move>moves= new LinkedList<Move>();

        for( TroopAction i : troop.actions(face)){
            moves.addAll(i.movesFrom(pos,side,state));
        }
        return moves;
    }

    public TroopTile flipped() {
        if(face == TroopFace.REVERS){
            return new TroopTile(this.troop, this.side, TroopFace.AVERS);
        }
        return new TroopTile(this.troop, this.side, TroopFace.REVERS);
    }
}

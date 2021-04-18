package thedrake;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class BoardTroops {
	private final PlayingSide playingSide;
	private final Map<BoardPos, TroopTile> troopMap;
	private final TilePos leaderPosition;
	private final int guards;
	
	public BoardTroops(PlayingSide playingSide) { 
		this.troopMap = Collections.EMPTY_MAP;
		this.leaderPosition = TilePos.OFF_BOARD;
		this.playingSide = playingSide;
		//zadne straze
		guards = 0;

	}
	
	public BoardTroops(
			PlayingSide playingSide,
			Map<BoardPos, TroopTile> troopMap,
			TilePos leaderPosition, 
			int guards) {
		this.playingSide = playingSide;
		this.troopMap = troopMap;
		this.leaderPosition = leaderPosition;
		this.guards = guards;

	}

	public Optional<TroopTile> at(TilePos pos) {
		if(pos == TilePos.OFF_BOARD){
			return Optional.empty();
		}
		if(troopMap.get((BoardPos)pos) == null){
			return Optional.empty();
		}

		return Optional.of(troopMap.get(pos));
	}
	
	public PlayingSide playingSide() {
		return playingSide;
	}
	
	public TilePos leaderPosition() {
		return leaderPosition;
	}

	public int guards() {
		return guards;
	}
	
	public boolean isLeaderPlaced() {
		if(leaderPosition != TilePos.OFF_BOARD){
			return true;
		}
		else {
			return false;}
	}
	
	public boolean isPlacingGuards() {
		if(isLeaderPlaced() && troopMap.size() <= 2){
			return true;
		}
		else {
			return false;}
	}	
	
	public Set<BoardPos> troopPositions() {
		return troopMap.keySet();
	}

	public BoardTroops placeTroop(Troop troop, BoardPos target) {
		TroopTile troopTileNew = new TroopTile(troop, playingSide, TroopFace.AVERS);
		if(at(target).isPresent()){
			throw new IllegalArgumentException();
		}
		TilePos newLeaderPosition = leaderPosition;
		int newGuards = guards;
		if(!isLeaderPlaced()){
			newLeaderPosition= target;
		}
		if(isPlacingGuards()){
			newGuards = newGuards + 1;
		}
		Map<BoardPos, TroopTile> newTroopMap = new HashMap<>(troopMap);

		newTroopMap.put(target, troopTileNew);

		return new BoardTroops(playingSide, newTroopMap, newLeaderPosition, newGuards);
	}
	
	public BoardTroops troopStep(BoardPos origin, BoardPos target) {
		if(!isLeaderPlaced()) {
			throw new IllegalStateException("Cannot move troops before the leader is placed.");
		}
		if(isPlacingGuards()) {
			throw new IllegalStateException("Cannot move troops before guards are placed.");
		}
		if(!at(origin).isPresent() || at(target).isPresent()){
			throw new IllegalArgumentException();
		}
		TilePos newLeaderPosition = leaderPosition;
		if(leaderPosition.equals(origin)) newLeaderPosition = target;
		Map<BoardPos, TroopTile> newTroopName = new HashMap<>(troopMap);
		TroopTile tile = newTroopName.remove(origin);
		newTroopName.put(target, tile.flipped());
		return new BoardTroops(playingSide(), newTroopName, newLeaderPosition, guards);
	}
	
	public BoardTroops troopFlip(BoardPos origin) {
		if(!isLeaderPlaced()) {
			throw new IllegalStateException("Cannot move/flip troops before the leader is placed.");
		}
		if(isPlacingGuards()) {
			throw new IllegalStateException("Cannot move/flip troops before guards are placed.");
		}
		if(!at(origin).isPresent()){
			throw new IllegalArgumentException();
		}
		Map<BoardPos, TroopTile> newTroops = new HashMap<>(troopMap);
		TroopTile tile = newTroops.remove(origin);
		newTroops.put(origin, tile.flipped());
		return new BoardTroops(playingSide(), newTroops, leaderPosition, guards);
	}
	
	public BoardTroops removeTroop(BoardPos target) {
		if(!isLeaderPlaced()) {throw new IllegalStateException("Cannot remove troops before the leader is placed."); }
		if(isPlacingGuards()) {throw new IllegalStateException("Cannot remove troops before guards are placed."); }
		if(!at(target).isPresent()) throw new IllegalArgumentException();
		TilePos LeaderPositionNew = leaderPosition;
		int guardsNew = guards;
		if(leaderPosition.equals(target)) LeaderPositionNew = TilePos.OFF_BOARD;
		else guardsNew = guardsNew - 1;
		Map<BoardPos, TroopTile> troopMapNew = new HashMap<>(troopMap);
		troopMapNew.remove(target);
		return new BoardTroops(playingSide, troopMapNew, LeaderPositionNew, guardsNew);	}
}

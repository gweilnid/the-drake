package thedrake;

import java.util.Optional;

public class GameState{
	private final Board board;
	private final PlayingSide sideOnTurn;
	private final Army blueArmy;
	private final Army orangeArmy;
	private final GameResult result;
	
	public GameState(
			Board board, 
			Army blueArmy, 
			Army orangeArmy) {
		this(board, blueArmy, orangeArmy, PlayingSide.BLUE, GameResult.IN_PLAY);
	}
	
	public GameState(
			Board board, 
			Army blueArmy, 
			Army orangeArmy, 
			PlayingSide sideOnTurn, 
			GameResult result) {
		this.board = board;
		this.sideOnTurn = sideOnTurn;
		this.blueArmy = blueArmy;
		this.orangeArmy = orangeArmy;
		this.result = result;
	}
	
	public Board board() {
		return board;
	}
	
	public PlayingSide sideOnTurn() {
		return sideOnTurn;
	}
	
	public GameResult result() {
		return result;
	}
	
	public Army army(PlayingSide side) {
		if(side == PlayingSide.BLUE) {
			return blueArmy;
		}
		
		return orangeArmy;
	}
	
	public Army armyOnTurn() {
		return army(sideOnTurn);
	}
	
	public Army armyNotOnTurn() {
		if(sideOnTurn == PlayingSide.BLUE)
			return orangeArmy;
		
		return blueArmy;
	}
	
	public Tile tileAt(TilePos pos) {
		if (armyNotOnTurn().boardTroops().at(pos).isPresent()) {
			return armyNotOnTurn().boardTroops().at(pos).get();
		}

		if (armyOnTurn().boardTroops().at(pos).isPresent()) {
			return armyOnTurn().boardTroops().at(pos).get();
		}

		return board.at(pos);

	}
	
	private boolean canStepFrom(TilePos origin) {
		if(result != GameResult.IN_PLAY ||
				!armyOnTurn().boardTroops().at(origin).isPresent() ||
				armyOnTurn().boardTroops().isPlacingGuards() ||
				// !armyOnTurn().boardTroops().isLeaderPlaced() ||
				armyNotOnTurn().boardTroops().at(origin).isPresent()) {
			return false;
		}
		return true;
	}

	private boolean canStepTo(TilePos target) {
		if (result != GameResult.IN_PLAY||target == TilePos.OFF_BOARD){
			return false;
		}

		return tileAt((BoardPos) target).canStepOn();

	}
	
	private boolean canCaptureOn(TilePos target) {
		if(result!=GameResult.IN_PLAY || !armyNotOnTurn().boardTroops().at(target).isPresent()) {
			return false;
		}
		if (target.equals(TilePos.OFF_BOARD)) {
			return false;
		}
		return true;
	}
	
	public boolean canStep(TilePos origin, TilePos target)  {
		return canStepFrom(origin) && canStepTo(target);
	}
	
	public boolean canCapture(TilePos origin, TilePos target)  {
		return canStepFrom(origin) && canCaptureOn(target);
	}
	
	public boolean canPlaceFromStack(TilePos target) {
		// Místo pro váš
		if (result!=GameResult.IN_PLAY||
				armyOnTurn().stack().isEmpty()||
				!canStepTo(target)) {
			return false;
		}

		if (!armyOnTurn().boardTroops().isLeaderPlaced()) {
			if (sideOnTurn == PlayingSide.ORANGE) {

				return target.j() == board.dimension() - 1;
			}
			else {

				return target.j() == 0;
			}
		}
		if (armyOnTurn().boardTroops().isPlacingGuards()) {

			for (TilePos tmp : armyOnTurn().boardTroops().leaderPosition().neighbours()) {

				if (target.equals(tmp)){

					return true;
				}
			}

			return false;
		}

		for (BoardPos tmp : armyOnTurn().boardTroops().troopPositions()) {

			for (BoardPos position : tmp.neighbours()) {

				if (target.equals(position)){

					return true;
				}
			}
		}
		return false;
	}
	
	public GameState stepOnly(BoardPos origin, BoardPos target) {		
		if(canStep(origin, target))		 
			return createNewGameState(
					armyNotOnTurn(),
					armyOnTurn().troopStep(origin, target), GameResult.IN_PLAY);
		
		throw new IllegalArgumentException();
	}
	
	public GameState stepAndCapture(BoardPos origin, BoardPos target) {
		if(canCapture(origin, target)) {
			Troop captured = armyNotOnTurn().boardTroops().at(target).get().troop();
			GameResult newResult = GameResult.IN_PLAY;
			
			if(armyNotOnTurn().boardTroops().leaderPosition().equals(target))
				newResult = GameResult.VICTORY;
			
			return createNewGameState(
					armyNotOnTurn().removeTroop(target), 
					armyOnTurn().troopStep(origin, target).capture(captured), newResult);
		}
		
		throw new IllegalArgumentException();
	}
	
	public GameState captureOnly(BoardPos origin, BoardPos target) {
		if(canCapture(origin, target)) {
			Troop captured = armyNotOnTurn().boardTroops().at(target).get().troop();
			GameResult newResult = GameResult.IN_PLAY;
			
			if(armyNotOnTurn().boardTroops().leaderPosition().equals(target))
				newResult = GameResult.VICTORY;
			
			return createNewGameState(
					armyNotOnTurn().removeTroop(target),
					armyOnTurn().troopFlip(origin).capture(captured), newResult);
		}
		
		throw new IllegalArgumentException();
	}
	
	public GameState placeFromStack(BoardPos target) {
		if(canPlaceFromStack(target)) {
			return createNewGameState(
					armyNotOnTurn(), 
					armyOnTurn().placeFromStack(target), 
					GameResult.IN_PLAY);
		}
		
		throw new IllegalArgumentException();
	}
	
	public GameState resign() {
		return createNewGameState(
				armyNotOnTurn(), 
				armyOnTurn(), 
				GameResult.VICTORY);
	}
	
	public GameState draw() {
		return createNewGameState(
				armyOnTurn(), 
				armyNotOnTurn(), 
				GameResult.DRAW);
	}
	
	private GameState createNewGameState(Army armyOnTurn, Army armyNotOnTurn, GameResult result) {
		if(armyOnTurn.side() == PlayingSide.BLUE) {
			return new GameState(board, armyOnTurn, armyNotOnTurn, PlayingSide.BLUE, result);
		}
		
		return new GameState(board, armyNotOnTurn, armyOnTurn, PlayingSide.ORANGE, result); 
	}
}

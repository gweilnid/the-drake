package thedrake;

public class Board {

	private final int dimension;
	private final BoardTile[][] board;

	// Konstruktor. Vytvoří čtvercovou hrací desku zadaného rozměru, kde všechny dlaždice jsou prázdné, tedy BoardTile.EMPTY
	public Board(int dimension) {
		this.dimension = dimension;
		//-----------------------
		board = new BoardTile[dimension][dimension];

		// (i,j)

		for (int i = 0; i < dimension; i++){
			for (int j = 0; j < dimension; j++){
				board[i][j] = BoardTile.EMPTY;
			}
		}

	}

	// Rozměr hrací desky
	public int dimension() {
		return dimension;
	}

	// Vrací dlaždici na zvolené pozici.
	public BoardTile at(TilePos pos) {
		// pozice i,j
		return board[pos.i()][pos.j()];
	}

	// Vytváří novou hrací desku s novými dlaždicemi. Všechny ostatní dlaždice zůstávají stejné
	public Board withTiles(TileAt ...ats) {
		// nova deska
		Board freshBoard =new Board(dimension);
		// obj.clone()
		for (int i = 0 ; i < dimension ; i++){
			freshBoard.board[i] = this.board[i].clone();
		}
		// for each
		for ( TileAt tile : ats ){
			freshBoard.board[tile.pos.i()][tile.pos.j()] = tile.tile;
		}
		return freshBoard;
	}

	// Vytvoří instanci PositionFactory pro výrobu pozic na tomto hracím plánu
	public PositionFactory positionFactory() {
		return new PositionFactory(dimension);
	}
	
	public static class TileAt {
		public final BoardPos pos;
		public final BoardTile tile;
		
		public TileAt(BoardPos pos, BoardTile tile) {
			this.pos = pos;
			this.tile = tile;
		}
	}
}


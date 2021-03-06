package hr.nhex.battle;

import hr.nhex.board.BoardTile;

/**
 * Razred koji predstavlja polje u borbi, odnosno polje na koje su primjenjeni svi bonusi od okolnih
 * jedinica.
 * 
 * @author Luka Rukli�
 *
 */

public class BattleTile {

	/**
	 * Originalno polje na plo�i na koje se primjenjuju bonusi.
	 */
	private BoardTile tile;

	public BattleTile(BoardTile tile) {
		this.tile = tile;
	}

	public BoardTile getTile() {
		return tile;
	}

	public void setTile(BoardTile tile) {
		this.tile = tile;
	}

	/**
	 * Getter za x koordinatu polja sadr�anog unutar instance polja u borbi (engl. <i>BattleTile</i>).
	 * @return x koordinata polja
	 */
	public int getX() {
		return tile.getX();
	}

	/**
	 * Getter za y koordinatu polja sadr�anog unutar instance polja u borbi (engl. <i>BattleTile</i>).
	 * @return y koordinata polja
	 */
	public int getY() {
		return tile.getY();
	}

}

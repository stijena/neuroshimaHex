package hr.nhex.battle;

import hr.nhex.board.Board;
import hr.nhex.board.BoardTile;
import hr.nhex.board.IBasicBoard;
import hr.nhex.board.controls.MedicControl;
import hr.nhex.generic.Pair;
import hr.nhex.model.AbstractTile;
import hr.nhex.model.HQ;
import hr.nhex.model.Module;
import hr.nhex.model.Netter;
import hr.nhex.model.Unit;
import hr.nhex.model.ability.Ability;
import hr.nhex.model.ability.AbilityType;
import hr.nhex.model.player.Player;
import hr.nhex.model.unit.Attack;
import hr.nhex.model.unit.AttackType;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents battle simulator in Neuroshima Hex board game.
 *
 * @author Luka Rukli�
 * @author Marin Bu�an�i�
 *
 */

public class BattleSimulator implements IBasicBoard {

	// hp treba updateat

	private Board board;

	private List<BattleTile> boardBattleTiles = new ArrayList<>();

	private List<BattleEvent> battleEvents = new ArrayList<BattleEvent>();

	private MedicControl medicControl = new MedicControl();

	public Integer currentInitiative = null;

	private static final int MAXSHIFT_SMALL_BOARD = 4;

	int[] angleX = {1, 0, -1, -1, 0, 1};	// objasni komentarom
	int[] angleY = {0, 1, 1, 0, -1, -1};

	/**
	 * Konstruktor koji popunjava listu s poljima koja �e se koristiti prilikom borbe.
	 *
	 * @param listTiles lista s poljima trenutno na plo�i
	 */
	public BattleSimulator(Board board) {
		this.board = board;
		for (BoardTile tile : board.getTiles()) {
			boardBattleTiles.add(new BattleTile(tile.copy()));
		}
	}

	/**
	 * Metoda koja obavlja funkciju konstruktora razreda BattleSimulator na na�in da kopira �itavu plo�u
	 * odnosno sva njezina polja u razred <code>BattleSimulator</code>.
	 *
	 * @param listTiles lista s poljima
	 */
	public void reloadBoardToBattle(List<BoardTile> listTiles) {
		boardBattleTiles.clear();
		for (BoardTile tile : listTiles) {
			boardBattleTiles.add(new BattleTile(tile.copy()));
		}
	}

	/**
	 * Metoda simulatora borbe koja izvr�ava borbu.
	 */
	public void executeBattle() {
		applyModuleBonus();

		for (int currentSpeed = defineHighestSpeed(); currentSpeed >= 0; currentSpeed--) {
			//applyInstant();
			executeBattleInitiative(currentSpeed);
			updateAfterEffects();
			if (currentSpeed != 0) {
				applyModuleBonus();
			}
		}

	}

	public boolean executeNextRound() {

		battleEvents.clear();

		applyModuleBonus();

		if (currentInitiative == null) {
			this.currentInitiative = defineHighestSpeed();
		}

		System.out.println("Executing initiative "+currentInitiative);
		executeBattleInitiative(currentInitiative);
		currentInitiative--;

		medicControl.executeMedic();

		if (currentInitiative < -1) {
			return true;
		} else {
			return false;
		}
	}

	public MedicControl getMedicControl() {
		return medicControl;
	}

	/**
	 * Metoda koja izvr�ava fazu borbe odre�enu ulaznom varijablom <code>currentSpeed</code> koja
	 * predstavlja trenutnu inicijativu.
	 *
	 * @param currentSpeed trenutna inicijativa
	 */

	private void executeBattleInitiative(int currentSpeed) {

		for (BattleTile bt : boardBattleTiles) {
			if ((bt.getTile() instanceof Unit)
					&& !tileIsNetted(bt.getX(), bt.getY(), 0, bt.getTile().getPlayer())) {
				Unit unit = ((Unit)bt.getTile());
				for (Integer unitSpeed : unit.getSpeed()) {
					if (unitSpeed == currentSpeed) {
						executeAttacks(unit);
					}
				}
			}
		}
	}

	/**
	 * <p>Metoda koja pretražuje napade odre�ene jedinice primljene u metodu preko varijable <code>unit</code> i te iste napade izvr�ava.
	 *
	 * <p>Ako je riječ o napadu prsa-o-prsa (engl. <i>melee</i>), napad se izvr�ava nad jedinicom na polju na koje je napad usmjeren.
	 * Ukoliko je pak riječ o napadu na daljinu (engl. <i>ranged</i>), napad se izvr�ava nad svim poljima u liniji smjera napada, dok ne
	 * do�e do protivničke jedinice. Napad na daljinu je mogu�e oslabiti �titom (engl. <i>block</i>).
	 *
	 * @param unit jedinica �iji se napadi izvr�avaju
	 */

	private void executeAttacks(Unit unit) {

		for (Attack attack : unit.getAttacks()) {

			if (attack.getType() == AttackType.MELEE || attack.getType() == AttackType.HQ_MELEE) {
				int pointsToTileX = unit.getX() + angleX[(attack.getPointsTo() + unit.getAngle()) % 6];	// vjerojatno se % 6 mo�e maknuti
				int pointsToTileY = unit.getY() + angleY[(attack.getPointsTo() + unit.getAngle()) % 6];
				BattleTile attacked = getBattleTile(pointsToTileX, pointsToTileY);
				if (attacked != null && !unit.getPlayer().equals(attacked.getTile().getPlayer()) && !(unit instanceof HQ && attacked.getTile() instanceof HQ)) {
					addBattleEvent(BattleEventType.MELEE_ATTACK, unit, attacked.getTile(), attack.getValue());
					attack.getType().attack(attacked, attack.getValue(), attack.getType());
				}
			}
			else if (attack.getType() == AttackType.RANGED) {
				int shift = 1;
				while (true) {
					int pointsToTileXrange =
							unit.getX() + angleX[(attack.getPointsTo() + unit.getAngle()) % 6]*shift;
					int pointsToTileYrange =
							unit.getY() + angleY[(attack.getPointsTo() + unit.getAngle()) % 6]*shift;
					if (shift > MAXSHIFT_SMALL_BOARD) {
						break;
					}
					BattleTile attacked = getBattleTile(pointsToTileXrange, pointsToTileYrange);
					if (attacked != null && !unit.getPlayer().equals(attacked.getTile().getPlayer())) {

						if (attacked.getTile() instanceof Unit || attacked.getTile() instanceof Module) {
							BoardTile boardTileBlock = attacked.getTile();
							int pointsToBlock = (attack.getPointsTo() + unit.getAngle() + 3 - boardTileBlock.getAngle()) % 6;
							if (boardTileBlock.getAbilities().contains(new Ability(pointsToBlock, AbilityType.BLOCK))) {
								attack.getType().attack(attacked, attack.getValue() - 1, attack.getType());
							} else {
								attack.getType().attack(attacked, attack.getValue(), attack.getType());
							}
							addBattleEvent(BattleEventType.RANGED_ATTACK, unit, attacked.getTile(), attack.getValue());
							break;
						}
					}
					shift++;
				}

			}
		}
	}

	private void addBattleEvent(BattleEventType eventType, BoardTile attacker, BoardTile target, int value) {

		battleEvents.add(new BattleEvent(eventType, attacker, value, target));

	}

	/**
	 * Method that determines the highest initiative during battle.
	 */

	private int defineHighestSpeed() {
		int highestSpeed = 0;
		for (BattleTile bt : boardBattleTiles) {
			if (bt.getTile() instanceof Unit) {
				Unit unit = (Unit)bt.getTile();
				for (int speed : unit.getSpeed()) {
					if (speed > highestSpeed) {
						highestSpeed = speed;
					}
				}
			}
		}
		return highestSpeed;
	}

	public void applyModuleBonus() {

		for (BattleTile battleTile : boardBattleTiles) {
			BoardTile currentTile = battleTile.getTile();
			// provjera da li postoje mogu�i potezi unutar borbe
			if (!tileIsNetted(currentTile.getX(), currentTile.getY(), 0, battleTile.getTile().getPlayer())) {
				for (Ability ability : currentTile.getAbilities()) {
					int pointsToTileX = currentTile.getX() + angleX[(ability.getPointsTo() + currentTile.getAngle()) % 6];
					int pointsToTileY = currentTile.getY() + angleY[(ability.getPointsTo() + currentTile.getAngle()) % 6];
					BattleTile bonusApplicant = getBattleTile(pointsToTileX, pointsToTileY);
					if (bonusApplicant != null && currentTile.getPlayer().equals(bonusApplicant.getTile().getPlayer())) {
						if (ability.getType() == AbilityType.MEDIC) {
							medicControl.addMedicTargetPair(currentTile, bonusApplicant.getTile());
						}
						ability.getType().applyBonus(bonusApplicant);
					}
				}
			}
		}
	}

	/**
	 * <p>Metoda koja nakon izvr�ene borbe a�urira stanje plo�e (�ivotne bodove, mre�e, otrov) te
	 * mi�e jedinice �iji su se �ivotni bodovi spustili ispod 1.
	 * <p>Tako�er, metoda nakon svake borbe provjerava maknute jedinice sa sposobnosti bacanje mre�e (
	 * engl. <i>Netter</i>) te ukoliko je netko od njih ima
	 */

	public void updateAfterEffects() {

		List<BoardTile> deadTiles = new ArrayList<BoardTile>();

		for (BattleTile bt : boardBattleTiles) {
			if (bt.getTile().getHitPoints() <= 0) {
				deadTiles.add(bt.getTile());
			}
			else if (bt.getTile().getHitPoints() != board.getTile(bt.getX(), bt.getY()).getHitPoints()) { // greška nakon Move, provjeri
				board.getTile(bt.getTile().getX(), bt.getTile().getY()).setHitPoints(bt.getTile().getHitPoints());
			}
		}

		board.getTiles().removeAll(deadTiles);
		reloadBoardToBattle(board.getTiles());

	}

	@Override
	public boolean isFilled(int x, int y) {
		for (BattleTile battleTile : boardBattleTiles) {
			if (battleTile.getTile().getX() == x && battleTile.getTile().getY() == y) {
				return true;
			}
		}
		return false;
	}

	public BattleTile getBattleTile(int x, int y) {
		for (BattleTile battleTile : boardBattleTiles) {
			if (battleTile.getTile().getX() == x && battleTile.getTile().getY() == y) {
				return battleTile;
			}
		}
		return null;
	}

	@Override
	public AbstractTile getTile(int x, int y) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<BattleEvent> getBattleEvents() {
		return battleEvents;
	}

	/**
	 * Metoda koja ispisuje trenutna polja na plo�i, zajedno s njihovim imenima, �ivotnim bodovima te lokacijom
	 */

	public void printBoardState() {
		for (BattleTile battleTile : boardBattleTiles) {
			BoardTile tile = battleTile.getTile();
			System.out.println(tile.getName() + ", HP: " +
					tile.getHitPoints() + ", LOC: " +
					tile.getX() + ", " + tile.getY());
		}
	}

	public void printBoardUnitState() {
		for (BattleTile battleTile : boardBattleTiles) {
			if (battleTile.getTile() instanceof Unit) {
				Unit unit = (Unit)battleTile.getTile();
				System.out.println(unit.getName() + ", HP: " +
						unit.getHitPoints() + ", LOC: " +
						unit.getX() + ", " + unit.getY());
				System.out.print("speed:");
				for (Integer speed : unit.getSpeed()) {
					System.out.println(speed + " ");
				}
				System.out.print("attacks: ");
				for (Attack attack : unit.getAttacks()) {
					System.out.println("Attack Side: " + attack.getPointsTo() + " Attack Value: "+attack.getValue());
				}
				System.out.println();
			}
		}
	}

	@Override
	public boolean tileIsNetted(int x, int y, int noOfIterations, Player player) {
		if (noOfIterations == 10) {
			return false;
		}
		List<Pair> adjacentTiles = board.getAdjecantTiles(x, y);
		for (BattleTile tile : boardBattleTiles) {
			if (adjacentTiles.contains(new Pair(tile.getX(), tile.getY()))) {
				if (tile.getTile() instanceof Netter && !tile.getTile().getPlayer().equals(player)) {
					if (!tileIsNetted(tile.getX(), tile.getY(), noOfIterations+1, tile.getTile().getPlayer())) {

						Netter netter = ((Netter)tile.getTile());
						netter.getNettedTiles().clear();
						for (Ability ability : netter.getAbilities()) {
							if (ability.getType() == AbilityType.NET) {
								int pointsToTileX = netter.getX() + angleX[(ability.getPointsTo() + netter.getAngle()) % 6];
								int pointsToTileY = netter.getY() + angleY[(ability.getPointsTo() + netter.getAngle()) % 6];
								if (isFilled(pointsToTileX, pointsToTileY)) { // i ne sadr�i mre�u usmjerenu prema meni

								}
								netter.addNettedTile(new Pair(pointsToTileX, pointsToTileY));
							}
						}
						for (Pair p : ((Netter) tile.getTile()).getNettedTiles()) {
							if (p.getX() == x && p.getY() == y) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

}

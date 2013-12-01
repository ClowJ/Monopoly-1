package monopoly;

import java.util.LinkedList;

import monopoly.tiles.*;
import monopoly.xml.TileLoader;
import monopoly.xml.CardLoader;

public class Monopoly {
	//
	// Member Variables
	//
	
	private LinkedList<Player> players;
	private int curPlayerIndex;
	private Player currentPlayer;
	
	private Dice dice;
	private Board board;
	
	private MonopolyModelState modelState;
	
	//
	// Constructors
	//
	
	public Monopoly(String boardFilename, String chanceFilename, String commChestFilename) {
		// init player stuff
		players = new LinkedList<Player>();
		curPlayerIndex = 0;
		
		// load the dice
		dice = new Dice();
		
		// load the board components from files
		ITile[] boardTiles = TileLoader.loadFromXML(boardFilename);
		CardDeck chanceDeck = new CardDeck(CardLoader.loadFromFile(chanceFilename));
		CardDeck comChestDeck = new CardDeck(CardLoader.loadFromFile(commChestFilename));
		
		// load the board
		board = new Board(boardTiles, chanceDeck, comChestDeck);
	}
	
	//
	// Main Functions
	//
	
	public boolean nextMove() {
		// don't do anything if the model currently isn't in a playable state
		if (modelState != MonopolyModelState.PLAYING) {
			return false;
		}
		
		// roll the dice and cycle the player
		dice.roll();
		
		// get the current player and calculate distance
		currentPlayer = players.get(curPlayerIndex);
		int distance = dice.getFirstValue() + dice.getSecondValue();
		
		// no longer in jail if they rolled doubles
		if (dice.getFirstValue() == dice.getSecondValue()) {
			currentPlayer.setInJail(false);
		}
		
		// player will not move if they are in jail
		currentPlayer.move(distance);
		
		// run the landOn
		ITile tile = board.getPropertyAt(currentPlayer.getPosition());
		modelState = tile.landOn(currentPlayer);
		
		// increment the current player
		curPlayerIndex++;
		if (curPlayerIndex == players.size()) {
			curPlayerIndex = 0;
		}
		
		// return true if there isn't any other action required by
		// the controller to continue the game
		return (modelState == MonopolyModelState.PLAYING);
	}
	
	public boolean answerBuyRequest(boolean buying) {
		if (modelState != MonopolyModelState.BUY_REQUEST) {
			return false;
		}
		
		boolean success = true;
		if (buying) {
			Property propertyToBuy = board.getPropertyAt(currentPlayer.getPosition());
			success = currentPlayer.buyProperty(propertyToBuy);
		}
		
		return success;
	}
	
	//
	// Getters
	//
	
	public Player[] getPlayers() {
		return players.toArray(new Player[0]);
	}
	
	public Player getCurrentPlayer() {
		return currentPlayer;
	}
	
	public Board getBoard() {
		return board;
	}
	
	public MonopolyModelState getModelState() {
		return modelState;
	}
}

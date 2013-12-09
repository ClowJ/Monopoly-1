package monopoly;

import java.util.LinkedList;

import monopoly.tiles.*;
import monopoly.xml.TileLoader;
import monopoly.xml.CardLoader;

class NoPlayersException extends RuntimeException {
	public NoPlayersException() { super("There are not enough players added to the game!"); }	
}

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
		
		// only load the chance deck if the file was specified
		CardDeck chanceDeck = null;
		if (chanceFilename != null) {
			chanceDeck = new CardDeck(CardLoader.loadFromFile(chanceFilename));
		}
		
		// only load the com chance deck if the file was specified
		CardDeck commChestDeck = null;
		if (commChestFilename != null) {
			commChestDeck = new CardDeck(CardLoader.loadFromFile(commChestFilename));
		}
		
		// load the board
		board = new Board(boardTiles, chanceDeck, commChestDeck);
		
		modelState = modelState.PLAYING;
	}
	
	//
	// Main Functions
	//
	
	public boolean nextMove() {
		if (players.size() < 2) {
			throw new NoPlayersException();
		}
		
		// don't do anything if the model currently isn't in a playable state
		if (modelState != MonopolyModelState.PLAYING) {
			return false;
		}
		
		// roll the dice and cycle the player
		dice.roll();
		
		// get the current player and calculate distance
		int distance = dice.getFirstValue() + dice.getSecondValue();
		
		// no longer in jail if they rolled doubles
		if (dice.getFirstValue() == dice.getSecondValue()) {
			currentPlayer.setInJail(false);
		}
		
		// player will not move if they are in jail
		currentPlayer.move(distance);
		
		// run the landOn
		ITile tile = board.getTileAt(currentPlayer.getPosition());
		modelState = tile.landOn(currentPlayer);
		
		// return true if there isn't any other action required by
		// the controller
		return (modelState == MonopolyModelState.PLAYING);
	}
	
	public boolean handleBuyRequest(boolean buying) {
		if (modelState != MonopolyModelState.BUY_REQUEST) {
			return false;
		}
		
		boolean success = true;
		if (buying) {
			Property propertyToBuy = board.getPropertyAt(currentPlayer.getPosition());
			success = currentPlayer.buyProperty(propertyToBuy);
		}
		
		// reset the model state and increment the player
		modelState = MonopolyModelState.PLAYING;
		incrementPlayer();
		
		return success;
	}
	
	public boolean handleChancePull() {
		// get the pulled card
		Card pulledCard = board.getChanceDeck().getTopCard();
		
		// run the script on the card
		pulledCard.runScript(this);
		
		// reset the model state
		modelState = MonopolyModelState.PLAYING;
		incrementPlayer();
		board.getChanceDeck().nextCard(); // increment the card deck
		
		return true;
	}
	
	public boolean handleCommChestPull() {
		// get the pulled card
		Card pulledCard = board.getCommunityChestDeck().getTopCard();
		
		// run the script on the card
		pulledCard.runScript(this);
		
		// reset the model state
		modelState = MonopolyModelState.PLAYING;
		incrementPlayer();
		board.getCommunityChestDeck().nextCard(); // increment the card deck
		
		return true;
	}
	
	public boolean handleIdleState() {
		// make sure the modelState is playing
		modelState = MonopolyModelState.PLAYING;
		
		// and increment the player
		incrementPlayer();
		
		return true;
	}
	
	public void addPlayer(Player p) {
		// add the player to the list of current players
		players.add(p);
		
		// if this is the first player, they are the current player
		if (players.size() == 1) {
			currentPlayer = p;
		}
	}
	
	//
	// Getters
	//
	
	public Player[] getPlayers() {
		return players.toArray(new Player[0]);
	}
	
	public int getCurrentPlayerIndex() {
		return curPlayerIndex;
	}
	
	public Player getCurrentPlayer() {
		return currentPlayer;
	}
	
	public Dice getDice() {
		return dice;
	}
	
	public Board getBoard() {
		return board;
	}
	
	public MonopolyModelState getModelState() {
		return modelState;
	}
	
	//
	// Private Functions
	//
	
	private void incrementPlayer() {		
		// increment the current player
		curPlayerIndex++;
		if (curPlayerIndex == players.size()) {
			curPlayerIndex = 0;
		}
		
		// assign the value of the current player
		currentPlayer = players.get(curPlayerIndex);
	}
}

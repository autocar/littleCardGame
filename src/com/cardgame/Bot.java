package com.cardgame;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.cardgame.manager.GameTable;
import com.cardgame.util.Card;
import com.cardgame.util.Deck;
import com.cardgame.util.GameConstants;
import com.cardgame.util.Rank;


public abstract class Bot implements Comparable<Bot>{

	
	protected 	GameTable 		gameTable 				= null;
	protected 	List<Card> 		hand 					= null;	
	private 	int 			botNumber				= 0;
	protected 	int				collectedNumberOfCards	= 0;
	protected 	int 			collectedPoints 		= 0;
	private 	boolean 		lastCollected			= false;
	private 	int				numberOfWins			= 0;
	
	public Bot(GameTable table){
		this.gameTable = table;
	}
	
	@Override
	public int compareTo(Bot b){
		return this.botNumber - b.botNumber;
	}
	
	
	public int deal(List<Card> cards, int cardPointer){
		
		// if first hand, put first 4 cards to the pile
		if (cardPointer == 0){
			Stack<Card> pile = gameTable.getPile();
			while ( cardPointer < GameConstants.PLAYER_CARD_COUNT_FOR_EACH_HAND ){
				pile.push(cards.get(cardPointer));
				gameTable.updateDiscardedCards(cards.get(cardPointer++));
			}
		}
		
		// deal cards to other players
		for (int i = 0 ; i < GameConstants.PLAYER_COUNT ; i++){
			
			// find bot to deal cards
			Bot currBot = gameTable.getNext(gameTable.getDealer().getBotNumber() + i);
			
			// set current bot's hand
			currBot.hand = new ArrayList<Card>(cards.subList(cardPointer, cardPointer + GameConstants.PLAYER_CARD_COUNT_FOR_EACH_HAND));
			
			// update card array pointer 
			cardPointer = cardPointer + GameConstants.PLAYER_CARD_COUNT_FOR_EACH_HAND;
		}
		
		return cardPointer;
	}
	
	public abstract Card play(Card top);
	
	private int getCardPoint(Card c){
		return Deck.getPointsLookupTable().get(c);
	}
	
	protected int getSmallestPointIndex(){
		// initialize smallestPoint value and smallestCardIndex
		int smallestPointIndex = -1;
		int	smallestPoint = GameConstants.DIAMOND_TEN;
		
		for (int i = 0 ; i < hand.size() ; i++){
			int currPoint = getCardPoint(hand.get(i));
			if (currPoint <= smallestPoint){
				smallestPoint = currPoint;
				smallestPointIndex = i;
			}
		}
		
		return smallestPointIndex;
	}
	
	protected boolean isJack(Card c){
		return c.getRank().equals(Rank.JACK);
	}
	
	protected Card playLastCard(){
		return this.getHand().remove(0);
	}
	
	public int getBotNumber() {
		return botNumber;
	}

	public void setBotNumber(int botNumber) {
		this.botNumber = botNumber;
	}
	
	public List<Card> getHand(){
		return hand;
	}
	
	public void incrementCollectedNumOfCards(int cardCount){
		this.collectedNumberOfCards += cardCount;
	}
	
	public int getCollectedNumOfCards(){
		return this.collectedNumberOfCards;
	}
	
	public void incrementCollectedPoints(int points){
		this.collectedPoints += points;
	}
	
	public int getCollectedPoints(){
		return this.collectedPoints;
	}


	public boolean isLastCollected() {
		return lastCollected;
	}


	public void setLastCollected(boolean lastCollected) {
		this.lastCollected = lastCollected;
	}
	
	public void resetStatistics(){
		this.collectedNumberOfCards = 0;
		this.collectedPoints 		= 0;
		this.lastCollected 			= false;
	}
	
	public int getNumOfWins(){
		return this.numberOfWins;
	}
	
	public void incrementNumOfWins(){
		this.numberOfWins++;
	}
}

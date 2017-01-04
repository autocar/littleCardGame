package com.cardgame.manager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.cardgame.Bot;
import com.cardgame.Main;
import com.cardgame.util.Card;
import com.cardgame.util.CollectedPointsComparator;
import com.cardgame.util.Deck;
import com.cardgame.util.GameConstants;
import com.cardgame.util.GameUtil;
import com.cardgame.util.NumOfCardsComparator;
import com.cardgame.util.Rank;


public class GameTable implements Runnable{

	private Deck 					deck 			= new Deck();
	
	private Bot[] 					bots 			= null;
	
	private List<Card> 				cards 			= null;
	
	private int 					cardPointer 	= 0;
	
	private Stack<Card> 			pile 			= null;
	
	private int 					handCount 		= 0;
	
	private int 					totalPlayCount	= 0;
	
	private Map<Rank,Integer>		discardedCards	= null;
	
	
	
	public GameTable(String[] botTypes, int totalPlayCount) throws InstantiationException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException{

		bots = new Bot[4];
		
		// create bots
		for (int i = 0 ; i <botTypes.length ; i++){
			Constructor cons = Class.forName(botTypes[i]).getConstructor(GameTable.class);
			Bot b = (Bot)cons.newInstance(this);
			b.setBotNumber(i);
			bots[i] = b;
		}
		
		// create deck
		cards = deck.getCards();

		// create pile 
		pile = new Stack<Card>();
		
		// initialize discarded cards map
		initializeDiscardedCardMap();
		
		this.totalPlayCount = totalPlayCount;
		
		
	}
	
	
	public void startGame(){
		//System.out.println("Game started! - Hand Count: " + handCount + " Thread: " + Thread.currentThread().getName());
		
		int playCount = 0;
		
		while (playCount < this.totalPlayCount){
			
			while (cardPointer < GameConstants.CARD_COUNT){
			
				Bot dealer = this.getDealer();
				
				cardPointer = dealer.deal(cards, cardPointer);
				
				// get bot next to dealer
				Bot next = this.getNext(dealer.getBotNumber());
				
				this.playPlayerTurns(next);
				
				// update handCount
				handCount++;
			}
			
			// set winner
			evaluateResults();
			
			// reset bot order according to botNumber
			Arrays.sort(bots);
			
//			for (int i = 0 ; i < bots.length ; i++){
//				System.out.println("Thread No:" +Thread.currentThread().getName()+ " getBotNum: " + bots[i].getBotNumber() + " with points: " + bots[i].getCollectedPoints()
//						+" numOfWins: " + bots[i].getNumOfWins() + " bot type:" + bots[i].getClass());
//			}
			
			// reset stats for next play
			for (int i = 0 ; i < bots.length ; i++){
				bots[i].resetStatistics();
			}
			
			// increment play count
			playCount++;
			
			// reset pile
			this.getPile().clear();
			
			// shuffle cards for the next play 
			deck.shuffle(cards);
			
			// initialize card pointer
			cardPointer = 0;
			 
		}
//		System.out.println("playCount:" + playCount);
//		System.out.println("Game over! - HandCount: " + handCount);
	}
	
	
	private void playPlayerTurns(Bot firstBot) {
		
		Bot currentBot = firstBot;
		
		int cardCounter = 0 ;
		
		while(GameConstants.TOTAL_CARD_COUNT_FOR_EACH_HAND > cardCounter) {
		
			Card top = null;
			if (getPile().size() != 0) {
				top = getPile().peek();
			}
			
			Card played = currentBot.play(top);
			
			// perform actions after each play
			updateGameState(played, currentBot);
			
			// update discarded cards map
			updateDiscardedCards(played);
			
			
		//	System.out.println("Player: " + currentBot.getBotNumber() + " played: " + played);
			
			// update current bot
			currentBot = this.getNext(currentBot.getBotNumber());
			
			// update card counter
			cardCounter++;
		}
		
	}
	
	public void updateDiscardedCards(Card c){
		Rank currRank = c.getRank();
		Integer currentRankCount = discardedCards.get(currRank);
		discardedCards.put(currRank, ++currentRankCount);
	}
	
	private void evaluateResults() {
		
		// if pile is not empty, award pile contents to lastCollected bot
		if (this.getPile().size() != 0){
			for (int i = 0 ; i < bots.length ; i++){
				Bot currBot = bots[i];
				if (currBot.isLastCollected()){
					currBot.incrementCollectedNumOfCards(pile.size());
					currBot.incrementCollectedPoints(GameUtil.getPilePoints(this.getPile()));
					break;
				}
			}
		}
		
		// compare collected number of cards for each bot and award 3 points to most collected
		Arrays.sort(bots,new NumOfCardsComparator());
		bots[bots.length-1].incrementCollectedPoints(GameConstants.MAJORITY_OF_CARDS);
		
		// decide the winner of this play
		Arrays.sort(bots,new CollectedPointsComparator());
		bots[bots.length-1].incrementNumOfWins();
		
		Main.incrementResultsMap(bots[bots.length-1].getBotNumber());
		
	}
	
	private void updateGameState(Card c, Bot currentBot){
		
		int points = 0;
		Card topCard = null;
		
		if (pile.size() != 0) {
			topCard = pile.peek();
		
			boolean isSameRank = topCard.isSameRank(c);
			boolean isPlayedJack = c.getRank().equals(Rank.JACK);
			
			boolean isCollectable = isSameRank | isPlayedJack;
			
			// check pisti case
			if (pile.size() == 1){
				points = GameUtil.getOneCardPilePoints(isSameRank, isPlayedJack, topCard, c);
			} else {
				points = GameUtil.getMultiCardPilePoints(isCollectable, pile, c);
			}
				
			currentBot.incrementCollectedPoints(points);

			if (isCollectable){
				collectCards(currentBot, pile);
				return;
			}
			
		}
		
		pile.push(c);

	}
	
	private void collectCards(Bot currentBot, Stack<Card> pile){
		
		// get card count of the pile and played card
		int pileSize = pile.size() + 1;
		currentBot.incrementCollectedNumOfCards(pileSize);
		
		// empty pile
		pile.clear();
		
		// set the current bot as the last bot who got the cards from pile
		for (int i = 0 ; i < bots.length ; i ++){
			bots[i].setLastCollected(false);
		}
		currentBot.setLastCollected(true);
	}
	
	private void initializeDiscardedCardMap(){
		
		discardedCards = new HashMap<Rank,Integer>();
		
		for (Rank r : Rank.values()){
			discardedCards.put(r, 0);
		}
		
	}
	
	public Map<Rank,Integer> getDiscardedCards(){
		return this.discardedCards;
	}
	
	public int handCount(){
		return handCount;
	}
	
	// returns the current dealer for this hand
	public Bot getDealer(){
		return bots[this.handCount % GameConstants.PLAYER_COUNT];
	}
	
	// returns the next player to be dealed cards
	public Bot getNext(int botNum){
		int nextBotNum = (botNum + 1) %  GameConstants.PLAYER_COUNT;
		return bots[nextBotNum];
	}
	
	public Stack<Card> getPile(){
		return pile;
	}
	
	public Bot[] getBots(){
		return bots;
	}
	
	public int getCardPointer(){
		return cardPointer;
	}
	
	@Override
	public void run(){
		this.startGame();
	}
	
}

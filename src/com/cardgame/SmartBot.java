package com.cardgame;

import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.cardgame.manager.GameTable;
import com.cardgame.util.Card;
import com.cardgame.util.GameConstants;
import com.cardgame.util.GameUtil;
import com.cardgame.util.Rank;


public class SmartBot extends Bot{

	
	public SmartBot(GameTable table) {
		super(table);
	}

	@Override
	public Card play(Card top) {
		
		// plays for achieving the max score
		
		// get discarded cards 
		Map<Rank,Integer> discardedCards = gameTable.getDiscardedCards();
		
		// get hand
		List<Card> currentHand = this.getHand();
		int currentHandSize = currentHand.size();
		
		// get pile info
		Stack<Card> currentPile = gameTable.getPile();
		int pileSize = currentPile.size();
		
		// jack index
		int jackIndex = -1;
		
		if (currentHandSize != 1){
		
			if (pileSize != 0){
				
				for (int i = 0 ; i < currentHandSize ; i++){
					if(currentHand.get(i).isSameRank(top)){
						return currentHand.remove(i);
					}
					if (currentHand.get(i).getRank().equals(Rank.JACK)){
						jackIndex = i;
					}
					
				}
				
				// decide to play jack or not
				if (jackIndex != -1){
					// calculate pile's current point
					int pilePoints = GameUtil.getPilePoints(currentPile);
					if (playJack(pilePoints,pileSize)){
						return currentHand.remove(jackIndex);
					}
				}
				
			} else {
				
				// top equals null, pile is empty, play most discarded card
				return currentHand.remove(getMostDiscardedCardIndex(discardedCards));
				
			}
		} else {
			return playLastCard();
		}
		
		return currentHand.remove(getMostDiscardedCardIndex(discardedCards));
	}
	
	private boolean playJack(int pilePoints, int pileSize){
		// if current ratio exceeds threshold, play jack
		return ((pilePoints) / pileSize) > GameConstants.JACK_THRESHOLD; 
	}
	
	
	private int getMostDiscardedCardIndex(Map<Rank,Integer> discardedCards){
		
		int mostDiscardedCount = -1;
		int mostDiscardedIndex = -1;
		for (int i = 0 ; i < hand.size() ; i++){
			if (!isJack(hand.get(i))){
				int currMostDiscardedCount = discardedCards.get(hand.get(i).getRank());
				if (currMostDiscardedCount > mostDiscardedCount){
					mostDiscardedCount = currMostDiscardedCount;
					mostDiscardedIndex = i;
				}
			}
		}
		
		// if both cards are jack
		if (mostDiscardedIndex == -1){
			mostDiscardedIndex = 0;
		}
		
		return mostDiscardedIndex;
	}


}

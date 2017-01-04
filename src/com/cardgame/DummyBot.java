package com.cardgame;

import java.util.Stack;

import com.cardgame.manager.GameTable;
import com.cardgame.util.Card;
import com.cardgame.util.Rank;


public class DummyBot extends Bot{

	
	public DummyBot(GameTable table) {
		super(table);
	}
	
	@Override
	public Card play(Card top) {
		
		int jackIndex = -1;
		int smallestPointIndex = -1;
		
		int currentHandSize = hand.size();
		
		if (currentHandSize != 1) {
			
			// first check same rank, then checks j, else plays smallest
			
			// get pile info: if pileSize = 0, play smallest
			Stack<Card> currentPile = gameTable.getPile();
			int pileSize = currentPile.size();
			
			if (pileSize != 0) {
			
				for (int i = 0 ; i < currentHandSize ; i++){
					if (top.isSameRank(hand.get(i))){
						return this.hand.remove(i);
					}

					if (hand.get(i).getRank().equals(Rank.JACK)){
						jackIndex = i;
					}

				}
				
			} 
			
		} else {
			
			return playLastCard();
		
		}
		
		if (jackIndex != -1){
			return this.hand.remove(jackIndex);
		} else {
			// pilesize == 0 or noJack or noSameRank 
			smallestPointIndex = this.getSmallestPointIndex();
			return this.hand.remove(smallestPointIndex);
		}
		
	}
	
	
	


}

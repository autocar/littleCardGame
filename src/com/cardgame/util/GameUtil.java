package com.cardgame.util;

import java.util.Iterator;
import java.util.Stack;

public class GameUtil {

	
	// if c1 and c2 both equals jack, award 20 points, 
	// if c1 and c2 has same rank, award 10,
	// else 0 
	public static int getOneCardPilePoints(boolean isSameRank, boolean isPlayedJack, Card top, Card played) {
		
		if (isSameRank && isPlayedJack){
			return GameConstants.DOUBLE_PISTI;
		}
		
		if (isSameRank){
			return GameConstants.PISTI;
		}
		
		if (isPlayedJack){
			return Deck.getPointsLookupTable().get(top) + Deck.getPointsLookupTable().get(played);
		}
		
		return GameConstants.NO_POINTS;
		
	}
	
	public static int getMultiCardPilePoints(boolean isCollectable, Stack<Card> pile, Card played){
		
		// calculate pile's and played card's points
		int pilePoints = 0;
		if (isCollectable) {
			pilePoints = GameUtil.getPilePoints(pile);
			pilePoints += Deck.getPointsLookupTable().get(played);
		}
		
		return pilePoints;
	}
	
	public static int getPilePoints(Stack<Card> pile){
		int pointCount = 0;
		Iterator<Card> cardIterator = pile.iterator();
		while(cardIterator.hasNext()){
			pointCount += Deck.getPointsLookupTable().get(cardIterator.next());
		}
		return pointCount;
	}
	
	
}

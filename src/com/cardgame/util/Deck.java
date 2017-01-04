package com.cardgame.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Deck {

	
	private static 	List<Card> 			defaultCards 				= null;
	private static 	Map<Card,Integer> 	pointsLookupTable 			= null;
	private 		List<Card> 			playCards 					= null;
	
	
	// made card generation static to do it one time
	static {
		if (defaultCards == null) {
			defaultCards = new ArrayList<Card>(GameConstants.CARD_COUNT);
			pointsLookupTable = new HashMap<>();
	
			for (Suit s : Suit.values()){
				for (Rank r : Rank.values()){
					Card c = new Card(s,r);
					defaultCards.add(c);
					
					// generate card points table
					int points = Deck.getCardPoints(c);
					pointsLookupTable.put(c, points);
				}
			}
		}
	}
	
	public Deck(){

		playCards = new ArrayList<Card>(defaultCards);
		
		shuffle(playCards);
	
	}
	
	
	public void shuffle(List<Card> cards){
		
		// shuffles given deck 
		Collections.shuffle(cards);
		
	}
	
	public List<Card> getCards(){
		return playCards;
	}
	
	private static int getCardPoints(Card c){
		
		if (c.getSuit().equals(Suit.CLUBS) && c.getRank().equals(Rank.TWO)){
			return 2;
		}
		
		if (c.getSuit().equals(Suit.DIAMONDS) && c.getRank().equals(Rank.TEN)){
			return 3;
		}
		
		if (c.getRank().equals(Rank.JACK) || c.getRank().equals(Rank.ACE)){
			return 1;
		}
		
		return 0;
		
	}
	
	public static Map<Card,Integer> getPointsLookupTable(){
		return Deck.pointsLookupTable;
	}
	
	
}

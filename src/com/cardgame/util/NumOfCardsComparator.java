package com.cardgame.util;

import java.util.Comparator;

import com.cardgame.Bot;


public class NumOfCardsComparator implements Comparator<Bot>{

	@Override
	public int compare(Bot b1, Bot b2) {
		return b1.getCollectedNumOfCards() - b2.getCollectedNumOfCards();
	}


	
}

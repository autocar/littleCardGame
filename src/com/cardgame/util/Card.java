package com.cardgame.util;


public class Card {

	// made them final to make obj immutable
	private final Rank rank;
	private final Suit suit;
	
	
	public Card(Suit suit,Rank rank){
		this.suit = suit;
		this.rank = rank;
	}
	
	public String toString(){
		return suit +" "+rank.getRankValue();
	}
	
	public boolean isSame(Card card) {
		if ((this.rank != card.rank) || (this.suit != card.suit))
			return false;
		else
			return true;
	}
	
	public boolean isSameRank(Card card) {
		if ((this.rank == card.rank))
			return true;
		
		return false;
	}
	
	
	@Override
    public boolean equals(final Object obj)
    {
        if (obj == null || obj == this || !(obj instanceof Card)) 
            return false;

        Card other = (Card) obj;

        if (other.rank != this.rank)       
        	return false;
        if (other.suit != this.suit)     
        	return false;

        return true;
    }
    
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((rank == null) ? 0 : rank.hashCode());
		result = prime * result + ((suit == null) ? 0 : suit.hashCode());
		return result;
	}
	
	
	public Rank getRank() {
		return rank;
	}

	public Suit getSuit() {
		return suit;
	}
	
	
	
}

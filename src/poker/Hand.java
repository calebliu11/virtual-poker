package poker;

import java.util.ArrayList;
import java.util.Collections;

public class Hand {

	public ArrayList<Card> handCards = new ArrayList<Card>();

	public Rank high;
	public Rank low;
	public ArrayList<Card> kickers = new ArrayList<Card>();

	protected boolean usesLow = false;
	protected boolean usesAllCards = false;

	public HandRank highestRank;

	public enum HandRank {
		ROYAL_FLUSH, STRAIGHT_FLUSH, FOUR_OF_A_KIND, FULL_HOUSE, FLUSH, STRAIGHT, THREE_OF_A_KIND, TWO_PAIR, PAIR,
		HIGH_CARD,
	}

	public Hand() {
	}

	public ArrayList<Card> getHand() {
		return handCards;
	}


	public Rank getHigh() {
		return high;
	}

	public Rank getLow() {
		return low;
	}

	public ArrayList<Card> getKickers() {
		return kickers;
	}

	public void displayHand() {
		for (int i = 0; i < handCards.size(); i++) {
			System.out.println(handCards.get(i));
		}
	}

	public HandRank getHighestRank() {
		return highestRank;
	}

	public void determineHighestRank(ArrayList<Card> communityCards) {
		ArrayList<Card> allCards = concatenateArrayLists(communityCards);
		Collections.sort(allCards);
		CardSet allCardsSet = new CardSet(allCards);
		if (isRoyalFlush(allCardsSet)) {
			highestRank = HandRank.ROYAL_FLUSH;
		} else if (isStraightFlush(allCardsSet)) {
			highestRank = HandRank.STRAIGHT_FLUSH;
		} else if (isFourOfAKind(allCardsSet)) {
			highestRank = HandRank.FOUR_OF_A_KIND;
		} else if (isFullHouse(allCardsSet)) {
			highestRank = HandRank.FULL_HOUSE;
		} else if (isFlush(allCardsSet)) {
			highestRank = HandRank.FLUSH;
		} else if (isStraight(allCardsSet)) {
			highestRank = HandRank.STRAIGHT;
		} else if (isThreeOfAKind(allCardsSet)) {
			highestRank = HandRank.THREE_OF_A_KIND;
		} else if (isTwoPair(allCardsSet)) {
			highestRank = HandRank.TWO_PAIR;
		} else if (isPair(allCardsSet)) {
			highestRank = HandRank.PAIR;
		} else {
			high = null;
            low = null;
            usesLow = false;
            usesAllCards = false;
			highestRank = HandRank.HIGH_CARD;
		}
	}

	public ArrayList<Card> concatenateArrayLists(ArrayList<Card> communityCards) {
		ArrayList<Card> allCards = new ArrayList<Card>();
		for (int i = 0; i < handCards.size(); i++) {
			allCards.add(handCards.get(i));
		}
		for (int j = 0; j < communityCards.size(); j++) {
			allCards.add(communityCards.get(j));
		}
		return allCards;
	}

	public boolean isRoyalFlush(CardSet allCardsSet) {
		if (isStraightFlush(allCardsSet)) {
			ArrayList<Card> allCards = allCardsSet.getCardSet();
			boolean aceExists = false;
			boolean kingExists = false;
			boolean queenExists = false;
			for (Card c : allCards) {
				switch (c.getRank()) {
				case ACE:
					aceExists = true;
					break;
				case KING:
					kingExists = true;
					break;
				case QUEEN:
					queenExists = true;
					break;
				}
			}
			return (aceExists && kingExists && queenExists);
		} else {
			return false;
		}
	}

	private boolean isStraightFlush(CardSet allCardsSet) {
		usesLow = false;
		usesAllCards = false;
		return isFlush(allCardsSet) && isStraight(allCardsSet);
	}

	private boolean isFourOfAKind(CardSet allCardsSet) {
		usesLow = false;
		usesAllCards = false;

		for (Rank rank : Rank.values()) {
			if (allCardsSet.getRank(rank).size() == 4) {
				high = rank;
				kickers = allCardsSet.removeByRank(rank, 4);
				return true;
			}
		}
		return false;
	}

	private boolean isFullHouse(CardSet allCardsSet) {
		usesLow = true;
		usesAllCards = false;
		high = null;
		low = null;

		for (Rank rank : Rank.values()) {
			if (allCardsSet.getRank(rank).size() == 3 && high == null) {
				high = rank;
			} else if (allCardsSet.getRank(rank).size() == 2 && low == null) {
				low = rank;
			}

			if (high != null && low != null) {
				return true;
			}
		}
		return false;
	}

	private boolean isFlush(CardSet allCardsSet) {
		usesLow = false;
		usesAllCards = true;

		ArrayList<Card> allSuitedCards;

		for (Suit suit : Suit.values()) {
			if (allCardsSet.getSuit(suit).size() >= 5) {
				allSuitedCards = allCardsSet.getSuit(suit);
				high = allSuitedCards.get(0).getRank();
				return true;
			}
		}
		return false;
	}

	private boolean isStraight(CardSet allCardsSet) {
		usesLow = false;
		usesAllCards = false;

		int numOfCardsInRow = 0;
		int position = 0;

		ArrayList<Card> allCards = allCardsSet.getCardSet();

		while (position < allCards.size() - 1) {
			if (allCards.get(position).getRank().isOneLower(allCards.get(position + 1).getRank())) {
				numOfCardsInRow++;
				if (numOfCardsInRow == 4) {
					high = allCards.get(position + 1).getRank();
					return true;
				} else {
					position++;
				}
			} else {
				numOfCardsInRow = 0;
				position++;
			}
		}
		return false;
	}

	private boolean isThreeOfAKind(CardSet allCardsSet) {
		usesLow = false;
		usesAllCards = false;

		for (Rank rank : Rank.values()) {
			if (allCardsSet.getRank(rank).size() == 3) {
				high = rank;
				kickers = allCardsSet.removeByRank(rank, 3);
				return true;
			}
		}
		return false;
	}

	public boolean isTwoPair(CardSet allCardsSet) {
		usesLow = true;
		usesAllCards = false;

		high = null;
		low = null;

		for (Rank rank : Rank.values()) {
			ArrayList<Card> rankedCards = allCardsSet.getRank(rank);
			if (rankedCards.size() == 2) {
				if (high == null) {
					high = rank;
					kickers = allCardsSet.removeByRank(rank, 2);
				} else {
					low = rank;
					kickers = allCardsSet.removeByRank(rank, 2);
					return true;
				}
			}
		}
		return false;
	}

	private boolean isPair(CardSet allCardsSet) {
		for (Rank rank : Rank.values()) {
			if (allCardsSet.getRank(rank).size() == 2) {
				high = rank;
				kickers = allCardsSet.removeByRank(rank, 2);
				return true;
			}
		}
		return false;
	}

	public Card getHighCard(CardSet allCardsSet) {
		ArrayList<Card> allCards = allCardsSet.getCardSet();
		return allCards.get(0);
	}

	public int compareTo(Hand opponentHand, ArrayList<Card> communityCards) {
		determineHighestRank(communityCards);
		HandRank bestOpponentRank = opponentHand.getHighestRank();

		if (this.highestRank == opponentHand.getHighestRank()) {

			if (high == opponentHand.getHigh()) {
				if (usesLow && low != opponentHand.getLow()) {

					return -1 * low.compareTo(opponentHand.getLow());
				}
				for (int i = 0; i < kickers.size(); i++) {
					if (kickers.get(i).compareTo(opponentHand.getKickers().get(i)) != 0) {
						return -1 * kickers.get(i).compareTo(opponentHand.getKickers().get(i));
					}
				}
			} else {
				return (-1) * high.compareTo(opponentHand.getHigh());
			}

		} else {
			return -1 * highestRank.compareTo(bestOpponentRank);
		}
		
		return 0;
	}
}

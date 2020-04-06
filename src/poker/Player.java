package poker;

import java.util.Scanner;

public class Player {
	String name;
	Hand hand;
	int money;
	int bet;
	Scanner in = new Scanner(System.in);

	/**
	 * Constructor for player class
	 * @param name
	 * @param money
	 */
	public Player(String name, int money) {
		this.name = name;
		this.money = money;
	}

	public enum PlayerChoice {
		CHECK,CALL,FOLD,RAISE
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Hand getHand() {
		return hand;
	}

	public void setHand(Hand hand) {
		this.hand = hand;
	}

	public void receiveCard(Card c) {
		hand.receiveCard(c);
	}

	public int getMoney() {
		return money;
	}

<<<<<<< HEAD
	public void setMoney(int money) {
		this.money = money;
	}

	public int getBet() {
		return bet;
	}

=======
	/**
	 * determine the amount of bet placed
	 * @param bet
	 * @return the amount of money bet
	 */
>>>>>>> 15cf18958af2311fc8a59f15120fd8102db81aa7
	public int bet(int bet) {
		int sizeOfBet = Math.min(money, bet);
		

		this.bet += sizeOfBet;
		money -= sizeOfBet;
		return sizeOfBet;
	}

	public void resetBet() {
		this.bet = 0;
	}
	
	public boolean isAllIn() {
		if(money == 0) {
			return true;
		}
		return false;
	}

	public PlayerChoice checkRaise() {
		System.out.println("Please enter your choice:");
		System.out.println("1 to CHECK and 2 to RAISE");
		while (true) {
			switch (in.nextInt()) {
			case 1:
				return PlayerChoice.CHECK;
			case 2:
				return PlayerChoice.RAISE;
			default:
				System.out.println("That's not an option. Try again!");
				break;
			}
		}
	}
	
	public PlayerChoice callRaiseFold() {
		Scanner in = new Scanner(System.in);
		System.out.println("Please enter your choice:");
		System.out.println("1 to CALL, 2 to RAISE, and 3 to FOLD");
		while (true) {
			switch (in.nextInt()) {
			case 1:
				return PlayerChoice.CALL;
			case 2:
				return PlayerChoice.RAISE;
			case 3:
				return PlayerChoice.FOLD;
			default:
				System.out.println("That's not an option. Try again!");
				break;
			}
		}
	}
}

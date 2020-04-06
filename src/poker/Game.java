<<<<<<< HEAD
package poker;

import java.util.ArrayList;
import java.util.Scanner;

public class Game {

	public int smallBlind = 5;
	public int startingMoney = 100;

	Player player = new Player("Player", startingMoney);
	ComputerPlayer computer = new ComputerPlayer("Computer", startingMoney);
	ArrayList<Player> players = new ArrayList<Player>();

	Hand playerHand = player.getHand();
	Hand computerHand = computer.getHand();

	boolean isPlayerTurn = true;
	ArrayList<Card> communityCards = new ArrayList<Card>();

	boolean isPreflop = true;

	boolean playerActed = false;
	boolean computerActed = false;

	int maxBet = getMaxBet();

	Player winner = null;

	Deck deck = new Deck();

	public Game(Player player, ComputerPlayer computer) {
		this.player = player;
		this.computer = computer;
	}

	public Game(int smallBlind, int startingMoney, ArrayList<Player> players) {
		this.smallBlind = smallBlind;
		this.startingMoney = startingMoney;
		this.players = players;
	}

	public void startGame() {
		players.add(player);
		players.add(computer);
		player.setHand(new Hand());
		computer.setHand(new Hand());

		while (!checkForWinner()) {
			playRound();
		}
	}

	public int getSmallBlind() {
		return smallBlind;
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	
	public void playRound() {
		System.out.println("New Round!");
		System.out.println(player.getName() + " has " + player.getMoney() + ".");
		System.out.println(computer.getName() + " has " + computer.getMoney() + ".");
		playPreflop();

		if (winner == null) {
			playFlop();
		}
		if (winner == null) {
			playTurn();
		}
		if (winner == null) {
			playRiver();
		}
		if (winner == null) {
			playShowdown();
		}
		doRoundWinner();
	}

	public void playPreflop() {
		isPreflop = true;
		deck.shuffle();
		dealPlayerCards();
		Hand playerHand = player.getHand();
		System.out.println("Your hand:");
		playerHand.displayHand();
		player.resetBet();
		computer.resetBet();
		playBettingRound(isPreflop);
	}

	public void dealPlayerCards() {
		player.setHand(new Hand());
		computer.setHand(new Hand());
		player.receiveCard(deck.deal());
		computer.receiveCard(deck.deal());
		player.receiveCard(deck.deal());
		computer.receiveCard(deck.deal());
	}

	public void playFlop() {
		System.out.println("FLOP");
		isPreflop = false;
		dealFlop();
		displayCommunityCards();
		playBettingRound(isPreflop);
	}

	public void dealFlop() {
		deck.deal();
		deck.deal();
		deck.deal();
		communityCards.clear();
		communityCards.add(deck.deal());
		communityCards.add(deck.deal());
		communityCards.add(deck.deal());
	}

	public void playTurn() {
		System.out.println("TURN");
		dealTurn();
		displayCommunityCards();
		playBettingRound(isPreflop);
	}

	public void dealTurn() {
		deck.deal();
		communityCards.add(deck.deal());
	}

	public void playRiver() {
		System.out.println("RIVER");
		dealRiver();
		displayCommunityCards();
		playBettingRound(isPreflop);
	}

	public void dealRiver() {
		deck.deal();
		communityCards.add(deck.deal());
	}

	public void displayCommunityCards() {
		System.out.println("Community cards:");
		for (int i = 0; i < communityCards.size(); i++) {
			System.out.println(communityCards.get(i));
		}

	}

	public void playBettingRound(boolean isPreflop) {
		maxBet = getMaxBet();
		do {
			playerActed = false;
			computerActed = false;
			if (isPlayerTurn) {
				isPlayerTurn = !isPlayerTurn;
				playerBettingRound();
				if(winner == null && !(computerActed && playerActed)) {
					computerBettingRound();
				}
			} else {
				isPlayerTurn = !isPlayerTurn;
				computerBettingRound();
				if(winner == null  && !(computerActed && playerActed)) {
					playerBettingRound();
				}
			}
			System.out.println("Current Pot: " + this.getCurrentPot());
		} while (((!playerHasBet(player, maxBet) || !playerHasBet(computer, maxBet)) || !(computerActed && playerActed))
				&& (winner == null) && !player.isAllIn() && !computer.isAllIn());
	}

	public void playerBettingRound() {
		isPlayerTurn = !isPlayerTurn;
		if (!player.isAllIn()) {
			if (playerHasBet(player, maxBet)) {
				switch (player.checkRaise()) {
				case CHECK:
					playerActed = true;
					System.out.println(player.getName() + " checks.");
					break;
				case RAISE:
					playerActed = true;
					computerActed = false;
					int raiseAmount = player.bet(askPlayerForBet(player));
					maxBet += raiseAmount;
					System.out.println(player.getName() + " raises " + raiseAmount + ".");
					break;
				}
			} else {
				switch (player.callRaiseFold()) {
				case CALL:
					playerActed = true;
					computerActed = true;
					player.bet(maxBet - player.getBet());
					System.out.println(player.getName() + " calls.");
					break;
				case RAISE:
					playerActed = true;
					computerActed = false;
					int raiseAmount = player.bet(askPlayerForBet(player));
					maxBet += raiseAmount;
					System.out.println(player.getName() + " raises " + raiseAmount + ".");
					break;
				case FOLD:
					System.out.println(player.getName() + " folds.");
					winner = computer;
					break;

				}
			}
		}
	}

	public void computerBettingRound() {
		if (!computer.isAllIn()) {
			if (playerHasBet(computer, maxBet)) {
				switch (computer.simulateCheckRaise(isPreflop, communityCards)) {
				case CHECK:
					computerActed = true;
					System.out.println(computer.getName() + " checks.");
					break;
				case RAISE:
					computerActed = true;
					playerActed = false;
					int raiseAmount = computer.bet(smallBlind * 2);
					maxBet += raiseAmount;
					System.out.println(computer.getName() + " raises " + raiseAmount + ".");
					break;
				}
			} else {
				switch (computer.simulateCallRaiseFold(isPreflop, communityCards)) {
				case CALL:
					computerActed = true;
					playerActed = true;
					computer.bet(maxBet - computer.getBet());
					System.out.println(computer.getName() + " calls.");
					break;
				case RAISE:
					computerActed = true;
					playerActed = false;
					int raiseAmount = computer.bet(smallBlind * 2);
					maxBet += raiseAmount;
					System.out.println(computer.getName() + " raises " + raiseAmount + ".");
					break;
				case FOLD:
					System.out.println(computer.getName() + " folds.");
					winner = player;
					break;

				}
			}
		}

	}

	public void playShowdown() {
		System.out.println("SHOWDOWN");
		Hand playerHand = player.getHand();
		Hand computerHand = computer.getHand();
		System.out.println("Computer hand: ");
		computerHand.displayHand();
		playerHand.determineHighestRank(communityCards);
		computerHand.determineHighestRank(communityCards);
		System.out.println(player.getName() + ": " + player.getHand().getHighestRank());
		System.out.println(computer.getName() + ": " + computer.getHand().getHighestRank());
		int comparison = playerHand.compareTo(computerHand, communityCards);
		if (comparison > 0) {
			winner = player;
		} else if (comparison < 0) {
			winner = computer;
		}
	}

	public int getMaxBet() {
		int maxBet = 0;

		if (player.getBet() > maxBet) {
			maxBet = player.getBet();
		}
		if (computer.getBet() > maxBet) {
			maxBet = computer.getBet();
		}
		return maxBet;
	}

	public boolean playerHasBet(Player player, int maxBet) {
		if (player.getBet() < maxBet) {
			return false;
		}
		return true;
	}

	public int getCurrentPot() {
		int pot = 0;

		pot += player.getBet();
		pot += computer.getBet();

		return pot;
	}

	public int askPlayerForBet(Player player) {
		Scanner in = new Scanner(System.in);
		System.out.println("How much would you like to bet?");
		return in.nextInt();
	}

	public void doRoundWinner() {
		if (winner == player) {
			player.setMoney(player.getMoney() + this.getCurrentPot());
		} else if (winner == computer) {
			computer.setMoney(computer.getMoney() + this.getCurrentPot());
		} else {
			player.setMoney(player.getMoney() + player.getBet());
			computer.setMoney(computer.getMoney() + computer.getBet());
		}
		System.out.println(winner.getName() + " wins the hand and the pot of " + this.getCurrentPot() + "!");
		System.out.println("");
		winner = null;

	}

	public boolean checkForWinner() {
		if (player.getMoney() == 0) {
			System.out.println("The computer wins the game!");
			return true;
		} else if (computer.getMoney() == 0) {
			System.out.println("You win the game!");
			return true;
		}
		return false;
	}
}
=======
package poker;

import java.util.ArrayList;

public class Game {

	public int smallBlind = 5;
	public int startingMoney = 100;

	Player player = new Player("Player", startingMoney);
	Player computer = new Player("Computer", startingMoney);
	ArrayList<Player> players = new ArrayList<Player>();

	boolean isPlayerTurn = true;
	ArrayList<Card> communityCards = new ArrayList<Card>();

	Deck deck = new Deck();

	public Game(int smallBlind, int startingMoney) {
		this.smallBlind = smallBlind;
		this.startingMoney = startingMoney;
	}

	public void startGame() {
		players.add(player);
		players.add(computer);
		deck.shuffle();
		playRound();
	}

	public int getSmallBlind() {
		return smallBlind;
	}

	public void dealPlayerCards(Player player, Player computer) {
		player.receiveCard(deck.deal());
		computer.receiveCard(deck.deal());
		player.receiveCard(deck.deal());
		computer.receiveCard(deck.deal());
	}

	public void playRound() {
		playPreflop();
		playFlop();
		playTurn();
	}

	public void playPreflop() {
		dealPlayerCards(player, computer);
		playBettingRound();
	}

	public void playFlop() {
		dealFlop();
		playBettingRound();
	}

	public void dealFlop() {
		deck.deal();
		deck.deal();
		deck.deal();
		communityCards.add(deck.deal());
		communityCards.add(deck.deal());
		communityCards.add(deck.deal());
	}

	public void playTurn() {
		dealTurn();
		playBettingRound();
	}

	public void dealTurn() {
		deck.deal();
		communityCards.add(deck.deal());
	}

	public void playRiver() {
		dealRiver();
		playBettingRound();
	}

	public void dealRiver() {
		deck.deal();
		communityCards.add(deck.deal());
	}

	public void playBettingRound() {
		Player playerOnTurn;
		int maxBet = getMaxBet();
		if(isPlayerTurn) {
			if(playerHasBet(player, maxBet)) {
				switch(player.hasBetAction()) {
				case CHECK:
					break;
				case RAISE:
					int raiseAmount = askPlayerForBet();
					maxBet += raiseAmount;
					break;
				}
			}
			else {
				switch(player.callRaiseFold()) {
				case FOLD:
					break;
				case CALL:
					break;
				case RAISE:
					break;
				
				}
			}
		}
		else {
			computerBettingRound();
		}
	}

	public int getMaxBet() {
		int maxBet = 0;

		if (player.getBet() > maxBet) {
			maxBet = player.getBet();
		}
		else if(computer.getBet() > maxBet) {
			maxBet = computer.getBet();
		}
		return maxBet;
	}
	
	public boolean playerHasBet(Player player, int maxBet) {
		if(player.getBet() < maxBet) {
			return false;
		}
		return true;
	}

	public int getCurrentPot() {
        int pot = 0;
        
        for (Player player : players) {
            pot += player.getBet();
        }
      
        return pot;
   }

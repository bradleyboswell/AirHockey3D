package model;

public class ScoreBoard{
	int player1score;
	int player2score;
	
	public ScoreBoard(){
		this.player1score = 0;
		this.player2score = 0;
	}
	
	public int getPlayer1score() {
		return player1score;
	}

	public void setPlayer1score(int player1score) {
		this.player1score = player1score;
	}

	public int getPlayer2score() {
		return player2score;
	}

	public void setPlayer2score(int player2score) {
		this.player2score = player2score;
	}


}
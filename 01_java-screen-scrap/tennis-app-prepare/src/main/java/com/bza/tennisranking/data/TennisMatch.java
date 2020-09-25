package com.bza.tennisranking.data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "tennismatch")
public class TennisMatch {
	private static AtomicInteger count = new AtomicInteger(0);
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String event;
	
	@NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdatedAt = new Date();
	//@Column(columnDefinition = "DATETIME", nullable = true)
	private int tmpId;
	private Date date;
	private String score;
	private int player1Id;
	private int player2Id;
	private String player1Name;
	private String player2Name;
	private String victory;
	private String compValue1;
	private String ranking1;
	private String compValue2;
	private String ranking2;
	private String url;
	private String fromFile;
	// bonus staff for the winner
	private int gamesWon;
	private int gamesLost;
	private int setsWon;
	private int setsLost;
	public String getEvent() {return event;}
	public void setEvent(String event) {this.event = event;}
	public Date getDate() { return date;}
	public void setDate(Date date) {this.date = date;}
	public String getScore() { return score;}
	public void setScore(String score) { this.score = score;}
	public String getUrl() { return url;}
	public void setUrl(String url) {this.url = url;}
	public int getPlayer1Id() { return player1Id;}
	public void setPlayer1Id(int player1Id) { this.player1Id = player1Id;}
	public int getPlayer2Id() {return player2Id;}
	public void setPlayer2Id(int player2Id) {this.player2Id = player2Id;}
	public String getPlayer1Name() {return player1Name;}
	public void setPlayer1Name(String player1Name) {this.player1Name = player1Name;}
	public String getPlayer2Name() {return player2Name;}
	public void setPlayer2Name(String player2Name) {this.player2Name = player2Name;}
	public String getVictory() {return victory;}
	public void setVictory(String victory) {this.victory = victory;}
	public String getRanking2() {return ranking2;}
	public void setRanking2(String ranking2) {this.ranking2 = ranking2;}
	public String getCompValue2() {return compValue2;}
	public void setCompValue2(String CompValue2) {this.compValue2 = CompValue2;}
	public String getCompValue1() {return compValue1;}
	public void setCompValue1(String CompValue1) {this.compValue1 = CompValue1;}
	public String getRanking1() {return ranking1;}
	public void setRanking1(String ranking1) { this.ranking1 = ranking1;}
	public int getGamesWon() {
		return gamesWon;
	}
	public void setGamesWon(int gamesWon) {
		this.gamesWon = gamesWon;
	}
	public int getGamesLost() {
		return gamesLost;
	}
	public void setGamesLost(int gamesLost) {
		this.gamesLost = gamesLost;
	}
	public int getSetsWon() {
		return setsWon;
	}
	public void setSetsWon(int setsWon) {
		this.setsWon = setsWon;
	}
	public int getSetsLost() {
		return setsLost;
	}
	public void setSetsLost(int setsLost) {
		this.setsLost = setsLost;
	}
	public String getFromFile() {
		return fromFile;
	}
	public void setFromFile(String fromFile) {
		this.fromFile = fromFile;
	}


	public TennisMatch(String event, Date date, String score, int player1Id, String url ) {
		this.event = event;
		this.date = date;
		this.score = score;
		this.player1Id = player1Id;
		this.url = url;
	}
	
	public TennisMatch(Date date, String score, int player1Id, int player2Id ) {
		this.date = date;
		this.score = score;
		this.player1Id = player1Id;
		this.player2Id = player2Id;
	}
	
	public TennisMatch(){
		tmpId = count.incrementAndGet(); 
	}

	public String toString2() {
		DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		return "id: " + tmpId + " " + formatter.format(date) + " : " + " " + score + " " + player1Id + " " + player2Id + " " +  player1Name + 
			" " + player2Name + " " + " "  + ranking1 + " " + ranking2 + " " + " SW: " + setsWon + " SL: " + setsLost + " GW: " + gamesWon + " GL: " + gamesLost;
	}
	
	public String toString() {
		DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		return "id: " + tmpId + " " + formatter.format(date) + " : " + " " + score + " " + player1Id + " " + player2Id + ", " +  player1Name + 
			", " + player2Name + " " + " "  + ranking1 + " " + ranking2 + " " + " CV1: " + compValue1 + " CV2: " + compValue2 + ", V: " +victory;
	}
	
	public int getTmpId() {
		return tmpId;
	}
	public void setTmpId(int tmpId) {
		this.tmpId = tmpId;
	}
	
	public boolean checkMatch() {
		boolean ok = false;
		return ok;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TennisMatch) {
			TennisMatch temp = (TennisMatch) obj;
			if ((this.player1Id == temp.player1Id) && (this.player2Id == temp.player2Id)  && 
			(this.date.equals(temp.date)))
				return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
	    return this.date.hashCode() + this.player1Id + this.player2Id;        
	}

}

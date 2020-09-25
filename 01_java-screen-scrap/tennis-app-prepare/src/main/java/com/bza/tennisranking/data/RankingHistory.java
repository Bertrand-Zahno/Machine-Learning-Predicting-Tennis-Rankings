package com.bza.tennisranking.data;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;


// I first tried to model player to matches as a one to many relationship in order to avoid the problems
// with a many to many relationship. As anyway I will save the matches not at the same time as the players
// and for some other reasons, I decided to remove the (entity) relation between player and matches
// so there is no more @OneToMany Relation between the entities (but which perfectly works when you have
// a real 1:n relation. 

@Entity
@Table(name = "rankingHistory")
public class RankingHistory {

	public int getSwisstennisId() {
		return swisstennisId;
	}
	public void setSwisstennisId(int swisstennisId) {
		this.swisstennisId = swisstennisId;
	}


	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	//@ManyToOne(fetch = FetchType.EAGER)
	@ManyToOne(fetch = FetchType.LAZY)	
    @JoinColumn(name = "player_id")
	
    private Player player;
	
	@NotNull
	private int rankingNumber;
	private String ranking;
	
	private String gradingValue;
	private String compValue;
	private String periode;
	
	@NotNull
	private int swisstennisId;
	
	@NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdatedAt = new Date();
	
	public int getRankingNumber() {return rankingNumber;}
    public void setRankingNumber(int rankingNumber) {this.rankingNumber = rankingNumber;}
	public String getRanking() { return ranking;}
	public void setRanking(String ranking) { this.ranking = ranking;}
	public String getGradingValue() {return gradingValue;}
	public String getCompValue() {return compValue;}
	public void setCompValue(String compValue) {this.compValue = compValue;}
	public void setGradingValue(String gradingValue) {this.gradingValue = gradingValue; }

	public RankingHistory(Player player, int rankingNumber, 
			String ranking, String gradingValue, String compValue, String periode) {
		this.player = player;
		this.rankingNumber = rankingNumber;
		this.ranking = ranking;
		this.gradingValue = gradingValue;
		this.compValue = compValue;
		this.periode = periode;
		this.swisstennisId = player.getSwisstennisId();
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public RankingHistory() {
		
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RankingHistory) {
			RankingHistory temp = (RankingHistory) obj;
			if (this.getCompValue().equals(temp.getCompValue()))  {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		return id +  rankingNumber +
				" " + ranking + " " + gradingValue + " " + compValue + "SwtId:  " + swisstennisId;
	}
}

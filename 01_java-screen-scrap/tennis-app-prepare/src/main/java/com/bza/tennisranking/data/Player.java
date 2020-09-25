package com.bza.tennisranking.data;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

/*
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
*/


@Entity
@Table(name = "player")
public class Player {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	@NotNull
	private String firstName;
	@NotNull
	private String lastName;
	@NotNull
	private String licenceNumber;
	@NotNull
	private int swisstennisId;
	@NotNull
	private int rankingNumber;
	private String ranking;
	
	private String gradingValue;
	private String compValue;
	private String memberShip;
	private String currentPeriode;
	private String previousRanking;
	private String previousRankingNumber;
	private String licenceStatus;
	private String ageCategory;
	
	@NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdatedAt = new Date();
	

	//@OneToMany(mappedBy = "player", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@OneToMany(mappedBy = "player", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<RankingHistory> rankingHistory;
	
	public Set<RankingHistory> getRankingHistory() {
		return rankingHistory;
	}
	
	@Transient
	private Set<TennisMatch> matches = new HashSet<TennisMatch>();
	
	public Set<TennisMatch> getMatches() { return matches; }
	public int getSwisstennisId() { return swisstennisId; }
	public int getRankingNumber() {return rankingNumber;}
    public void setRankingNumber(int rankingNumber) {this.rankingNumber = rankingNumber;}
	public String getRanking() { return ranking;}
	public void setRanking(String ranking) { this.ranking = ranking;}
	public String getFirstName() {return firstName;}
	public void setFirstName(String firstName) {this.firstName = firstName;}
	public String getLastName() {return lastName;}
	public void setLastName(String lastName) {this.lastName = lastName;}
	public String getGradingValue() {return gradingValue;}
	public String getCompValue() {return compValue;}
	public void setCompValue(String compValue) {this.compValue = compValue;}
	public void setGradingValue(String gradingValue) {this.gradingValue = gradingValue; }
	public String getCurrentPeriode() {return currentPeriode; }
	public void setCurrentPeriode(String currentPeriode) {this.currentPeriode = currentPeriode; }
	public String getPreviousRanking() {return previousRanking; }
	public void setPreviousRanking(String previousRanking) {this.previousRanking = previousRanking; }
	public String getPreviousRankingNumber() {return previousRankingNumber; }
	public void setPreviousRankingNumber(String previousRankingNumber) {this.previousRankingNumber = previousRankingNumber; }
	public String getLicenceStatus() {return licenceStatus; }
	public void setLicenceStatus(String licenceStatus) {this.licenceStatus = licenceStatus; }
	public String getAgeCategory() {return ageCategory; }
	public void setAgeCategory(String ageCategory) {this.ageCategory = ageCategory; }
	

	public Player(String firstName, String lastName, String licenceNumber, int swisstennisId, int rankingNumber, 
			String ranking, String gradingValue, String compValue, String memberShip, String currentPeriode,
			String previousRanking, String previousRankingNumber, String licenceStatus, String ageCategory) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.licenceNumber = licenceNumber;
		this.swisstennisId = swisstennisId;
		this.rankingNumber = rankingNumber;
		this.ranking = ranking;
		this.gradingValue = gradingValue;
		this.compValue = compValue;
		this.memberShip = memberShip;
		this.currentPeriode = currentPeriode;
		this.previousRanking = previousRanking;
		this.previousRankingNumber = previousRankingNumber;
		this.licenceStatus = licenceStatus;
		this.ageCategory = ageCategory;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Player() {
		
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Player) {
			Player temp = (Player) obj;
			if (this.getSwisstennisId() == temp.getSwisstennisId())  {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
	    return this.swisstennisId;    
	}
	
	public String toString() {
		return "swt_id: " + swisstennisId + " Name: " + firstName + " " + lastName + " Rank: " 
				+ ranking + " Nbr: " + rankingNumber + " CV current: " + compValue + " GV: " 
				+ gradingValue + " Rank prev: " + previousRanking + " Nbr prev: " + previousRankingNumber 
				+ " LicStatus: " + licenceStatus + " Cat: " + ageCategory;
	}
	
}

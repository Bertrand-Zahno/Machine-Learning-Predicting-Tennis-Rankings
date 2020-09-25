package com.bza.tennisranking.data;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;


// All the players which were loaded from the Swisstennis side are listed here


@Entity
@Table(name = "loadedplayer")
public class LoadedPlayer {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	@NotNull
	@Column(unique=true)
	private int swisstennisId;
	@NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdatedAt = new Date();
	
	public int getSwisstennisId() {
		return swisstennisId;
	}

	public void setSwisstennisId(int swisstennisId) {
		this.swisstennisId = swisstennisId;
	}

	public LoadedPlayer() {
		
	}
	public LoadedPlayer(int swisstennisId) {
		this.swisstennisId = swisstennisId;
	}
	
	@Override
	public String toString() {
		return String.valueOf(swisstennisId) + " ";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LoadedPlayer) {
			LoadedPlayer temp = (LoadedPlayer) obj;
			if (this.swisstennisId == temp.swisstennisId)
				return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
	    return this.swisstennisId;        
	}
	
	
	
}

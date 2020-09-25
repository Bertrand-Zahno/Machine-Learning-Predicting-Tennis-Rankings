package com.bza.tennisranking.data;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "calculationrun")
public class CalculationRun {
	public int getRunId() {
		return runId;
	}
	public void setRunId(int runId) {
		this.runId = runId;
	}
	public String getCurrentCompValue() {
		return currentCompValue;
	}
	public void setCurrentCompValue(String currentCompValue) {
		this.currentCompValue = currentCompValue;
	}
	public String getCurrentRiskValue() {
		return currentRiskValue;
	}
	public void setCurrentRiskValue(String currentRiskValue) {
		this.currentRiskValue = currentRiskValue;
	}
	public String getCalculatedCompValue() {
		return calculatedCompValue;
	}
	public void setCalculatedCompValue(String calculatedCompValue) {
		this.calculatedCompValue = calculatedCompValue;
	}
	public String getCalculatedRiskValue() {
		return calculatedRiskValue;
	}
	public void setCalculatedRiskValue(String calculatedRiskValue) {
		this.calculatedRiskValue = calculatedRiskValue;
	}
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private int runId;
	@NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdatedAt = new Date();
	private int swisstennisId;
	@NotNull
	private String firstName;
	@NotNull
	private String lastName;
	@Override
	public String toString() {
		return "CalculationRun [runId=" + runId + ", swisstennisId=" + swisstennisId + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", currentCompValue=" + currentCompValue + ", currentRiskValue="
				+ currentRiskValue + ", calculatedCompValue=" + calculatedCompValue + ", calculatedRiskValue="
				+ calculatedRiskValue + "]";
	}
	public int getSwisstennisId() {
		return swisstennisId;
	}
	public void setSwisstennisId(int swisstennisId) {
		this.swisstennisId = swisstennisId;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	private String currentCompValue;
	private String currentRiskValue;
	private String calculatedCompValue;
	private String calculatedRiskValue;

}

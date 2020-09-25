package com.bza.tennisranking.util;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.bza.tennisranking.data.TennisMatch;
import com.bza.tennisranking.repository.TennisMatchRepository;

public class TennisMatchUtil {
	
	// stores all the matches in the Set
	// can later be used when we have different periodes
	public static void storeMatches(TennisMatchRepository matchRepository, Set<TennisMatch> matches) {
		matches = TennisMatchUtil.resizeMatches(matches);
		Date maxDate = matches.stream().map(m -> m.getDate()).max(Date::compareTo).get();
    	Date minDate = matches.stream().map(m -> m.getDate()).min(Date::compareTo).get();
    	Set<TennisMatch> storedMatches = matchRepository.findByDateBetween(minDate, maxDate);
    	storedMatches.addAll(matches);
    	matchRepository.save(storedMatches);
	}
	
	
	// stores all the matches in the Set
	public static void storeMatches2(TennisMatchRepository matchRepository, Set<TennisMatch> matches) {
		System.out.println("Number of matches to store:  " + matches.size());
    	matchRepository.save(matches);
	}
	
	// adjust the matches for the period, set competition value to all matches
	// the foreign matches have all id's from -1, -2, -3 ... so do not touch those ones
	public static Set<TennisMatch> adjustGradingValues(Set<TennisMatch> matches) {
		Map<Integer, String> gradingMap = new HashMap<>();
		
		matches.stream().forEach(match -> {
			if ((match.getCompValue1() != null) && (match.getPlayer1Id() > 0)) { // no foreign player
				gradingMap.put(match.getPlayer1Id() , match.getCompValue1());
			}
			if ((match.getCompValue2() != null) && (match.getPlayer2Id() > 0)) {
				gradingMap.put(match.getPlayer2Id() , match.getCompValue2());
			}
			
		});
		
		matches.forEach(match -> {
			String compValueP1 = gradingMap.get(match.getPlayer1Id());
			if (compValueP1 !=null) { match.setCompValue1(compValueP1);}
			
			String compValueP2 = gradingMap.get(match.getPlayer2Id());
			if (compValueP2 !=null) { match.setCompValue2(compValueP2);}
		});	
		return matches;
	}
	
	
	
	
	// for each Tennismatch new order of the order, so that player1, ranking1, .... etc is always the 
	// winning player. Player2 is the loosing player
	public static Set<TennisMatch> resizeMatches(Set<TennisMatch> matches) {
		Set<TennisMatch> resizedMatches = 
				matches.stream().map(item -> resizeMatch(item)).collect(Collectors.toSet()); 
		return resizedMatches;
	}
	
	public static TennisMatch resizeMatch(TennisMatch match) {
		String victory = match.getVictory();
		
		if (victory.equals("N") || (victory.equals("Z"))) {
			int player1IdTmp = match.getPlayer1Id();
			String player1NameTmp = match.getPlayer1Name();
			String gradingValue1Tmp = match.getCompValue1();
			String ranking1Tmp = match.getRanking1();
			
			match.setPlayer1Id(match.getPlayer2Id());
			match.setPlayer1Name(match.getPlayer2Name());
			match.setCompValue1(match.getCompValue2());
			match.setRanking1(match.getRanking2());
			
			match.setPlayer2Id(player1IdTmp);
			match.setPlayer2Name(player1NameTmp);
			match.setCompValue2(gradingValue1Tmp);
			match.setRanking2(ranking1Tmp);
			
			if (victory.equals("N")) {match.setVictory("S");}
			else {match.setVictory("W");}
			// an exception can happen here
			try {
				 match.setScore(reverseScore(match.getScore()));
			} catch (StringIndexOutOfBoundsException e) {
					System.out.println("StringIndexOutOfBoundsException: " + match.getScore() + " match:" + match);
			}	
		}
		match.setGamesWon(getGamesWon(match.getScore()));
		match.setGamesLost(getGamesLost(match.getScore()));
		match.setSetsWon(getSetsWon(match.getScore()));
		match.setSetsLost(getSetsLost(match.getScore()));
		return match;
	}
	// the score is written for the winner of the match
	public static String reverseScore(String score) {
		if ((score != null ) && (score.length() > 0)) {
			String[] sets = score.split(" ");
			String reverse = "";
			for (String s : sets) {
				char c1 = s.charAt(0);
				char c2 = s.charAt(2);
				reverse = reverse + c2 + ":" + c1 + " ";
			}
			return reverse;
		}
		return "";
	}
	
	public static int getGamesWon(String score) {
		int gamesWon = 0;
		if ((score != null ) && (score.length() > 0))  {
			//System.out.println("score: " + score);
			String[] sets = score.split(" ");
			for (String s : sets) {
				gamesWon = gamesWon + Character.getNumericValue(s.charAt(0));
			}
		}
		return gamesWon;
	}
	
	public static int getGamesLost(String score) {
		int gamesLost = 0;
			if ((score != null ) && (score.length() > 0)) {
			String[] sets = score.split(" ");
			for (String s : sets) {
				gamesLost = gamesLost + Character.getNumericValue(s.charAt(2));
			}
		}
		return gamesLost;
	}
	
	// implement it right with all the exceptions
	public static int getSetsWon(String score) {
		int setsWon = 0;
		if ((score != null ) && (score.length() > 0))  {
			setsWon = 2;	
		}
		return setsWon;
	}

 static int getSetsLost(String score) {
		int setsLost = 0;
		if ((score != null ) && (score.length() > 0))  {
			//System.out.println("score: " + score);
			String[] sets = score.split(" ");
			setsLost = sets.length - 2;	
			if (setsLost < 0) setsLost = 0;
		}
		return setsLost;
	}
	
	
}

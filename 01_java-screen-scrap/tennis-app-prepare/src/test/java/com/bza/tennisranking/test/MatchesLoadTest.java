package com.bza.tennisranking.test;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.bza.tennisranking.data.Player;
import com.bza.tennisranking.data.RankingHistory;
import com.bza.tennisranking.data.TennisMatch;
import com.bza.tennisranking.httpClient.SwtApplicationClient;
import com.bza.tennisranking.repository.PlayerRepository;
import com.bza.tennisranking.repository.RankingHistoryRepository;
import com.bza.tennisranking.repository.TennisMatchRepository;
import com.bza.tennisranking.util.PlayersUtil;
import com.bza.tennisranking.util.TennisMatchUtil;
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class MatchesLoadTest {
	@Autowired
	private TennisMatchRepository matchRepository;
    @Autowired
    private RankingHistoryRepository rankingHistoryRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private SwtApplicationClient applicationClient;

   
 
    
    // setup one player on two periods
    @Before
    public void setUp() throws Exception {
    	rankingHistoryRepository.deleteAll();
    	playerRepository.deleteAll();
    	String directory = applicationClient.getDirectory();
    	Set<Player> players = applicationClient.readAllPlayersFromLocal(directory);
    	PlayersUtil.storePlayers(playerRepository, rankingHistoryRepository, players);	
    	
    	String directory2 = applicationClient.getDirectory2();
    	System.out.println("dir2: " + directory2);
    	players = applicationClient.readAllPlayersFromLocal(directory2);
    	PlayersUtil.storePlayers(playerRepository, rankingHistoryRepository, players);	
    	
    }
    
    // stores the matches from a local directory
    @Test
    public void loadMatchesFromLocalTest() throws Exception {
    	matchRepository.deleteAll();
    	String directory = applicationClient.getDirectory();
    	Set<TennisMatch> matches = applicationClient.readAllMatchesFromLocal(directory);
    	System.out.println("Number of matches read from:  " + directory + ": "+ matches.size());
    	TennisMatchUtil.storeMatches(matchRepository, matches);
    	
    	String directory2 = applicationClient.getDirectory2();
    	matches = applicationClient.readAllMatchesFromLocal(directory2);
    	System.out.println("Number of matches read from:  " + directory2 + ": "+ matches.size());
    	TennisMatchUtil.storeMatches(matchRepository, matches);
    }
    
    @Test
    public void listMatchesTest() throws ParseException {
    	DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
		Date startDate = formatter.parse("01.04.2017");
		Date endDate = formatter.parse("31.03.2018");
		
		Set<TennisMatch> matches = matchRepository.findByDateBetween(startDate, endDate);
		List<TennisMatch> listMatches = new ArrayList<TennisMatch>(matches);
		listMatches.sort((m1,m2) -> m1.getDate().compareTo(m2.getDate()));
		listMatches.forEach(System.out::println);
    }
    
    @Test
    public void adjustMatchesWithPlayers() throws ParseException {
    	DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
		//Date startDate = formatter.parse("01.04.2017");
		//Date endDate = formatter.parse("31.03.2018");
		
		Date startDate = formatter.parse("08.07.2017");
		Date endDate = formatter.parse("17.07.2017");
		
		Set<TennisMatch> matches = matchRepository.findByDateBetween(startDate, endDate);
		List<TennisMatch> listMatches = new ArrayList<TennisMatch>(matches);
		listMatches.sort((m1,m2) -> m1.getDate().compareTo(m2.getDate()));
		System.out.println("number of matches: " + listMatches.size());
		listMatches.forEach(System.out::println);
		
		List<RankingHistory> rankingHistory = rankingHistoryRepository.findByPeriode("2/2017");
		rankingHistory.forEach(System.out::println);
		
		// Map<Integer, String> gradingMap = rankingHistory.stream().collect(Collectors.toMap(RankingHistory::getSwisstennisId, 
		//		RankingHistory::getCompValue));
		// bza Attention; has to be adjusted with gradingMap as argument for the next method
		
		matches = TennisMatchUtil.adjustGradingValues(matches);
		
		
		// test some filtering
		matches = matches.stream()
					.filter(m -> (m.getDate().after(startDate) && m.getDate().before(endDate)))
					.collect(Collectors.toSet());
		
		
		System.out.println("number of matches: " + matches.size());
		matches.forEach(System.out::println);
    }
    
    
    
   
}
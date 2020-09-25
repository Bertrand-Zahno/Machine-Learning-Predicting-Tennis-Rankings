package com.bza.tennisranking.test;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.bza.tennisranking.data.Player;
import com.bza.tennisranking.data.TennisMatch;
import com.bza.tennisranking.httpClient.SwtApplicationClient;
import com.bza.tennisranking.repository.PlayerRepository;
import com.bza.tennisranking.repository.RankingHistoryRepository;
import com.bza.tennisranking.repository.TennisMatchRepository;
import com.bza.tennisranking.util.PlayersUtil;
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")

public class PlayersUtilTest {

    @Autowired
    private PlayerRepository playerRepository;
    
    @Autowired
    private TennisMatchRepository matchRepository;
    
    @Autowired
    SwtApplicationClient applicationClient;
    
    @Autowired
    private RankingHistoryRepository rankingHistoryRepository;
    
    private Map<Integer, String> playersCompValue;
    
    @Before
    // reads the player thomas flury and his matches for two periods
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
    	playersCompValue = new HashMap<>();
    	
    	playersCompValue.put(54318, "10.489");
    	playersCompValue.put(22607, "5.272");
    	playersCompValue.put(7413, "3.800");
    	playersCompValue.put(11107, "12.256");
    	playersCompValue.put(12178, "7.494");
    	playersCompValue.put(5619, "11.744");
    	playersCompValue.put(135008, "9.199");
    	playersCompValue.put(19211, "7.973");
    	playersCompValue.put(5056, "7.910");
    	playersCompValue.put(5731, "7.293");
    	playersCompValue.put(29453, "5.288");
    	playersCompValue.put(25651, "3.576");
    	playersCompValue.put(19334, "7.883");  	
    	playersCompValue.put(16414, "8.288");
    	playersCompValue.put(6021, 	"7.939");
    	playersCompValue.put(13722, "9.830");
    	// Streichresultate
    	playersCompValue.put(17367, "8.961");
    	playersCompValue.put(9949, "10.128");
    	playersCompValue.put(8661, "9.771");
    }
    
    @Test
    public void calculateIt() throws ParseException {
    	// String periode = "1/2018";
    	DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
		Date startDate = formatter.parse("01.04.2017");
		Date endDate = formatter.parse("31.03.2018");
		// String previousPeriode = PlayersUtil.previousPeriode(periode);
		
		// find thomas flury
		// Map<Integer, RankingHistory > playersMap = rankingHistoryRepository.findByPeriode(previousPeriode).stream().collect(Collectors.toMap(RankingHistory::getSwisstennisId, e -> e));
		
		// read all matches within this periode
		Set<TennisMatch> matches = matchRepository.findByDateBetween(startDate, endDate);
		String interpolValue = "8.088";
		System.out.println("Neuer Wert: " + PlayersUtil.getNewCompValue(13482, playersCompValue, matches, interpolValue));
    }
    
    
    @Test
    // calculates the first run for the players in the table
	public void testCalculateFirstRun() throws ParseException {
    	DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
    	Date startDate = formatter.parse("01.05.2017");
		Date endDate = formatter.parse("10.01.2018");
    	System.out.println("Start");
		Set<Player> players = (Set<Player>) playerRepository.findAll();
		System.out.println("Number of players loaded: " + players.size());
		Set<TennisMatch> matches = matchRepository.findByDateBetween(startDate, endDate);
		//Set<TennisMatch> matches = matchRepository.findAll();
		System.out.println("Number of matches loaded: " + matches.size());
		long t1 = System.currentTimeMillis();
		players.forEach(p -> {
			System.out.print(p.getFirstName() + " " + p.getLastName());
			System.out.print("Alter Wert: " + p.getCompValue() + " ");
			//System.out.println("Neue Werte: " + PlayersUtil.getNewRankingValues(p.getSwisstennisId(), players, matches));
		});
		long t2 = System.currentTimeMillis();
		System.out.println("Zeit: " + (t2- t1));
    }
    
    
    
}
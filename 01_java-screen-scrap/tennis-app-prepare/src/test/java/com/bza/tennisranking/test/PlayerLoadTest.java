package com.bza.tennisranking.test;


import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

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
import com.bza.tennisranking.repository.PlayerRepository;
import com.bza.tennisranking.repository.RankingHistoryRepository;
import com.bza.tennisranking.repository.TennisMatchRepository;
import com.bza.tennisranking.util.PlayersUtil;
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class PlayerLoadTest {
	@Autowired
	private TennisMatchRepository matchRepository;
    @Autowired
    private RankingHistoryRepository rankingHistoryRepository;
    @Autowired
    private PlayerRepository playerRepository;

    final private int swtidA= 13210;
    final private int swtidB= 6719;
    
    
    @Before
    public void setUp() throws Exception {
    	rankingHistoryRepository.deleteAll();
    	playerRepository.deleteAll();
    	
    	Player p1 = new Player("SpielerA", "VornameA", "133.86.123.99" , swtidA, 149, "N1", "15.1", "10.0", "Old Boys", "1/2018", "x", "x", "x", "x"); 
    	Player p2 = new Player("SpielerB", "VornameB", "100.86.123.00" , swtidB, 6449, "R6", "4.1", "3.1", "Laussanne", "1/2018", "x", "x", "x", "x");
    	Set<Player> players = new HashSet<Player>();
    	players.add(p1); players.add(p2);
    	PlayersUtil.storePlayers(playerRepository, rankingHistoryRepository, players);
    	System.out.println("xxxxxxxxxxxxxxxxxxxxxxx");
    }
    
    @Test
    // store a new player
    public void newPlayer() {
    	Player p1 = new Player("SpielerB", "Stan", "133.86.123.00" , swtidB, 6449, "R6", "4.1", "3.1", "Laussanne", "1/2018", "x", "x", "x", "x"); 
    	Set<Player> players = new HashSet<Player>();
    	players.add(p1);
    	PlayersUtil.storePlayers(playerRepository, rankingHistoryRepository, players);
    }
    
    @Test
    public void updatePlayer3() {
    	Player p2 = new Player("SpielerA", "VornameA", "133.86.123.99" , swtidA, 149, "N1", "15.1", "10.0", "Old Boys", "2/2018", "x", "x", "x", "x"); 
    	Player p1 = new Player("SpielerB", "VornameB", "100.86.123.00" , swtidB, 6449, "R6", "4.1", "3.1", "Laussanne", "1/2018", "x", "x", "x", "x");
    	Set<Player> players = new HashSet<Player>();
    	players.add(p1);
    	players.add(p2);
    	PlayersUtil.storePlayers(playerRepository, rankingHistoryRepository, players);
    }
    
    
    
    @Test
    //@Transactional
    public void findAll() {
    	//rankingHistoryRepository.findAll().forEach(System.out::println);
    	//playerRepository.deleteAll();
    	Set<Player> players = playerRepository.findAll();
    	players.stream().forEach(System.out::println);
    	System.out.println("size: " + players.size());
    	Map<Integer, Player > playersMap = players.stream().collect(Collectors.toMap(Player::getSwisstennisId, e -> e));
    	System.out.println("Spieler mit id: " + 23741);
    	System.out.println(playersMap.get(23741));
    }
    
    @Test
    //@Transactional
    public void testAll() {
    	rankingHistoryRepository.findAll().forEach(System.out::println);
    	//playerRepository.deleteAll();
    	Set<Player> players = playerRepository.findAll();
    	players.stream().forEach(System.out::println);
    	playerRepository.save(players);
    }
    
    @Test
    @Transactional
    public void findOne() {
    	Player p1 = playerRepository.findBySwisstennisId(23741);
    	Set<RankingHistory> rHistory = p1.getRankingHistory();
    	rHistory.stream().forEach(System.out::println);
    }
    
  
    
    @Test
    // the small world sample with the 4 players in my excel sheet
    public void smallWorldSample() {
    	playerRepository.deleteAll();
    	Player p1 = new Player("S1", "VornameS1", "133.86.123.00" , 1, 1, "N1", "15.1", "10.0", "Old Boys", "2/2017", "x", "x", "x", "x" );
    	Player p2 = new Player("S2", "VornameS2", "193.91.231.00" , 2, 2, "N1", "15.0", "9.0", "Stade Lausanne", "2/2017", "x", "x", "x", "x"  );
    	Player p3 = new Player("S3", "VornameS3", "193.91.231.00" , 3, 2, "N1", "15.0", "8.0", "Stade Lausanne", "2/2017", "x", "x", "x", "x"  );
    	Player p4 = new Player("S4", "VornameS4", "193.91.231.00" , 4, 2, "N1", "15.0", "7.0", "Stade Lausanne", "2/2017", "x", "x", "x", "x"  );
    	Set<Player> players = new HashSet<Player>();
    	players.add(p1); players.add(p2);players.add(p3); players.add(p4);
    	playerRepository.save(players);
    	
    	Date now = new Date();
    	TennisMatch m1 = new TennisMatch(now, "6:3 6:4", 1,2);
    	TennisMatch m2 = new TennisMatch(now, "6:3 6:4", 1,3);
    	TennisMatch m3 = new TennisMatch(now,"6:3 6:4", 1,4);
    	TennisMatch m4 = new TennisMatch(now,"6:3 6:4",2,3);
    	TennisMatch m5 = new TennisMatch(now,"6:3 6:4",3,4);
    	TennisMatch m6 = new TennisMatch(now,"6:3 6:4",2,4);
    	matchRepository.deleteAll();
    	matchRepository.save(m1); matchRepository.save(m2);matchRepository.save(m3);
    	matchRepository.save(m4); matchRepository.save(m5);matchRepository.save(m6);
    }
    
}
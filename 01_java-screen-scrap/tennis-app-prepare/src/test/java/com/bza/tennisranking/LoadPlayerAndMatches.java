package com.bza.tennisranking;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.bza.tennisranking.data.LoadedPlayer;
import com.bza.tennisranking.data.Player;
import com.bza.tennisranking.data.RankingHistory;
import com.bza.tennisranking.data.TennisMatch;
import com.bza.tennisranking.httpClient.SwtApplicationClient;
import com.bza.tennisranking.repository.LoadedPlayerRepository;
import com.bza.tennisranking.repository.PlayerRepository;
import com.bza.tennisranking.repository.RankingHistoryRepository;
import com.bza.tennisranking.repository.TennisMatchRepository;
import com.bza.tennisranking.util.PlayersUtil;
import com.bza.tennisranking.util.TennisMatchUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("local")
public class LoadPlayerAndMatches {
	@Autowired
	private LoadedPlayerRepository loadedPlayerRepository;
	
	 @Autowired
	private RankingHistoryRepository rankingHistoryRepository;

	@Autowired
	private PlayerRepository playerRepository;

	@Autowired
	private TennisMatchRepository matchRepository;

	//@Autowired
	//private CalculationRunRepository crRepository;

	@Autowired
	private SwtApplicationClient applicationClient;
	
	@Test
	public void consistenceCheck() {
		Set<TennisMatch> matches = matchRepository.findAll();
		
		Map<Integer, List<TennisMatch>> matchById = matches
			    .stream()
			    .collect(Collectors.groupingBy(m -> m.getPlayer1Id()));
		
		matchById.forEach((id, match) -> {
			 Set<String> names =match.stream().map(m -> m.getPlayer1Name()).collect(Collectors.toSet());
			 if (names.size() > 1) {
				 System.out.println(id);
			 }
		});	 
	}
	
	// loads the table "loadedplayer" with all the entries from the local directory
	@Test
	public void loadTable() throws Exception {
		String directory = "C:/bza/misc/tennisapp/Oktober2019";
		long ts1 = System.currentTimeMillis();
		Set<Player> players = applicationClient.readAllPlayersFromLocal(directory);
		long ts2 = System.currentTimeMillis();
		System.out.format("readAllPlayersFromLocal: duration=%sms \n", ts2-ts1);
		
		Set<LoadedPlayer> loadedPlayers = players
				.stream()
				.map(player -> { LoadedPlayer lp = new LoadedPlayer(player.getSwisstennisId()); 
							 return lp;
								})
				.collect(Collectors.toSet());
		
		loadedPlayerRepository.save(loadedPlayers);
		System.out.println("all players in loadedplayers stored");
	}
	
	
	@Test
	// scans all document in the local directory and builds up the player table
	public void loadPlayersFromLocalDirectory() throws IOException, InterruptedException {
		String directory = "C:/bza/misc/tennisapp/Dezember2019";
		long ts1 = System.currentTimeMillis();
		Set<Player> players = applicationClient.readAllPlayersFromLocalParallel(directory);
	 
		long ts2 = System.currentTimeMillis();
		
		System.out.format("readAllPlayersFromLocal: duration=%sms \ns", ts2-ts1);
		
		PlayersUtil.storePlayers(playerRepository, rankingHistoryRepository, players);	
		System.out.println("All players in table players stored");
	}


	@Test
	// scans all document in the local directory and builds up the matches database table
	// this method is the right one
	// dec 2019: all the index out of bounds Exception are from w.o results after october, I don't know why right now
	// from Resultatblatt aktuell
	public void initializeMatchesFromLocalDirectory2() throws IOException, ParseException, InterruptedException {
    	DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
		Date startDate = formatter.parse("01.10.2018");
		Date endDate = formatter.parse("30.09.2019");
		
		String directory = "C:/bza/misc/tennisapp/Dezember2019";
		
		Set<TennisMatch> matches = applicationClient.readAllMatchesFromLocalParallel(directory);
		System.out.println("Number of matches read from directory:  " + matches.size());
		
		// just take the matches from the given periode
		/*
		matches = matches.stream()
				.filter(m -> ((m.getDate().after(startDate) || m.getDate().equals(startDate)) &&
						 (m.getDate().before(endDate)) || m.getDate().equals(endDate)))
				.collect(Collectors.toSet());

		*/
		matches = TennisMatchUtil.adjustGradingValues(matches);
		
		matches = TennisMatchUtil.resizeMatches(matches);
		
		System.out.println("Number of matches to store:  " + matches.size());
		
		TennisMatchUtil.storeMatches2(matchRepository, matches);
		System.out.println("All matches in table matches and loadedplayer stored");
	}
	
	

	@Test
	// scans all document in the local directory and builds up the matches database table
	// this method can probably be used later, when we have different periodes
	public void initializeMatchesFromLocalDirectory() throws IOException, ParseException {
    	DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
		Date startDate = formatter.parse("01.04.2017");
		Date endDate = formatter.parse("31.03.2018");
	
		
		String directory = "C:/bza/misc/tennisapp/Juli2018";
		
		Set<TennisMatch> matches = applicationClient.readAllMatchesFromLocal(directory);
		System.out.println("Number of matches read from directory:  " + matches.size());
		
		// to get the competition value for the previous periode
		List<RankingHistory> rankingHistory = rankingHistoryRepository.findByPeriode("2/2017");
		
		// put the gradings in the matches 
		matches = TennisMatchUtil.adjustGradingValues(matches);
		
		// just take the matches from the given periode
		matches = matches.stream()
				.filter(m -> (m.getDate().after(startDate) && m.getDate().before(endDate)))
				.collect(Collectors.toSet());
		
		TennisMatchUtil.storeMatches(matchRepository, matches);
		System.out.println("All matches in table matches and loadedplayer stored");
	}

	
	@Test
	// starts with the players in table LoadedPlayers and reversely lookups all
	// the other players
	// this is the main loading function 
	// old, could be reused for a first initial load
	public void testLoadPlayers() throws ParseException {
		String directory = applicationClient.getDirectory();
		String urlProp = applicationClient.getUrlTemplate();
		Set<LoadedPlayer> loadedPlayers = (Set<LoadedPlayer>) loadedPlayerRepository.findAll();

		Document document = null;
		Set<Integer> lookupPlayers = new HashSet<Integer>();

		for (LoadedPlayer loadP : loadedPlayers) {
			try {
				document = applicationClient
						.readHtmlDocumentFromLocal(directory + "/" + loadP.getSwisstennisId() + ".html");
				lookupPlayers.addAll(applicationClient.getOpponentsIdFromHtml(document, loadP.getSwisstennisId()));
			} catch (IOException e) {
				System.out.println(e.toString());
				System.out.println("Could not find file for player: " + loadP.getSwisstennisId());
			}
		}
		loadedPlayerRepository.save(loadedPlayers);

		// remove the already loaded player from this list
		lookupPlayers.removeAll(loadedPlayers.stream().map(lp -> lp.getSwisstennisId()).collect(Collectors.toList()));
		System.out.println("Number of Lookup Players: " + lookupPlayers.size());

		// get all the documents from Swisstennis for the lookup Players
		for (Integer lookupP : lookupPlayers) {

			String url = urlProp.replace("{playerid}", String.valueOf(lookupP));
			System.out.println("Lade URL: " + url);
			try {
				Response response = Jsoup.connect(url).execute();
				document = response.parse();
			} catch (IOException e) {
				System.out.println(e.toString());
				System.out.println("Could not read document: " + url);

			}
			final File f = new File(directory + "/" + String.valueOf(lookupP) + ".html");
			try {
				FileUtils.writeStringToFile(f, document.outerHtml(), "UTF-8");
			} catch (IOException e) {
				System.out.println(e.toString());
			}
			System.out.println("File created for player with id:  " + lookupP);
			loadedPlayers.add(new LoadedPlayer(lookupP));
			loadedPlayerRepository.save(loadedPlayers);

		}
		loadedPlayerRepository.save(loadedPlayers);
	}
	
	@Test
	// starts with the players in table LoadedPlayers and reversely lookups all
	// the other players
	// this is the main loading function 
	// old, could be reused for a first initial load
	public void testLoadPlayers2() throws Exception {
		String directory = applicationClient.getDirectory();
		Set<LoadedPlayer> loadedPlayers = (Set<LoadedPlayer>) loadedPlayerRepository.findAll();

		Document document = null;
		Set<Integer> lookupPlayers = new HashSet<Integer>();

		for (LoadedPlayer loadP : loadedPlayers) {
			try {
				document = applicationClient
						.readHtmlDocumentFromLocal(directory + "/" + loadP.getSwisstennisId() + ".html");
				lookupPlayers.addAll(applicationClient.getOpponentsIdFromHtml(document, loadP.getSwisstennisId()));
			} catch (IOException e) {
				System.out.println(e.toString());
				System.out.println("Could not find file for player: " + loadP.getSwisstennisId());
			} catch (NullPointerException ex) {
				System.out.println(ex.toString());
				System.out.println("Nullpointer when getting opponents of:  " + loadP.getSwisstennisId());
			}
		}
		
		loadedPlayerRepository.save(loadedPlayers);

		// remove the already loaded player from this list
		lookupPlayers.removeAll(loadedPlayers.stream().map(lp -> lp.getSwisstennisId()).collect(Collectors.toList()));
		System.out.println("Number of Lookup Players: " + lookupPlayers.size());

		// get all the documents from Swisstennis for the lookup Players
		
		ExecutorService executorService = Executors.newFixedThreadPool(5);
		lookupPlayers.forEach((playerId) -> {
			executorService.submit(()-> {
				System.out.println("File created for player with id:  " + playerId);
				applicationClient.retrieveSinglePlayerFromSwt(playerId);
				//loadedPlayers.add(new LoadedPlayer(playerId));
				loadedPlayerRepository.save(new LoadedPlayer(playerId));
				System.out.println("playerid saved to db: " + playerId);
			});
		});
		executorService.shutdown();
		executorService.awaitTermination(10, TimeUnit.HOURS);
		
		/*
		lookupPlayers.forEach(playerId -> {
			applicationClient.retrieveSinglePlayerFromSwt(playerId);
		});
		*/
	}
	
	
	@Test
	// initalload start with this
	public void testLoadPlayers3() throws Exception {
		Set<LoadedPlayer> loadedPlayers = (Set<LoadedPlayer>) loadedPlayerRepository.findAll();

		ExecutorService executorService = Executors.newFixedThreadPool(1);
		loadedPlayers.forEach((player) -> {
			executorService.submit(()-> {
				int playerId = player.getSwisstennisId();
				System.out.println("File created for player with id:  " + playerId);
				applicationClient.retrieveSinglePlayerFromSwt(playerId);
			});
		});
		executorService.shutdown();
		executorService.awaitTermination(10, TimeUnit.HOURS);
	
	}
	
// initalload start with this
	@ Test
	public void testLoadPlayersDelta() throws Exception {
		Set<LoadedPlayer> loadedPlayers = (Set<LoadedPlayer>) loadedPlayerRepository.findAll();

		Set<Integer> players = loadedPlayers.stream()
								.map(p -> p.getSwisstennisId())
								.collect(Collectors.toSet());
		
		String directoryPl = "C:/bza/misc/tennisapp/Playerlist";
		Set<Pair<Integer, String>> playerIds = applicationClient.getAllPlayerIds(directoryPl);
		
		//playerIds.removeAll(players);
		System.out.println("Number of players to get from Swisstennis: " + playerIds.size());
		
		ExecutorService executorService = Executors.newFixedThreadPool(5);
		playerIds.forEach((player) -> {
			executorService.submit(()-> {
				int playerId = player.getFirst();
				System.out.println("File created for player with id:  " + playerId);
				applicationClient.retrieveSinglePlayerFromSwt(playerId);
			});
		});
		executorService.shutdown();
		executorService.awaitTermination(10, TimeUnit.HOURS);
	
	}
	
	@ Test
	public void testLoadPlayersDelta2() throws Exception {
	 
		Scanner s = new Scanner(new File("C:/bza/misc/tennisapp/ml2.txt"));
		ArrayList<String> myList = new ArrayList<String>();
		while (s.hasNext()){
			myList.add(s.next());
		}
		s.close();
		 
		
		ExecutorService executorService = Executors.newFixedThreadPool(5);
		myList.forEach((li) -> {
			executorService.submit(()-> {
				System.out.println("File created for player with id:  " + li);
				applicationClient.retrieveSinglePlayerFromSwt(Integer.parseInt(li));
			});
		});
		executorService.shutdown();
		executorService.awaitTermination(10, TimeUnit.HOURS);
	
	}
	
	
	
	
	

		
 

}
package com.bza.tennisranking;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.junit.Test;
import org.springframework.data.util.Pair;

import com.bza.tennisranking.data.LoadedPlayer;
import com.bza.tennisranking.data.Player;
import com.bza.tennisranking.data.TennisMatch;
import com.bza.tennisranking.httpClient.SwtApplicationClient;
import com.bza.tennisranking.util.TennisMatchUtil;

// i will use this as general testing
public class LoadPlayerAndMatchesNoSpring {
	
	// helps testing initializeMatchesFromLocalDirectory2() but without Spring, is just for temporary testing
	@Test
    public void testInitializeMatches() throws IOException, ParseException, InterruptedException {
		SwtApplicationClient applicationClient = new SwtApplicationClient();
    	//String directory = "C:\\bza\\misc\\tennisapp\\Test2";
		DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
		Date startDate = formatter.parse("01.10.2018");
		Date endDate = formatter.parse("30.09.2019");
		
    	String directory = "C:\\bza\\misc\\tennisapp\\Test7";
    	
    	long ts1 = System.currentTimeMillis();
    	Set<TennisMatch> matches = applicationClient.readAllMatchesFromLocalParallel(directory);
    	long ts2 = System.currentTimeMillis();
    	System.out.format("testMatchesList::readAllMatchesFromLocal(): # matches read: %s, duration=%s ms\n", matches.size(), ts2-ts1);

		// just take the matches from the given periode
    	/*
		matches = matches.stream()
				.filter(m -> ((m.getDate().after(startDate) || m.getDate().equals(startDate)) &&
						 (m.getDate().before(endDate)) || m.getDate().equals(endDate)))
				.collect(Collectors.toSet());

		*/
		
		matches = TennisMatchUtil.adjustGradingValues(matches);
		System.out.format("Number of matches to store before resize: %s\n", matches.size());   
		
    	matches = TennisMatchUtil.resizeMatches(matches);
    	System.out.println("Number of matches to store after resize:  " + matches.size());
    	
    	System.out.println("print matches:");
    	
		
		matches.stream()
			.sorted((s1, s2) -> { return s1.getDate().compareTo(s2.getDate()); })
			.forEach(System.out::println);
    }
	
	
	@Test
	// scans all document in the local directory and builds up the player table
	public void loadPlayersFromLocalDirectory() throws IOException {
		SwtApplicationClient applicationClient = new SwtApplicationClient();
		String directory = "C:/bza/misc/tennisapp/Test6";
		long ts1 = System.currentTimeMillis();
		Set<Player> players = applicationClient.readAllPlayersFromLocal(directory);
		long ts2 = System.currentTimeMillis();
		System.out.format("readAllPlayersFromLocal: duration=%sms \n", ts2-ts1);
		
		players.forEach(System.out::println);
	}
	
	// gets all the html files from the player search form of Swisstennis
	@Test
	public void getSearchFilesFromSwt() throws IOException {
		String urlTemplate = "https://www.swisstennis.ch/player-search?page={pageId}&last_name=&first_name=&licence_number=";
		String directory = "C:/bza/misc/tennisapp/Playerlist";
		SwtApplicationClient applicationClient = new SwtApplicationClient(directory);
		
		for (int i= 0; i <= 2622; i++) {
			String url = urlTemplate.replace("{pageId}", String.valueOf(i));
			applicationClient.getPageFromSwt(url, i);
			
		}
	}
	
	@Test
	public void getAllPlayerIds() throws Exception {
		SwtApplicationClient applicationClient = new SwtApplicationClient();
		//String directory = "C:/bza/misc/tennisapp/Test-Playersearch";
		String directory = "C:/bza/misc/tennisapp/Playerlist";
		
		Set<Pair<Integer, String>> playerIds = applicationClient.getAllPlayerIds(directory);
		playerIds.forEach(p -> System.out.println(p.getFirst() + " " + p.getSecond()));
	}
	
	// gets all the html files from the player search form of Swisstennis
	@Test
	public void getOneSearchFileFromSwt() throws IOException {
		String urlTemplate = "https://www.swisstennis.ch/player-search?page={pageId}&last_name=&first_name=&licence_number=";
		String directory = "C:/bza/misc/tennisapp/Test-Playersearch";
		SwtApplicationClient applicationClient = new SwtApplicationClient(directory);
		
		int i = 1866;
		String url = urlTemplate.replace("{pageId}", String.valueOf(i));
		applicationClient.getPageFromSwt(url, i);

	}
	
	@Test
	public void getList() throws Exception {
		Scanner s = new Scanner(new File("C:/bza/misc/tennisapp/ml2.txt"));
		ArrayList<String> myList = new ArrayList<String>();
		while (s.hasNext()){
			myList.add(s.next());
		}
		s.close();
		myList.forEach(System.out::println);
	}



}
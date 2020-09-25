package com.bza.tennisranking.test;


import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import com.bza.tennisranking.data.Player;
import com.bza.tennisranking.data.TennisMatch;
import com.bza.tennisranking.httpClient.SwtApplicationClient;
import com.bza.tennisranking.util.TennisMatchUtil;


// this class does mainly the same as the SwisstennisAppClientTest such with loading the Application context
public class SwisstennisAppClientTestNoSpring {
	
	String fileName = "C:/bza/misc/tennisapp/Oktober2019/12936.html";
	
	
    @Test
    public void testApplicationClient() throws IOException, ParseException {
    	SwtApplicationClient applicationClient = new SwtApplicationClient();
    	
    	// Bossel foreign results testing - 12936.html
		//File htmlFile = new File("C:/bza/misc/tennisapp/Oktober2019/12936.html");
		
		File htmlFile = new File("C:/bza/misc/tennisapp/Oktober2019/2236.html");
	    Document document = Jsoup.parse(htmlFile, "UTF-8");
	     
    	//Player player = applicationClient.getPlayerFromHtml(document, 12936);
    	
    	Set<TennisMatch> matches = applicationClient.getMatchesFromHtml(document, 2236);
    	
    	matches.stream()
		.sorted((s1, s2) -> { return s1.getDate().compareTo(s2.getDate()); })
		.forEach(System.out::println);
    	
    	
    	// TODO: adjust the grading values (=Competition value) for all players
    	matches = TennisMatchUtil.adjustGradingValues(matches);
    	
    	// do the right ordering of the matches
    	matches = TennisMatchUtil.resizeMatches(matches);    
    }
    
    @Test
    public void testGetOpponents() throws IOException, ParseException {
    	SwtApplicationClient applicationClient = new SwtApplicationClient();
    	File htmlFile = new File("C:/bza/misc/tennisapp/January2018/2946.html");
	    Document document = Jsoup.parse(htmlFile, "UTF-8");
    	Set<Integer> todoPlayers = applicationClient.getOpponentsIdFromHtml(document, 2946);
    	System.out.println(todoPlayers);
    }
    
    @Test
    public void testReadAllHtmlDocumentsFromLocal() throws IOException {
    	SwtApplicationClient applicationClient = new SwtApplicationClient();
    	String path ="C:/bza/misc/tennisapp/Oktober2019";
    	Set<Player> players = applicationClient.readAllPlayersFromLocal(path);
    	players.forEach(System.out::println);
    	System.out.println("Anzahl: " + players.size());
    }
    @Test
    public void testReadPlayers() throws IOException {
    	SwtApplicationClient applicationClient = new SwtApplicationClient();
    	String directory = "C:/bza/misc/tennisapp/Oktober2019";
	    List<String> fileNameList = applicationClient.getFileNamesFromDirectory(directory);
		Set<Player> players = new HashSet<Player>();
		for (String fileName : fileNameList) {
			String[] words = fileName.split("\\.");
			Document document = applicationClient.readHtmlDocumentFromLocal(directory + "/" + fileName);
			Player player = applicationClient.getPlayerFromHtml(document, Integer.parseInt(words[0]));
			players.add(player);
			System.out.println("Player: " + player);
		}
    }
    
    
    @Test
    public void TestSomeMatches() throws IOException, ParseException {
    	SwtApplicationClient applicationClient = new SwtApplicationClient();
    	//Document document = Jsoup.connect("https://www.swisstennis.ch/user/13482/results-summary").get();
		File htmlFile1 = new File("C:/bza/misc/tennisapp/January2018/15372.html");
	    Document document = Jsoup.parse(htmlFile1, "UTF-8");
    	Set<TennisMatch> matches1 = applicationClient.getMatchesFromHtmlExp(document, 15372);
    	matches1 = TennisMatchUtil.resizeMatches(matches1);
    	
    	File htmlFile2 = new File("C:/bza/misc/tennisapp/January2018/13482.html");
    	document = Jsoup.parse(htmlFile2, "UTF-8");

    	Set<TennisMatch> matches2 = applicationClient.getMatchesFromHtmlExp(document, 13482);
    	matches2 = TennisMatchUtil.resizeMatches(matches2);
    	
    	System.out.println("Matches for id: " + 15372);
    	System.out.println("================================");
    	matches1.forEach(System.out::println);
    	
    	System.out.println("*********************************");
    	System.out.println("Matches for id: " + 13482);
    	System.out.println("================================");
    	matches2.forEach(System.out::println);
    	
    	System.out.println("All matches together");
    	matches1.addAll(matches2);
    	
    	matches1.forEach(System.out::println);
    	TennisMatch m1 = null;
    	TennisMatch m2 = null;
    	
    
  
    	for (TennisMatch m : matches1 ) {
    	        if (m.getTmpId() == 24) { m1 = m; }
    	        if (m.getTmpId() == 62) { m2 = m; }
    	 }
   
    	System.out.println("======COMPARE======");
    	System.out.println(m1);
    	System.out.println(m2);
    	System.out.println(m1.equals(m2));
    }
    	

    
    @Test
    public void downloadPage() throws Exception {
        final Response response = Jsoup.connect("https://www.swisstennis.ch/user/13482/results-summary").execute();
        final Document doc = response.parse();

        final File f = new File("c:/bza/misc/tennisapp/test.html");
        FileUtils.writeStringToFile(f, doc.outerHtml(), "UTF-8");
    }
    
    @Test
    public void someCalculation() {
    	List<String> values = Arrays.asList("12.3", "11.28", "13.3", "7.2", "6.3");
    	int strResult = 6;
    	strResult = (values.size() > strResult) ? strResult : values.size();
    	
    	values = values.stream().sorted((n1,n2) -> Double.compare(Double.parseDouble(n2),
    			Double.parseDouble(n1))).collect(Collectors.toList());
    	System.out.println(values);
    	
    	values = values.stream()
                .limit(values.size() - strResult)
                .collect(Collectors.toList());
    	System.out.println(values);
    }
    
  
   
 
}
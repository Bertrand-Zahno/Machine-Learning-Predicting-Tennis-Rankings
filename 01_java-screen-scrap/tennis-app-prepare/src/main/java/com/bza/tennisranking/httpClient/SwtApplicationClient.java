package com.bza.tennisranking.httpClient;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.data.util.Pair;

import com.bza.tennisranking.data.Player;
import com.bza.tennisranking.data.TennisMatch;



public class SwtApplicationClient {
	final String mainBlock = "block-system-main";
	final String rankingInfo = "ranking-info";
	final String licenceNumberClass = "field field-name-field-license-number field-type-text field-label-inline clearfix";
	final String firstNameClass = "field field-name-field-surname field-type-text field-label-inline clearfix";
	final String lastNameClass = "user-profile profile current_ranking";
	final String memberShipId = "user-user-current-ranking-group-club-membership";
	final String rankingClass = "field field-name-field-rank-classification field-type-text field-label-inline clearfix";
	final String rankNumberClass = "field field-name-field-current-rank field-type-number-integer field-label-inline clearfix";
	final String scoreClass = "field field-name-field-current-score field-type-number-float field-label-inline clearfix";
	final String compValueClass = "field field-name-field-current-competition-value field-type-text field-label-inline clearfix";		
	final String resultSummaryCurrent = "quicktabs-tabpage-results_summary-0";      
	final String prevRankingClass ="field field-name-field-old-classification field-type-text field-label-inline clearfix";
	final String prevRankNumberClass = "field field-name-field-old-rank field-type-number-integer field-label-inline clearfix";
	final String licenceStatusClass = "field field-name-field-licence-status field-type-list-text field-label-inline clearfix";
	final String ageCategoryClass = "field field-name-field-age-category field-type-text field-label-inline clearfix";
	
	private String urlTemplate;
	private String directory;
	private String directory2;
	
	public SwtApplicationClient(String urlTemplate, String directory) {
		this.urlTemplate = urlTemplate;
		this.directory = directory;
	}
	
	public SwtApplicationClient(String directory) {
		this.directory = directory;
	}
	
	public SwtApplicationClient(String urlTemplate, String directory, String directory2) {
		this.urlTemplate = urlTemplate;
		this.directory = directory;
		this.directory2 = directory2;
	}
	
	public SwtApplicationClient() {};
	
	public String getEtwas() {
		return "irgendwas";
	}
	
	public String getUrlTemplate() {
		return urlTemplate;
		
	}
	
	public String getDirectory() {
		return directory;
	}
	
	public String getDirectory2() {
		return directory2;
	}
	
	// retrieves the main player of the html document
	public Player getPlayerFromHtml(Document document, int swisstennisId) {
		Function<Element, String> getStringOfElement = (e -> { String str = e.text(); return str.substring(str.indexOf(" ") + 1); });
		
		Element form = document.getElementById(mainBlock);
		String periode = getStringOfElement.apply(form.getElementsByClass(rankingInfo).first());
	    String licenceNumber = getStringOfElement.apply(form.getElementsByClass(licenceNumberClass).first());
	    String firstName = getStringOfElement.apply(form.getElementsByClass(firstNameClass).first());
	    String lastName = form.getElementsByClass(lastNameClass).first().text().split(" ")[2];
	    Element memberShipElement = form.getElementById(memberShipId);
	    String memberShip = form.getElementById(memberShipId).text().split(" ")[3];
	    String licenceStatus = memberShipElement.getElementsByClass(licenceStatusClass).first().text().split(" ")[1];
	    String ageCategory = memberShipElement.getElementsByClass(ageCategoryClass).first().text().split(" ")[1];
	       
	    String ranking = form.getElementsByClass(rankingClass).first().text().split(" ")[1];
	   // int rankingNumber = Integer.parseInt(form.getElementsByClass(rankNumberClass).first().text().split(" ")[1]);
	    String rankNrStr = form.getElementsByClass(rankNumberClass).first().text();
	    int index = rankNrStr.indexOf(" ");
	    String rankNr = form.getElementsByClass(rankNumberClass).first().text().substring(index);
	    rankNr = rankNr.replaceAll("\\s","");
	    
	    int rankingNumber = Integer.parseInt(rankNr);
	    String gradingValue = form.getElementsByClass(scoreClass).first().text().split(" ")[1];
	    String compValue = form.getElementsByClass(compValueClass).first().text().split(" ")[1];
	    String previousRanking = form.getElementsByClass(prevRankingClass).first().text().split(" ")[2];
	    String previousRankingNumber = form.getElementsByClass(prevRankNumberClass).first().text();
	    int t_index = previousRankingNumber.indexOf(" ", previousRankingNumber.indexOf(" ") + 1);
	    previousRankingNumber  = form.getElementsByClass(prevRankNumberClass).first().text().substring(t_index);
	    previousRankingNumber = previousRankingNumber.replaceAll("\\s","");   
	 
	    Player player = new Player(firstName, lastName, licenceNumber, swisstennisId, rankingNumber, 
	    				ranking, gradingValue, compValue, memberShip, periode, previousRanking, previousRankingNumber, 
	    				licenceStatus, ageCategory); 
	   
		return player;
	}
	
	// dec 2019: added foreign result to it
	public Set<TennisMatch> getMatchesFromHtml(Document document, int swisstennisId) throws ParseException { 
		Player currentPlayer = this.getPlayerFromHtml(document, swisstennisId);
	    Elements elements = document.getElementById(resultSummaryCurrent).select("tbody").select("td");
	    Set<TennisMatch> matches = new HashSet<>();
	    TennisMatch match = new TennisMatch();
	    
	    List<Element> foreignResults = new ArrayList<>();
	    
	    for (int i = 0; i < elements.size(); i++) {
			Element currentElement = elements.get(i);
			// once we have a Auslandresultat, start collecting everything
			if (foreignResults.size() > 0) { 
				foreignResults.add(elements.get(i));
			}
			
			try {
				switch (i%8) {
				case 0: {
					match = new TennisMatch();
					break;
				}
				case 1: {	
					DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
					match.setDate(formatter.parse(currentElement.text().trim()));
					break;
				}
				
				case 2: {
					//System.out.println(currentElement);
					//System.out.println("Swisstennisid: " + swisstennisId);
					Element hrefElement = currentElement.getElementsByTag("a").first();
					match.setEvent(hrefElement.text());
					match.setUrl(hrefElement.attr("href"));
					break;
				}
				
				case 3: {
					Element hrefElement = currentElement.getElementsByTag("a").first();
					String href = hrefElement.attr("href");
					match.setPlayer2Id(Integer.valueOf(href.split("/")[2]));
					match.setPlayer2Name(hrefElement.text() );
					break; 
				}
				
				case 4: {
					match.setCompValue2(currentElement.text());
					break;
				}
				
				case 5: {
					match.setRanking2(currentElement.text());
					break;
				}
				
				case 6: {
					match.setScore(currentElement.text());
					break;
				}
				
				case 7: {
					match.setPlayer1Id(swisstennisId);
					match.setPlayer1Name((currentPlayer.getLastName() + " " + currentPlayer.getFirstName()));
					match.setRanking1(currentPlayer.getRanking());
					// the next statement is wrong, so comment it out
					// match.setGradingValue1(currentPlayer.getGradingValue());
					match.setVictory(currentElement.text());
					match.setFromFile(swisstennisId + ".html");
					// murks
					if (match.getPlayer2Name() != null) { matches.add(match); }
					break;
				}
				
				}
			} catch (NullPointerException e) {
				// Auslandresultat
				// System.out.println("Nullpointer bei: " + swisstennisId);
				// the first time we also need the previous element
				if (foreignResults.size() == 0) {
					foreignResults.add(elements.get(i-2));
					foreignResults.add(elements.get(i-1));
					foreignResults.add(elements.get(i));
				}
				
			}	
		}
	        
	    elements = document.getElementById("quicktabs-tabpage-results_summary-1").select("tbody").select("td");
	    // current results
	    for (int i = 0; i < elements.size(); i++) {
			Element currentElement = elements.get(i);
			
			try {
				switch (i%7) {
				case 0: {
					match = new TennisMatch();
					DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
					match.setDate(formatter.parse(currentElement.text().trim()));
					break;
				}
				case 1: {	
					Element hrefElement = currentElement.getElementsByTag("a").first();
					match.setEvent(hrefElement.text());
					match.setUrl(hrefElement.attr("href"));
					break;
				}
				
				case 2: {
					Element hrefElement = currentElement.getElementsByTag("a").first();
					String href = hrefElement.attr("href");
					match.setPlayer2Id(Integer.valueOf(href.split("/")[2]));
					match.setPlayer2Name(hrefElement.text() );	
					break;
				}
				
				case 3: {
					match.setCompValue2(currentElement.text());
					break; 
				}
				
				case 4: {
					match.setRanking2(currentElement.text());
					break;
				}
				
				case 5: {
					match.setScore(currentElement.text());
					break;
				}
				
				case 6: {
					match.setPlayer1Id(swisstennisId);
					match.setPlayer1Name((currentPlayer.getLastName() + " " + currentPlayer.getFirstName()));
					match.setRanking1(currentPlayer.getRanking());
					
					match.setVictory(currentElement.text());
					match.setFromFile(swisstennisId + ".html");
					// murks
					if (match.getPlayer2Name() != null) { matches.add(match); }
					
					break;
				}
				
				}
			} catch (NullPointerException e) {
				
				// Auslandresultat
				//fSystem.out.println("Nullpointer bei: " + swisstennisId);
				//System.out.println("bza2");
				//System.out.println(currentElement);
			}	
		} 
	    
	    if (foreignResults.size() > 0) {
	    	Set<TennisMatch> foreignResultMatches = getMatchesForeignResult(foreignResults, currentPlayer);
	    	matches.addAll(foreignResultMatches);
	    }
		return matches;
		
	}
	
	// returns the foreign matches in the whole set
	public Set<TennisMatch> getMatchesForeignResult(List<Element> foreignResults, Player currentPlayer) throws ParseException {
		Set<TennisMatch> matches = new HashSet<>();
		TennisMatch match = new TennisMatch();
		
		// foreignId, to count downward
		int foreignId = -1;
		
		for (int i = 0; i < foreignResults.size(); i++) {
			 Element currentElement = foreignResults.get(i);
			 // System.out.println(currentElement);
			 try {
					switch (i%8) {
					case 0: {
						// not currently used, indicates the "Streichresultat"
						match = new TennisMatch();
						break;
					}
					case 1: {	
						DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
						match.setDate(formatter.parse(currentElement.text().trim()));
						break;
					}
					
					case 2: {
						// not currently used, atp ranking
						break;
					}
					
					case 3: {
						// for a foreign player set Id to a negative number, but always to a new one
						// otherwise the match is skiped due to the hash fonction of TennisMatch
						match.setPlayer2Id(foreignId);
						foreignId = foreignId - 1;
						match.setPlayer2Name(currentElement.text().trim());	
						break; 
					}
					
					case 4: {
						match.setCompValue2(currentElement.text());
						break;
					}
					
					case 5: {
						match.setRanking2(currentElement.text());				
						break;
					}
					
					case 6: {
						match.setScore(currentElement.text());						
						break;
					}
					
					case 7: {
						match.setVictory(currentElement.text());
						match.setPlayer1Id(currentPlayer.getSwisstennisId());
						
						match.setPlayer1Name((currentPlayer.getLastName() + " " + currentPlayer.getFirstName()));
						match.setRanking1(currentPlayer.getRanking());
						
						matches.add(match);
						break;
					}
					
					}
				} catch (NullPointerException e) {
					//System.out.println(e);
					System.out.println("Nullpointer foreignresult for player: " + currentPlayer.getSwisstennisId());
					
				}	
			} 
			 
		 return matches;
	}
		
			

	// getMatchesFromHtml experimental
		public Set<TennisMatch> getMatchesFromHtmlExp(Document document, int swisstennisId) throws ParseException { 
			Player currentPlayer = this.getPlayerFromHtml(document, swisstennisId);
		    Elements elements = document.getElementById(resultSummaryCurrent).select("tbody").select("td");
		    Set<TennisMatch> matches = new HashSet<TennisMatch>();
		    TennisMatch match = new TennisMatch();
		    
		    for (int i = 0; i < elements.size(); i++) {
				Element currentElement = elements.get(i);
				//System.out.println(currentElement);
				
				try {
					switch (i%8) {
					case 0: {
						match = new TennisMatch();
						break;
					}
					case 1: {	
						DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
						match.setDate(formatter.parse(currentElement.text().trim()));
						break;
					}
					
					case 2: {
						//System.out.println(currentElement);
						//System.out.println("Swisstennisid: " + swisstennisId);
						Element hrefElement = currentElement.getElementsByTag("a").first();
						match.setEvent(hrefElement.text());
						match.setUrl(hrefElement.attr("href"));
						break;
					}
					
					case 3: {
						Element hrefElement = currentElement.getElementsByTag("a").first();
						String href = hrefElement.attr("href");
						match.setPlayer2Id(Integer.valueOf(href.split("/")[2]));
						match.setPlayer2Name(hrefElement.text() );
						break; 
					}
					
					case 4: {
						match.setCompValue2(currentElement.text());
						break;
					}
					
					case 5: {
						match.setRanking2(currentElement.text());
						break;
					}
					
					case 6: {
						match.setScore(currentElement.text());
						break;
					}
					
					case 7: {
						match.setPlayer1Id(swisstennisId);
						match.setPlayer1Name((currentPlayer.getLastName() + " " + currentPlayer.getFirstName()));
						match.setRanking1(currentPlayer.getRanking());
						// the next statement is wrong, so comment it out
						// match.setGradingValue1(currentPlayer.getGradingValue());
						match.setVictory(currentElement.text());
						match.setFromFile(swisstennisId + ".html");
						// murks
						if (match.getPlayer2Name() != null) { matches.add(match); }
						break;
					}
					
					}
				} catch (NullPointerException e) {
					// Auslandresultat
					// System.out.println("Nullpointer bei: " + swisstennisId);
				}	
			}
		    elements = document.getElementById("quicktabs-tabpage-results_summary-1").select("tbody").select("td");
		    // current results
		    for (int i = 0; i < elements.size(); i++) {
				Element currentElement = elements.get(i);
				
				try {
					switch (i%7) {
					case 0: {
						match = new TennisMatch();
						DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
						match.setDate(formatter.parse(currentElement.text().trim()));
						break;
					}
					case 1: {	
						Element hrefElement = currentElement.getElementsByTag("a").first();
						match.setEvent(hrefElement.text());
						match.setUrl(hrefElement.attr("href"));
						break;
					}
					
					case 2: {
						Element hrefElement = currentElement.getElementsByTag("a").first();
						String href = hrefElement.attr("href");
						match.setPlayer2Id(Integer.valueOf(href.split("/")[2]));
						match.setPlayer2Name(hrefElement.text() );	
						break;
					}
					
					case 3: {
						match.setCompValue2(currentElement.text());
						break; 
					}
					
					case 4: {
						match.setRanking2(currentElement.text());
						break;
					}
					
					case 5: {
						match.setScore(currentElement.text());
						break;
					}
					
					case 6: {
						match.setPlayer1Id(swisstennisId);
						match.setPlayer1Name((currentPlayer.getLastName() + " " + currentPlayer.getFirstName()));
						match.setRanking1(currentPlayer.getRanking());
						
						match.setCompValue1(currentPlayer.getGradingValue());
						match.setVictory(currentElement.text());
						match.setFromFile(swisstennisId + ".html");
						// murks
						if (match.getPlayer2Name() != null) { matches.add(match); }
						
						break;
					}
					
					}
				} catch (NullPointerException e) {
					// Auslandresultat
					//fSystem.out.println("Nullpointer bei: " + swisstennisId);
				}	
			} 
			return matches;
			
		}
	
	// retrieves all the opponents from the main player
	public Set<Integer> getOpponentsIdFromHtml(Document document, int swisstennisId) throws ParseException{
		return getMatchesFromHtmlExp(document, swisstennisId).stream().map(m -> m.getPlayer2Id()).collect(Collectors.toSet());
	}
	
	public Document readHtmlDocumentFromLocal(String path) throws IOException {
		File htmlFile = new File(path);
	    return Jsoup.parse(htmlFile, "UTF-8");
		
	}
	
	// retrieves all the player from the written documents on the local directory
	public Set<Player> readAllPlayersFromLocal(String directory) throws IOException {
		List<String> fileNameList = getFileNamesFromDirectory(directory);
		System.out.println("Number of files to read: " + fileNameList.size());
		
		Set<Player> players = new HashSet<Player>();
		int counter = 0;
		for (String fileName : fileNameList) {
			String[] words = fileName.split("\\.");
			
			Document document = readHtmlDocumentFromLocal(directory + "/" + fileName);
			try {
				Player player = getPlayerFromHtml(document, Integer.parseInt(words[0]));
				players.add(player);
			} catch (NullPointerException e) {
				System.out.println("Nullpointer at: " + Integer.parseInt(words[0]) );
			}	
			counter = counter + 1;
			
			if ((counter % 1000) == 0) System.out.println("counter: " + counter);
			
		}
		return players;
	}
	
	
	// retrieves all the player from the written documents on the local directory
	public Set<Player> readAllPlayersFromLocalParallel(String directory) throws IOException, InterruptedException {
		List<String> fileNameList = getFileNamesFromDirectory(directory);
		System.out.println("Number of files to read: " + fileNameList.size());
		
		Set<Player> players = Collections.synchronizedSet(new HashSet<Player>());
		
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		fileNameList.forEach((fileName) ->
		executorService.submit(()-> {
				String[] words = fileName.split("\\.");
				try {
					Document document = readHtmlDocumentFromLocal(directory + "/" + fileName);
					Player player = getPlayerFromHtml(document, Integer.parseInt(words[0]));
					players.add(player);
					
				} catch(IOException ex) { System.out.println("could not read file: " + fileName);}
				  catch(NullPointerException e) { System.out.println("Nullpointer: " + Integer.parseInt(words[0]));}
			 
		}));
		executorService.shutdown();
		executorService.awaitTermination(10, TimeUnit.HOURS);
		
		return players;
	}	
	
	
	// retrieves all the player from the written documents on the local directory
	public Set<TennisMatch> readAllMatchesFromLocalParallel(String directory) throws IOException, ParseException, InterruptedException {
		List<String> fileNameList = getFileNamesFromDirectory(directory);
 
		Set<TennisMatch> matches = Collections.synchronizedSet(new HashSet<TennisMatch>());
	
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		fileNameList.forEach((fileName) ->
			executorService.submit(()-> {
					String[] words = fileName.split("\\.");
					try {
						Document document = readHtmlDocumentFromLocal(directory + "/" + fileName);
						matches.addAll(getMatchesFromHtml(document, Integer.parseInt(words[0])));
						
					} catch(IOException ex) { System.out.println("could not read file: " + fileName);}
					  catch(ParseException e) { System.out.println("could not parse file: " + fileName);}
					  catch(NullPointerException e) { System.out.println("Nullpointer: " + Integer.parseInt(words[0]));}
				 
			}));
		executorService.shutdown();
		executorService.awaitTermination(10, TimeUnit.HOURS);
		
		return matches;
	}
	
	// gets all the playerid's out of the search form located in the given directory
	// retrieves all the player from the written documents on the local directory
	public Set<Pair<Integer, String>>  getAllPlayerIds(String directoryP) throws IOException {
		List<String> fileNameList = getFileNamesFromDirectory(directoryP);
		Set<Pair<Integer, String>> playerIds = new HashSet<>();
		
		fileNameList.forEach(fileName -> {
			String[] words = fileName.split("\\.");
			
			try {
				Document document = readHtmlDocumentFromLocal(directoryP + "/" + fileName);
				System.out.println("Reading: " + directoryP + "/" + fileName);
				
				Elements elements = document.select("tbody").select("td");
				
				String lastMenId = null;
				for (int i = 0; i < elements.size(); i++) {
					Element currentElement = elements.get(i);
					//System.out.println("bza:" + i + " " + currentElement);
					switch (i%7) {
					case 1: {
						String href = currentElement.getElementsByTag("a").first().attr("href");
						String[] hrefParts = href.split("\\/");
						lastMenId = hrefParts[2];
						break;
						}
					case 6: {
						Pair<Integer, String> p = Pair.of(Integer.parseInt(lastMenId), fileName);
						playerIds.add(p);
						 
						break;
						}
					}
				} 
				
			} catch(IOException ex) { System.out.println("could not read file: " + fileName);}
			  catch(NullPointerException e) { System.out.println("Nullpointer: " + Integer.parseInt(words[0]));}
			
		});	
		return playerIds;
	}
	
	// retrieves all the player from the written documents on the local directory
	// will probably be replaced with the Parallel version above
	public Set<TennisMatch> readAllMatchesFromLocal(String directory) throws IOException, ParseException {
		List<String> fileNameList = getFileNamesFromDirectory(directory);
		 
		System.out.format("readAllMatchesFromLocal-getFileNamesFromDirectory(): # = %s \n", fileNameList.size());
		
		Set<TennisMatch> matches = new HashSet<TennisMatch>();
		int counter = 0;
		for (String fileName : fileNameList) {
			counter ++;
			if ((counter % 1000) == 0) {
				System.out.format("readAllMatchesFromLocal, # of processed files = %s \n", counter);
			}
			String[] words = fileName.split("\\.");
			Document document = readHtmlDocumentFromLocal(directory + "/" + fileName);
			try {
			  matches.addAll(getMatchesFromHtmlExp(document, Integer.parseInt(words[0])));
			} catch (NullPointerException e) {
				System.out.println("Nullpointer at: " + Integer.parseInt(words[0]) );
			}	
		}
		return matches;
	}
	
	 
	public List<String> getFileNamesFromDirectory(String directory) {
		List<String> fileNameList = new ArrayList<String>();
		File[] files = new File(directory).listFiles();
		for (File file : files) {
			String fileName = file.getName();
		    if (file.isFile() && fileName.contains("html")) { fileNameList.add(fileName);}
		}
		return fileNameList;
			
	}
		
	// retrieves the first n matches from the directory
	public Set<TennisMatch> readSomeMatchesFromLocal(String directory, int n) throws IOException, ParseException {
		List<String> fileNameList = getFileNamesFromDirectory(directory);
		Set<TennisMatch> matches = new HashSet<TennisMatch>();
		for (int i= 0; (i < n) && (i < fileNameList.size()); i++ ) {
			String fileName = fileNameList.get(i);
			String[] words = fileName.split("\\.");
			Document document = readHtmlDocumentFromLocal(directory + "/" + fileName);
			matches.addAll(getMatchesFromHtmlExp(document, Integer.parseInt(words[0])));
		}
		return matches;
	}
		
	// retrieves a single player from Swisstennis Website and stores it on the local directory
	public void retrieveSinglePlayerFromSwt(int swisstennisId) {
		Document document = null;
		String url = urlTemplate.replace("{playerid}", String.valueOf(swisstennisId));
		System.out.println("bza: " + url);
		try {
			Response response = Jsoup.connect(url).execute();
			document = response.parse();
		} catch (IOException e) {
			System.out.println(e.toString());
			System.out.println("Could not read document from Swisstennis " + url);
		}
		
		final File f = new File(directory + "/" + swisstennisId + ".html");
		try {
			FileUtils.writeStringToFile(f, document.outerHtml(), "UTF-8");
		} catch (IOException e) {
			System.out.println(e.toString());
		}
		System.out.println("retrieveSinglePlayerFromSwt: File created for player with id:  " + swisstennisId);
		
	}
	
	// retrieves a page from SWT
	public void getPageFromSwt(String url, int id) {
		Document document = null;
		try {
			Response response = Jsoup.connect(url).execute();
			document = response.parse();
		} catch (IOException e) {
			System.out.println(e.toString());
			System.out.println("Could not read document from Swisstennis " + url);
		}
		
		final File f = new File(directory + "/" + id + ".html");
		try {
			FileUtils.writeStringToFile(f, document.outerHtml(), "UTF-8");
		} catch (IOException e) {
			System.out.println(e.toString());
		}
		System.out.println("getPageFromSwt: File created with id:  " + id);
	}	
	
	
	
	
	// retrieves a Set of of players from Swisstennis website and stores it on the local directory
	// the list contains the swisstennisid
	public void retrievePlayersFromSwt(Set<Integer> ids) {
		ids.stream().forEach(id -> retrieveSinglePlayerFromSwt(id));
	}
	
}

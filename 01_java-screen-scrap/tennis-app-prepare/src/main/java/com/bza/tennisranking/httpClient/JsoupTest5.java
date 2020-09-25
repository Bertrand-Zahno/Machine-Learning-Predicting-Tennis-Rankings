package com.bza.tennisranking.httpClient;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class JsoupTest5 {
	public static void main(String[] args) throws IOException  {
 
		  //Document doc = Jsoup.connect("https://www.swisstennis.ch/user/13482/results-summary").get();
		
		  File htmlFile = new File("C:/bza/tmp/tennisapp/January2018/2946.html");
	      Document doc = Jsoup.parse(htmlFile, "UTF-8");
	      Element form = doc.getElementById("quicktabs-tabpage-results_summary-0");     
	      Elements elements = form.select("tbody").select("td");
	    
		  for (int i = 0; i < elements.size(); i++) {
			Element currentElement = elements.get(i);
			System.out.println(currentElement);
			switch (i%8) {
			case 1: {	
				System.out.println("Datum der Partie: " + currentElement.text());
				break;
			}
			
			case 2: {
				Element hrefElement = currentElement.getElementsByTag("a").first();
				String href = hrefElement.attr("href");
				System.out.println("Anlass: " + hrefElement.text() + " und: " + href);
				break;
			}
			
			case 3: {
				Element hrefElement = currentElement.getElementsByTag("a").first();
				String href = hrefElement.attr("href");
				String text2 = hrefElement.text();
				System.out.print("href URL: " + href + "  ");
				System.out.println("Gegner: " + text2);
				break; }
			
			case 4: {
				System.out.println("Klassierungswert: " + currentElement.text());
				break;
			}
			
			case 5: {
				System.out.println("Klassierung Gegner: " + currentElement.text());
				break;
			}
			
			case 6: {
				System.out.println("Resultat: " + currentElement.text());
				break;
			}
			
			case 7: {
				System.out.println("Gewinn: " + currentElement.text());
				break;
			}
			
			}	
			
			System.out.println("==============================");
		}
	      
	   
	}
}

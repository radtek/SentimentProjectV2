package utils;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import com.google.gson.Gson;

import newsAPI.JsonHandler;
import preProcessing.NewsArticlesWithTickers;

public class ArticleSorter {
	
	public ArticleSorter(){
			
	}
	
	public NewsArticlesWithTickers getArticlesWithTicker(String ticker, NewsArticlesWithTickers inputArticles){
		NewsArticlesWithTickers newsArticlesWithGivenTicker = new NewsArticlesWithTickers();
		
		for(int i=0; i<inputArticles.getNewsArticlesWithTickers().size(); i++){
			for(int j=0; j<inputArticles.getNewsArticlesWithTickers().get(i).getTickerList().size(); j++){
				//System.out.println("TICKER: " + inputArticles.getNewsArticlesWithTickers().get(i).getTickerList().get(j));
				if(inputArticles.getNewsArticlesWithTickers().get(i).getTickerList().get(j).equals(ticker)){
					System.out.println("DSANT");
					newsArticlesWithGivenTicker.getNewsArticlesWithTickers().add(inputArticles.getNewsArticlesWithTickers().get(i));
				}
			}
		}
		return newsArticlesWithGivenTicker;
	}
	
	public void writeToFile(String text) throws IOException{
		Writer out = new BufferedWriter(new OutputStreamWriter(
			new FileOutputStream(this.getPath()+"/PreProcessedArticles/STLarticles.txt"), "UTF-8"));
			try {
			    out.write(text);
			} finally {
			    out.close();
			}
	}

	public String getPath() {
	    String path = String.format("%s/%s", System.getProperty("user.dir"), this.getClass().getPackage().getName().replace(".", "/"));
	    return path.split("/")[0]+"/ArticleResources/";
	}
	
	
	public static void main(String args[]) throws IOException{
		ArticleSorter a = new ArticleSorter();
		JsonHandler h = new JsonHandler("PreProcessedArticles/DesiredArticles","ticker");
		NewsArticlesWithTickers norwegianTickerArticles = a.getArticlesWithTicker("STL", h.tickerArticles);
		Gson g = new Gson();
	    a.writeToFile(g.toJson(norwegianTickerArticles));  
	    
	}
	
	
	

}
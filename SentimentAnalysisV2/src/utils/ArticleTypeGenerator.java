package utils;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import com.google.gson.Gson;

import preProcessing.HegnarTickerScraper;
import preProcessing.NewsArticlesWithPosTaggedWords;
import preProcessing.NewsArticlesWithStemmedVersion;
import preProcessing.NewsArticlesWithTickers;
import preProcessing.PosTaggedJsonCreater;
import newsAPI.JsonHandler;
import newsAPI.NewsArticlesRaw;

public class ArticleTypeGenerator {
	
	
	public ArticleTypeGenerator(){
		
	};
	public void generateCleanRawArticles(String UntouchedArticlesFileSource, String newFileName) throws IOException{
		JsonHandler untouchedHandler = new JsonHandler(UntouchedArticlesFileSource, "raw");
		ArticleCleaner ac = new ArticleCleaner();
		NewsArticlesRaw rawArticles = ac.cleanAllArticles(untouchedHandler.getArticles());;
		Gson gson = new Gson();
		String rawArticlesAsJson = gson.toJson(rawArticles);
		this.writeToArticleFile(rawArticlesAsJson, this.getPath()+"ArticleSteps/1_RawArticles", newFileName);
		
	}
	public void generateTickerArticles(String RawArticlesFileSource, String newFileName) throws IOException{
		JsonHandler rawHandler = new JsonHandler(RawArticlesFileSource, "raw");
		HegnarTickerScraper hts = new HegnarTickerScraper();
		NewsArticlesWithTickers tickerArticles = hts.getArticlesWithTicker(rawHandler);
		System.out.println(tickerArticles.getNewsArticlesWithTickers().get(0).getText());
		Gson gson = new Gson();
		String tickerArticlesAsJson = gson.toJson(tickerArticles);
		this.writeToArticleFile(tickerArticlesAsJson, this.getPath()+"ArticleSteps/2_TickerArticles", newFileName);	
	}
	
	public void generatePOStaggedArticles(String TickerArticlesFileSource, String newFileName) throws IOException{
		JsonHandler tickerHandler = new JsonHandler(TickerArticlesFileSource, "ticker");
		PosTaggedJsonCreater ptjc = new PosTaggedJsonCreater();
		String posTaggedArticlesAsJson = ptjc.getAllArticlesAsJson(tickerHandler.getTickerArticles());
		this.writeToArticleFile(posTaggedArticlesAsJson, this.getPath()+"ArticleSteps/3_POStaggedArticles", newFileName);	
	}
	
	public void generateStemmedArticles(String PosTaggedArticleFileSource, String newFileName) throws IOException{
		JsonHandler posHandler = new JsonHandler(PosTaggedArticleFileSource, "pos");
		Stemmer s = new Stemmer();
		NewsArticlesWithStemmedVersion stemmedArticles = s.stemAllArticles(posHandler.getPosTaggedArticles());
		Gson gson = new Gson();
		String stemmedArticlesAsJson = gson.toJson(stemmedArticles);
		this.writeToArticleFile(stemmedArticlesAsJson, this.getPath()+"ArticleSteps/4_StemmedArticles", newFileName);	
	}
	
	
	public void writeToArticleFile(String text, String path, String name) throws IOException{
		Writer out = new BufferedWriter(new OutputStreamWriter(
			    new FileOutputStream(path + "/"+name+".json"), "UTF-8"));
			try {
			    out.write(text);
			} finally {
			    out.close();
			}
	}
	public String getPath() {
	    String path = String.format("%s/%s", System.getProperty("user.dir"), this.getClass().getPackage().getName().replace(".", "/"));
	    return path.split(this.getClass().getPackage().getName())[0]+"/ArticleResources/";
	}
	
	public static void main(String[] args) throws IOException{
		ArticleTypeGenerator atg = new ArticleTypeGenerator();
		//atg.generateCleanRawArticles("ArticleSteps/0_UntouchedArticles/ArticleGeneratorTest.txt", "ArticleGeneratorTestClean");
		//atg.generateTickerArticles("ArticleSteps/1_RawArticles/ArticleGeneratorTestClean.json", "ArticleGeneratorTestTicker");
		//atg.generatePOStaggedArticles("ArticleSteps/2_TickerArticles/ArticleGeneratorTestTicker.json", "ArticleGeneratorTestPOS");
		atg.generateStemmedArticles("ArticleSteps/3_POStaggedArticles/StemmingTestArticles.txt", "ArticleGeneratorTestStemmed");
	}
	

}
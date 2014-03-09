package cot;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import featureExctraction.WordCountList;
import preProcessing.NewsArticleWithCots;
import preProcessing.NewsArticleWithStemmedVersion;
import preProcessing.NewsArticlesWithStemmedVersion;
import preProcessing.PosTaggedWord;
import preProcessing.TextFileHandler;
import newsAPI.JsonHandler;

public class CoTCounter {
	
	/*
	 * This class counts the number of CoTs within distance r in a news artile
	 */
	
	public HashMap<String, Integer> map;
	public HashMap<String, CotCountTFDF> tfdfmap;
	boolean tfdf = false;
	public int radius;
	public String filename;
	
	public CoTCounter(String filename, int radius) {
		this.map = new HashMap<String, Integer>();
		this.filename = filename;
		this.radius = radius;
	}
	public CoTCounter(int radius) {
		this.map = new HashMap<String, Integer>();
		this.radius = radius;
	}
	public CoTCounter(String filename, int radius, boolean tfdf) {
		this.tfdf = tfdf;
		this.filename = filename;
		this.tfdfmap = new HashMap<String, CotCountTFDF>();
		this.radius = radius;
	}
	
	public ArrayList<String> getCoTsFromIndex(ArrayList<PosTaggedWord> allPosTaggedWords, int index) {
		ArrayList<String> cots = new ArrayList<String>();
		String current = allPosTaggedWords.get(index).stem;
		if (current.length() >= 1 && current.charAt(0) == '$') {
			return cots;
		}
		int steps = 1;
		for (int i = index + 1 ; i < allPosTaggedWords.size(); i++) {
			if (steps > radius) {
				return cots;
			}
			String candidate = allPosTaggedWords.get(i).stem;
			if (candidate.length() >= 1 && candidate.charAt(0) == '$') {
				// some kind of sign - could be comma or stop sign
				if (candidate.length() >= 2 && ".!?".contains(candidate.charAt(1)+"")) {
					return cots;
				} else {
					// Some other sign ',', ';' etc, --> skip
				}
			} else if (candidate.length() < 1){
				// skip Strings of length 0
			} else {
				// word - add CoT
				cots.add(current + " " + candidate);
				steps++;
			}
		}
		return cots;
	}

	public static ArrayList<String> getCoTsFromIndex(ArrayList<PosTaggedWord> allPosTaggedWords, int index, int radius) {
		ArrayList<String> cots = new ArrayList<String>();
		String current = allPosTaggedWords.get(index).stem;
		if (current.length() >= 1 && current.charAt(0) == '$') {
			return cots;
		}
		int steps = 1;
		for (int i = index + 1 ; i < allPosTaggedWords.size(); i++) {
			if (steps > radius) {
				return cots;
			}
			String candidate = allPosTaggedWords.get(i).stem;
			if (candidate.length() >= 1 && candidate.charAt(0) == '$') {
				// some kind of sign - could be comma or stop sign
				if (candidate.length() >= 2 && ".!?".contains(candidate.charAt(1)+"")) {
					return cots;
				} else {
					// Some other sign ',', ';' etc, --> skip
				}
			} else if (candidate.length() < 1){
				// skip Strings of length 0
			} else {
				// word - add CoT
				cots.add(current + " " + candidate);
				steps++;
			}
		}
		return cots;
	}

	public void cotCountArticles(JsonHandler jh) {
		map = new HashMap<String, Integer>();
		for (NewsArticleWithStemmedVersion nawsv : jh.stemmedArticles.getNawsv()) {
//			for (int i = 0; i < nawsv.getAllPosTaggedWords().size();i++) {
//				System.out.print(nawsv.getAllPosTaggedWords().get(i).stem + " ");
//			}
			for (int i = 0; i < nawsv.getAllPosTaggedWords().size();i++) {
				ArrayList<String> cots = getCoTsFromIndex(nawsv.getAllPosTaggedWords(), i);
				addCoTsToMap(cots);
			}
		}
	}
	
	public void cotCountArticlesNotMainText(JsonHandler jh) {
		map = new HashMap<String, Integer>();
		for (NewsArticleWithStemmedVersion nawsv : jh.stemmedArticles.getNawsv()) {
//			for (int i = 0; i < nawsv.getAllPosTaggedWords().size();i++) {
//				System.out.print(nawsv.getAllPosTaggedWords().get(i).stem + " ");
//			}
			ArrayList<PosTaggedWord> ptwList = new ArrayList<PosTaggedWord>();
			
			if(nawsv.getPosTaggedLeadText().getPosTaggedWords()!= null){
				for (PosTaggedWord posTaggedWord : nawsv.getPosTaggedTitle().getPosTaggedWords()) {
					ptwList.add(posTaggedWord);
				}
			}	
			if(nawsv.getPosTaggedLeadText().getPosTaggedWords()!= null){
				for (PosTaggedWord posTaggedWord : nawsv.getPosTaggedLeadText().getPosTaggedWords()) {
					ptwList.add(posTaggedWord);
				}
			}
			
			for (int i = 0; i < ptwList.size();i++) {
				ArrayList<String> cots = getCoTsFromIndex(ptwList, i);
				addCoTsToMap(cots);
			}
		}
	}
	
	public void cotCountArticlesNotMainTextTFDF(JsonHandler jh) {
		tfdfmap = new HashMap<String, CotCountTFDF>();
		for (NewsArticleWithStemmedVersion nawsv : jh.stemmedArticles.getNawsv()) {
//			for (int i = 0; i < nawsv.getAllPosTaggedWords().size();i++) {
//				System.out.print(nawsv.getAllPosTaggedWords().get(i).stem + " ");
//			}
			ArrayList<PosTaggedWord> ptwList = new ArrayList<PosTaggedWord>();
			
			if(nawsv.getPosTaggedLeadText().getPosTaggedWords()!= null){
				for (PosTaggedWord posTaggedWord : nawsv.getPosTaggedTitle().getPosTaggedWords()) {
					ptwList.add(posTaggedWord);
				}
			}	
			if(nawsv.getPosTaggedLeadText().getPosTaggedWords()!= null){
				for (PosTaggedWord posTaggedWord : nawsv.getPosTaggedLeadText().getPosTaggedWords()) {
					ptwList.add(posTaggedWord);
				}
			}
			
			for (int i = 0; i < ptwList.size();i++) {
				ArrayList<String> cots = getCoTsFromIndex(ptwList, i);
				addCoTsToMapTFDF(tfdfmap, cots);
			}
		}
	}
	
	public HashMap<String, Integer> getCotsForArticle(NewsArticleWithStemmedVersion stemmedArticle){
		HashMap<String, Integer> articleHashmap = new HashMap<String, Integer>();

		for (int i = 0; i < stemmedArticle.getAllPosTaggedWords().size(); i++) {
			//System.out.println("Stemmed article cots: " + stemmedArticle.getAllPosTaggedWords().size());
			ArrayList<String> cots = getCoTsFromIndex(stemmedArticle.getAllPosTaggedWords(), i);
	
			addCoTsToMap(articleHashmap, cots);
		}
		System.out.println("Cots: " + articleHashmap.toString());
		return articleHashmap;
		
	}
	public NewsArticleWithCots initiateCotsArticle(NewsArticleWithStemmedVersion stemmedArticle){
		NewsArticleWithCots nawc = new NewsArticleWithCots();
		
		
		nawc.setcat(stemmedArticle.getcat());
		nawc.setId(stemmedArticle.getId());
		nawc.setImageUrl(stemmedArticle.getImageUrl());
		nawc.setlast_modified(stemmedArticle.getlast_modified());
		nawc.setlead_text(stemmedArticle.getlead_text());
		nawc.setLinks(stemmedArticle.getLinks());
		nawc.setpublished(stemmedArticle.getpublished());
		nawc.setPublisher(stemmedArticle.getPublisher());
		nawc.setSentimentValue(stemmedArticle.getSentimentValue());
		nawc.setSignature(stemmedArticle.getSignature());
		nawc.setTags(stemmedArticle.getTags());
		nawc.setText(stemmedArticle.getText());
		nawc.setTitle(stemmedArticle.getTitle());
		nawc.setversion(stemmedArticle.getversion());
		nawc.setTickerList(stemmedArticle.getTickerList());
		nawc.setKeywordList(stemmedArticle.getKeywordList());
		nawc.setAuthorName(stemmedArticle.getAuthorName());
		
		nawc.setPosTaggedTitle(stemmedArticle.getPosTaggedTitle());
		nawc.setPosTaggedLeadText(stemmedArticle.getPosTaggedLeadText());
		nawc.setPosTaggedMainText(stemmedArticle.getPosTaggedMainText());
		
		nawc.setStemmedTitle(stemmedArticle.getStemmedTitle());
		nawc.setStemmedLeadText(stemmedArticle.getStemmedLeadText());
		nawc.setStemmedText(stemmedArticle.getStemmedText());
		
		nawc.setCots(this.getCotsForArticle(stemmedArticle));
		
		//System.out.println("Initiating article" + nawc.getCots().toString());
		
		
		return nawc;
		
	}
	

	public static HashMap<String, Integer> cotCountArticles(NewsArticleWithStemmedVersion nawsv, int radius) {
		HashMap<String, Integer> hashmap = new HashMap<String, Integer>();
			for (int i = 0; i < nawsv.getAllPosTaggedWords().size();i++) {
				ArrayList<String> cots = getCoTsFromIndex(nawsv.getAllPosTaggedWords(), i, radius);
				hashmap = addCoTsToMap(hashmap, cots);
			}
		return hashmap;
	}
	
	public static HashMap<String, CotCountTFDF> cotCountArticlesTFDF(NewsArticleWithStemmedVersion nawsv, int radius) {
		HashMap<String, CotCountTFDF> hashmap = new HashMap<String, CotCountTFDF>();
			for (int i = 0; i < nawsv.getAllPosTaggedWords().size();i++) {
				ArrayList<String> cots = getCoTsFromIndex(nawsv.getAllPosTaggedWords(), i, radius);
				hashmap = addCoTsToMapTFDF(hashmap, cots);
			}
		return hashmap;
	}

	public static HashMap<String, Integer> addCoTsToMap(HashMap<String, Integer> hashmap, ArrayList<String> cots) {
		for (String cot : cots) {
			if ( hashmap.containsKey(cot)) {
				int count = hashmap.get(cot);
				hashmap.put(cot, ++count);
			} else {
				hashmap.put(cot, 1);
			}
		}
		return hashmap;
	}

	public static HashMap<String, CotCountTFDF> addCoTsToMapTFDF(HashMap<String, CotCountTFDF> hashmap, ArrayList<String> cots) {
		for (String cot : cots) {
			if ( hashmap.containsKey(cot)) {
				CotCountTFDF cctfdf = new CotCountTFDF();
				cctfdf = hashmap.get(cot);
				cctfdf.setTermFrequency(cctfdf.getTermFrequency()+1);
				hashmap.put(cot, cctfdf);
			} else {
				CotCountTFDF cctfdf = new CotCountTFDF();
				cctfdf.setDocumentFrequency(cctfdf.getDocumentFrequency()+1);
				hashmap.put(cot, new CotCountTFDF());
			}
		}
		return hashmap;
	}

	public void addCoTsToMap(ArrayList<String> cots) {
		for (String cot : cots) {
			if ( map.containsKey(cot)) {
				int count = map.get(cot);
				map.put(cot, ++count);
			} else {
				map.put(cot, 1);
			}
		}
	}
	
	public void writeMapToFile() throws IOException {
		Gson gson = new Gson();
		writeToFile(gson.toJson(this));
	}
	public void writeSortedMapToFile(HashMap<String, Integer> sortedMap) throws IOException {
		Gson gson = new Gson();
		writeToFile(gson.toJson(sortedMap));
	}
	
	public void writeToFile(String text) throws IOException{
		Writer out = new BufferedWriter(new OutputStreamWriter(
			new FileOutputStream(getPath()+filename), "UTF-8"));
			try {
			    out.write(text);
			} finally {
			    out.close();
			}
	}
	
	public String getPath() {
	    String path = String.format("%s/%s", System.getProperty("user.dir"), this.getClass().getPackage().getName().replace(".", "/"));
	    path = path.substring(0, path.length()-4);
	    return path + "/ArticleResources/CoTs/";
	}

	public static String getStaticPath() {
		CoTCounter cc = new CoTCounter("cotsannotated.json", 0);
		String path = String.format("%s/%s", System.getProperty("user.dir"), cc.getClass().getPackage().getName().replace(".", "/"));
		path = path.substring(0, path.length()-4);
		return path + "/ArticleResources/CoTs/";
	}
	
	// Return the cots in a NewsArticleWithStemmedVersion object
	public HashMap<String, Integer> getCoTsWithCountForArticles(NewsArticleWithStemmedVersion nawsv) {
		HashMap<String, Integer> cotsMap = new HashMap<String, Integer>();
		for (int i = 0; i < nawsv.getAllPosTaggedWords().size();i++) {
			ArrayList<String> cots = getCoTsFromIndex(nawsv.getAllPosTaggedWords(), i);
			addCoTsToMap(cots);
		}
		return cotsMap;
	}
	
	public static int getNumberOfMatchingCoTsForArticles(NewsArticleWithStemmedVersion nawsv, int value, int radius) throws Exception {
		int counter = 0;
		Gson gson = new Gson();
		CoTCounter cc = new CoTCounter("cotsannotated.json", 0);
		cc = gson.fromJson(TextFileHandler.getCotsAnnotatedString(), cc.getClass());
		HashMap<String, Integer> annotatedMap = cc.map;
		System.out.println(annotatedMap);
		
		HashMap<String, Integer> articleMap = cotCountArticles(nawsv, radius);
		System.out.println(articleMap);
		for (String key : articleMap.keySet()) {
			if (annotatedMap.containsKey(key) && annotatedMap.get(key) == value) {
				counter++;
			}
		}
		return counter;
	}

	public static int getNumberOfPositiveCoTsForArticles(NewsArticleWithStemmedVersion nawsv, int radius) throws Exception {
		return getNumberOfMatchingCoTsForArticles(nawsv, 1, radius);
	}
	
	public static int getNumberOfNegativeCoTsForArticles(NewsArticleWithStemmedVersion nawsv, int radius) throws Exception {
		return getNumberOfMatchingCoTsForArticles(nawsv, -1, radius);
	}
	
	public static int getNumberOfNeutralCoTsForArticles(NewsArticleWithStemmedVersion nawsv, int radius) throws Exception {
		return getNumberOfMatchingCoTsForArticles(nawsv, 0, radius);
	}
	
	public String toString() {
		return map.toString();
	}
	
	public LinkedHashMap sortHashMapByValuesD(HashMap passedMap) {
		   List mapKeys = new ArrayList(passedMap.keySet());
		   List mapValues = new ArrayList(passedMap.values());
		   Collections.sort(mapValues);
		   Collections.sort(mapKeys);

		   LinkedHashMap sortedMap = new LinkedHashMap();

		   Iterator valueIt = mapValues.iterator();
		   while (valueIt.hasNext()) {
		       Object val = valueIt.next();
		       Iterator keyIt = mapKeys.iterator();

		       while (keyIt.hasNext()) {
		           Object key = keyIt.next();
		           String comp1 = passedMap.get(key).toString();
		           String comp2 = val.toString();

		           if (comp1.equals(comp2)){
		               passedMap.remove(key);
		               mapKeys.remove(key);
		               sortedMap.put((String)key, (Integer)val);
		               break;
		           }

		       }

		   }
		   return sortedMap;
	}
	
	public void generateChiSquaredCots(int index) throws JsonSyntaxException, IOException{
		double ccFreq = 0;
		int freqTermV = 0;
		int freqTermU = 0;
		int N = 0;
		
		
		Gson gson = new Gson();
		TextFileHandler tfh = new TextFileHandler();
		
		WordCountList cotsWords = gson.fromJson(tfh.getWclList(), WordCountList.class);
		HashMap<String, Double> cots = gson.fromJson(tfh.getCots(), HashMap.class);
		
		System.out.println("Cotswords" + cotsWords.getWords().size() + " Hashmap size: " + cots.size());
		
		System.out.println(cots.get("aksje i"));
		
		ccFreq = cots.get("aksje i");
		
		
		for(int i=0; i<cotsWords.getWords().size(); i++){
			if(cotsWords.getWords().get(i).getWord() == "aksje"){
				freqTermV = cotsWords.getWords().get(i).getCounter();
			}
			else if(cotsWords.getWords().get(i).getWord() == "i"){
				freqTermU = cotsWords.getWords().get(i).getCounter();
			}
		}
		N = cotsWords.getTotalTitleCount()+cotsWords.getTotalLeadTextCount();
		System.out.println("CCFREQ: " +  ccFreq + "FREQTERMV " + freqTermV + "FREQTERMU " +  freqTermU + " N"  + N);

	}
	
	
	
	
	
	public static void main(String[] args) throws Exception {
//		CoTCounter cc = new CoTCounter(10);
//		cc.generateChiSquaredCots(10);
		
		
//		JsonHandler jh = new JsonHandler("/ArticleSteps/4_StemmedArticles/MainDataSetStemmed.json", "stemmed");
//
//		CoTCounter cc = new CoTCounter("cotsTFDF.json", 10);
//		cc.cotCountArticlesNotMainText(jh);
//		
//		HashMap<String, Integer> sortedHashmap = new HashMap<String, Integer>();
//		Iterator it = cc.map.entrySet().iterator();
//		
//		
//		while (it.hasNext()) {
//				
//		        Map.Entry pairs = (Map.Entry)it.next();
//		        System.out.println(pairs.getKey() + " = " + pairs.getValue());
//		        if(Integer.parseInt(pairs.getValue().toString()) > 0){
//		        	System.out.println("DSANT");
//		        	sortedHashmap.put(pairs.getKey().toString(), Integer.parseInt(pairs.getValue().toString()));	
//		        }
//		        it.remove(); // avoids a ConcurrentModificationException
//		}
//			
//		
//		cc.writeSortedMapToFile(cc.sortHashMapByValuesD(sortedHashmap));
//		cc.writeMapToFile();
		
		
		
		JsonHandler jh = new JsonHandler("/ArticleSteps/4_StemmedArticles/MainDataSetStemmed.json", "stemmed");

		CoTCounter cc = new CoTCounter("cotsTFDF.json", 10, true);
		cc.cotCountArticlesNotMainTextTFDF(jh);
		
//		HashMap<String, CotCountTFDF> sortedHashmap = new HashMap<String, CotCountTFDF>();
//		Iterator it = cc.map.entrySet().iterator();
//		
//		
//		while (it.hasNext()) {
//				
//		        Map.Entry pairs = (Map.Entry)it.next();
//		        System.out.println(pairs.getKey() + " = " + pairs.getValue());
//		        if(Integer.parseInt(pairs.getValue().toString()) > 0){
//		        	System.out.println("DSANT");
//		        	sortedHashmap.put(pairs.getKey().toString(), Integer.parseInt(pairs.getValue().toString()));	
//		        }
//		        it.remove(); // avoids a ConcurrentModificationException
//		}
			
		
//		cc.writeSortedMapToFile(cc.sortHashMapByValuesD(sortedHashmap));
		cc.writeMapToFile();
		//System.out.println(getNumberOfPositiveCoTsForArticles(nawsv, 2));
		//System.out.println(getNumberOfNeutralCoTsForArticles(nawsv, 2));
		//System.out.println(getNumberOfNegativeCoTsForArticles(nawsv, 2));
	}
}
package cot;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;

import preProcessing.NewsArticleWithStemmedVersion;
import preProcessing.PosTaggedWord;
import preProcessing.TextFileHandler;
import newsAPI.JsonHandler;

public class CoTCounter {
	
	/*
	 * This class counts the number of CoTs within distance r in a news artile
	 */
	
	public HashMap<String, Integer> map;
	public int radius;
	public String filename;
	
	public CoTCounter(String filename, int radius) {
		this.map = new HashMap<String, Integer>();
		this.filename = filename;
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

	public static HashMap<String, Integer> cotCountArticles(NewsArticleWithStemmedVersion nawsv, int radius) {
		HashMap<String, Integer> hashmap = new HashMap<String, Integer>();
			for (int i = 0; i < nawsv.getAllPosTaggedWords().size();i++) {
				ArrayList<String> cots = getCoTsFromIndex(nawsv.getAllPosTaggedWords(), i, radius);
				hashmap = addCoTsToMap(hashmap, cots);
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
	
	public static void main(String[] args) throws Exception {
		JsonHandler jh = new JsonHandler("/ArticleSteps/4_StemmedArticles/ArticleGeneratorTestStemmed.json", "stemmed");
		NewsArticleWithStemmedVersion nawsv = jh.stemmedArticles.getNawsv().get(0);
//		CoTCounter cc = new CoTCounter("cots.json", 2);
//		cc.cotCountArticles(jh);
//		cc.writeMapToFile();
		System.out.println(getNumberOfPositiveCoTsForArticles(nawsv, 2));
		System.out.println(getNumberOfNeutralCoTsForArticles(nawsv, 2));
		System.out.println(getNumberOfNegativeCoTsForArticles(nawsv, 2));
	}
}
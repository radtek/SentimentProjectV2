package featureExctraction;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;

import preProcessing.NewsArticlesWithPosTaggedWords;
import newsAPI.JsonHandler;

import com.google.gson.Gson;

public class WordCountList {

	public ArrayList<WordCount> words = new ArrayList<WordCount>();
	
	
	public WordCountList(){
		
	}
	
	public void addWord(String word){
		boolean alreadyExists = false;
		for(int i=0; i<words.size(); i++){
			if(words.get(i).getWord().equals(word)){
				words.get(i).counter += 1;
				alreadyExists = true;
			}
		}
		if(!alreadyExists){
			WordCount newWord = new WordCount(word);
			words.add(newWord);
		}
	}

	public void writeToFileAsJson() throws IOException{
			Gson gson = new Gson();
			String json = gson.toJson(this);
			Writer out = new BufferedWriter(new OutputStreamWriter(
			    new FileOutputStream("C:/Users/Lars/Desktop/SentimentProject/SentimentAnalysis/src/xml/mostFrequentAdverbs.txt"), "UTF-8"));
			try {
			    out.write(json);
			} finally {
			    out.close();
			}
	}

	public ArrayList<WordCount> getWords() {
		return words;
	}
	public void setWords(ArrayList<WordCount> words) {
		this.words = words;
	}
	public void sortWords(){
		ArrayList<WordCount> sorted = this.getWords();
		Collections.sort(sorted, new WordCountComparator());
		this.setWords(sorted);
		
	}
	
	
	public static void main(String args[]) throws IOException{
		JsonHandler handler = new JsonHandler();
		Gson g = new Gson();
		
		
		NewsArticlesWithPosTaggedWords articleSource = g.fromJson(handler.getJsonSource(), NewsArticlesWithPosTaggedWords.class); 
		WordCountList wcl = new WordCountList();
		
		for(int i=0; i<articleSource.getNawpti().size(); i++){
			System.out.println("Counter: " + i);
			if(articleSource.getNawpti().get(i).getPosTaggedTitle().getPosTaggedWords() != null)
				for(int a=0; a<articleSource.getNawpti().get(i).getPosTaggedTitle().getPosTaggedWords().length; a++){
					if(articleSource.getNawpti().get(i).getPosTaggedTitle().getPosTaggedWords()[a].getWordclass().equals("adv")){
						wcl.addWord(articleSource.getNawpti().get(i).getPosTaggedTitle().getPosTaggedWords()[a].getInput());
					}
				
				}
			if(articleSource.getNawpti().get(i).getPosTaggedLeadText().getPosTaggedWords() != null)
				for(int b=0; b<articleSource.getNawpti().get(i).getPosTaggedLeadText().getPosTaggedWords().length; b++){
					if(articleSource.getNawpti().get(i).getPosTaggedLeadText().getPosTaggedWords()[b].getWordclass().equals("adv")){
						wcl.addWord(articleSource.getNawpti().get(i).getPosTaggedLeadText().getPosTaggedWords()[b].getInput());
					}
				}
			if(articleSource.getNawpti().get(i).getPosTaggedMainText().getPosTaggedWords() != null)
				for(int c=0; c<articleSource.getNawpti().get(i).getPosTaggedMainText().getPosTaggedWords().length; c++){
					if(articleSource.getNawpti().get(i).getPosTaggedMainText().getPosTaggedWords()[c].getWordclass().equals("adv")){
						wcl.addWord(articleSource.getNawpti().get(i).getPosTaggedMainText().getPosTaggedWords()[c].getInput());
					}
				}
		}
		wcl.sortWords();
		wcl.writeToFileAsJson();
	
	}

}

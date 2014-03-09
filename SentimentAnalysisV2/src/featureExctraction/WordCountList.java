package featureExctraction;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;

import preProcessing.NewsArticlesWithPosTaggedWords;
import utils.ArticleSorter;
import newsAPI.JsonHandler;

import com.google.gson.Gson;

public class WordCountList {

	public ArrayList<WordCount> words = new ArrayList<WordCount>();
	public int totalTitleCount;
	public int totalLeadTextCount;
	
	

	public WordCountList(){
		this.totalTitleCount = 0;
		this.totalLeadTextCount = 0;
	}
	
	public void addWordTF(String word, String position){
		boolean alreadyExists = false;
		for(int i=0; i<words.size(); i++){
			if(words.get(i).getWord().equals(word)){
				words.get(i).termFrequency += 1;
				alreadyExists = true;
				if(position == "title"){
					words.get(i).titleCounter+=1;
				}
				else{
					words.get(i).leadCounter+=1;
				}
			}
		}
		if(!alreadyExists){
			WordCount newWord = new WordCount(word);
			words.add(newWord);
		}
	}
	public void addWordDF(String word, String position){
		for(int i=0; i<words.size(); i++){
			if(words.get(i).getWord().equals(word)){
				words.get(i).documentFrequency += 1;
			}
		}
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
	
	public int getTotalTitleCount() {
		return totalTitleCount;
	}

	public void setTotalTitleCount(int totalTitleCount) {
		this.totalTitleCount = totalTitleCount;
	}

	public int getTotalLeadTextCount() {
		return totalLeadTextCount;
	}

	public void setTotalLeadTextCount(int totalLeadTextCount) {
		this.totalLeadTextCount = totalLeadTextCount;
	}
	
	public static void main(String args[]) throws IOException{
		JsonHandler handler = new JsonHandler("ArticleSteps/3_POStaggedArticles/MainDataSetPOS.json", "pos");
		Gson g = new Gson();
		WordCountList wcl = new WordCountList();	
		
		NewsArticlesWithPosTaggedWords articleSource = handler.getPosTaggedArticles(); 
		System.out.println(articleSource.getNawpti().size());
		
		for(int i=0; i< articleSource.getNawpti().size(); i++){
			
			ArrayList<String> wordsInTitle = new ArrayList<String>();
			ArrayList<String> wordsInLead = new ArrayList<String>();
			
			if(articleSource.getNawpti().get(i).getPosTaggedTitle().getPosTaggedWords()!=null){
			
				wcl.totalTitleCount+=1;
				for(int j=0; j<articleSource.getNawpti().get(i).getPosTaggedTitle().getPosTaggedWords().length; j++){
					
					String currentWord = articleSource.getNawpti().get(i).getPosTaggedTitle().getPosTaggedWords()[j].getInput();
					
					//ADD WORD TO TF
					wcl.addWordTF(currentWord, "title");
					
					//CHECK IF TITLE HAS CURRENT WORD AND ADD TO DF IF NOT
					if(!wordsInTitle.contains(currentWord)){
						wcl.addWordDF(currentWord, "title");
					}
					
					//ADD WORD TO TITLE WORDS OVERVIEW LIST
					wordsInTitle.add(currentWord);
					
				}
			}
			if(articleSource.getNawpti().get(i).getPosTaggedLeadText().getPosTaggedWords()!=null){
				wcl.totalLeadTextCount+=1;
				for(int k=0; k<articleSource.getNawpti().get(i).getPosTaggedLeadText().getPosTaggedWords().length; k++){
					
					
					String currentWord = articleSource.getNawpti().get(i).getPosTaggedLeadText().getPosTaggedWords()[k].getInput();
					
					//CHECK IF WORDS IN TITLE OR WORDS IN LEAD CONTAINS WORD
					if(wordsInTitle.contains(currentWord)|| wordsInLead.contains(currentWord)){
						wcl.addWordTF(articleSource.getNawpti().get(i).getPosTaggedLeadText().getPosTaggedWords()[k].getInput(), "lead");
					}
					else{
						wcl.addWordTF(articleSource.getNawpti().get(i).getPosTaggedLeadText().getPosTaggedWords()[k].getInput(), "lead");
						wcl.addWordDF(articleSource.getNawpti().get(i).getPosTaggedLeadText().getPosTaggedWords()[k].getInput(), "lead");
					}
					
					wordsInLead.add(articleSource.getNawpti().get(i).getPosTaggedLeadText().getPosTaggedWords()[k].getInput());
					
				}
			}
		}

		wcl.sortWords();
		String wclNew = g.toJson(wcl);
		wcl.writeToArticleFile(wclNew, wcl.getPath()+"WordlistsOfImportance/", "WCLCOTS");
	
	}

}

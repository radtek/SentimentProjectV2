package featureExctraction;

public class WordCount {
	
	int counter;
	String word;
	String sentimentValue;
	
	


	public WordCount(String word){
		this.word = word;
		this.counter = 0;
		this.sentimentValue = "0";
	}


	public int getCounter() {
		return counter;
	}


	public void setCounter(int counter) {
		this.counter = counter;
	}


	public String getWord() {
		return word;
	}


	public void setWord(String word) {
		this.word = word;
	}

	public String getSentimentValue() {
		return sentimentValue;
	}


	public void setSentimentValue(String sentimentValue) {
		this.sentimentValue = sentimentValue;
	}
	
	
	

}

package utils;

public class Stemmer extends norwegianStemmer {
	
	
	
	public Stemmer(){
		
	}
	
	public static void main(String[] args){
		Stemmer s = new Stemmer();
		s.setCurrent("Dansende");
		s.stem();
		System.out.println(s.getCurrent());	
	}
}

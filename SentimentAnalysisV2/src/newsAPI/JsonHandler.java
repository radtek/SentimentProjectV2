package newsAPI;

import com.google.gson.*;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;


public class JsonHandler {
	
	
	public NewsArticlesRaw articles;
	public String jsonSource;
	


	public JsonHandler() throws IOException{
		
	}
	
	public JsonHandler(String articleSource) throws IOException{
		this.jsonSource = getJsonSource(this.getPath()+articleSource);
		this.articles = newsArticles(this.jsonSource);
	
	}
	
	
	
	
	/*FIELD NAMING STRATEGY FOR GSON*/
	public class MyFieldNamingStrategy implements FieldNamingStrategy {
	    public String translateName(Field arg0) {
	        String result = "";
	        String name = arg0.getName();
	        for(char ch : name.toCharArray()){
	        	if(ch == '-'){
	        		ch = '_';
	        	}
	        	else if(ch == '_'){
	        		ch = '-';
	        	}
	            result += ch;
	        }
	        return result;
	    }
	}
	
	/* READS FILE */
	static String readFile(String path, Charset encoding) throws IOException 
	{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return encoding.decode(ByteBuffer.wrap(encoded)).toString();
	}
	
	/* GET THE JSON SOURCE OF FILE */
	public String getJsonSource(String source) throws IOException{
		String jsonFile = readFile(source, StandardCharsets.UTF_8);
		return jsonFile;
	}

	/* CREATES NewsArticlesRaw object from JSON source */
	public NewsArticlesRaw newsArticles(String jsonSource){
		MyFieldNamingStrategy strategy = new MyFieldNamingStrategy();
		Gson gson = new GsonBuilder().setFieldNamingStrategy(strategy).create();
		NewsArticlesRaw articles = gson.fromJson(jsonSource, NewsArticlesRaw.class);
		return articles;
	}
	

	
	/* METHOD FOR DETERMINING RELATIVE PATHS */
	public String getPath() {
	    String path = String.format("%s/%s", System.getProperty("user.dir"), this.getClass().getPackage().getName().replace(".", "/"));
	    return path.split("/")[0]+"/ArticleResources/";
	}
	
	/* GETTERS AND SETTERS */
	public NewsArticlesRaw getArticles() {
		return articles;
	}
	public void setArticles(NewsArticlesRaw articles) {
		this.articles = articles;
	}
	public String getJsonSource() {
		return jsonSource;
	}

	public void setJsonSource(String jsonSource) {
		this.jsonSource = jsonSource;
	}

	
	/* TEST METHOD */
	public static void main(String[] args) throws IOException{
		JsonHandler handler = new JsonHandler("/Misc/lars_annotated_1000_1426_19_13.json");
		System.out.println(handler.getArticles().articles[0].text);
	}

}

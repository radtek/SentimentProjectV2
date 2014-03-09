package utils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import preProcessing.TextFileHandler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import cot.CotChiSquared;
import cot.CotCountTFDF;
import featureExctraction.WordCountList;

public class CotRankingHelper {
	
	
	
	public void generateChiSquaredList () throws JsonSyntaxException, IOException{
		
		ArrayList<CotChiSquared> chiSquaredList = new ArrayList<CotChiSquared>();
		
	
		
		
//		Co-occurring frequency mellom to termer (u og v) innenfor radius r: freq_r(u,v)
//		Frequency (antall title + lead term u forekommer) av term u: freq(u)
//		Frequency (antall title + lead term u forekommer) av term u: freq(v)
//		antall titles + lead: N
//
//		Kan da regne ut:
//		chi-sq = (freq_r(u,v) - N * freq(u) * freq(v)) ^2 / (N * freq(u) * freq(v))
		
		Gson gson = new Gson();

		WordCountList allWordsTFDF = gson.fromJson(TextFileHandler.getWclList(), WordCountList.class);
		Type stringIntegergMap = new TypeToken<HashMap<String, CotCountTFDF>>(){}.getType();
		HashMap<String, CotCountTFDF> cotsTFDF = gson.fromJson(TextFileHandler.getCotsTFDF(), stringIntegergMap);
		
		for (String key : cotsTFDF.keySet()) {
			
				double ccTF = 0;
				int vTF = 0;
				int uTF = 0;
				int N = 0;
			   //System.out.println("Key = " + key + " - " + cotsTFDF.get(key));
				String v = "";
				String u = "";
					
				v = key.split(" ")[0];
			   
			   if(key.split(" ").length>1){
				   u = key.split(" ")[1];
			   }
			   
			   for(int i=0; i<allWordsTFDF.getWords().size(); i++){
				   if(allWordsTFDF.getWords().get(i).getWord().equals(v)){
					   System.out.println("FANT V");
					   vTF = allWordsTFDF.getWords().get(i).getTermFrequency();
				   }
				   else if(allWordsTFDF.getWords().get(i).getWord().equals(u)){
					   System.out.println("FANT U");
					   uTF = allWordsTFDF.getWords().get(i).getTermFrequency();
				   }
			   }
			   
			   ccTF = cotsTFDF.get(key).getTermFrequency();
			   N = allWordsTFDF.getTotalTitleCount()+allWordsTFDF.getTotalLeadTextCount();
			   
			   
			   System.out.println("U: " + u +" V: " + v);
			   
			   System.out.println("ALLE VERDIER: " + "CCTF: " + ccTF + " VTF: " + vTF + " UTF: " + uTF + " N: " + N);
		}
		
		
		
		System.out.println("Cotswords" + allWordsTFDF.getWords().size() + " Hashmap size: " + cotsTFDF.size());
		
		
		
	}
	
	
	public static void main(String[] args) throws JsonSyntaxException, IOException{
		CotRankingHelper crh = new CotRankingHelper();
		crh.generateChiSquaredList();
		
	}

}
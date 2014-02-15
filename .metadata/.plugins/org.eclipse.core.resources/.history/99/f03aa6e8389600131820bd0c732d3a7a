package featureExctraction;

import java.util.ArrayList;

public class FeaturesVektor {
	

	public ArrayList<Double> featureVector;
	public ArrayList<Double> sentimentVector;
	
	
	public FeaturesVektor(){
		this.featureVector = new ArrayList<Double>();
		this.sentimentVector = new ArrayList<Double>();
	}


	public ArrayList<Double> getFeatureVector() {
		return featureVector;
	}

	/* RETURNS THE FEATURE VECTOR OF AN ARTICLE AS ARRAY */
	public double[] getFeatureVectorAsArray() {
		double[] array = new double[this.featureVector.size()];
		int i = 0;
		for (Double d : this.featureVector) {
			array[i] = d;
			i++;
		}
		return array;
		
	}
	
	/* RETURNS SENTIMENT VECTOR AS ARRAY */
	public double[] getSentimentAsArray() {
		return new double[]{this.sentimentVector.get(0)};
		
	}

	
	public void setFeatureVector(ArrayList<Double> featureVector) {
		this.featureVector = featureVector;
	}
	public ArrayList<Double> getSentimentVector() {
		return sentimentVector;
	}
	public void setSentimentVector(ArrayList<Double> sentimentVector) {
		this.sentimentVector = sentimentVector;
	}





	



	
	
}

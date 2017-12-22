package test;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class algorithmMain {

	public static void main(String[] args) {

		double[] values = {-5.69,-4.58,-3.21,-2.69,-1.59,0.42,1.65,2.97,1.23,0.52,-1.30,-2.47,-3.68,-2.54,-1.24,0.85,1.59,2.08,3.05};
		//double[] values = {-5.0,10.0,10.0,14.0,14.0,8.0,8.0,6.0,6.0,-3.0,2.0,2.0,2.0,2.0,-3.0};
		int[] trends = new int[values.length-1];
		double[] diffs = diffValue(values);
		for (int index=diffs.length-1; index>=0; index--) {
			double diffVal = diffs[index];
			int trendVal = diffVal>0?1:diffVal<0?-1:0;
			trends[index] = trendVal;
		}
		for (int index=0; index<trends.length-2; index++) {
			if (trends[index]==0) {
				int tempVal = trends[index+1]>=0?1:-1;
				trends[index] = tempVal;
			}
		}
		List<Double> peakList = new ArrayList<Double>();
		List<Double> troughList = new ArrayList<Double>();
		int[] rArray = diffValue(trends);
		for (int index=0; index<rArray.length; index++) {
			if (rArray[index] == -2) peakList.add(values[index+1]);
			else if (rArray[index] == 2) troughList.add(values[index+1]);
		}
		System.out.println("²¨·åÖµ: " + StringUtils.join(peakList.toArray(), ","));
		System.out.println("²¨¹ÈÖµ: " + StringUtils.join(troughList.toArray(), ","));
	}

	private static double[] diffValue(double[] values) {
		double[] diffs = new double[values.length-1];
		for (int index=values.length-2; index>=0; index--) {
			diffs[index] = values[index+1] - values[index];
		}
		return diffs;
	}

	private static int[] diffValue(int[] values) {
		int[] diffs = new int[values.length-1];
		for (int index=values.length-2; index>=0; index--) {
			diffs[index] = values[index+1] - values[index];
		}
		return diffs;
	}
}

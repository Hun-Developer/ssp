package ssp_01_solution;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class P4SortAggregation1 {

	public static void main(String[] args) {
		String filename = "data/ssp_01/score2.txt";
		
		ArrayList<Score> scoresM = new ArrayList<>();
		
		// File input
		try(BufferedReader br = new BufferedReader(new FileReader(filename))){
			String line;
			while((line = br.readLine()) != null){
				if(line.startsWith("M")) {
					scoresM.add(Score.parseM(line));
				} 
			}
		} catch (Exception e) {}
		
		// Sort
		Collections.sort(scoresM, new Comparator<Score>() {
			@Override
			public int compare(Score o1, Score o2) {
				if(o1.total != o2.total) return o2.total - o1.total;
				else if(o1.score1 != o2.score1) return o2.score1 - o1.score1;
				else return o1.testNum - o2.testNum;
			}
		});
		
		// Print result
		for(int i = 0 ; i < 3 ; i++) {
			Score s = scoresM.get(i);
			System.out.println(s.testNum+","+s.total);
		}
	}

}

package ssp_01_solution;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class P3FileIO {

	public static void main(String[] args) {

		String filename = "data/ssp_01/score.txt";
		
		ArrayList<Score> scoresM = new ArrayList<>();
		ArrayList<Score> scoresL = new ArrayList<>();
		
		try(BufferedReader br = new BufferedReader(new FileReader(filename))){
			String line;
			while((line = br.readLine()) != null){
				if(line.startsWith("M")) {
					scoresM.add(Score.parseM(line));
				} else if(line.startsWith("L")) {
					scoresL.add(Score.parseL(line));
				}
			}
		} catch (Exception e) {}
		
		double sumM = 0, sumL = 0;
		for(Score s : scoresM) sumM += s.total;
		for(Score s : scoresL) sumL += s.total;
		
		System.out.println(sumM/scoresM.size());
		System.out.println(sumL/scoresL.size());
	}

}

package ssp_01_solution;

import java.util.ArrayList;
import java.util.Scanner;

public class P2DataManipulation2 {

	public static void main(String[] args) {
		ArrayList<Score> scoresM = new ArrayList<>();
		ArrayList<Score> scoresL = new ArrayList<>();
		
		Scanner sc = new Scanner(System.in);
		String line;
		
		while((line=sc.nextLine()) != null) {
			if("Q".equals(line)) {
				break;
			} else if(line.startsWith("M")) {
				scoresM.add(Score.parseM(line));
			} else if(line.startsWith("L")) {
				scoresL.add(Score.parseL(line));
			}
		}
		
		double sumM = 0, sumL = 0;
		for(Score s : scoresM) sumM += s.total;
		for(Score s : scoresL) sumL += s.total;
		
		System.out.println(sumM/scoresM.size());
		System.out.println(sumL/scoresL.size());
	}

}

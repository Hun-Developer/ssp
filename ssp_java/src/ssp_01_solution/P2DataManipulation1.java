package ssp_01_solution;

import java.util.ArrayList;
import java.util.Scanner;

public class P2DataManipulation1 {

	public static void main(String[] args) {
		ArrayList<Score> scores = new ArrayList<>();
		
		Scanner sc = new Scanner(System.in);
		String line;
		
		while((line=sc.nextLine()) != null) {
			if("Q".equals(line)) {
				break;
			} else if(line.startsWith("M")) {
				scores.add(Score.parseM(line));
			}
		}
		
		double sum = 0;
		for(int i = 0 ; i < scores.size() ; i++) sum += scores.get(i).total;
		System.out.println(sum/scores.size());
	}
}

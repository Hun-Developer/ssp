package ssp_01_solution;

import java.util.Scanner;

public class P1ConsoleIO {

	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		String line;
		int cnt = 0, sum = 0;
		
		while((line=sc.nextLine()) != null) {
			if("Q".equals(line)) {
				break;
			} else {
				sum += Integer.valueOf(line);
				cnt++;
			}
		}
		
		System.out.println(sum);
		System.out.println(Double.valueOf(sum)/cnt);
	}

}

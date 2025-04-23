package ssp_01_solution;

public class Score{
	String test;	//구분
	String year;		//년도
	String seq;		//회차
	int testNum;	//수험번호
	int score1;		//필기점수
	int score2;		//실기점수
	int total;		//종합점수
	public Score(String test, String year, String seq, int testNum, int score1, int score2) {
		super();
		this.test = test;
		this.year = year;
		this.seq = seq;
		this.testNum = testNum;
		this.score1 = score1;
		this.score2 = score2;
		this.total = score1+score2;
	}
	
	public static Score parseM(String line) {
		String[] item = line.split("#");
		Score s = new Score(
				item[0], 
				item[1], 
				item[2], 
				Integer.parseInt(item[3]), 
				Integer.parseInt(item[4]), 
				Integer.parseInt(item[5]));
		return s;
	}
	
	public static Score parseL(String line) {
		//L 2020 1 40001 80 20
		//0 1234 5 67890 11 13
		Score s = new Score(
				line.substring(0,1),
				line.substring(1,5),
				line.substring(5,6),
				Integer.parseInt(line.substring(6,11)),
				Integer.parseInt(line.substring(11,13)),
				Integer.parseInt(line.substring(13,15)));
		return s;
	}
}

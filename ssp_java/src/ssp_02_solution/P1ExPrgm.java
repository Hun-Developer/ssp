package ssp_02_solution;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class P1ExPrgm {

	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		String line;
		
		while((line=sc.nextLine()) != null) {
			if("Q".equals(line)) {
				break;
			} else {
				String execResult = runExprgm(line);
				System.out.print(line+"#"+execResult);
			}
		}
		
		sc.close();
	}

	public static String runExprgm(String param) {
		// 실행할 명령
		List<String> command = new ArrayList<>();
		command.add("data/ssp_02/bin/exprgm.exe");
		command.add(param);
		
		// 프로세스 객체 생성
		ProcessBuilder pBuilder = new ProcessBuilder();
		pBuilder.redirectErrorStream(true);
		pBuilder.command(command);
		
		// 프로세스 실행 결과값
		String result = "";
		
		try {
			// 프로세스 실행
			Process proc = pBuilder.start();
			
			// 프로세스의 출력을 읽어서 값이 Null이 아니면 결과값에 추가
			try(BufferedReader buffReader = new BufferedReader(new InputStreamReader(proc.getInputStream()))){
				String tmpLine = null;				
				while((tmpLine = buffReader.readLine())!=null){
					result += tmpLine + "\n";
				}
			}
			
			// 프로세스가 끝날때 까지 대기
			proc.waitFor();
		} catch (IOException | InterruptedException e) {
		} 

		return result;
	}
}

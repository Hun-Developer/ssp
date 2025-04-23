package ssp_01_solution;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class P5FileSystem {
	public static void main(String[] args) {
		String checkRoot = "data/ssp_01/q05";

		ArrayList<String> result = checkSecurity(checkRoot);
		Collections.sort(result);
		
		for(int i = 0 ; i < result.size() ; i++) {
			System.out.println(result.get(i));
		}
	}
	
	// 파일 보안 점검
	private static ArrayList<String> checkSecurity(String root){
		ArrayList<String> result = new ArrayList<>();
		
		String[] list = new File(root).list();
		for(String fn : list) {
			HashSet<String> exList = exceptList(root);			
			String fullPath = root + File.separatorChar + fn;
			
			if(new File(fullPath).isDirectory()) {
				// 하위폴더 점검
				ArrayList<String> subDirResult = checkSecurity(fullPath);
				result.addAll(subDirResult);
			} else {
				// 파일 점검
				if((fn.endsWith(".xlsx") || fn.endsWith(".pptx") || fn.endsWith(".pdf"))
					&& !(fn.startsWith("S_") || fn.startsWith("C_"))
					&& !exList.contains(fn)) {
					// 위반 파일
					result.add(fn);
				}				
			}
		}
		
		return result;
	}
	
	// 예외파일 목록 읽기
	private static HashSet<String> exceptList(String dirName){
		HashSet<String> exSet = new HashSet<>();
		
		File exFile = new File(dirName + File.separatorChar + "except_policy.txt");
		if(exFile.exists()) {
			try(BufferedReader br = new BufferedReader(new FileReader(exFile))){
				String line;
				while((line=br.readLine())!= null) {
					exSet.add(line);
				}
			} catch(Exception e) {}
		}
		
		return exSet;
	}
}

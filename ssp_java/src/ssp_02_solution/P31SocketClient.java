package ssp_02_solution;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;
import java.util.Scanner;

public class P31SocketClient {
	
	private static final String DOC_ROOT = "data/ssp_02/src/";

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		String line;
		
		while((line=sc.nextLine()) != null) {
			if("Q".equals(line)) {
				break;
			} else {
				// 파일 존재여부 체크
				File file = new File(DOC_ROOT+line);
				if(!file.exists() || file.isDirectory()) continue;
				
				// 파일전송
				sendFile(file);
			}
		}
		
		sc.close();
	}

	private static void sendFile(File file) {
		try (Socket sock = new Socket("127.0.0.1", 5000);
			BufferedInputStream bi = new BufferedInputStream(sock.getInputStream());
			BufferedOutputStream bo = new BufferedOutputStream(sock.getOutputStream())) {
			if (!sock.isConnected()) return;
			
			// 파일정보 전송
			String header = "SEND#"+file.getName()+"#"+file.length();
			bo.write(header.getBytes());
			bo.flush();
			
			
			// 전송준비완료 응답수신
			byte[] buffer = new byte[1000];
			int readBytes = 0;
			String aspectRes = "READY#"+file.getName();
			String res = "";
			while((readBytes = bi.read(buffer)) > 0) {
				res += new String(buffer, 0, readBytes);
				if(aspectRes.equals(res)) break;
			}
			
			// 파일전송
			buffer = new byte[1000];
			readBytes = 0;
			try(FileInputStream fi = new FileInputStream(file)){				
				while((readBytes = fi.read(buffer)) > 0) {
					bo.write(buffer, 0, readBytes);
					bo.flush();
				}
			}
			
			//전송종료
			System.out.println("END#"+file.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

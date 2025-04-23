package ssp_02_solution;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class P32SockerServer {

	private static final String DOC_ROOT = "data/ssp_02/target/";
	
	public static void main(String[] args) {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(5000);
			
			while (true) {
				Socket client = serverSocket.accept();
				processSocket(client);
			}
		} catch (Exception e) {
		}		
	}

	private static void processSocket(Socket client) {
		String[] fileInfo = null;
		try(BufferedInputStream bi = new BufferedInputStream(client.getInputStream());
			BufferedOutputStream bo = new BufferedOutputStream(client.getOutputStream())){
			
			// 파일정보 수신
			byte[] buffer = new byte[1000];	
			int readBytes = bi.read(buffer);
			fileInfo = new String(buffer, 0, readBytes).split("#");
			
			// 파일수신대기 응답
			String ack = "READY#"+fileInfo[1];
			bo.write(ack.getBytes());
			bo.flush();
			
			// 파일데이터 수신
			readBytes = 0;
			buffer = new byte[1000];
			long fileSize = Long.valueOf(fileInfo[2]);
			try(FileOutputStream fo = new FileOutputStream(DOC_ROOT+fileInfo[1])){
				long totalBytes = 0;
				while((readBytes = bi.read(buffer)) > 0) {
					fo.write(buffer, 0, readBytes);
					totalBytes += readBytes;
					if(totalBytes >= fileSize) break;
				}
			} catch (IOException e) {
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

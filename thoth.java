package bb;

/**
 * @author Batuhan Büyükgüzel
 * @version 0.0.2
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class thoth {

	public static final String atig = "http://fx.atig.com.tr/veriyayini/forexverileri.aspx";
	public static BufferedReader in;
	static String[] parity = new String[13];
	static String[] buy = new String[13];
	static String[] sell = new String[13];
	static String[] time = new String[13];
	
	

	public static void main(String[] args) throws IOException {
		URLReader(atig);
		URLParse();
		
		for(int i=0; i<13; i++){
			System.out.println(parity[i]);
			System.out.println(buy[i]);
			System.out.println(sell[i]);
			System.out.println(time[i]);
			System.out.println("--------");
		}
		
	}


	public static void URLParse() {
		int begin = 1;
		int end = 0;
		String inputLine;
		StringBuilder s = new StringBuilder();
		try {
			while ((inputLine = in.readLine()) != null) {
				if (inputLine.length() == 0 || inputLine.length() == 8 || inputLine.length() == 5)
					continue;
				else
					s.append(inputLine);
			}
			in.close();

			for(int i=0; i<13; i++){
				begin = s.indexOf("bir", end)+6;
				end = s.indexOf("</", begin);
				parity[i] = s.substring(begin, end);
				
				begin = s.indexOf("</i", end)+5;
				end = s.indexOf("</", begin);
				sell[i] = s.substring(begin, end);
				
				begin = s.indexOf("</i", end)+5;
				end = s.indexOf("</", begin);
				buy[i] = s.substring(begin, end);
				
				begin = s.indexOf("\">", end)+22;
				end = s.indexOf("</", begin);
				time[i] = s.substring(begin, end);
			}


		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public static BufferedReader URLReader(String s) {
		try {
			URL url = new URL(s);
			in = new BufferedReader(new InputStreamReader(url.openStream()));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return in;
	}

}

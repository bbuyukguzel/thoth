package thoth;

/**
 * @author Batuhan Büyükgüzel
 * @version 0.0.5
 */

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class thoth {

	public static final String atig = "http://fx.atig.com.tr/veriyayini/forexverileri.aspx";
	public static final String investing = "http://www.investing.com/common/technical_summary/api.php?action=TSB_updatePairs&pairs=5,15,10,6,9,66,1,50655,2,8,7,3,18&timeframe=300";
	public static BufferedReader in;

	static String[] signal = new String[13];
	static String x = "a";

	static String[] parity = new String[13];
	static String[] buy = new String[13];
	static String[] sell = new String[13];
	static String[] time = new String[13];


	public static void main(String[] args) throws IOException {

		try {
			sayMyName();
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	public static void sayMyName() throws SQLException, ClassNotFoundException {

		String url = "jdbc:mysql://localhost/thoth_db";
		String user = "root";
		String password = "XXXXX";
		Class.forName("com.mysql.jdbc.Driver");
		Connection con = DriverManager.getConnection(url, user, password);
		
		Statement stmt = con.createStatement();

		while (true) {

			URLReader(atig);
			URLParse();
			URLReader(investing);
			URLParse2();

			String timeStamp = new SimpleDateFormat("dd/MM/yyyy")
					.format(Calendar.getInstance().getTime());

			for (int i = 0; i < 13; i++) {

				stmt.execute("INSERT INTO data (parity,buy,sell,time,signal_) VALUES ('"+parity[i]+"','"+buy[i]+"','"+sell[i]+"','"+time[i]+"','"+signal[i]+"')");
			}

			oneMomentPlease(); // wait 5 min
		}

	}


	public static void oneMomentPlease() {
		try {
			Thread.sleep(5 * 60 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


	public static void ParseQuotes() {
		String q = "\"";
		for (int i = 0; i < 13; i++) {
			if (signal[i].startsWith(q))
				signal[i] = signal[i].substring(1, signal[i].length() - 1);
		}
	}


	public static void URLParse2() {
		int begin = 1;
		int end = 0;
		String key = "technicalSummary\"";
		String inputLine;
		StringBuilder s = new StringBuilder();

		try {
			while ((inputLine = in.readLine()) != null) {
				s.append(inputLine);
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < 13; i++) {
			begin = s.indexOf(key, begin) + 18;
			end = s.indexOf(",\"", begin);
			signal[i] = s.substring(begin, end);
			begin = end;
		}

		ParseQuotes();

	}


	public static void URLParse() {
		int begin = 1;
		int end = 0;
		String inputLine;
		StringBuilder s = new StringBuilder();
		try {
			while ((inputLine = in.readLine()) != null) {
				if (inputLine.length() == 0 || inputLine.length() == 8
						|| inputLine.length() == 5)
					continue;
				else
					s.append(inputLine);
			}
			in.close();

			for (int i = 0; i < 13; i++) {
				begin = s.indexOf("bir", end) + 6;
				end = s.indexOf("</", begin);
				parity[i] = s.substring(begin, end);

				begin = s.indexOf("</i", end) + 5;
				end = s.indexOf("</", begin);
				sell[i] = s.substring(begin, end).replaceAll("\\s", "")
						.replaceAll(",", ".");

				begin = s.indexOf("</i", end) + 5;
				end = s.indexOf("</", begin);
				buy[i] = s.substring(begin, end).replaceAll("\\s", "")
						.replaceAll(",", ".");

				begin = s.indexOf("\">", end) + 22;
				end = s.indexOf("</", begin);
				time[i] = s.substring(begin, end).replaceAll("\\s", "");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public static BufferedReader URLReader(String s) {
		try {
			URL url = new URL(s);
			HttpURLConnection httpcon = (HttpURLConnection) url
					.openConnection();
			httpcon.addRequestProperty("User-Agent", "Mozilla/4.76");
			httpcon.connect();
			in = new BufferedReader(new InputStreamReader(
					httpcon.getInputStream()));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return in;
	}

}

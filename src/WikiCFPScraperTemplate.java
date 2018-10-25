import java.net.*;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;


public class WikiCFPScraperTemplate {
	public static int DELAY = 7;
	public static void main(String[] args) {

		try {			
			//String category = "data mining";
			//String category = "databases";
			//String category = "machine learning";
			String category = "artificial intelligence";

			int numOfPages = 20;

			//create the output file
			File file = new File("wikicfp_crawl_"+category+".csv");
			file.createNewFile();
			FileWriter writer = new FileWriter(file); 

			// create the header of the csv file
			writer.write("conference_acronym"); 
			writer.write("\t"); 
			writer.write("conference_name"); 
			writer.write("\t"); 
			writer.write("conference_location"); 
			writer.write("\n"); 

			//now start crawling the all 'numOfPages' pages
			for(int i = 1;i<=numOfPages;i++) {
				//Create the initial request to read the first page 
				//and get the number of total results
				String linkToScrape = "http://www.wikicfp.com/cfp/call?conference="+
						URLEncoder.encode(category, "UTF-8") +"&page=" + i;
				String content = getPageFromUrl(linkToScrape);
				//parse or store the content of page 'i' here in 'content'
				//YOUR CODE GOES HERE
				Document doc = Jsoup.connect(linkToScrape).get();
				// get the second data from the souce that contains the required information
				Element table = doc.select("table").get(2);
				// get all the rows from that table
				Elements rows = table.getElementsByTag("tr"); 
				// size of the rows
				int size = rows.size();

				// counter to when we get to rows with one td
				int one_col = 0;

				// start with the 8th value 
				for(int j = 8; j < size-2; j++) {
					// get the row
					Element row = rows.get(j);
					// get columns of that row
					Elements columns = row.select("td");

					if(columns.size() == 1) {
						// increment when column is only 1 (i.e. Expired CFPs column)
						one_col++;	        	    	
					}
					else if(columns.size() >1) {
						// even number of one column rows
						if(one_col%2==0) {
							// even rows
							if(j%2==0) {
								Element conference_acr = row.select("td").get(0);
								writer.write(conference_acr.text());
								writer.write("\t");
								Element conference_name = row.select("td").get(1);
								writer.write(conference_name.text());
								writer.write("\t");
							}
							// odd rows
							else if (j%2!=0) {
								Element conference_location = row.select("td").get(1);
								writer.write(conference_location.text());
								writer.write("\n");
							}
						}
						// odd number of one column rows
						else if(one_col%2!=0) {
							// odd rows
							if(j%2!=0) {
								Element conference_acr = row.select("td").get(0);
								writer.write(conference_acr.text());
								writer.write("\t");
								Element conference_name = row.select("td").get(1);
								writer.write(conference_name.text());
								writer.write("\t");
							}
							// even rows
							else if (j%2==0) {
								Element conference_location = row.select("td").get(1);
								writer.write(conference_location.text());
								writer.write("\n");
							}
						}
					}

				}
				//IMPORTANT! Do not change the following:
				Thread.sleep(DELAY*1000); //rate-limit the queries
			}

			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Given a string URL returns a string with the page contents
	 * Adapted from example in 
	 * http://docs.oracle.com/javase/tutorial/networking/urls/readingWriting.html
	 * @param link
	 * @return
	 * @throws IOException
	 */
	public static String getPageFromUrl(String link) throws IOException {
		URL thePage = new URL(link);
		URLConnection yc = thePage.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				yc.getInputStream()));
		String inputLine;
		String output = "";
		while ((inputLine = in.readLine()) != null) {
			output += inputLine + "\n";
		}
		in.close();
		return output;
	}



}


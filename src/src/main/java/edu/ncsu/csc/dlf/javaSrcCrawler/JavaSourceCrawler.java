package edu.ncsu.csc.dlf.javaSrcCrawler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.ncsu.csc.dlf.JComp;

public class JavaSourceCrawler {
	
	public static void main(String[] args) {
		
		JavaSourceCrawler crawler = new JavaSourceCrawler();
		Document doc = crawler.getDoc("http://cr.openjdk.java.net/~jjg/diags-examples.html");
		Map<String, String> srcMap =  crawler.getClassMap(doc);
		List<String> errStr = crawler.getError(srcMap);
		crawler.writeOp(errStr);
		crawler.writeMap(srcMap);
	}
	
	private void writeOp(List<String> errStr) {
		
			Writer writer = null;

			try {
			    writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("data\\op.txt"), "utf-8"));
			    for(String line: errStr)
				{
			    	writer.write(line);
			    	writer.write("\n");
				}
			} catch (IOException ex) {
			  // report
			} finally {
			   try {writer.close();} catch (Exception ex) {}
			}
	}

	private List<String> getError(Map<String, String> srcMap) {
		JComp jc = new JComp();
		List<String> returnList = new ArrayList<String>();
		for(String fileName: srcMap.keySet())
		{
			returnList.add(jc.compile(fileName.replace(".java", ""), srcMap.get(fileName)));
		}
		return returnList;
	}

	private void writeMap(Map<String, String> srcMap) {
		for(String fileName: srcMap.keySet())
		{
			Writer writer = null;

			try {
			    writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("data\\jvaSrc\\" + fileName), "utf-8"));
			    writer.write(srcMap.get(fileName));
			} catch (IOException ex) {
			  // report
			} finally {
			   try {writer.close();} catch (Exception ex) {}
			}
		}
		
	}

	private Map<String, String> getClassMap(Document doc) {
		Map<String, String> returnMap = new HashMap<String, String>();
		Elements eleList = doc.select("h4");
		StringBuilder sb;
		for(Element element:eleList)
		{
			if(element.text().toString().endsWith(".java"))
			{
				if(element.nextElementSibling().nodeName().equalsIgnoreCase("div")&&element.nextElementSibling().className().equals("file"))
				{
					sb = new StringBuilder();
					Element ele = element.nextElementSibling().child(1);
					
					System.out.println(element.text() +"\n" + ele.text().replaceAll("lllbbb", "\n"));
					if(returnMap.containsKey(element.text()))
						System.out.println("Here");
					returnMap.put(element.text(), ele.text().replaceAll("lllbbb", "\n"));
				}
			}
		}
		return returnMap;
	}

	private Document getDoc(String url)
	{
		Document doc = null; 
		try{
			doc = Jsoup.parse(new File("data\\java.html"),"UTF-8");	
		}
		catch(Exception e)
		{
			//TODO Log;
		}
		return doc;
	}
}

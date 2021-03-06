package edu.ncsu.csc.dlf.javaSrcCrawler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import edu.ncsu.csc.dlf.JComp;

public class JavaSourceCrawler {
	
	public static final String UTF_8 = "utf-8";
	public static final String OP_TXT = "data" + File.separator + "op.xlsx";
	public static final String JVA_SRC = "data"+ File.separator + "jvaSrc" +  File.separator;
	public static final String JAVA_ERR_EXAMPLES_URL = "http://cr.openjdk.java.net/~jjg/diags-examples.html";

	public void crawl() {
		
		Document doc = getDoc(JAVA_ERR_EXAMPLES_URL);
		Map<String, String> srcMap =  getJavaSrcMap(doc);
		Map<String, String> errStr = getError(srcMap);
		writeOp(errStr, OP_TXT);
		writeMap(srcMap, JVA_SRC);
		cleanup();
	}
	
	/**
	 * Gets the {@code html} web document from the network
	 * @param url of the {@code html} document
	 * @return the JSOUP document representation of {@code html} page at {@code url}
	 */
	private Document getDoc(String url)
	{
		Document doc = null; 
		try{
			doc = Jsoup.parse(new URL(url), 10000000);
		}
		catch(Exception e)
		{
			e.printStackTrace(System.err);
		}
		return doc;
	}
	

	/**
	 * Extracts Example Java Source Files (demonstrating compiler errors) from the HTML {@link Document} 
	 * @param doc
	 * @return a {@link Map} with file names as Keys and file contents as values
	 */
	private Map<String, String> getJavaSrcMap(Document doc) {
		Map<String, String> returnMap = new HashMap<String, String>();
		Elements eleList = doc.select("h4");
		for(Element element:eleList)
		{
			if(element.text().toString().endsWith(".java"))
			{
				if(element.nextElementSibling().nodeName().equalsIgnoreCase("div")
						&& element.nextElementSibling().className().equals("file"))
				{
					String javaText = getText(element.nextElementSibling().child(1));
					returnMap.put(element.text(), javaText);
				}
			}
		}
		return returnMap;
	}
	
	/**
	 * Gets the text from the HTML {@link Element}, preserving the new lines
	 * @param element
	 * @return text from the {@code element}
	 */
	private String getText(Element element) {
	     StringBuffer buffer = new StringBuffer();
	     for (Node child : element.childNodes()) {
	          if (child instanceof TextNode) {
	              buffer.append(((TextNode)child).getWholeText());
	          }
	          if (child instanceof Element) {
	              Element childElement = (Element)child;
	              if (childElement.tag().getName().equalsIgnoreCase("br") || childElement.tag().getName().equalsIgnoreCase("p")) {
	                   buffer.append("\n");
	              }                  
	              buffer.append(getText(childElement));
	          }
	     }

	     return buffer.toString();
	 }
	
	
	/**
	 * Returns the error messages returned by {@link JComp}
	 * @param srcMap a {@link Map} with file names as Keys and source code contents as values
	 * @return
	 */
	private Map<String, String> getError(Map<String, String> srcMap) {
		JComp jc = new JComp();
		Map<String, String> returnList = new HashMap<String, String>();
		for (String fileName : srcMap.keySet()) {
			returnList.put(fileName, jc.compile(fileName.replace(".java", ""), srcMap.get(fileName)));
		}
		return returnList;
	}

	/**
	 * Write Compiler Error Message List to persistence
	 * @param errStr
	 */
	private void writeOp(Map<String, String> errMap, String fileName) {
		FileOutputStream fileOut = null;
		try 
		{
			Workbook wb = new XSSFWorkbook();
			Sheet sheet = wb.createSheet();
			int i=0;
			Row row;
			for (String srcFileName : errMap.keySet()) {
				row = sheet.createRow(i++);
				row.createCell(0).setCellValue(srcFileName);
				row.createCell(1).setCellValue(errMap.get(srcFileName));
			}
			
			fileOut = new FileOutputStream(fileName, false);
			wb.write(fileOut);
		    fileOut.close();
		    wb.close();
		} 
		catch (Exception e) {
			e.printStackTrace(System.err);
		}
		
		
	    
		
	}

	/**
	 * Writes the crawled Java Sources to persistence
	 * @param srcMap
	 * @param directory
	 */
	private void writeMap(Map<String, String> srcMap, String directory) {
		for (String fileName : srcMap.keySet()) {
			Writer writer = null;

			try {
				writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(directory + fileName, false), UTF_8));
				writer.write(srcMap.get(fileName));
			} catch (IOException ex) {
				// report
			} finally {
				try {
					writer.close();
				} catch (Exception ex) {
				}
			}
		}

	}
	
	/**
	 * This methods cleans the "{@code .class}" files generated by java compilation
	 */
	private void cleanup() {
		File dir = new File(".");
		File [] files = dir.listFiles(new FilenameFilter() {
			
			public boolean accept(File dir, String name) {
				return name.endsWith(".class");
			}
		});

		for (File classFile : files) {
		    classFile.delete();
		}
		
	}

	
}

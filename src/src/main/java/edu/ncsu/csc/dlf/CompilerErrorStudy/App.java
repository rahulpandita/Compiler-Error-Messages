package edu.ncsu.csc.dlf.CompilerErrorStudy;

import edu.ncsu.csc.dlf.javaSrcCrawler.JavaSourceCrawler;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	JavaSourceCrawler crawler = new JavaSourceCrawler();
    	crawler.crawl();
    }
}

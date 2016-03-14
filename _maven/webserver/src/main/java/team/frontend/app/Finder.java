package team.frontend.app;
/* Finder.java
   A supplemental feature to the Tabula project which detects tables within a pdf
   document (entire document or by page) and outputs the page and coordinates.
*/
import java.io.PrintWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.lang.Integer;

import org.apache.pdfbox.pdmodel.PDDocument;
//
//import technology.tabula.detectors.DetectionAlgorithm;
//import technology.tabula.detectors.NurminenDetectionAlgorithm;
//import technology.tabula.detectors.SpreadsheetDetectionAlgorithm;
//import technology.tabula.extractors.BasicExtractionAlgorithm;
//import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;
//import technology.tabula.writers.CSVWriter;
//import technology.tabula.writers.JSONWriter;
//import technology.tabula.writers.TSVWriter;
//import technology.tabula.writers.Writer;
import technology.tabula.CommandLineApp;
import technology.tabula.*;

public class Finder extends CommandLineApp {

	private boolean hasPageNum;
	private File pdfFile;
	private int pageNumber;

    public Finder (File pdfFile) {
        this.pdfFile = pdfFile;
        hasPageNum = false;
    }

    public Finder (File pdfFile, int pageNumber) {
        this.pdfFile = pdfFile;
        this.pageNumber = pageNumber;
        hasPageNum = true;
    }

	public static void main(String[] args) {

//	    Finder f = new Finder(args[0], args[1]);

        File pdfFile = null;

		if ( args.length < 1 || args.length >  2) {
			System.err.println("Usage: input.pdf [pageNumber]\n");
		}
		else if (args.length == 1) {
			File pdfInput = new File(args[0]);
			if (!pdfInput.exists()) {
				System.err.println("File does not exist\n");
				System.exit(1);
			} else {
				new Finder(pdfInput).findTables();
			}
		} else {
			int pageNum = 1;

			try {
				pageNum = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				System.err.println("Argument" + args[1] + " must be an integer.");
				System.exit(1);
			}

			pdfFile = new File(args[0]);

			if (!pdfFile.exists()) {
				System.err.println("File does not exist\n");
				System.exit(1);
			} else {
				new Finder(pdfFile, pageNum).findTables();
			}
		}
	}

	public void findTables() throws IOException {

		NurminenDetectionAlgorithm detectionAlgorithm = new NurminenDetectionAlgorithm();
        PDDocument pdfDocument = PDDocument.load(this.pdfFile);
        ObjectExtractor extractor = new ObjectExtractor(pdfDocument);
        PrintWriter writer = new PrintWriter("out.txt", "UTF-8");
        PageIterator pages = extractor.extract();

        System.out.println("{");
        System.out.println("\"fileName\":" + "\"" + pdfFile + "\",");
        System.out.println("\"coordinates\": [");

        while (pages.hasNext()) {
			Page page = pages.next();
            List<Rectangle> tablesOnPage = detectionAlgorithm.detect(page);
            int numTables = tablesOnPage.size();
            if (numTables > 0) {
              	writer.write("{\n");
                writer.write("\"fileName\":" + "\"" + pdfFile + "\",\n");
                writer.write("\"coordinates\": [\n");

              	if ( !hasPageNum ) {
               		for (int i = 0; i < numTables; i++) {
               			System.out.println("        {\n");
						System.out.println("            \"page\": \"" + page.getPageNumber() + "\",");
						System.out.println("            \"y1\": " + tablesOnPage.get(i).getTop() + ",");
						System.out.println("            \"x1\": " + tablesOnPage.get(i).getLeft() + ",");
						System.out.println("            \"y2\": " + tablesOnPage.get(i).getBottom() + ",");
						System.out.println("            \"x2\": " + tablesOnPage.get(i).getRight() + "");

						// if we are printing last table, dont append comma after the '}'.
	                    if (i == (numTables-1)) System.out.println("        }\n");
	                    else System.out.println("       },\n");

	                    writer.write("\"page\":  \"" + page.getPageNumber() + "\",\n");
						writer.write("\"y1\": " + tablesOnPage.get(i).getTop() + ",\n");
						writer.write("\"x1\": " + tablesOnPage.get(i).getLeft() + ",\n");
						writer.write("\"y2\": " + tablesOnPage.get(i).getBottom() + ",\n");
						writer.write("\"x2\": " + tablesOnPage.get(i).getRight() + "\n");
						writer.write("},\n");
                	}
                } else {
					for (int i = 0; i < numTables; i++) {
						if( page.getPageNumber() == pageNumber ) {
							System.out.println("        {\n");
							System.out.println("            \"page\": \"" + page.getPageNumber() + "\",");
							System.out.println("            \"y1\": " + tablesOnPage.get(i).getTop() + ",");
							System.out.println("            \"x1\": " + tablesOnPage.get(i).getLeft() + ",");
							System.out.println("            \"y2\": " + tablesOnPage.get(i).getBottom() + ",");
							System.out.println("            \"x2\": " + tablesOnPage.get(i).getRight() + "");

							writer.write("\"page\":  \"" + page.getPageNumber() + "\",\n");
							writer.write("\"y1\": " + tablesOnPage.get(i).getTop() + ",\n");
							writer.write("\"x1\": " + tablesOnPage.get(i).getLeft() + ",\n");
							writer.write("\"y2\": " + tablesOnPage.get(i).getBottom() + ",\n");
							writer.write("\"x2\": " + tablesOnPage.get(i).getRight() + "\n");
							writer.write("},\n");
						}
					}
				}
                writer.write("]\n");
                writer.write("}");
            }
        }
        writer.close();

    // close "coordinates"
    System.out.println("    ]\n");
    // close json
    System.out.println("}\n");
	}
}

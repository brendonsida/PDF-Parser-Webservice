/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.color.PDGamma;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationSquareCircle;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;

import java.io.File;
import java.util.List;
import java.io.FileReader;
import java.util.Scanner;
import java.io.FileNotFoundException;

public class Highlighter
{
    /**     
     * @param args The command line arguments.
     *
     * @throws Exception If there is an error parsing the document.
     */
    public static void main( String[] args ) throws Exception
    {
        if( args.length != 2 ) {
            System.out.println("Usage: <input pdf> <coords file>");
        }
        else {
        	File file = new File(args[0]);  
        	PDDocument document = PDDocument.load(file);
        	
        	File file2 = new File(args[1]);   
        	Scanner inFile = null;
        	String sequence = "";
        	try {
    			inFile = new Scanner(new FileReader(file2));
    			while(inFile.hasNext()) {
    				sequence += inFile.next();
    			}
    		} catch (FileNotFoundException fe) {
    				System.out.println("File " + file2 + " not found.");
    				fe.printStackTrace();
    			}
    		String[] coords = sequence.split(",");
    		float[] coordinates = new float[4];
    		for (int i = 0; i <4; i++) {
    			coordinates[i] = Float.parseFloat(coords[i]);
    		}
    		
    	    List<PDPage> documentPages = document.getDocumentCatalog().getAllPages();
        	
            try {
            	PDPage page = documentPages.get(0);
                //document.addPage(page);
                List annotations = page.getAnnotations();

                // Setup some basic reusable objects/constants
                // Annotations themselves can only be used once!

                float inch = 72;
                PDGamma colourGreen = new PDGamma();
                colourGreen.setG(1);
                PDGamma colourBlue = new PDGamma();
                colourBlue.setB(1);

                PDBorderStyleDictionary borderThick = new PDBorderStyleDictionary();
                borderThick.setWidth(inch/12);  // 12th inch
                PDBorderStyleDictionary borderThin = new PDBorderStyleDictionary();
                borderThin.setWidth(inch/72); // 1 point
                PDBorderStyleDictionary borderULine = new PDBorderStyleDictionary();
                borderULine.setStyle(PDBorderStyleDictionary.STYLE_UNDERLINE);
                borderULine.setWidth(inch/72); // 1 point

                float pw = page.getMediaBox().getUpperRightX();
                float ph = page.getMediaBox().getUpperRightY();

                // Add the markup annotation, a highlight to PDFBox text
                PDFont font = PDType1Font.HELVETICA_BOLD;

                float textWidth = (font.getStringWidth( "PDFBox" )/1000) * 10;
                PDRectangle position = new PDRectangle();
                
                // Now a square annotation

                PDAnnotationSquareCircle aHighlight =
                    new PDAnnotationSquareCircle( PDAnnotationSquareCircle.SUB_TYPE_SQUARE);
                aHighlight.setContents("Table 1");
                aHighlight.setColour(colourGreen);  // Outline in green
                aHighlight.setInteriorColour(colourGreen);  // Fill in green
                aHighlight.setBorderStyle(borderThick);
                aHighlight.setConstantOpacity((float)0.25);

                position = new PDRectangle(); 
                
                position.setLowerLeftX(coordinates[1]);                
                position.setLowerLeftY(ph-coordinates[2]);
                position.setUpperRightX(coordinates[3]);
                position.setUpperRightY(ph-coordinates[0]);
                aHighlight.setRectangle(position);
                
                //  add to the annotations on the page
                annotations.add(aHighlight);
                document.save(args[0]);
            }
            finally {
                document.close();
            }
        }
    }
}

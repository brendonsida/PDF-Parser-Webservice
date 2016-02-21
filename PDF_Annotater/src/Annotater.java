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
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationTextMarkup;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;

import java.io.File;
import java.util.List;


public class Annotater
{


    /**
     * This will create a document showing various annotations.
     *
     * @param args The command line arguments.
     *
     * @throws Exception If there is an error parsing the document.
     */
    public static void main( String[] args ) throws Exception
    {
        if( args.length != 2 )
        {
            System.out.println("Usage: <input pdf> <output file>");
        }
        else
        {
        	File file = new File("/Users/acole/code/workspace/PDF_Annotater/" + args[0]);
        	PDDocument document = PDDocument.load(file);

    	    List<PDPage> documentPages = document.getDocumentCatalog().getAllPages();

        	
            try
            {
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
                PDAnnotationTextMarkup txtMark = new PDAnnotationTextMarkup(PDAnnotationTextMarkup.SUB_TYPE_HIGHLIGHT);
                txtMark.setColour(colourBlue);
                txtMark.setConstantOpacity((float)0.1);   // Make the highlight 20% transparent

                // Set the rectangle containing the markup

                float textWidth = (font.getStringWidth( "PDFBox" )/1000) * 18;
                PDRectangle position = new PDRectangle();
                position.setLowerLeftX(inch);
                position.setLowerLeftY( ph-inch-18 );
                position.setUpperRightX(72 + textWidth);
                position.setUpperRightY(ph-inch);
                txtMark.setRectangle(position);

                // work out the points forming the four corners of the annotations
                // set out in anti clockwise form (Completely wraps the text)
                // OK, the below doesn't match that description.
                // It's what acrobat 7 does and displays properly!
                float[] quads = new float[8];

                quads[0] = position.getLowerLeftX();  // x1
                quads[1] = position.getUpperRightY()-2; // y1
                quads[2] = position.getUpperRightX(); // x2
                quads[3] = quads[1]; // y2
                quads[4] = quads[0];  // x3
                quads[5] = position.getLowerLeftY()-2; // y3
                quads[6] = quads[2]; // x4
                quads[7] = quads[5]; // y5

                txtMark.setQuadPoints(quads);
                txtMark.setContents("Highlighted since it's important");

                annotations.add(txtMark);


               

                // Now a square annotation

                PDAnnotationSquareCircle aSquare =
                    new PDAnnotationSquareCircle( PDAnnotationSquareCircle.SUB_TYPE_SQUARE);
                aSquare.setContents("Square Annotation");
                aSquare.setColour(colourGreen);  // Outline in red, not setting a fill
                aSquare.setBorderStyle(borderThick);
                aSquare.setConstantOpacity((float)0.1);

                // Place the annotation on the page, we'll make this 1" (72points) square
                // 3.5" down, 1" in from the right on the page

                position = new PDRectangle(); // Reuse the variable, but note it's a new object!
                position.setLowerLeftX(pw-(2*inch));  // 1" in from right, 1" wide
                position.setLowerLeftY(ph-(float)(3.5*inch) - inch); // 1" height, 3.5" down
                position.setUpperRightX(pw-inch); // 1" in from right
                position.setUpperRightY(ph-(float)(3.5*inch)); // 3.5" down
                aSquare.setRectangle(position);

                //  add to the annotations on the page
                annotations.add(aSquare);



                document.save(args[1]);
            }
            finally
            {
                document.close();
            }
        }
    }

}

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
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationSquareCircle;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationTextMarkup;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;

import java.io.File;
import java.util.List;



/**
 * @author acole, tWilder
 * 
 * JSON: REQUIREMENTS
 * 
 * Usage: For 1 annotation:
 *	<input.pdf> <output.pdf> <PageNumber,LowerLeftX,LowerLeftY,UpperRightX,UpperRightY,R,G,B>
 *
 *	where:
 *		input.pdf is the original file in which a copy is made and highlighted
 *		output.pdf is the copy of input.pdf with the highlights embedded
 *		PageNumber is an integer correlating to the page of the input.pdf document to which the highlight is to be applied
 *		LowerLeftX is a float variable representing 1/4 corners of the bounding box to be highlighted 
 *		LowerLeftY is a float variable representing 1/4 corners of the bounding box to be highlighted
 *		UpperRightX is a float variable representing 1/4 corners of the bounding box to be highlighted
 *		UpperRightX is a float variable representing 1/4 corners of the bounding box to be highlighted
 *		R is an integer value from 0 to 255 representing the degree to which the red channel is set in the RGB color spectrum
 * 		G is an integer value from 0 to 255 representing the degree to which the Green channel is set in the RGB color spectrum
 *		B is an integer value from 0 to 255 representing the degree to which the Blue channel is set in the RGB color spectrum
 *
 *Usage: For multiple annotations:
 *	<input.pdf> <output.pdf> <PageNumber,LowerLeftX,LowerLeftY,UpperRightX,UpperRightY,R,G,B> <PageNumber,LowerLeftX,LowerLeftY,UpperRightX,UpperRightY,R,G,B> ...
 *
 *	Where all variable are representative of the single annotation form except additional annotations are given separated by " "
 */
public class HighLighter
{


    
    public static void main( String[] args ) throws Exception
    {
        if( args.length != 6 )
        {
            System.out.println("Usage: <input pdf> <output file> <lower left x> <lower left y> <upper right x> <upper right y>");
        }
        else
        {
        	File file = new File("/Users/acole/code/workspace/PDF_Annotater/" + args[0]);
        	PDDocument document = PDDocument.load(file);

    	    List<PDPage> documentPages = document.getDocumentCatalog().getAllPages();

        	
            try
            {
            	PDPage page = documentPages.get(0);
                List<PDAnnotation> annotations = page.getAnnotations();

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

                float pageHeight = page.getMediaBox().getUpperRightY();

                // Now a square annotation

                PDAnnotationSquareCircle aSquare = new PDAnnotationSquareCircle( PDAnnotationSquareCircle.SUB_TYPE_SQUARE);
                aSquare.setColour(colourGreen); 
                aSquare.setInteriorColour(colourGreen);
                aSquare.setBorderStyle(borderThick);
                aSquare.setConstantOpacity((float)0.5);


                PDRectangle position = new PDRectangle(); 
                position.setLowerLeftX(Float.parseFloat(args[3]));
                position.setLowerLeftY(pageHeight-(Float.parseFloat(args[4]))); 
                position.setUpperRightX(Float.parseFloat(args[5]));
                position.setUpperRightY(pageHeight-(Float.parseFloat(args[2])));
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

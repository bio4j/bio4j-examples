/*
 * Copyright (C) 2010-2011  "Bio4j"
 *
 * This file is part of Bio4j
 *
 * Bio4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.era7.bioinfo.bio4j.tools.uniprot;

import com.era7.bioinfo.bio4j.CommonData;
import java.io.*;

/**
 *
 * @author Pablo Pareja Tobes <ppareja@era7.com>
 */
public class SplitUniprotXmlFile {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("This program expects two parameters: \n"
                    + "1. Uniprot xml input file\n"
                    + "2. Number of entries per resulting part file\n");
        } else {

            int numberOfEntries = Integer.parseInt(args[1]);
            int currentFile = 1;
            int currentEntry = 1;

            File inFile = new File(args[0]);

            String prefixOutFile = args[0].split("\\.")[0];

            try {

                BufferedWriter outBuff = new BufferedWriter(new FileWriter(new File(prefixOutFile + currentFile + ".xml")));
                outBuff.write("<entries>\n");

                BufferedReader reader = new BufferedReader(new FileReader(inFile));
                String line;

                while ((line = reader.readLine()) != null) {
                    if (line.trim().startsWith("<" + CommonData.ENTRY_TAG_NAME)) {

                        if(currentEntry % numberOfEntries == 0){
                            outBuff.write("</entries>\n");
                            outBuff.close();
                            System.out.println("Closing file...");
                            currentFile++;
                            outBuff = new BufferedWriter(new FileWriter(new File(prefixOutFile + currentFile + ".xml")));
                            outBuff.write("<entries>\n");
                        }

                        outBuff.write((line + "\n"));

                        while (!line.trim().startsWith("</" + CommonData.ENTRY_TAG_NAME + ">")) {
                            line = reader.readLine();
                            outBuff.write((line + "\n"));
                        }

                        outBuff.flush();

                        currentEntry++;

                        if(currentEntry % 10000 == 0){
                            System.out.println(currentEntry + " already...");
                        }
                    }
                }

                outBuff.close();

            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }
}

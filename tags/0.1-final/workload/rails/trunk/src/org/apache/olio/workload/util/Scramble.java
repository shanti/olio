/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.apache.olio.workload.util;
import java.util.*;

/**
 * Scramble scrambles char sequences in a controlled random fasion. The
 * outcome of running the Scramble utility will be the same for the same
 * random number generator. Scramble is a utility to generate a code segment
 * and is not used in the benchmark itself.
 *
 * @author Akara Sucharitakul
 */
public class Scramble {

    /**
     * The alpha-numeric char sequence.
     */
    private static char[] alpha =
        {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
         'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
         's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '_'};
	    /**
     * The alpha characters only. A name would start with these.
     */
    private static char[] characs =
        {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
         'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
 
    /**
     * Generates the char sequences for each length.
     *
     * @param digits The length of the name to generate the scramble for
     * @return The scrambled sequence for each digit
     */
    public static char[][] genChars(int digits) {
        Random r = new Random(digits);
        char[][] result = new char[digits][];
        ArrayList<Character> list = new ArrayList<Character>(alpha.length);

        // Generate scrambled sequence for first digit.
        result[0] = new char[characs.length];
        for (int j = 0; j < characs.length; j++)
            list.add(characs[j]);
        for (int j = 0; j < characs.length; j++) {
            int idx = r.nextInt(list.size());
            result[0][j] = list.remove(idx);
        }

        // Generate scrambled sequence for the following digits.
        for (int i = 1; i < digits; i++) {
            result[i] = new char[alpha.length];
            for (int j = 0; j < alpha.length; j++)
                list.add(alpha[j]);
            for (int j = 0; j < alpha.length; j++) {
                int idx = r.nextInt(list.size());
                result[i][j] = list.remove(idx);
            }
        }
        return result;
    } 

    /**
     * Dumps the generated scramble to standard output in a ready-to-use
     * Java array representation. The user will have to redirect the output
     * to a Java class by him/herself.
     *
     * @param arrays The scramble for each length
     */
    public static void dumpArrays(char[][][] arrays) {
        StringBuilder buffer = new StringBuilder(8096);
        buffer.append("private static final char[][][] scramble = {");
        for (int i = 0; i < arrays.length; i++) {
            buffer.append('{');
            for (int j = 0; j < arrays[i].length; j++) {
                buffer.append('{');
                for (int k = 0; k < arrays[i][j].length; k++) {
                    buffer.append("'");
                    buffer.append(arrays[i][j][k]);
                    if (k < arrays[i][j].length - 1) {
                        buffer.append("',");
                        if (buffer.length() > 70) {
                            System.out.println(buffer);
                            buffer.setLength(0);
                        } else {
                            buffer.append(' ');
                        }
                    } else {
                        buffer.append("'");
                    }
                }
                buffer.append("}");
                if (j < arrays[i].length - 1) {
                    buffer.append(',');
                    System.out.println(buffer);
                    buffer.setLength(0);
                }
            }
            buffer.append("}");
            if (i < arrays.length - 1) {
                buffer.append(',');
                System.out.println(buffer);
                buffer.setLength(0);
            }
        }
        buffer.append("};");
        System.out.println(buffer);
    }

    /**
     * The main method to drive the scrambling.
     *
     * @param args ignored
     */
    public static void main(String[] args) {
        char[][][] results = new char[8][][];
        char[][] nullArray = new char[1][1];
        nullArray[0][0] = '0';
        for (int i = 0; i < 2; i++)
            results[i] = nullArray;
        for (int i = 2; i < 8; i++)
            results[i] = genChars(i + 1);
        dumpArrays(results);
    }
}

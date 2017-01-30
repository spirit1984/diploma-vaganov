package ru.arriah;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author shevchenko-dv-100705
 */
public class App {
    private static final Logger logger = Logger.getLogger(App.class);
    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            logger.error("Pattern name or output directory name is not provided");
            System.exit(1);
        }

        String patternName = args[0];
        logger.info(String.format("Pattern name is: %s", patternName));
        String outputName = args[1];
        logger.info(String.format("Output directory is: %s", outputName));

        StringBuffer sb = new StringBuffer();
        Set<String> set = new HashSet();
        String fileName = null;

        try (BufferedReader br = new BufferedReader(new FileReader(patternName))) {

            String s;
            while ((s = br.readLine()) != null) {
                if (StringUtils.isGeneDescription(s)) {
                    String name = String.format("%s/%s.pat", outputName, StringUtils.convertToFileName(s));
                    logger.info("Generating new file with name: " + name);
                    if (fileName != null) {
                        try (FileWriter fw = new FileWriter(fileName)) {
                            fw.write(complement(fileName, sb.toString()));
                        }
                    }
                    fileName = name;
                    if (set.contains(fileName)) {
                        logger.warn("Filename already handled:" + fileName);
                    }
                    set.add(fileName);
                    sb = new StringBuffer();
                } else {
                    sb.append(s);
                }
            }


        }

        if (fileName != null) {
            try (FileWriter fw = new FileWriter(fileName)) {
                fw.write(complement(fileName, sb.toString()));
            }
        }
    }

    private static String complement(String fileName, String s) {
        if (!fileName.contains("complement")) {
            return s;
        }

        StringBuffer sb = new StringBuffer();


        for (char ch : s.toUpperCase().toCharArray()) {
            sb.append(complement(ch));
        }

        return sb.reverse().toString();
    }

    private static char complement(char symbol) {
        if (symbol == 'A') return 'T';
        if (symbol == 'T') return 'A';
        if (symbol == 'G') return 'C';
        if (symbol == 'C') return 'G';
        return symbol;
    }


}

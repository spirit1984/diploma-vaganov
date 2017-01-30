package ru.arriah;

import org.apache.log4j.Logger;

import java.io.*;


/**
 * Hello world!
 *
 */
public class App 
{
	private static final Logger logger = Logger.getLogger(App.class);
	private final StringMatcher matcher = new StringMatcher();

    public static void main( String[] args )
    {
        try {
        	new App().run(args);
        } catch (IllegalArgumentException e) {
        	logger.error(e.getMessage());
        } catch (Exception e) {
        	logger.error("Program terminated unexpectedly.", e);
        }
    }

    private void run(String[] args) throws IOException {
    	validateArguments(args);
    	String patternName = args[0];
    	String textName = args[1];
    	logger.info(String.format("Pattern file name: %s. Text file name: %s", patternName, textName));
    	logger.info("Reading pattern...");
    	String pattern = readString(patternName);
    	logger.info(String.format("Pattern read successfully. Raw string contains: %d bytes", pattern.length()));
    	logger.info("Reading text...");
    	String text = readString(textName);
    	logger.info(String.format("Text read successfully. Raw string contains: %d bytes", text.length()));
		StringMatcher.MatcherResponse response = matcher.handleBuffer(pattern, text);



		String htmlName = patternName.replace("pat", "html");
		logger.info(String.format("Saving info to %s", htmlName));
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(htmlName))) {
			bw.write(new HtmlFormatter().getResponse(response));
		}


    }

    private String readString(String fileName) {
    	StringBuffer buffer = new StringBuffer();    	

    	try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
    		String s;
    		while ((s=reader.readLine())!=null) {
    			buffer.append(s);
    		}

    	} catch (FileNotFoundException e) {
    		throw new IllegalArgumentException("Failed to open file: " + fileName);
    	} catch (IOException e) {
    		throw new RuntimeException(e);
    	}


    	return buffer.toString();
    }

    private void validateArguments(String[] args) {
    	if (args == null || args.length != 2) {
    		throw new IllegalArgumentException("You should provide exactly two arguments - pattern file name and text file name");
    	}
    }


}

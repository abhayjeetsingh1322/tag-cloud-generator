import java.util.Comparator;

import components.map.Map;
import components.map.Map1L;
import components.set.Set;
import components.set.Set1L;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;
import components.sortingmachine.SortingMachine;
import components.sortingmachine.SortingMachine1L;

/**
 * This program takes a input of a file that has text, and generates a HTML page
 * that shows the words in tag cloud format in order.
 *
 * @author Abhayjeet S., Wesam K., Pravin H.
 */
public final class TagCloudGenerator {

    /**
     * No argument constructor--private to prevent instantiation.
     */
    private TagCloudGenerator() {
        // no code needed here
    }

    /**
     * Min number of occurrence of a word.
     */
    private static int minOccur = 0;
    /**
     * Max number of occurrence of a word.
     */
    private static int maxOccur = 0;

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments; unused here
     */
    public static void main(String[] args) {

        //Creating output and input streams
        SimpleWriter out = new SimpleWriter1L();
        SimpleReader in = new SimpleReader1L();

        //Asking for input file
        out.print("Please enter the name of the file "
                + "(include folder name, file name, and extension): ");
        String fileName = in.nextLine();

        //Asking for output file
        out.print("Please enter the name of the output file "
                + "(include folder name, file name, and extension): ");
        String outputFileName = in.nextLine();

        //Asking for the size tag cloud
        out.print("Please enter the size of the tag cloud: ");
        int size = in.nextInteger();

        //Creating set for separators and using method to extract
        final String separatorsStr = " \t\n\r,-.!?[]';:/()";
        Set<Character> separatorSet = new Set1L<>();
        generateElements(separatorsStr, separatorSet);

        //Creating map for words and count
        //Calling method to update them based on the input file
        Map<String, Integer> wordsAndCount = new Map1L<>();
        updateMap(wordsAndCount, separatorSet, fileName);

        //Creating a sorting machine for number ordering.
        Comparator<Map.Pair<String, Integer>> numComp = new NumberLT();
        SortingMachine<Map.Pair<String, Integer>> numSortMach = new SortingMachine1L<>(
                numComp);

        //Sorting.
        Map<String, Integer> tempMap = wordsAndCount.newInstance();
        while (wordsAndCount.size() > 0) {
            Map.Pair<String, Integer> pair = wordsAndCount.removeAny();
            numSortMach.add(pair);
            tempMap.add(pair.key(), pair.value());
        }
        numSortMach.changeToExtractionMode();
        wordsAndCount.transferFrom(tempMap);

        //Extracting top words as specified and updating min and max
        //Note: numSortMach is not restored
        Map<String, Integer> topWords = wordsAndCount.newInstance();
        int index = 0;
        while (index < size && numSortMach.size() > 0) {
            Map.Pair<String, Integer> pair = numSortMach.removeFirst();

            //Decreasing order thus first is max and last is min
            if (index == 0) {
                maxOccur = pair.value();
            } else if (index == size - 1) {
                minOccur = pair.value();
            }

            //Adding to top word map
            topWords.add(pair.key(), pair.value());
            index++;
        }

        //Creating a sorting machine for alphabetical ordering.
        Comparator<Map.Pair<String, Integer>> strComp = new StringLT();
        SortingMachine<Map.Pair<String, Integer>> strSortMach = new SortingMachine1L<>(
                strComp);

        //Sorting.
        while (topWords.size() > 0) {
            Map.Pair<String, Integer> pair = topWords.removeAny();
            strSortMach.add(pair);
            tempMap.add(pair.key(), pair.value());
        }
        strSortMach.changeToExtractionMode();
        topWords.transferFrom(tempMap);

        //Calling method to create HTML page
        createPage(strSortMach, fileName, outputFileName, size);

        //Telling user the program is done
        out.print("Valid HTML pages have been generated and the program "
                + "has termainted.");

        //Closing input and output streams
        out.close();
        in.close();
    }

    /**
     * Generates the set of characters in the given {@code String} into the
     * given {@code Set}.
     *
     * @param str
     *            the given {@code String}
     * @param charSet
     *            the {@code Set} to be replaced
     * @replaces charSet
     * @ensures charSet = entries(str)
     */
    private static void generateElements(String str, Set<Character> charSet) {
        assert str != null : "Violation of: str is not null";
        assert charSet != null : "Violation of: charSet is not null";

        //Staring position
        int position = 0;

        //Entering while loop until all separators are covered
        while (position < str.length()) {

            //Getting character
            char x = str.charAt(position);

            //If separatorSet doesn't contain the character adding it
            if (!charSet.contains(x)) {
                charSet.add(x);
            }

            position++;
        }
    }

    /**
     * Returns the first "word" (maximal length string of characters not in
     * {@code separators}) or "separator string" (maximal length string of
     * characters in {@code separators}) in the given {@code text} starting at
     * the given {@code position}.
     *
     * @param text
     *            the {@code String} from which to get the word or separator
     *            string
     * @param position
     *            the starting index
     * @param separators
     *            the {@code Set} of separator characters
     * @return the first word or separator string found in {@code text} starting
     *         at index {@code position}
     * @requires 0 <= position < |text|
     * @ensures <pre>
     * nextWordOrSeparator =
     *   text[position, position + |nextWordOrSeparator|)  and
     * if entries(text[position, position + 1)) intersection separators = {}
     * then
     *   entries(nextWordOrSeparator) intersection separators = {}  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      intersection separators /= {})
     * else
     *   entries(nextWordOrSeparator) is subset of separators  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      is not subset of separators)
     * </pre>
     */
    private static String nextWordOrSeparator(String text, int position,
            Set<Character> separators) {

        //Declaring a variable for final index
        int finalPosition = position;

        //Checking if the current index is a separator
        if (separators.contains(text.charAt(position))) {

            //Entering while loop until separators are extracted or length is
            //crossed, and incrementing final index
            while (finalPosition < text.length()
                    && separators.contains(text.charAt(finalPosition))) {
                finalPosition++;
            }

            //Else if the current index is a letter
        } else {

            //Entering while loop until letters are extracted or length is
            //crossed, and incrementing final index
            while (finalPosition < text.length()
                    && !separators.contains(text.charAt(finalPosition))) {
                finalPosition++;
            }
        }

        //Getting a substring from initial index to final index
        String nextStr = text.substring(position, finalPosition);

        return nextStr;
    }

    /**
     * Method updates map that contains words and their count.
     *
     * @param wordsAndCount
     *            Map that contains words (key) and their count (value)
     * @param charSet
     *            Set that contains possible separators in text
     * @param fileName
     *            String that contains the name of the input file
     * @updates wordsAndCount, uniqueWords
     * @requires charSet all possible separators
     * @ensures <pre>
     * uniqueWords contains  entries of words from text from fileName &
     * wordsAndCount contains entries as keys and associated counts as values.
     *  </pre>
     */
    private static void updateMap(Map<String, Integer> wordsAndCount,
            Set<Character> charSet, String fileName) {

        //Creating a input stream from the input file
        SimpleReader in = new SimpleReader1L(fileName);

        //Entering loop until at the end of file
        while (!in.atEOS()) {

            //Getting line from input file
            String text = in.nextLine() + "\n";

            //Declaring a index variable for the line
            int position = 0;

            //Entering loop until index variable equals text length
            while (position < text.length()) {

                //Calling method to get word or separators
                String wordOrSeparator = nextWordOrSeparator(text, position,
                        charSet);

                //Updating index variable
                position += wordOrSeparator.length();

                //Checking if the string extracted is a word by checking first
                //character
                if (!charSet.contains(wordOrSeparator.charAt(0))) {

                    //If Map has the word updating count (value)
                    if (wordsAndCount.hasKey(wordOrSeparator)) {
                        int count = wordsAndCount.value(wordOrSeparator);
                        count++;
                        wordsAndCount.replaceValue(wordOrSeparator, count);

                        //Else adding word to Map with word (key) and
                        //count (value)
                    } else {
                        wordsAndCount.add(wordOrSeparator, 1);
                    }
                }
            }
        }

        //Closing input stream
        in.close();
    }

    /**
     * Method generates valid HTML format page for the words and their counts.
     *
     * @param strSortMach
     *            Sorting Machine that contains words (key) and their count
     *            (value)
     * @param inputFile
     *            String that contains the name of the input file
     * @param outputFileName
     *            String that contains the name of the output file
     * @param size
     *            Size of the tag cloud
     * @clears strSortMach
     * @ensures <pre>
     * Valid HTML page will be generated and will be saved to location provided
     * by outputFileName.
     * </pre>
     */
    private static void createPage(
            SortingMachine<Map.Pair<String, Integer>> strSortMach,
            String inputFile, String outputFileName, int size) {

        //Creating output stream to the output file
        SimpleWriter out = new SimpleWriter1L(outputFileName);

        // Prints HTML headers
        out.println("<html>");
        out.println("<head>");
        out.println("<title>" + "Top " + size + " words in " + inputFile
                + "</title>");
        out.println(
                "<link href=\"http://web.cse.ohio-state.edu/software/2231/web-sw2/"
                        + "assignments/projects/tag-cloud-generator/data/tagcloud.css\" "
                        + "rel=\"stylesheet\" type=\"text/css\">");
        out.println("</head>");
        out.println("<body>");
        out.println(
                "<h2>" + "Top " + size + " words in " + inputFile + "</h2>");
        out.println("<hr>");
        out.println("<div class=\"cdiv\">");
        out.println("<p class=\"cbox\">");

        //Printing Words
        //Note: strSortMach is no longer restored (updated contract)
        while (strSortMach.size() > 0) {
            Map.Pair<String, Integer> pair = strSortMach.removeFirst();
            out.println("<span style=\"cusor:default\" class=\"f"
                    + fontSizer(pair.value()) + "\" title=\"count:"
                    + pair.value() + "\">" + pair.key().toLowerCase()
                    + "</span>");
        }

        // Outputs closing tags of generated HTML file
        out.println("</p>");
        out.println("</div>");
        out.println("</body>");
        out.println("</html>");
        out.close();
    }

    /**
     * Returns the font size using linear conversions for any cases that are not
     * edge.
     *
     * @param occurs
     *            number of occurrences of the word
     * @return fontSize number used as font size
     */
    private static int fontSizer(int occurs) {
        //Range according to CSS
        final int maxFont = 48;
        final int minFont = 11;

        //Variable to hold font
        int fontSize = minFont;

        //Edge cases
        if (occurs == minOccur) {
            fontSize = minFont;
        } else if (occurs == maxOccur) {
            fontSize = maxFont;
        } else {
            //Using Linear Conversion

            // Calculate proportions
            double occurRange = maxOccur - minOccur;
            double fontRange = maxFont - minFont;
            double scaled = (occurs - minOccur) / occurRange;

            // Apply the linear equation
            fontSize = (int) (scaled * fontRange + minFont);
        }

        return fontSize;
    }

    /**
     * Returns zero if strings are equal. Negative if s1 comes first not s2,
     * which is correct order. Positive integer if s2 comes first not s1, which
     * is not the correct order. Used to sort strings alphabetically.
     */
    private static class StringLT
            implements Comparator<Map.Pair<String, Integer>> {
        @Override
        public int compare(Map.Pair<String, Integer> s1,
                Map.Pair<String, Integer> s2) {
            return (s1.key().compareToIgnoreCase(s2.key()));
        }
    }

    /**
     * Returns zero if integers are equal. Negative if i2 comes first not i1,
     * which is correct order. Positive integer if i1 comes first not i2, which
     * is not the correct order. Used to sort integers decreasing order.
     */
    private static class NumberLT
            implements Comparator<Map.Pair<String, Integer>> {
        @Override
        public int compare(Map.Pair<String, Integer> i1,
                Map.Pair<String, Integer> i2) {
            return (i2.value().compareTo(i1.value()));
        }
    }
}

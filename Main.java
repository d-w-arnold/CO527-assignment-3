import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

/**
 * @author David W. Arnold
 * @version 13/03/2018
 */
public class Main
{
    public static void main(String[] args)
    {
        String input = "example1.in";
        String inputPath = "./inputs/" + input;
        String[] contents = Objects.requireNonNull(fileContents(inputPath)).split("\n");
        String[] header = contents[0].split(" ");

        // bits in Word.
        int W = Integer.parseInt(header[0]);
        // Bytes in Cache.
        int C = Integer.parseInt(header[1]);
        // Bytes in Block.
        int B = Integer.parseInt(header[2]);
        // lines in Block.
        int k = Integer.parseInt(header[3]);

        int numOfBlocks = C / B;
        int bytesInLine = B / k;

        int indexLength = (int) (Math.log(numOfBlocks) / Math.log(2));
        int offsetLength = (int) (Math.log(bytesInLine) / Math.log(2));
        int tagLength = W - (indexLength + offsetLength);

        // Creates Cache.
        Map<String, ArrayList<Integer>> cache = new HashMap<String, ArrayList<Integer>>();

        // Builds Cache, with the correct number of blocks.
        for (int i = 0; i < numOfBlocks; i++) {
            String s = Integer.toBinaryString(i);
            String paddedS = String.format("%" + W + "s", s).replace(' ', '0');
            String blockIndex = paddedS.substring(W - indexLength, W);
            cache.put(blockIndex, new ArrayList<Integer>());
        }

        StringBuilder output = new StringBuilder();

        // Iterate through each address (provided in decimal with each input file).
        for (int j = 1; j < contents.length; j++) {
            int address = Integer.parseInt(contents[j]);
            String addressString = Integer.toBinaryString(address);
            String paddedAddressString = String.format("%" + W + "s", addressString).replace(' ', '0');

            String tag = paddedAddressString.substring(0, tagLength);
            String index = paddedAddressString.substring(tagLength, tagLength + indexLength);
            // String offset = paddedAddressString.substring(tagLength + indexLength, W);

            int decimalTag = Integer.parseInt(tag, 2);

            if (cache.containsKey(index)) {
                ArrayList<Integer> blk = cache.get(index);
                if (blk.contains(decimalTag)) {
                    // Block of the Cache does contains the decimalTag.
                    Iterator<Integer> it = blk.iterator();
                    while (it.hasNext()) {
                        if (it.next().equals(decimalTag)) {
                            it.remove();
                            break;
                        }
                    }
                    blk.add(decimalTag);
                    output.append("C");
                } else {
                    // Block of the Cache does NOT contain the decimalTag.
                    if (blk.size() == k) {
                        // Cache is full.
                        blk.remove(0);
                        blk.add(decimalTag);
                    } else if (blk.size() < k) {
                        // Cache is NOT full.
                        blk.add(decimalTag);
                    }
                    output.append("M");
                }
            }
        }

        String outputPath = "./outputs/out-" + input;
        System.out.println("\n" + "Input: " + input + "\n\n" + "Output: " + output.toString() + "\n" + result(outputPath, output.toString()));
    }

    private static String fileContents(String file)
    {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            br.close();
            return sb.toString();
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    private static String result(String path, String output)
    {
        String[] contents = Objects.requireNonNull(fileContents(path)).split("\n");

        StringBuilder expected = new StringBuilder();

        for (String content : contents) {
            expected.append(content);
        }

        if (expected.toString().equals(output)) {
            // Success.
            return "Expected: " + expected.toString() + "\n\n" + "Success!";
        } else {
            // Fail.
            return "Expected Output: " + expected.toString() + "\n\n" + "Fail.";
        }
    }
}

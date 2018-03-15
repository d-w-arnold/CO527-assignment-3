import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

/**
 * CO527-Assignment-3
 *
 * @author David W. Arnold
 * @version 13/03/2018
 */
public class Main
{
    public static void main(String[] args)
    {
        // Examples from the CO527-Assignment-3 brief.
        executeFiles("./examples/");

        // All 20x Input files.
        executeFiles("./inputs/");
    }

    private static void executeFiles(String path)
    {
        // Executes each file in the directory at the specified path.
        ArrayList<String> inputs = listFilesForFolder(new File(path));
        for (String input : inputs) {
            execute(path, input);
        }
    }

    private static ArrayList<String> listFilesForFolder(final File folder) {
        ArrayList<String> inputs = new ArrayList<String>();
        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            if (!fileEntry.isDirectory()) {
                inputs.add(fileEntry.getName());
            }
        }
        return inputs;
    }

    private static void execute(String path, String input)
    {
        // Creates a mock up Cache.
        Map<String, ArrayList<Long>> cache = new HashMap<String, ArrayList<Long>>();

        // Each line of the input.
        String[] contents = Objects.requireNonNull(fileContents(path + input)).split("\n");
        // Each number from the first line of the input.
        String[] header = contents[0].split(" ");

        // Bits in Word.
        int W = Integer.parseInt(header[0]);
        // Bytes in Cache.
        int C = Integer.parseInt(header[1]);
        // Bytes in Block.
        int B = Integer.parseInt(header[2]);
        // Lines in Block (k-way).
        int k = Integer.parseInt(header[3]);

        int numOfBlocks = C / B;
        int bytesInLine = B / k;

        int indexLength = (int) (Math.log(numOfBlocks) / Math.log(2));
        int offsetLength = (int) (Math.log(bytesInLine) / Math.log(2));
        int tagLength = W - (indexLength + offsetLength);

        // Builds the Cache, with the correct number of blocks.
        for (int i = 0; i < numOfBlocks; i++) {
            String s = Long.toBinaryString(i);
            String paddedS = String.format("%" + W + "s", s).replace(' ', '0');
            String blockIndex = paddedS.substring(W - indexLength, W);
            cache.put(blockIndex, new ArrayList<Long>());
        }

        // The output.
        StringBuilder output = new StringBuilder();

        // A count for the number of characters to be output to the console.
        long count = 0;
        // Limits the number of characters in a line (on the console).
        long limit = 100;

        // Iterate through each address. Each address is provided in decimal (base 10) as a String.
        for (int j = 1; j < contents.length; j++) {
            long address = Long.parseUnsignedLong(contents[j]);
            String addressString = Long.toBinaryString(address);
            String paddedAddressString = String.format("%" + W + "s", addressString).replace(' ', '0');

            String tag = paddedAddressString.substring(0, tagLength);
            String index = paddedAddressString.substring(tagLength, tagLength + indexLength);
            // String offset = paddedAddressString.substring(tagLength + indexLength, W);

            long decimalTag = Long.parseUnsignedLong(tag, 2);

            if (cache.containsKey(index)) {
                ArrayList<Long> blk = cache.get(index);
                if (blk.contains(decimalTag)) {
                    // Block of the Cache contains the decimalTag.
                    Iterator<Long> it = blk.iterator();
                    while (it.hasNext()) {
                        if (it.next().equals(decimalTag)) {
                            it.remove();
                            break;
                        }
                    }
                    blk.add(decimalTag);
                    output.append(print(count, limit, "C"));
                    count++;
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
                    output.append(print(count, limit,"M"));
                    count++;
                }
            }
        }

        System.out.println("\n" + "Input: " + input + "\n" + "Output: " + output.toString());
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

    private static String print(long count, long limit, String letter)
    {
        if (count % limit == 0) {
            return "\n" + letter;
        } else {
            return letter;
        }
    }
}

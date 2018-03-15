import java.io.BufferedReader;
import java.io.File;
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
        executeFiles("./examples/");
        executeFiles("./inputs/");
    }

    private static void executeFiles(String path)
    {
        // Executes each file in the directory at the specified path.
        final File folder = new File(path);
        ArrayList<String> inputs = listFilesForFolder(folder);
        for (String input : inputs) {
            execute(path, input);
        }
    }

    private static ArrayList<String> listFilesForFolder(final File folder) {
        ArrayList<String> inputs = new ArrayList<String>();
        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);
            } else {
                inputs.add(fileEntry.getName());
            }
        }
        return inputs;
    }

    // TODO Make this method small, have it comprise of multiple smaller methods.
    private static void execute(String path, String input)
    {
        String[] contents = Objects.requireNonNull(fileContents(path + input)).split("\n");
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
        Map<String, ArrayList<Long>> cache = new HashMap<String, ArrayList<Long>>();

        // Builds Cache, with the correct number of blocks.
        for (int i = 0; i < numOfBlocks; i++) {
            String s = Long.toBinaryString(i);
            String paddedS = String.format("%" + W + "s", s).replace(' ', '0');
            String blockIndex = paddedS.substring(W - indexLength, W);
            cache.put(blockIndex, new ArrayList<Long>());
        }

        StringBuilder output = new StringBuilder();

        // Iterate through each address (provided in decimal with each input file).
        for (int j = 1; j < contents.length; j++) {
            long address = Long.parseUnsignedLong(contents[j]);
            String addressString = Long.toBinaryString(address);
            String paddedAddressString = String.format("%" + W + "s", addressString).replace(' ', '0');

            String tag = paddedAddressString.substring(0, tagLength);
            String index = paddedAddressString.substring(tagLength, tagLength + indexLength);
            // String offset = paddedAddressString.substring(tagLength + indexLength, W);

            long decimalTag = Long.parseLong(tag, 2);

            if (cache.containsKey(index)) {
                ArrayList<Long> blk = cache.get(index);
                if (blk.contains(decimalTag)) {
                    // Block of the Cache does contains the decimalTag.
                    Iterator<Long> it = blk.iterator();
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
}

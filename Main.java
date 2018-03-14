import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * @author David W. Arnold
 * @version 13/03/2018
 */
public class Main
{
    public static void main(String[] args)
    {
        List<Integer> block = new ArrayList<Integer>();

        String[] contents = Objects.requireNonNull(fileContents("./inputs/example1.in")).split("\n");

        String[] header = contents[0].split(" ");

        int W = Integer.parseInt(header[0]);
        int C = Integer.parseInt(header[1]);
        int B = Integer.parseInt(header[2]);
        int k = Integer.parseInt(header[3]);

        int numOfBlocks = C / B;
        int bytesInLine = B / k;

        int indexLength = (int) (Math.log(numOfBlocks) / Math.log(2));
        int offsetLength = (int) (Math.log(bytesInLine) / Math.log(2));
        int tagLength = W - (indexLength + offsetLength);

        StringBuilder output = new StringBuilder();

        // Iterate through each address (provided in decimal with each input file).
        for (int j = 1; j < contents.length; j++) {
            int address = Integer.parseInt(contents[j]);
            String addressString = Integer.toBinaryString(address);
            String paddedAddressString = String.format("%" + W + "s", addressString).replace(' ', '0');

            String tag = paddedAddressString.substring(0, tagLength);
            String index = paddedAddressString.substring(tagLength, tagLength + indexLength);
            String offset = paddedAddressString.substring(tagLength + indexLength, W);

//            System.out.println("Tag: " + tag);
//            System.out.println("Index: " + index);
//            System.out.println("Offset: " + offset);
//            System.out.println("\n");



            int decimalTag = Integer.parseInt(tag, 2);

            // Assuming all addresses have the same index (same block).
            if (block.contains(decimalTag)) {
                // If block of the cache already contains the decimalTag,
                // and cache is either full or NOT full.
                Iterator<Integer> it = block.iterator();
                while (it.hasNext()) {
                    if (it.next().equals(decimalTag)) {
                        it.remove();
                        break;
                    }
                }
                block.add(decimalTag);
                output.append("C");
            } else {
                if (block.size() == k) {
                    // If the block of the cache does NOT contain the decimalTag, and cache is full.
                    block.remove(0);
                    block.add(decimalTag);
                } else if (block.size() < k) {
                    // If the block of the cache does NOT contain the decimalTag, and cache is NOT full.
                    block.add(decimalTag);
                }
                output.append("M");
            }
        }
        System.out.println(output);
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

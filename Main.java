import java.io.BufferedReader;
import java.io.FileReader;

/**
 * @author David W. Arnold
 * @version 13/03/2018
 */
public class Main
{
    // private HashMap<String, ...>

    public static void main(String[] args)
    {
        // Repeat for each input file.
        for (int i = 1; i < 2; i++) {

            String[] contents = fileContents("./inputs/example" + i + ".in").split("\n");

            String[] header = contents[0].split(" ");

            int W = Integer.parseInt(header[0]);

            int C = Integer.parseInt(header[1]);

            int B = Integer.parseInt(header[2]);

            int k = Integer.parseInt(header[3]);

            int numOfBlocks = C / B;

            int indexLength = (int) (Math.log(numOfBlocks) / Math.log(2));

            int bytesInLine = B / k;

            int offsetLength = (int) (Math.log(bytesInLine) / Math.log(2));

            int tagLength = W - (indexLength + offsetLength);

            System.out.println("Tag length: " + tagLength);
            System.out.println("Offset length: " + offsetLength);
            System.out.println("Index length: " + indexLength);
            System.out.println("\n");

            // Iterate through each address (provided in decimal with each input file).
            for (int j = 1; j < contents.length; j++) {
                int address = Integer.parseInt(contents[j]);
                String addressString = Integer.toBinaryString(address);
                String paddedAddressString = String.format("%" + W + "s", addressString).replace(' ', '0');

                String tag = paddedAddressString.substring(0, tagLength);
                String index = paddedAddressString.substring(tagLength, tagLength + indexLength);
                String offset = paddedAddressString.substring(tagLength + indexLength, W);

                System.out.println("Tag: " + tag);
                System.out.println("Index: " + index);
                System.out.println("Offset: " + offset);
                System.out.println("\n");
            }
        }
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

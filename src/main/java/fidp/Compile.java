package fidp;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * 
 * For compiling an easier to understand version of definitions to the binary file.
 * Format per line: Name|EXT(|Bytes, Hex|Offset)+
 * 
 * Example: `Java 8 Class File|CLASS|CAFEBABE|0|34|7`
 *
 */
public class Compile {
	public static void main(String[] args) throws IOException {
		Scanner sc = new Scanner(new File("defs.txt"));
		FileOutputStream fos = new FileOutputStream("definitions.fidp");
		
		// Write header
		fos.write(new byte[] {0x31, 0x41, 0x59, 0x26});
		
		Pattern p = Pattern.compile("(?<!\\\\)\\|");
		
		int numdefs = 0;
		while(sc.hasNext()) {
			String line = sc.nextLine();
			
			if(line.trim().isEmpty() || line.trim().charAt(0) == '#')
				continue;
			
			numdefs++;
			String[] com = p.split(line);
			
			// Write begin marker
			fos.write(0xFF);
			
			// Write description
			com[0] = com[0].replace("\\|", "|");
			com[0] = com[0].replace("\\\\", "\\");
			fos.write(com[0].length());
			fos.write(com[0].getBytes());
			
			// Write extension
			fos.write(com[1].length());
			fos.write(com[1].getBytes());
			
			for(int j = 2; j < com.length; j += 2) {
				// Write begin marker
				fos.write(0xFF);
				
				// Write signature
				byte[] sig = hexStringToByteArray(com[j].replace(" ", ""));
				
				fos.write(sig.length);
				fos.write(sig);
				
				// Write offset
				fos.write(Byte.parseByte(com[j + 1]));
			}
			fos.write(0);
		}
		
		// Cap off with terminating byte
		fos.write(0);
		
		fos.flush();
		fos.close();
		
		System.out.println("Wrote " + numdefs + " definitions to file. ");
	}
	
	public static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
}

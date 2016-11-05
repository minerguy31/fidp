package fidp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Pattern;

public class CompilerNew {
	public static void main(String[] args) throws Exception {
		Scanner sc = new Scanner(new File("defs.txt"));
		FileOutputStream fos = new FileOutputStream("definitions.fidp");
		
		Pattern p = Pattern.compile("(?<!\\\\)\\|");
		
		// Write header
		fos.write(new byte[] {0x31, 0x41, 0x59, 0x26});
		
		@SuppressWarnings("unchecked")
		ArrayList<Signature>[] sigs = new ArrayList[257];
		
		int numdefs = 0;
		while(sc.hasNext()) {
			String line = sc.nextLine();
			String[] com = p.split(line);
			
			if(line.trim().isEmpty() || line.trim().charAt(0) == '#')
				continue;
			
			numdefs++;
			com[0] = com[0].replace("\\|", "|");
			com[0] = com[0].replace("\\\\", "\\");

			byte[][] bytes = new byte[com.length / 2 - 1][];
			int[] offsets = new int[com.length / 2 - 1];
			for(int j = 2; j < com.length; j += 2) {
				byte[] sig;
				try {
					// Write signature
					sig = hexStringToByteArray(com[j].replace(" ", ""));
				} catch(Exception e) {
					System.err.println("Error at line " + line);
					return;
				}
				bytes[j / 2 - 1] = sig;
				offsets[j / 2 - 1] = Integer.parseInt(com[j + 1]);
			}
			

			Signature sig = new Signature(com[0], com[1], bytes, offsets);

			if(offsets[0] != 0) {
				
				sigs[256] = sigs[256] == null ? new ArrayList<Signature>() : sigs[256];
				sigs[256].add(sig);
			} else {
				int b = bytes[0][0] & 0xFF;
				sigs[b] = sigs[b] == null ? new ArrayList<Signature>() : sigs[b];
				sigs[b].add(sig);
			}
		}
		
		System.out.println("Wrote " + numdefs + " defs to file");
		
		Signature[][] sigarr = new Signature[257][];
		
		Signature[] sref = new Signature[] {};
		for (int i = 0; i < sigs.length; i++) {
			ArrayList<Signature> s = sigs[i];
			sigarr[i] = s == null ? null : s.toArray(sref);
		}
		
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(sigarr);
		oos.flush();
		oos.close();
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

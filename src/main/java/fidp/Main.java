package fidp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Main {
	public static void main(String[] args) throws IOException {
		File f = new File("C:/Users/minerguy31/Downloads/audiocheck.net_sin_1000Hz_-3dBFS_3s.wav");
		
		// Signature s = new Signature("Java Class File", "CLASS", new byte[] {(byte)0xCA, (byte)0xFE, (byte)0xBA, (byte)0xBE}, 0);
		
		Set<Signature> sigs = new HashSet<Signature>();
		
		File defs = new File("definitions.fidp");
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(defs));

		if(bis.read() != 0x31) throw new Error("Invalid definitions.fidp");
		if(bis.read() != 0x41) throw new Error("Invalid definitions.fidp");
		if(bis.read() != 0x59) throw new Error("Invalid definitions.fidp");
		if(bis.read() != 0x26) throw new Error("Invalid definitions.fidp");
		
		while(bis.read() == 0xFF) {
			// Read name
			int len = bis.read();
			byte[] namebuf = new byte[len];
			bis.read(namebuf);
			String name = new String(namebuf);
			
			// Read extension
			len = bis.read();
			byte[] extbuf = new byte[len];
			bis.read(extbuf);
			String ext = new String(extbuf);

			ArrayList<byte[]> signatures = new ArrayList<byte[]>();
			ArrayList<Integer> offsets = new ArrayList<Integer>();
			
			while(bis.read() == 0xFF) {
				// Read signature
				len = bis.read();
				byte[] sig = new byte[len];
				bis.read(sig);
				
				// Read offset
				int offset = bis.read();
				offsets.add(offset);
				signatures.add(sig);
			}
			
			// Add to list
			sigs.add(new Signature(name, ext, signatures, offsets));
		}
		
		bis.close();
		
		FileInputStream fis = new FileInputStream(f);
		byte[] buf = new byte[1024];
		int i = fis.read(buf);
		fis.close();
		buf = Arrays.copyOfRange(buf, 0, i);
		
		for(Signature sig : sigs) {
			// System.out.println(sig);
			if(sig.matches(buf)) {
				System.out.println(sig);
				// break;
			}
		}
		
	}
}

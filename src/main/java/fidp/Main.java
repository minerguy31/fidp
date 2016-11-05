package fidp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Arrays;

public class Main {
	public static void main(String[] args) throws Exception {
		if(args.length != 1) {
			System.err.println("Expected 1 argument, got " + args.length);
			return;
		}
		
		File f = new File(args[0]);
		
		if(!f.exists()) {
			System.err.println("Input file " + args[0] + " not found!");
		}
		
		File defs = new File("definitions.fidp");
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(defs));

		byte[] fidpsig = new byte[] {0x31, 0x41, 0x59, 0x26};
		
		// Check signature
		for(byte b : fidpsig) {
			if(bis.read() != b) {
				bis.close();
				return;
			}
		}
		
		ObjectInputStream ois = new ObjectInputStream(bis);
		
		Signature[][] sigs = (Signature[][]) ois.readObject();
		
		ois.close();
		bis.close();
		
		FileInputStream fis = new FileInputStream(f);
		byte[] buf = new byte[1024];
		int i = fis.read(buf);
		fis.close();
		buf = Arrays.copyOfRange(buf, 0, i);
		
		int firstbyte = buf[0];
		
		for(Signature sig : sigs[firstbyte]) {
			if(sig.matches(buf))
				System.out.println(sig);
		}
		
	}
}

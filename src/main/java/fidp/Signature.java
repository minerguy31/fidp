package fidp;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class Signature implements Serializable {
	public Signature(String name, String extension, byte[][] sequence, int[] offset) {
		this.name = name;
		this.sequence = sequence;
		this.extension = extension;
		this.offset = offset;
	}
	
	private String name;
	private String extension;
	private byte[][] sequence;
	private int[] offset;
	
	@Override
	public String toString() { 
		return "." + extension + "\t" + name; 
	}
	
	public boolean matches(byte[] fn) throws IOException {
		for(int i = 0; i < sequence.length; i++) {
			byte[] seq = sequence[i];
			int off = offset[i];
			
			for(int j = 0; j < seq.length; j++) {
				if(seq[j] != fn[j + off])
					return false;
			}
		}
		
		return true;
	}
}

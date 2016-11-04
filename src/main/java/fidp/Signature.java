package fidp;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;

public class Signature {
	public Signature(String name, String extension, ArrayList<byte[]> sequence, ArrayList<Integer> offset) {
		this.name = name;
		this.sequence = sequence;
		this.extension = extension;
		this.offset = offset;
	}
	
	private String name;
	private String extension;
	private ArrayList<byte[]> sequence;
	private ArrayList<Integer> offset;
	
	@Override
	public String toString() { 
		return "." + extension + "\t" + name; 
	}
	
	public boolean matches(byte[] fn) throws IOException {
		for(int i = 0; i < sequence.size(); i++) {
			byte[] seq = sequence.get(i);
			int off = offset.get(i);
			for(int j = 0; j < seq.length; j++) {
				if(seq[j] != fn[j + off])
					return false;
			}
		}
		
		return true;
	}
}

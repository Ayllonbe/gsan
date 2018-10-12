package gsan.distribution.gsan_api.read_write;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class Format {
	
	public DecimalFormat df;
	
	
	public Format(int lim) {
		String sep = "0.";
		for(int i=0;i<lim;i++) {
			sep=sep+"0";
		}
	//	System.out.println("The format is "+sep);
		this.df = new DecimalFormat(sep);
		DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
		dfs.setDecimalSeparator('.');
		this.df.setDecimalFormatSymbols(dfs);	
	}
	
	public double round(double n) {
		
		return Double.parseDouble( df.format(n));
		
	}
	
	

}

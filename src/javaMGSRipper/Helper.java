package javaMGSRipper;

public class Helper {
	public static int bytesToInt(byte[] b) {
		int i;
		i = 0;
		if (b.length==4) {
			i+=(b[3] & 0xff) << 24;
			i+=(b[2] & 0xff) << 16;
			i+=(b[1] & 0xff) << 8;
			i+=(b[0] & 0xff);
		} else if (b.length==2) {
			i+=(b[1] & 0xff) << 8;
			i+=(b[0] & 0xff);			
		}
		return i;
	}
	
  public static long bytesToLong(byte[] b) {
    long l;
    l = 0;
    if (b.length==4) {
      l+=(b[3] & 0xff) << 24;
      l+=(b[2] & 0xff) << 16;
      l+=(b[1] & 0xff) << 8;
      l+=(b[0] & 0xff);
    } else if (b.length==2) {
      l+=(b[1] & 0xff) << 8;
      l+=(b[0] & 0xff);     
    }
    return l;
  }
	
	public static String bytesToString(byte[] b) {
		String s;
		s = "";
		for(int i=0;i<b.length;i++) {
			s+=(char)b[i];
		}
		return s;
	}
}

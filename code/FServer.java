import java.net.*;
import java.io.*;
import java.util.*;
 
public class FServer {
	public static byte[] CRLF = new byte[] { 0x0d, 0x0a };
	public static byte[] RDT = new byte[] { 0x52, 0x44, 0x54 };
	public static byte[] END = new byte[] { 0x45, 0x4e, 0x44 };

	public static void main(String[] args) {


		DatagramSocket ss = null;
		FileInputStream fis = null;
		DatagramPacket rp, sp;
		final int [] frame = new int[10];
		byte[] rd, sd,td,mymsg;
		String intlength;
		InetAddress ip = null;
		byte [] id;
		int port = 0;
		int los =0;
		int i =0; 

		try {
			if(args.length>2){
				for(i=0;i<args.length-2;i++){
	
				frame[i] =Integer.parseInt(args[i+2]);
				}}
				else{frame[0]=-2;}
			int consignment = 0;
			intlength = Integer.toString((consignment));
			String strConsignment;
			String strGreeting;
			int result = 0; // number of bytes read
	 		td = new byte[512];
			ss = new DatagramSocket(Integer.parseInt(args[0]));
			
			System.out.println("Server is up....\n");
			rd=new byte[512];
			rp = new DatagramPacket(rd,rd.length);
			mymsg = new byte[517 + intlength.length()];
			while(true)
			{
				rd=new byte[512];
				sd=new byte[512];
				id = new byte[1];
				rp = new DatagramPacket(rd,rd.length);
				try
				{
					ss.receive(rp);
					strConsignment = new String(rp.getData());
					ip = rp.getAddress(); 
					port =rp.getPort();
				
					if(new String(rp.getData()).contains("REQUEST"))
					{
						String a =  strConsignment.trim();
						a = a.substring(7,a.length());
						a = args[1]+a;
						fis = new FileInputStream(a);
						System.out.println("Received request for " + a + " from "  + ip + " port "  + port);
						consignment  = 0;
						ss.setSoTimeout(30);
					}
					else
					{
						
						//String cons = strConsignment.trim();
						//System.out.println(cons);
						//consignment = Integer.parseInt(cons.substring(3,cons.length()));
						consignment =  Byte.toUnsignedInt(rp.getData()[3]);
						//consignment = rp.getData()[3];
						System.out.println("Received ACK " + consignment  + "\n");}
						intlength = Integer.toString((consignment));
						mymsg  = new byte[518];
						
						result = fis.read(sd);
						if(result != -1)
						{
							td = sd;
							if(consignment == frame[los])
							{
								System.out.println("Forgot Consignment " + consignment);
								if(los<2){
									los++;
								}
								continue;
							}
							if(result < 512)
							{
								sd = new byte[result];
								mymsg = new byte[result + 9];
								sd = Arrays.copyOfRange(td,0,result);
								id[0] = (byte) consignment; 
								mymsg = concatenateByteArrays(RDT, id, sd,END,CRLF); 
								sp=new DatagramPacket(mymsg,mymsg.length,ip,port);
								ss.send(sp); 
								System.out.println("Sent Consignment #" + consignment + "\n");
							}
							else
							{
								
								id[0] = (byte) consignment;
								
								mymsg = concatenateByteArrays(RDT, id, sd,CRLF); 
								sp=new DatagramPacket(mymsg,mymsg.length,ip,port);
							
								ss.send(sp); 
								
								System.out.println("Sent Consignment #" + consignment + "\n");
							}

							rp=null;
							sp = null;

						}
						else{	
							result = -1;
							ss.setSoTimeout(0);
				}}
				catch(SocketTimeoutException ex)
				{
					intlength = Integer.toString((consignment));
					mymsg  = new byte[517 + intlength.length()];
					System.out.println("Timeout!");
					id[0] = (byte) consignment;
					if(result < 512)
					{
							sd = Arrays.copyOfRange(td,0,result);
							mymsg = concatenateByteArrays(RDT, id, sd,END,CRLF); 
					}
					else
							mymsg = concatenateByteArrays(RDT, id, td,CRLF);
					sp=new DatagramPacket(mymsg,mymsg.length,ip,port);
					ss.send(sp);
					System.out.println("Sent Consignment #" + (consignment) + "\n");
					
					}
					sp = null;
					}
					
				}
	
				
		catch (IOException ex) {
			System.out.println(ex.getMessage());}
		
		finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException ex) {
				System.out.println(ex.getMessage());
			}
		}
	}
	

	public static byte[] concatenateByteArrays(byte[] a, byte[] b, byte[] c, byte[] d) {
        byte[] result = new byte[a.length + b.length + c.length + d.length]; 
        System.arraycopy(a, 0, result, 0, a.length); 
        System.arraycopy(b, 0, result, a.length, b.length);
        System.arraycopy(c, 0, result, a.length+b.length, c.length);
        System.arraycopy(d, 0, result, a.length+b.length+c.length, d.length);
        return result;
    }

	public static byte[] concatenateByteArrays(byte[] a, byte[] b, byte[] c, byte[] d, byte[] e) {
        byte[] result = new byte[a.length + b.length + c.length + d.length + e.length]; 
        System.arraycopy(a, 0, result, 0, a.length); 
        System.arraycopy(b, 0, result, a.length, b.length);
        System.arraycopy(c, 0, result, a.length+b.length, c.length);
        System.arraycopy(d, 0, result, a.length+b.length+c.length, d.length);
        System.arraycopy(e, 0, result, a.length+b.length+c.length+d.length, e.length);
        return result;
    }
    
}

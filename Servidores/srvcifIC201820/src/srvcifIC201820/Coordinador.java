package srvcifIC201820;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.Security;
import java.util.concurrent.*;
import javax.management.*;

/**
 * Servidor con mecanismos de seguridad implementados
 * @author pa.suarezm
 */

public class Coordinador {

	private static ServerSocket ss;	
	private static final String MAESTRO = "MAESTRO: ";
	static java.security.cert.X509Certificate certSer; /* acceso default */
	static KeyPair keyPairServidor; /* acceso default */
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{

		System.out.println(MAESTRO + "Establezca puerto de conexion:");
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);
		int ip = Integer.parseInt(br.readLine());
		System.out.println(MAESTRO + "Empezando servidor maestro en puerto " + ip);
		// Adiciona la libreria como un proveedor de seguridad.
		// Necesario para crear certificados.
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		keyPairServidor = Seg.grsa();
		certSer = Seg.gc(keyPairServidor);
		
		//Crea el pool
		int numT = 2;
		ExecutorService exec = Executors.newFixedThreadPool(numT);
		System.out.println(MAESTRO + "Creado el pool de tamanio " + numT);
		int idThread = 0;
		// Crea el socket que escucha en el puerto seleccionado.
		ss = new ServerSocket(ip);
		System.out.println(MAESTRO + "Socket creado.");		
		while (true) {
			try {
				Socket sc = ss.accept();
				System.out.println(MAESTRO + "Cliente " + idThread + " aceptado.");
				//Delegado3 d3 = new Delegado3(sc,idThread);
				exec.execute(new Delegado3(sc, idThread));
				idThread++;
				//d3.start();
				System.out.println(getSystemCpuLoad()); //Cada vez que se acepta una nueva conexi�n, se imprime
														//el porcentaje de uso de la CPU
			} catch (IOException e) {
				System.out.println(MAESTRO + "Error creando el socket cliente.");
				e.printStackTrace();
			}
		}
	}
	
	public static double getSystemCpuLoad() throws Exception{
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		ObjectName name = ObjectName.getInstance("java.lang:type=OperatingSystem");
		AttributeList list = mbs.getAttributes(name,  new String[]{"SystemCpuLoad"});
		
		if(list.isEmpty())
			return Double.NaN;
		
		Attribute att = (Attribute) list.get(0);
		Double value = (Double)att.getValue();
		
		//Usually takes a couple of seconds before we get real values
		if(value == -1.0)
			return Double.NaN;
		//Returns a percentage value with 1 decimal point precision
		return ((int)(value*1000)/10.0);
	}
	


}

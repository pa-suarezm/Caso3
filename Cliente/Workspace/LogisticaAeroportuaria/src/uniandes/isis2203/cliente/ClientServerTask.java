package uniandes.isis2203.cliente;

import uniandes.gload.core.*;

/**
 * GLoad Core Class - Task
 * @author pa.suarezm
 */
public class ClientServerTask extends Task{
	
	@Override
	public void execute(){
		Cliente client = new Cliente();
		client.sendMessageToServer();
	}
	
	@Override
	public void fail(){
		System.out.println(Task.MENSAJE_FAIL);
	}
	
	@Override
	public void success(){
		System.out.println(Task.OK_MESSAGE);
	}

}

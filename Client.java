import java.io.*;
import java.net.*;

public class Client{
	static int threadCount=5;
	static String server="localhost";
	static int port=1008;
	public static void main(String[] args){
		for(int i=1;i<=threadCount;i++){
			System.out.println("启动线程"+i);
			new MultClient("线程"+i).start();
			try{
				Thread.sleep(500);
			}catch(InterruptedException e){
				System.out.println("客户端线程休眠时被打断.");
			}
		}
	}
}

class MultClient extends Thread{
	private Socket client;
	private String name;
	public MultClient(String name){
		this.name=name;
	}
	public void run(){
		try{
			client=new Socket(Client.server,Client.port);
			System.out.println(name+"连接服务器成功.");
			try{
				BufferedReader in=new BufferedReader(new InputStreamReader(client.getInputStream(),"UTF-8"));
				PrintWriter out=new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream(),"UTF-8")),true);
				for(int i=1;i<=10;i++){
					String sendMsg=name+"发来第"+i+"次问候.";
					System.out.println("客户端发送: "+sendMsg);
					out.println(sendMsg);
					String str=in.readLine();
					System.out.println("服务器回应: "+str);
					for(long j=0;j<1000000000;j++);
				}
				out.println("END");
			}catch(IOException ee){
        		System.out.println(name+"获取服务端输入输出流时出错.");
			}
			System.out.println(name+"断开与服务器的连接.");
			client.close();
		}catch(IOException e){
			System.out.println(name+"连接服务器失败.");
		}
	}
}
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.net.*;
public class Server{
    public static void main(String[] args){
        int port=1008;
        ServerSocket server=null;
        try{
            server=new ServerSocket(port);
            System.out.println("启动服务器成功.");
            ExecutorService pool=Executors.newCachedThreadPool();
            Object semaphor=new Object();
            while(true){
                Socket client=server.accept();
                pool.execute(new Mult(client,semaphor));
            }
        }catch(IOException e){
            System.out.println("启动服务器失败.");
        }
    }
}
class Mult implements Runnable{
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    public Object semaphor;
    public Mult(Socket client,Object semaphor){
        this.client=client;
        this.semaphor=semaphor;
        try{
            in=new BufferedReader(new InputStreamReader(client.getInputStream(),"UTF-8"));
            out=new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream(),"UTF-8")),true);
        }catch(IOException e){
            System.out.println("获取客户端输入输出流时出错.");
        }
    }
    public void run(){
        StringBuilder LogMsg=new StringBuilder();
        LogMsg.append("<b>"+(new Date().toString())+"</b><br>\n<b>"+client.toString()+"</b><br><hr>\n");
        System.out.println("客户端 "+client+" 连接成功.");
        int count=0;
        while(true){
            try{
                String str=in.readLine();
                if(str==null)break;
                System.out.println(str);
                LogMsg.append("客户端: "+str+"<br>\n");
                if(str.equals("END"))break;
                count++;
                String msg="已收到"+count+"条消息.";
                LogMsg.append("服务器: "+msg+"<br>\n");
                out.println(msg);
            }catch(IOException e){
                break;
            }
        }
        try{
            System.out.println("客户端"+client+"断开连接.");
            client.close();
        }catch(IOException e){
            System.out.println("客户端断开时出错.");
        }
        LogMsg.append("\n<br>");
        synchronized(semaphor){
            try{
                OutputStream fout=new FileOutputStream("ServerLog.html",true);
                try{
                    fout.write(LogMsg.toString().getBytes("utf-8"));
                    fout.close();
                }catch(IOException ioe){
                    System.out.println("写入文件失败.");
                }
            }catch(FileNotFoundException fe){
                System.out.println("无法打开文件.");
            }
        }
    }
}
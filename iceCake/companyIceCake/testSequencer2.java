package companyIceCake;

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;

import java.net.MulticastSocket;



import companyIceCake.Group.MsgHandler;

/**this is where testSequencer instances are created 
 * they are all instances of Group 
 * 
 * the main role here is to join the Group 
 * and be to send messages to the group 
  */

public class testSequencer2 implements MsgHandler, Runnable  {

    public long currentSequence;
    private String message;
    public int count;
    long expectedSequence;
    String missing;
    long MissingSequence;

    String SenderName;
    //InetAddress ip = s.getLocalAddress();
    
   
    
    public void setSenderName(String name){
        this.SenderName = name;
    }

    public String getSenderName(){
        return this.SenderName;
    }

  

    public testSequencer2(String host, String name){
    try{
        MulticastSocket s = new MulticastSocket(6789);
        InetAddress ip = s.getLocalAddress();
        String newIP = ip.toString();
        Group group =  new Group(host, this, name);  
        this.setSenderName(name);
        InetAddress mcastaddr = InetAddress.getByName(host);
      //  s.joinGroup(mcastaddr);
        Thread th = new Thread(group);
        th.start();
        s.close();
       }catch(Exception e){
           e.printStackTrace();
       }

    }
    public static void main(String[] args) throws InterruptedException {
        try{
            System.out.println("please enter group address, like '225.0.0.1': ");
            BufferedReader addr = new BufferedReader(new InputStreamReader(System.in));
            String address = addr.readLine();

            System.out.println("Enter prefered name: ");
            BufferedReader nam = new BufferedReader(new InputStreamReader(System.in));
            String name = nam.readLine();
            
            testSequencer2 ts2 = new testSequencer2(address, name);
            Thread t = new Thread(ts2);

        t.start();
        }catch(Exception e){}
        
      
    }

    public Group group(){
        Group groupMember = null ;
        try{
         groupMember =  new Group("225.0.0.1", this, this.getSenderName());
         System.out.println("hullo "+this.getSenderName());  
        }catch(Exception e){

        }
        return groupMember;
    }

    @Override
    public void run() {
        
        try{
            Group group = group();
            MulticastSocket s = new MulticastSocket(6789);
         do{
            BufferedReader options = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("press 'q' to leave group or 'm' to enter text message");
            String option = options.readLine();


            if(option.trim().equals("q")){
                group.leave();
                System.exit(1);
            }
            if(option.trim().equals("m")){
                BufferedReader user_in = new BufferedReader(new InputStreamReader(System.in));
                System.out.println("enter text:");
                String info = user_in.readLine();
                
                byte [] m = info.getBytes();
                
               
                long lastReceiveSeq = group.generateNextSequence();
                
                group.send(this.getSenderName(), m, lastReceiveSeq);

                byte [] buffer = new byte[1000];

                DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
                
               // s.receive(messageIn);
                //System.out.println("received.."+ new String(messageIn.getData()));
            }
            s.close();
        }while(true);
     }catch(Exception e){

     }

    }

    @Override
    public void handle(int count, byte[] msg) {
        String messag = new String(msg, 0, count);
    }

    //this is the no Arg constructor...
    public testSequencer2(){
        
    }

   
}
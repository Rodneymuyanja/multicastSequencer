package companyIceCake;

import java.rmi.*;
import java.rmi.server.*;

import companyIceCake.history.MessageDetails;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * this is the Sequencer implementation and also acts as the stub whose methods 
 * are invoked by group.java 
 * 
 * the stub also serializes join info and returns it group.java where 
 * deserializing is done whenever a new user joins 
 *  
 */
public class SequencerImpl extends UnicastRemoteObject implements Sequencer {

    /**
     *
     */
    private static final long serialVersionUID = -4661320006414977562L;

    protected SequencerImpl() throws RemoteException {
        super();
    }
     

    @Override
    public SequencerJoinInfo join(String sender, String host) throws IOException {
        MessageDetails msgDetails = new MessageDetails();
        SequencerJoinInfo sequencerJoinInfo = null;
        long sequence = msgDetails.Sequence();

        MulticastSocket s = new MulticastSocket(6789);
        try{
            //joining the multicastgroup
           // String host = "127.0.0.1";
            
            InetAddress group = InetAddress.getByName(host);
            s.joinGroup(group);
            InetAddress address = s.getInetAddress();
            sequencerJoinInfo = new SequencerJoinInfo(group, sequence);
            System.out.println(" joined group "+group);

            this.serialize(sequencerJoinInfo);

        }catch(IOException e){
               e.printStackTrace();
        }finally{if( s!=null){
            s.close();
        }} 

        return sequencerJoinInfo;
    }

    @Override
    public void send(String sender, byte[] msg, long msgID, long lastSequenceReceived, String host) throws IOException {
       
        MulticastSocket  ms = new MulticastSocket(6789);
        history hist = new history();
        try {
            InetAddress group = InetAddress.getByName(host);
        
            String msgIDStr = Long.toString(msgID);
            long CurrentSequence = lastSequenceReceived + 1;

            System.out.println("......."+CurrentSequence);
            String currentSeq = Long.toString(CurrentSequence); 

            ByteArrayOutputStream bstream = new ByteArrayOutputStream();

            bstream.write(msg);
           // bstream.write(msgIDStr.getBytes());
            //bstream.write(currentSeq.getBytes());

            byte [] details = bstream.toByteArray();
 
            String message = new String(msg);
            
            InetAddress addr = InetAddress.getLocalHost();

            hist.historyWriter(sender,addr, message, msgID, CurrentSequence);

            //send currentsequence so that if other peers try to send they just add to it

            DatagramPacket messageOut = new DatagramPacket(details, details.length, group,6789);
            ms.send(messageOut);

        }catch(Exception e){
            e.printStackTrace();
        }finally{if(ms!=null){
            ms.close();
        }}  

    }

    @Override
    public void leave(String host) throws RemoteException {
        try{  
           // int port = Integer.parseInt(host); 
           MulticastSocket  ms = new MulticastSocket(6789);
            
             InetAddress group = InetAddress.getByName(host);    
           //  System.out.println(sender+"leaving group....");  
             ms.leaveGroup( group );   
             ms.close();
        } catch ( IOException ioException ){
            ioException.printStackTrace();
        } 

    }

    @Override
    public String getMissing(long sequence) throws RemoteException{//, SequencerException {
        history hist = new history();
        String missingString = hist.historyReadMissing(sequence);
        return missingString;
    }

    @Override
    public void heartbeat(String sender, long lastSequenceReceived) throws RemoteException {
        // TODO Auto-generated method stub

    }
    
    public long generateMsgID(){
        MessageDetails messageDetails = new MessageDetails();
        return messageDetails.Msgid();
    }
    
    public long generateNextSequence(){
        MessageDetails messageDetails = new MessageDetails();
        return messageDetails.Sequence();
    }
   
    public void serialize(SequencerJoinInfo sequencerJoinInfo){
        try {
            FileOutputStream out = new FileOutputStream("serialized_details.ser");
            ObjectOutputStream outputStream = new ObjectOutputStream(out);
            outputStream.writeObject(sequencerJoinInfo);
            out.close();
            outputStream.close();
          System.out.println("serializing done!!!!!!");
           //}
        } catch (Exception e) {
            //TODO: handle exception
        }

    }

    @Override
    public String getSenderName() throws RemoteException {
        MessageDetails messageDetails = new MessageDetails();
        String lastline = messageDetails.lastline();
        System.out.println(lastline);
        String [] lastlineArray = lastline.split("-");
        
        String sendername = lastlineArray[1];
        System.out.println(sendername);
        return sendername;
    }

}

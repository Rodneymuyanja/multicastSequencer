package companyIceCake;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * this is the class that keeps records of all messages 
 * senders names, dates, addresses, messageIDS and their sequences 
 * 
 * this class is only accessed by the Sequencer on the server alone 
 * 
 * creates historyLog, writes to it, locates missing messages if requested 
 * 
 * it also contains an inner class called MessageDetails, this class 
 * assigns IDs and keeps sequences of texts in order 
 */

public class history {
    // creates file
    // writes to file
    // reads from file
    public void createFile(){
        try{
        File file = new File("history.txt");
        if(file.createNewFile()){
            System.out.println("created file");
        }else{
          //  System.out.println("file already exists");
        }

        }catch(Exception e){

        }
    }

    public long historyWriter(String name, InetAddress addr, String message, long MessageID, long sequence){
        try{
            this.createFile();

            /*so here we create the string we need to send */


            DateTimeFormatter dt = DateTimeFormatter.ofPattern("yyyy/MM/dd~HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            String time = dt.format(now);
            FileWriter write = new FileWriter("history.txt", true);

            //its abit easy to see the contents 
            // they are also easily easily read in history.txt
            String msg = time+"-"+name+"-"+addr+"-"+message+"-"+MessageID+"-"+sequence;
            write.write('\n');
            write.write(msg);
            write.close();
        }catch(IOException e){

        }
        return MessageID;
    }

    public String historyReadMissing(long MissingSequence){
        String MissingString = null;

        try{

            // this checks for the MissingSequence 
            // and returns the MissingString itself 

            File file = new File("history.txt");
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            StringBuffer sb = new StringBuffer();
            String line;
            while((line=br.readLine())!=null){
                
                long missingSeq = this.sequence(line);
                
                if(missingSeq == MissingSequence){
                    sb.append(line);
                    
                    MissingString = sb.toString();
                }
            }

            br.close();
        }catch(Exception e){

        }

        return MissingString;
    }

    public long sequence(String line){
        // this is where we seperated the string in printed in 
        // history.java using split()
        // we specify the regex incase its '-'

        String [] string = line.split("-");
       // System.out.println(string[0]);
        String missingStrLong = string[5];
        long missinLong = Long.parseLong(missingStrLong);
        return missinLong;
    }

    public static class MessageDetails{
        private long msgId ;
        private long sequence ;

        //this isolates the last received line...
        public String lastline(){
            String sCurrentLine;
            String lastLine = null;
            try{
                File file = new File("history.txt");
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);

                while ((sCurrentLine = br.readLine()) != null){
                   // System.out.println(sCurrentLine);
                    lastLine = sCurrentLine;
                }
                br.close();
            }catch(Exception e){

            }

            return lastLine;
        }

        //creates next messageid by incrementing the previous one in 
        //history.txt
        public long Msgid(){
            long previousID = this.getpreviousId();
            this.msgId = previousID + 1;
            return msgId;
        }

        //increases sequence
        public long Sequence(){
            long previousSequence = this.getprevioussequence();
            this.sequence = previousSequence  ;
            return sequence;
        }

        private long getpreviousId(){
            String lastline = this.lastline();
            String [] lastlineArray = lastline.split("-");
            String id = lastlineArray[4];
            long msgId = Long.parseLong(id);
            return msgId;
        }

        private long getprevioussequence(){
            String lastline = this.lastline();
            String [] lastlineArray = lastline.split("-");
            String sequence = lastlineArray[5];
            long msgSequence = Long.parseLong(sequence);
            return msgSequence;
        }

        //this checks the sequence of messages 
        //if the difference is greater than one then clearly we are missing 
        public boolean checkSequence(long sequence){
            long previousSequence = this.getprevioussequence();
            long difference = sequence - previousSequence;

            if(difference > 1){
                return true;
            }else{
                return false;
            }
            
        }
    }
}

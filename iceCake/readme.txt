with java installed.....

step 1: go into the folder CompanyIceCake and do "javac *.java"
step 2: go to the command line and do "rmic SequencerImpl" generates stub
step 3: on the command line type "rmiregistry 6789" 
step 4: on the commandline type "start rmiregistry"
step 5: change directory to iceCake and do "java companyIceCake.server" should start the server
step 6: do "java companyIceCake.testSequencer2" starts the client instances to send messages
step 7: do "java companyIceCake.MulticastPeer" so as to see other group members' messges 


make as many as possible for stress testing 

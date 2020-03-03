package example;

public class Main {
    public static void main(String[] arg){
    	Thread server = new Thread(new Runnable(){ public void run() { 							try{ 
    		new MServerApp(); /* Create Server */ 												}catch (Exception e) {e.printStackTrace();}
		}});        
        
        Thread client = new Thread(new Runnable(){ public void run() {							try{
        	new MClientApp(); /* Create Client */ 												}catch (Exception e) {e.printStackTrace();}
        }});
        
        server.start();
        client.start();
    }
}

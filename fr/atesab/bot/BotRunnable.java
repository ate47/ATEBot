package fr.atesab.bot;

import java.util.HashMap;
import java.util.Map;

public class BotRunnable implements Runnable {
	public static Map<String, Long> userPts = new HashMap<String,Long>();
	public static long addPts(String user, long pts){
		long l = userPts.getOrDefault(user, 0L);
    	l+=pts;
    	if(l<0){
    		if(userPts.containsKey(user)) userPts.remove(user);
    		l = 0L;
    	} else {
    		if(l-pts<Main.afBlockMessage && l>=Main.afBlockMessage) l = (long)(Main.afBlockMessage*1.5D);
    		userPts.put(user, l);
    	}
    	return l;
	}
	@Override
    public void run() {
        try {
	        long before, sleepDuration, operationTime;
	        while(true){
	            before = System.currentTimeMillis();
	            //ANTIFLOOD
	            for(Object key: userPts.keySet()){
	            	long l = userPts.get(key);
	            	l-=Main.afPointLosePerSecond;
	            	if(l<0){
	            		userPts.remove(key);
	            	} else {
	            		userPts.put((String) key, l);
	            	}
	            }
	            operationTime = (System.currentTimeMillis() - before);
	            if(operationTime>10000) System.out.println("Did the system time change ? An operation just take "+operationTime+"ms");
	            sleepDuration = Math.min(1000, Math.max(1000 - operationTime, 0));
	            Thread.sleep(sleepDuration);
	        }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

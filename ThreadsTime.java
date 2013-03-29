
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.Collection;
 
public final class ThreadsTime
    extends Thread
{
    private class Times {
        public long id;
        public long startCpuTime;
        public long startUserTime;
        public long endCpuTime;
        public long endUserTime;
    }
 
    private final long interval;
    private final long threadId;
    private final HashMap<Long,Times> history = new HashMap<Long,Times>();
 
    /** Create a polling thread to track times. */
    public ThreadsTime( final long interval ) {
        super( "Thread time monitor" );
        this.interval = interval;
        threadId = getId( );
        setDaemon( true );
        this.start();
    }
 
    /** Run the thread until interrupted. */
    public void run( ) {
        while ( !isInterrupted( ) ) {
            update( );
            try { sleep( interval ); }
            catch ( InterruptedException e ) { break; }
        }
    }
 
    /** Update the hash table of thread times. */
    private void update( ) {
        final ThreadMXBean bean =
            ManagementFactory.getThreadMXBean( );
        final long[] ids = bean.getAllThreadIds( );
        for ( long id : ids ) {
            if ( id == threadId )
                continue;   // Exclude polling thread
            final long c = bean.getThreadCpuTime( id );
            final long u = bean.getThreadUserTime( id );
            if ( c == -1 || u == -1 )
                continue;   // Thread died
 
            Times times = history.get( id );
            long usrCycle = 0;
            long totCycle = 0;
            if ( times == null ) {
                times = new Times( );
                times.id = id;
                times.startCpuTime  = c;
                times.startUserTime = u;
                times.endCpuTime    = c;
                times.endUserTime   = u;
                history.put( id, times );

            } else {
                usrCycle = u-times.endUserTime;
                totCycle = c-times.endCpuTime;
                times.endCpuTime  = c;
                times.endUserTime = u;
            }
            
            //long usr = times.endUserTime-times.startUserTime;
            //long tot = times.endCpuTime-times.startCpuTime;
            
            long thres=0;
            if(totCycle>thres){
            	System.out.println(times.id+"\t usr:"+usrCycle/1000000f+"\t sys:"+(totCycle-usrCycle)/1000000f+"\t\t"+bean.getThreadInfo(id).getThreadName());
            }
        }
        System.out.println(" ");
    }
 
    /** Get total CPU time so far in nanoseconds. */
    public long getTotalCpuTime( ) {
        final Collection<Times> hist = history.values( );
        long time = 0L;
        for ( Times times : hist )
            time += times.endCpuTime - times.startCpuTime;
        return time;
    }
 
    /** Get total user time so far in nanoseconds. */
    public long getTotalUserTime( ) {
        final Collection<Times> hist = history.values( );
        long time = 0L;
        for ( Times times : hist )
            time += times.endUserTime - times.startUserTime;
        return time;
    }
 
    /** Get total system time so far in nanoseconds. */
    public long getTotalSystemTime( ) {
        return getTotalCpuTime( ) - getTotalUserTime( );
    }
}
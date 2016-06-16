 package org.indigo.cloudproviderranker;
 
 import java.util.Map;
 
 public class Main {

    public static final void main(String[] args) {
    	
	Map<String, String> env = System.getenv();
	String home = env.get("HOME");

	int TCPPORT = 8080;
	boolean usessl = false;
	String keystorepath = null;
	String password = null;

	if(args.length<2) {
	  System.err.println("Must specify at least the SLA priority file as first argument and monitoring normalization file as second argument");
	  System.exit(1);
	}

	SlaNormalizations.priority_file = args[0];
	PaaSMetricRanked.normalization_file = args[1];

	if(args.length>2) {
	    TCPPORT = Integer.parseInt( args[2] );
	}	
	if(args.length>=4 && args.length <5) {
	    System.err.println("If a keystore path is specified then must specify also a password. STOP!");
	    System.exit(1);
	}
	if(args.length == 5) {
	    usessl = true;
	    keystorepath = args[3];
	    password = args[4];
	}
	
	RESTEngine re = new RESTEngine();
	try {
	  re.initHttpServer( usessl, TCPPORT, keystorepath, password );
	} catch(ServerException se) {
	  System.err.println(se.getMessage( ) );
	  System.exit(1);
	}
	re.addHandlerToContext("/rank", new RankHandler());
	re.startServer( );
	System.out.println("HTTP" + (usessl ? "S" : "") + " Server is listening on port "+TCPPORT+"\n");
    }
}
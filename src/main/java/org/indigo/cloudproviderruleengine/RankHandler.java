package org.indigo.cloudproviderruleengine;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Vector;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.google.gson.*;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

/**
 * 
 * Class to parse the request's POST payload and convert into CloudProvider Objects and rank them
 * 
 * @author dorigoa
 *
 */
class RankHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange t) throws IOException {
	if(t.getRequestMethod( ).compareToIgnoreCase("POST")!=0) {
	    String response = "API \"rank\" only supports POST method";
	    t.sendResponseHeaders(405, response.getBytes().length);
	    OutputStream os = t.getResponseBody();
	    os.write(response.getBytes());
	    os.close();
	    return;
	}
	
	List<CloudProvider> cpvec = null;
	try {
	    InputStream is = t.getRequestBody();
	    InputStreamReader inputReader = new InputStreamReader(is,"utf-8");
	    BufferedReader buffReader = new BufferedReader(inputReader);
	    String line = buffReader.readLine();
	    Gson gson = new Gson();
	    JsonElement E = gson.fromJson(line, JsonElement.class);
	    JsonObject obj = E.getAsJsonObject( );
	    if ( obj.has("cloudproviders") ) {
		cpvec = parse( obj.get("cloudproviders").getAsJsonArray( ) );
	    }
	    KieServices ks = KieServices.Factory.get();
	    KieContainer kContainer = ks.getKieClasspathContainer();
	    KieSession kSession = kContainer.newKieSession("ksession-rules");
	    for(CloudProvider cp : cpvec )
		kSession.insert(cp);
	    
	    int tot = kSession.fireAllRules();
	    System.out.println("Total rules applied="+tot+"\n");
	    
	    Vector<String> respVec = new Vector<String>();
	    for(CloudProvider cp : cpvec ) {
		RankedCloudProvider rcp = new RankedCloudProvider( cp.getID(), cp.getName(), cp.getTotalRank() );
		Gson gson2 = new Gson();
		String json = gson2.toJson(rcp);
		respVec.add(json);
	    }
	    String response = "{\"rankedcloudproviders\":[" + String.join(",", respVec) + "]}";
	    System.out.println(response+"\n\n");
	    t.sendResponseHeaders(200, response.getBytes().length);
	    OutputStream os = t.getResponseBody();
	    os.write(response.getBytes());
	    os.close();
	    
	} catch(Exception e) {
	    String err = "Exception parsing JSON client request: " + e.getMessage() + "\n";
	    t.sendResponseHeaders(400, err.getBytes().length);
	    OutputStream os = t.getResponseBody();
	    os.write(err.getBytes());
	    os.close();
	    e.printStackTrace();
	    
	} catch(Throwable e) {
	    String err = "Throwable parsing JSON client request: " + e.getMessage() + "\n";
	    t.sendResponseHeaders(400, err.getBytes().length);
	    OutputStream os = t.getResponseBody();
	    os.write(err.getBytes());
	    os.close();
	    e.printStackTrace();
	}
	
	
    }


    /**
     *
     *
     * Convert JsonObject to CloudProvider object
     *
     *
     */
    static List<CloudProvider> parse(JsonArray array) {
    	List<CloudProvider> cpvec = new Vector<CloudProvider>( );
        for(int i = 0; i < array.size( ); i++) {
	    try {
		JsonObject obj = array.get( i ).getAsJsonObject();
		System.out.println("Processing provider ["+obj.toString());
		Gson gson = new GsonBuilder().create();
		CloudProvider cp = gson.fromJson(obj, CloudProvider.class);
		cpvec.add(cp);
	    } catch(Exception e) {
		System.out.println(e.getMessage());
	    } catch(Throwable t) {
		System.out.println(t.getMessage());
	    } 
	}
	return cpvec;
    }
}
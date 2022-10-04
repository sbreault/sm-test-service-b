package org.acme.getting.started;

import org.eclipse.microprofile.config.ConfigProvider;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("/service-b")
public class GreetingResource {

    private static String HOSTNAME;
    private static int requestCounter;

    String appVersion = ConfigProvider.getConfig().getValue("app.version", String.class);
    int breakCounter = ConfigProvider.getConfig().getValue("app.break.exec.count.int", Integer.class);
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response hello()  {
        GreetingResource.requestCounter++;

        if(isMustBreak()){
            //throw exception
            return Response.status(Response.Status.GATEWAY_TIMEOUT).build();
        }
        return Response.status(Response.Status.OK)
            .entity("To get to the other side. (" + getIdentification() + ").").build();        
    }

    private boolean isMustBreak(){
        //use counter to determine when to 'break' (i.e. every 3 requests)
        if(GreetingResource.requestCounter == breakCounter){
            GreetingResource.requestCounter = 0;  //reset counter
            return true;
        }
        return false;
    }    

    private String getIdentification(){
        return appVersion + "; " + getHostName();
    }    

    private String getHostName(){
        if(GreetingResource.HOSTNAME == null){
            try{    
                GreetingResource.HOSTNAME = System.getenv("HOSTNAME");
                if(GreetingResource.HOSTNAME == null) 
                    GreetingResource.HOSTNAME = System.getenv("COMPUTERNAME");  //Windows desktop name
                
                System.out.println("HOSTNAME=" + GreetingResource.HOSTNAME);
                
                //max 128 char
                if(GreetingResource.HOSTNAME != null && GreetingResource.HOSTNAME.length() > 128){                    
                    GreetingResource.HOSTNAME = GreetingResource.HOSTNAME.substring(0, 127);
                }                
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        if(GreetingResource.HOSTNAME == null) GreetingResource.HOSTNAME = "/local PC/not hosted";
        return GreetingResource.HOSTNAME;
    }     
}
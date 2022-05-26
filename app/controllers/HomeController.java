package controllers;

import play.mvc.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Future;
import com.fasterxml.jackson.databind.*;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;

import dao.UserDAOImpl;
import models.Albums;
import models.AlbumsJSON;
import models.User;


/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
	
	UserDAOImpl dao = new UserDAOImpl();
	
    
    public Result index() {
        return ok(views.html.index.render());
    }
    
    public Result explore() {
        return ok(views.html.explore.render());
    }
    
    public Result tutorial() {
        return ok(views.html.tutorial.render());
    }
    
   
    public Result add(Http.Request request) {
    //	JsonNode json = request.body().asJson();
    	try {
    	    
    		List<Future<String>> completableFutures = new ArrayList<>();
    		completableFutures.add(dao.infoUser());
    		completableFutures.add(dao.infoModel());
            
            CompletableFuture<Void> allFutures = CompletableFuture
                    .allOf(completableFutures.toArray(new CompletableFuture[completableFutures.size()]));
            allFutures.get();
            ObjectMapper mapper = new ObjectMapper();
            List<User> listUser = new ArrayList<>();
            listUser = mapper.readValue(completableFutures.get(0).get(), mapper.getTypeFactory().constructCollectionType(List.class, User.class));
             
            List<AlbumsJSON> listAlbums = new ArrayList<>(); 
            listAlbums = mapper.readValue(completableFutures.get(1).get(),  mapper.getTypeFactory().constructCollectionType(List.class, AlbumsJSON.class));
            Integer j=0;
            List<Albums> listaInsert = new ArrayList<>();
            for (AlbumsJSON item : listAlbums) {
            	if (item.getUserId().equals(listUser.get(j).getId())) {
            		Albums a = new Albums();
            		a.setTitle(item.getTitle());
            		listaInsert.add(a);
            	}else{
            		listUser.get(j).setAlbums(listaInsert);
            		j++;
            		listaInsert = new ArrayList<>();
            		Albums a = new Albums();
            		a.setTitle(item.getTitle());
            		listaInsert.add(a);
            	}
            }
            String jsonStr = mapper.writeValueAsString(listUser);
            return ok(jsonStr);
    	}catch(Exception e) { return notFound(e.getMessage());}
    	
    }
   
}

package rest;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

import domain.Actor;
import domain.Comments;
import domain.Grade;
import domain.Movie;
import domain.services.ActorService;
import domain.services.MovieService;

@Path("/actors")
public class ActorResources {
	
	private ActorService Adb = new ActorService();
	private MovieService Mdb = new MovieService();
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Actor> getAll()
	{
		return Adb.getAll();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response Add(Actor actor){
		Adb.add(actor);
		return Response.ok(actor.getId()).build();
	}
	
	
	//--------------------------------------------------------
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@PathParam("id") int id){
		Actor result = Adb.get(id);
		if(result == null){
			return Response.status(404).build();
		}
		
		return Response.ok(result).build();
	}
	
	
	
	@GET
	@Path("/{actorId}/movies")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Movie> getMovies(@PathParam("actorId") int ActorId){
		Actor result = Adb.get(ActorId);
		if(result == null)
			return null;
		if(result.getMovies() == null)
			result.setMovies(new ArrayList<Movie>());
		return result.getMovies();
	}
	
	@POST
	@Path("/{id}/movies")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addComments(@PathParam("id") int ActorId, Movie movie){
		Actor result = Adb.get(ActorId);
		boolean choose = false;
		if(result == null)
			return Response.status(404).build();
		if(result.getMovies() == null)
			result.setMovies(new ArrayList<Movie>());
		result.getMovies().add(movie);
		Adb.add(result);
		for(Movie m1 : Mdb.getAll())
		{
			if(movie.getTitle().equalsIgnoreCase(m1.getTitle()))
				{
				choose = true;
				break;
				}
			
		}
		Actor actor = new Actor();
		actor.setId(result.getId());
		actor.setName(result.getName());
		actor.setSurname(result.getSurname());
		
		if(!choose){
		movie.setActors(new ArrayList<Actor>());
		movie.getActors().add(actor);
			Mdb.add(movie);	
		}
		else
		{
			for(Movie m2 : Mdb.getAll()){
				if(movie.getTitle().equalsIgnoreCase(m2.getTitle())){
					m2.getActors().add(actor);
				}
			}
		}
		return Response.ok().build();
	}

}

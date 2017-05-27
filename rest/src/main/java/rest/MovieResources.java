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


@Path("/movie")
public class MovieResources {

	private int commId = 0;
	private MovieService db = new MovieService();
	private ActorService Adb = new ActorService();
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Movie> getAll()
	{
		return db.getAll();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response Add(Movie movie){
		db.add(movie);
		return Response.ok(movie.getId()).build();
	}

	//---------------------------------------------
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@PathParam("id") int id){
		Movie result = db.get(id);
		if(result == null){
			return Response.status(404).build();
		}
		
		return Response.ok(result).build();
	}
	
	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(@PathParam("id") int id, Movie m){
		Movie result = db.get(id);
		if(result == null)
			return Response.status(404).build();
		m.setId(id);
		for(Actor a: Adb.getAll()){
			for(Movie x : a.getMovies()){
				if(x.getTitle().equalsIgnoreCase(result.getTitle())){
					x.setTitle(m.getTitle());
				}
			}
		}
		db.update(m);
		return Response.ok().build();		
	}
	
	@DELETE
	@Path("/{id}")
	public Response delete(@PathParam("id") int id){
		Movie result = db.get(id);
		if(result == null)
			return Response.status(404).build();
		db.delete(result);
		return Response.ok().build();		
	}
	
	@GET
	@Path("/{movieId}/comments")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Comments> getComments(@PathParam("movieId") int movieId){
		Movie result = db.get(movieId);
		if(result == null)
			return null;
		if(result.getComments() == null)
			result.setComments(new ArrayList<Comments>());
		return result.getComments();
	}
	
	@POST
	@Path("/{id}/comments")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addComments(@PathParam("id") int movieId, Comments comments){
		Movie result = db.get(movieId);
		if(result == null)
			return Response.status(404).build();
		if(result.getComments() == null)
			result.setComments(new ArrayList<Comments>());
		for(Comments com : result.getComments())
		{
			commId++;
		}
		comments.setId(++commId);
		result.getComments().add(comments);
		return Response.ok().build();
	}
	@DELETE
	@Path("/{id}/comments/{commentId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response removeComments(@PathParam("id") int movieId, @PathParam("commentId") int commentId){
		Movie result = db.get(movieId);
		commentId--;
		boolean choose = false;
		if(result == null)
			return Response.status(404).build();

				result.getComments().remove(commentId);
		
		return Response.ok().build();	
	}
	
	//---
	
	@GET
	@Path("/{movieId}/grades")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Grade> getGrade(@PathParam("movieId") int movieId){
		Movie result = db.get(movieId);
		double x= 0;
		int i = 0;
		if(result == null)
			return null;
		if(result.getGrades() == null)
			result.setGrades(new ArrayList<Grade>());
		for(Grade g : result.getGrades())
		{
			x += g.getGrade(); 
			i++;
		}
		x = x/i;
		Grade grade = new Grade();
		grade.setGrade(x);
		result.setGrades(new ArrayList<Grade>());
		result.getGrades().add(grade);
		return result.getGrades();
	}
	
	@POST
	@Path("/{id}/grades")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addGrade(@PathParam("id") int movieId, Grade grade){
		Movie result = db.get(movieId);
		if(result == null)
			return Response.status(404).build();
		if(result.getGrades() == null)
			result.setGrades(new ArrayList<Grade>());
		result.getGrades().add(grade);
		return Response.ok().build();
	}
	
	//Actors =================================
	
	@POST
	@Path("/{id}/actors")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addActor(@PathParam("id") int movieId, Actor actor){
		Movie result = db.get(movieId);
		boolean choose = false;
		if(result == null)
			return Response.status(404).build();
		if(result.getActors() == null)
			result.setActors(new ArrayList<Actor>());
		result.getActors().add(actor);
		for(Actor a : Adb.getAll()){
			if(actor.getName().equalsIgnoreCase(a.getName()) && actor.getSurname().equalsIgnoreCase(a.getSurname())){
				choose = true;
				break;
			}		
		}
		Movie movie = new Movie();
		movie.setId(result.getId());
		movie.setTitle(result.getTitle());
		if(!choose){			
			actor.setMovies(new ArrayList<Movie>());
			actor.getMovies().add(movie);
			Adb.add(actor);
		}
		else {
			for(Actor a2 : Adb.getAll()){
				if(actor.getName().equalsIgnoreCase(a2.getName()) && actor.getSurname().equalsIgnoreCase(a2.getSurname())){										
					a2.getMovies().add(movie);
				}
			}
		}
		return Response.ok().build();
	}
	
	
	@GET
	@Path("/{id}/actors")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Actor> getActors(@PathParam("id") int id){
		Movie result = db.get(id);
		if(result == null)
			return null;
		if(result.getActors() == null)
			result.setActors(new ArrayList<Actor>());
		return result.getActors();
	}
	
}

package module6;

import java.awt.TextArea;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.data.ShapeFeature;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.geo.Location;
import parsing.ParseFeed;
import processing.core.PApplet;
import controlP5.*;

/** An applet that shows airports (and routes)
 * on a world map.  
 * @author Adam Setters and the UC San Diego Intermediate Software Development
 * MOOC team
 *
 */
public class AirportMap extends PApplet {
	
	UnfoldingMap map;
	private List<Marker> airportList;
	private List<Marker> routeList;
	ControlP5 cp5;
	String url1,url2;
	AirportMarker m;
	SimpleLinesMarker sl;
	ArrayList<Integer> from_ID;
	ArrayList<Integer> to_ID;
	String value = "";
	Textarea ta;
	Textfield text1;
	Textfield text2;
	
	// get features from airport data
	List<PointFeature> features = ParseFeed.parseAirports(this, "airports.dat");
	
	//get features from routes data
	List<ShapeFeature> routes = ParseFeed.parseRoutes(this, "routes.dat");
	
	public void setup() {
		// setting up PAppler
		size(1000,700, OPENGL);
		
		// setting up map and default events
		map = new UnfoldingMap(this, 150, 50, 850, 600);
		MapUtils.createDefaultEventDispatcher(this, map);
		
		from_ID = new ArrayList<Integer>();
		to_ID = new ArrayList<Integer>();
		
		// list for markers, hashmap for quicker access when matching with routes
		airportList = new ArrayList<Marker>();
		HashMap<Integer, Location> airports = new HashMap<Integer, Location>();
		
		// create markers from features
		for(PointFeature feature : features) {
			 m = new AirportMarker(feature);
	
			m.setRadius(5);
			airportList.add(m);
			
			
			// put airport in hashmap with OpenFlights unique id for key
			airports.put(Integer.parseInt(feature.getId()), feature.getLocation());
		
		}
		
		
		// parse route data
		
		routeList = new ArrayList<Marker>();
		for(ShapeFeature route : routes) {
			
			// get source and destination airportIds
			int source = Integer.parseInt((String)route.getProperty("source"));
			int dest = Integer.parseInt((String)route.getProperty("destination"));
			
			// get locations for airports on route
			if(airports.containsKey(source) && airports.containsKey(dest)) {
				route.addLocation(airports.get(source));
				route.addLocation(airports.get(dest));
			}
			
			 sl = new SimpleLinesMarker(route.getLocations(), route.getProperties());
		
			System.out.println(route.getProperties()+"--------");
			System.out.println(sl.getProperties());
			
			//UNCOMMENT IF YOU WANT TO SEE ALL ROUTES
			routeList.add(sl);
			for(int i=0;i<routeList.size();i++){
				  routeList.get(i).setHidden(true);
			}
		}
		
		
		
		//UNCOMMENT IF YOU WANT TO SEE ALL ROUTES
		map.addMarkers(routeList);
		
		map.addMarkers(airportList);
		
		
		//text input and button
		cp5 = new ControlP5(this);
		 
		  text1=cp5.addTextfield("from")
		    .setPosition(20, 50)
		      .setSize(140, 40)
		      	.setFont(createFont("arial", 20))
		          .setFocus(true)
		              ;
		 
		  text2=cp5.addTextfield("to")
		    .setPosition(20, 120)
		      .setSize(140, 40)
		        .setFont(createFont("arial", 20))
		         .setFocus(true)
		          .setAutoClear(false)
		            ;
		 
		  cp5.addBang("Submit")
		    .setPosition(20, 190)
		      .setSize(140, 40)
		          .getCaptionLabel().align(ControlP5.CENTER, ControlP5.CENTER)
		          ;    
		
		
		  cp5.addBang("Reset")
		    .setPosition(20, 260)
		      .setSize(140, 40)
		          .getCaptionLabel().align(ControlP5.CENTER, ControlP5.CENTER)
		          ;    
		  
		 ta= cp5.addTextarea("txt")
                  .setPosition(15,330)
                  .setSize(150,300)
                  .setFont(createFont("arial",12))
                  .setLineHeight(10)
                  .setColor(color(128))
                  .setColorBackground(color(255,100))
                  .setColorForeground(color(255,100))
                  .setText("Airline Info will be shown here \n"+value)
                  ;
	}
	
	public void draw() {
		background(0);
		map.draw();
		
	}
	
	void bang1() {
		
		  from_ID.clear();
		  to_ID.clear();
		  print("the following text was submitted :\n");
		  url1 = cp5.get(Textfield.class,"from").getText();
		  url2 = cp5.get(Textfield.class,"to").getText();
		  
		  //ta.setText("hahahahhaha");
		  print(url1+"----"+url2);
		  for(int i=0;i<airportList.size();i++){
			  airportList.get(i).setHidden(true);
			  
			  if(airportList.get(i).getProperty("city").toString().toLowerCase().equals(('"'+url1+'"'))||
						airportList.get(i).getProperty("city").toString().toLowerCase().equals(('"'+url2+'"'))){
					 
				  ((SimplePointMarker)airportList.get(i)).setRadius(25);
					airportList.get(i).setColor(color(255,0,0));
				    airportList.get(i).setHidden(false);
					
				}
		  }	  
		  
		  for(PointFeature f : features){
			  if(f.getProperty("city").toString().toLowerCase().equals(('"'+url1+'"'))){

				  System.out.println(f.getId()+"---------"+Integer.parseInt(f.getId()));
				  from_ID.add(Integer.parseInt(f.getId()));
			  }
			  
			  if(  f.getProperty("city").toString().toLowerCase().equals(('"'+url2+'"'))){
				  System.out.println(f.getId()+"******");
				  to_ID.add(Integer.parseInt(f.getId()));

			  }
		  }
		  
		  for(int i=0;i<routeList.size();i++){
			  routeList.get(i).setHidden(true);

			  int routeStart = Integer.parseInt(routeList.get(i).getProperty("source").toString());
			  int routeEnd = Integer.parseInt(routeList.get(i).getProperty("destination").toString());
			  if(from_ID.contains(routeStart)&&to_ID.contains(routeEnd)){
					   
				  System.out.println("route route route");
				  routeList.get(i).setHidden(false);
				  
				 
			  }
		  }
		  
		  for(ShapeFeature route : routes) {
				
				// get source and destination airportIds
				int source = Integer.parseInt((String)route.getProperty("source"));
				int dest = Integer.parseInt((String)route.getProperty("destination"));
				if(from_ID.contains(source)&&to_ID.contains(dest)){
					   
					  System.out.println("222222");
					  System.out.println(route.getProperty("Airline"));
					  String airline =new String(route.getProperty("Airline").toString());
					  String stops = new String(route.getProperty("Stops").toString());
					  value = value+"Airline:  "+airline+" : "+"Stops:  "+stops+"\n";
					 
				  }
		  }
		  
		  ta.setText(value);
		  
	}
	
	public void bang2(){
		for(int i=0;i<airportList.size();i++){
			  airportList.get(i).setHidden(false);
		  }
		
		for(int i=0;i<routeList.size();i++){
			routeList.get(i).setHidden(true);
		}
		
		from_ID.clear();
		to_ID.clear();
		ta.setText("Airline information will be shown here");
		text1.setText("");
		text2.setText("");
		
	}
	
	
	public void mouseClicked(){
		if(mouseX>=20&&mouseX<=160&&mouseY<=230&&mouseY>=190){
			bang1();
		}else if(mouseX>=20&&mouseX<=160&&mouseY<=300&&mouseY>=260){
			bang2();
		}
	}
//	void controlEvent(ControlEvent theEvent) {
//		  /* events triggered by controllers are automatically forwarded to 
//		     the controlEvent method. by checking the name of a controller one can 
//		     distinguish which of the controllers has been changed.
//		  */ 
//		 
//		  /* check if the event is from a controller otherwise you'll get an error
//		     when clicking other interface elements like Radiobutton that don't support
//		     the controller() methods
//		  */
//		  
//		  if(theEvent.isController()) { 
//		    
//		    print("control event from : "+theEvent.controller().getName());
//		    println(", value : "+theEvent.controller().getValue());
//		    
//		    if(theEvent.controller().getName().equals("Submit")) {
//		      bang();   
//		    }
//	
//		  }
//	
//	}
}

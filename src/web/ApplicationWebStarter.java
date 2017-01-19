package web;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/")
public class ApplicationWebStarter extends Application{
	
	@Override
    public Set<Class<?>> getClasses()
    {
        final Set<Class<?>> classes = new HashSet<>();
        
        new Device1();
        classes.add(Device1.class);
        
        classes.add(GeospatialConfigurator.class);
        
        return classes;
    }

}

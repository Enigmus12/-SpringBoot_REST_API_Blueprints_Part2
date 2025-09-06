package edu.eci.arsw.blueprints;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistenceException;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.services.BlueprintsServices;
import java.util.Set;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


/**
 * @author Juan David Rodriguez
 */

/** App class for testing the blueprint services */

public class App {
    public static void main(String[] args) throws BlueprintPersistenceException, BlueprintNotFoundException {
        // Create context and scan the base package for components
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.scan("edu.eci.arsw.blueprints");
        ctx.refresh();

        BlueprintsServices svc = ctx.getBean(BlueprintsServices.class);

        // register a new blueprint
        Blueprint newBp = new Blueprint("juan", "plano1", new Point[]{ new Point(0,0), new Point(0,0), new Point(1,1), new Point(2,2), new Point(3,3)});
        svc.addNewBlueprint(newBp);

        // get all (filtered) blueprints
        Set<Blueprint> all = svc.getAllBlueprints();
        System.out.println("All (filtered): ");
        all.forEach(b -> {
            System.out.println(" - " + b.getAuthor() + "/" + b.getName() + " points: " + b.getPoints().size());
            b.getPoints().forEach(p -> System.out.print("("+p.getX()+","+p.getY()+") "));
            System.out.println();
        });

        // get by author
        Set<Blueprint> byAuthor = svc.getBlueprintsByAuthor("juan");
        System.out.println("Blueprints by juan (filtered): " + byAuthor.size());

        // get specific one
        Blueprint b = svc.getBlueprint("juan", "plano1");
        System.out.println("Blueprint 'juan/plano1' points (filtered): " + b.getPoints().size());

        ctx.close();
    }
}

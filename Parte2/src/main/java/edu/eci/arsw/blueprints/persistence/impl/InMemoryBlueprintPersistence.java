package edu.eci.arsw.blueprints.persistence.impl;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistenceException;
import edu.eci.arsw.blueprints.persistence.BlueprintsPersistence;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Repository;


/**
 *
 * @author Juan David Rodriguez
 */

/**
 * In-memory implementation of the blueprint persistence interface. This class is
 * a simple implementation that stores blueprints in the memory of the application.
 */

@Repository
public class InMemoryBlueprintPersistence implements BlueprintsPersistence {

    private final Map<Tuple<String,String>,Blueprint> blueprints=new HashMap<>();

    public InMemoryBlueprintPersistence() {
        // load some stub data
        Point[] pts1=new Point[]{new Point(140, 140), new Point(115, 115), new Point(115,115)};
        Blueprint bp1=new Blueprint("_authorname_", "_bpname_", pts1);
        blueprints.put(new Tuple<>(bp1.getAuthor(),bp1.getName()), bp1);

        Point[] pts2=new Point[]{new Point(10,10), new Point(20,20), new Point(30,30), new Point(40,40)};
        Blueprint bp2=new Blueprint("author2", "bp2", pts2);
        blueprints.put(new Tuple<>(bp2.getAuthor(),bp2.getName()), bp2);
    }

    @Override
    public void saveBlueprint(Blueprint bp) throws BlueprintPersistenceException {
        Tuple<String,String> key = new Tuple<>(bp.getAuthor(), bp.getName());
        if (blueprints.containsKey(key)){
            throw new BlueprintPersistenceException("The given blueprint already exists: "+bp);
        } else {
            blueprints.put(key, bp);
        }
    }

    @Override
    public Blueprint getBlueprint(String author, String bprintname) throws BlueprintNotFoundException {
        Blueprint bp = blueprints.get(new Tuple<>(author, bprintname));
        if (bp == null) {
            throw new BlueprintNotFoundException("Blueprint not found: " + author + ", " + bprintname);
        }
        return bp;
    }

    @Override
    public Set<Blueprint> getAllBlueprints() {
        return new HashSet<>(blueprints.values());
    }

    @Override
    public Set<Blueprint> getBlueprintsByAuthor(String author) throws BlueprintNotFoundException {
        Set<Blueprint> r = new HashSet<>();
        for (Map.Entry<Tuple<String,String>, Blueprint> e : blueprints.entrySet()) {
            if (e.getKey().getElem1().equals(author)) {
                r.add(e.getValue());
            }
        }
        if (r.isEmpty()) {
            throw new BlueprintNotFoundException("No blueprints found for author: " + author);
        }
        return r;
    }

}

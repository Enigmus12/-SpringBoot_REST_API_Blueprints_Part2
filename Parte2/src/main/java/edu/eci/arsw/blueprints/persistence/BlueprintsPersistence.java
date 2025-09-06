package edu.eci.arsw.blueprints.persistence;

import edu.eci.arsw.blueprints.model.Blueprint;
import java.util.Set;

/**
 *
 * @author Juan David Rodriguez
 */

/** 
 *  Interface defining the operations for managing blueprints in persistence.
 */

public interface BlueprintsPersistence {

    public void saveBlueprint(Blueprint bp) throws BlueprintPersistenceException;

    public Blueprint getBlueprint(String author,String bprintname) throws BlueprintNotFoundException;

    // Method to retrieve all blueprints
    public Set<Blueprint> getAllBlueprints();

    public Set<Blueprint> getBlueprintsByAuthor(String author) throws BlueprintNotFoundException;

}

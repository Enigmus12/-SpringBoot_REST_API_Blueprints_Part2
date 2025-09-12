## Description

This project implements a **REST API** for the management of *Blueprints*, developed with **Spring Boot**.  
The application allows **creating, querying, and updating** through endpoints, ensuring consistency and security in a **concurrent** environment.

The API was designed following **clean architecture principles** and **concurrency best practices**, ensuring proper handling of multiple simultaneous requests.

---

## Main Features

- Full CRUD for **Blueprints**:
  - Create new blueprints (`POST`)
  - Query blueprints (`GET`)
  - Update blueprints (`PUT`)
- Filtering blueprints (by author or globally).
- Exception handling with appropriate HTTP status codes (`404`, `400`, `500`).
- Defensive copies to prevent uncontrolled modifications under concurrency.
- Compatible with **Postman**, `curl`, and automated tests.

---

## Technologies Used

- **Java 17**  
- **Spring Boot** (Web, Dependency Injection)  
- **Maven** (dependency management and build)  
- **JUnit 5** (unit testing)  

---

## Project Structure

```plaintext
src/main/java/
 └── edu.eci.arsw.blueprints
     ├── controllers/       # REST Controllers
     ├── model/             # Entities (Blueprint, Point, etc.)
     ├── persistence/       # Interfaces and repositories (thread-safe)
     ├── services/          # Business logic and filters
     └── App                # Main class

```

## Execution

1. Clone the repository:

git clone https://github.com/Enigmus12/-SpringBoot_REST_API_Blueprints_Part2.git

cd Parte2

2. Compile:

mvn clean install

3. Run:

mvn spring-boot:run

4. The API will be available at:

http://localhost:8080/blueprints

## Main Endpoints

1. Get all blueprints

GET http://localhost:8080/blueprints

2. Get all blueprints by an author

GET http://localhost:8080/blueprints/{author}

3. Get a specific blueprint

GET http://localhost:8080/blueprints/{author}/{bpname}

4. Create a new blueprint — there are two ways: from the console or Postman. From console:

    Invoke-WebRequest -Uri "http://localhost:8080/blueprints" `
    -Method POST `
    -Headers @{ "Content-Type"="application/json"; "Accept"="application/json" } `
    -Body '{"author":"carlos","name":"nuevoPlano","points":[{"x":1,"y":1},{"x":2,"y":2}]}'

From Postman:

    * Método: POST
    * URL: http://localhost:8080/blueprints
    * Headers:
        + Content-Type: application/json
        + Accept: application/json

    * Body → Raw → JSON:

    ```plaintext
    {
        "author": "carlos",
        "name": "nuevoPlano",
        "points": [
            {"x": 1, "y": 1},
            {"x": 2, "y": 2}
        ]
    }
    ```

5. Update an existing blueprint — two ways: from the terminal:

    Invoke-WebRequest -Uri "http://localhost:8080/blueprints/juan/plano1" `
    -Method PUT `
    -Headers @{ "Content-Type"="application/json"; "Accept"="application/json" } `
    -Body '{"author":"juan","name":"plano1","points":[{"x":10,"y":10},{"x":20,"y":20}]}'

And Postman: 

    * Método: PUT
    * URL: http://localhost:8080/blueprints/juan/plano1
    * Headers:
        + Content-Type: application/json
        + Accept: application/json

    * Body → Raw → JSON:

    ```plaintext
    {
    "author": "juan",
    "name": "plano1",
    "points": [
        { "x": 10, "y": 10 },
        { "x": 20, "y": 20 }
        ]
    }
    ```

## CONCURRENCY_ANALYSIS Part III

1. Description of the concurrency problem
The BlueprintsRESTAPI component handles concurrent requests (multiple threads). The original implementation used a non-thread-safe HashMap and compound operations (query + modify) on mutable objects stored in the collection. This led to race conditions when:
 - Registering (POST) a new blueprint: two concurrent requests with the same author+name could both pass the existence check and insert, generating duplicates or inconsistencies.

 - Updating (PUT) a blueprint: the previous implementation fetched the stored object and mutated it (clear + addAll). If two threads updated the same blueprint simultaneously, operations interleaved and data was lost.

 - Concurrent reads while writes occurred could expose intermediate states (partially mutated objects).

2. Identified critical sections
 - The blueprints collection (map) itself — unprotected concurrent access.
 - The region "read object" + "modify object in memory" (in-place mutation of the points list).
 - Compound operations 'if-not-exists then put' (check-then-act) during save.

3. Solution applied
 - Replaced HashMap with ConcurrentHashMap (thread-safe collection).
 - Used atomic operations on the collection:
   * putIfAbsent(key, value) for insertions (avoids check-then-put race).
   * computeIfPresent(key, remappingFunction) for atomic updates by key (complete object replacement).
 - Avoided in-place mutations of stored objects: persistence stores/returns defensive copies (new Blueprint(...) from points). Updates replace the entire object (copy-on-write at object level).
 - No global synchronization (e.g., synchronized methods) was applied to avoid performance degradation; all measures were aimed at allowing concurrency with per-key atomic operations.

4. Justification and evaluation
 - The solution prevents main race conditions since insert and update operations are no longer compound but atomic within the thread-safe collection.
 - Returning defensive copies prevents external consumers from mutating stored objects, eliminating another race source.
 - According to course criteria: use of conditional atomic aggregation methods in the Thread-Safe collection → grade B.

5. Risks and future improvements
 - ConcurrentHashMap is sufficient for this lab. For production environments, consider:

    * Truly immutable objects (full immutability of Blueprint/Point),
    * Versioning validations (optimistic locking) if simultaneous edit control is required,
    * Persisting in a transactional database to guarantee durability and consistency across nodes.



## Author
    Juan David Rodríguez
    Escuela Colombiana de Ingeniería Julio Garavito.
package web.server.demo.client;

import org.springframework.http.ResponseEntity;

/**
 * Simple SPI used to define an interface for CRUD operations via HTTP.
 *
 * @param <E> the type of elements which the SPI handles
 */
public interface DemoSpi<E> {

    /**
     * Adds an element to the repository. The method should be mapped to a POST request.
     *
     * @param item the element to be saved
     * @return a <tt>ResponseEntity</tt> with the appropriate <tt>HttpStatus</tt> and desired response body
     */
    ResponseEntity<?> add(E item);

    /**
     * Adds an array of elements to the repository. The method should be mapped to a POST request.
     *
     * @param items the elements to be saved
     * @return a <tt>ResponseEntity</tt> with the appropriate <tt>HttpStatus</tt> and desired response body
     */
    ResponseEntity<?> add(E[] items);

    /**
     * Retrieves an element from the repository by its id. The method should be mapped to a GET request.
     *
     * @param id the id associated with the element to be retrieved
     * @return a <tt>ResponseEntity</tt> with the appropriate <tt>HttpStatus</tt> and desired response body
     */
    ResponseEntity<?> retrieve(long id);

    /**
     * Retrieves a list of elements names from the repository. The method should be mapped to a GET request.
     *
     * @param prefix the prefix of the elements' names to be retrieved
     * @return a <tt>ResponseEntity</tt> with the appropriate <tt>HttpStatus</tt> and desired response body
     */
    ResponseEntity<?> retrieve(String prefix);

    /**
     * Retrieves the list of all elements names from the repository. The method should be mapped to a GET request.
     *
     * @return a <tt>ResponseEntity</tt> with the appropriate <tt>HttpStatus</tt> and desired response body
     */
    ResponseEntity<?> retrieve();

    /**
     * Renames an element in the repository. The method should be mapped with a PUT request.
     *
     * @param id the id associated with the element to be renamed
     * @param name the new name of the element
     * @return a <tt>ResponseEntity</tt> with the appropriate <tt>HttpStatus</tt> and desired response body
     */
    ResponseEntity<?> rename(long id, String name);

    /**
     * Replaces an element in the repository. The method should be mapped with a POST request.
     *
     * @param id the id associated with the element to be replaced
     * @param item the new item associated with the id
     * @return a <tt>ResponseEntity</tt> with the appropriate <tt>HttpStatus</tt> and desired response body
     */
    ResponseEntity<?> replace(long id, E item);

    /**
     * Removes an element from the repository. The method should be mapped with a DELETE request.
     *
     * @param id the id associated with the element to be removed
     * @return a <tt>ResponseEntity</tt> with the appropriate <tt>HttpStatus</tt> and desired response body
     */
    ResponseEntity<?> remove(long id);

    /**
     * Removes a list of elements from the repository. The method should be mapped with a DELETE request.
     *
     * @param ids the ids of the elements to be removed
     * @return a <tt>ResponseEntity</tt> with the appropriate <tt>HttpStatus</tt> and desired response body
     */
    ResponseEntity<?> remove(long[] ids);
}

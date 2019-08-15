package web.server.demo.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface FileDataRepository extends CrudRepository<FileData, Long> {

    /**
     * Finds all files starting with a matching prefix.
     *
     * @param prefix the prefix
     * @return a <tt>Collection</tt> of <tt>FileData</tt> who's names starts with the prefix
     */
    Collection<FileData> findByNameStartingWith(String prefix);
}

package eg.edu.alexu.cse.mail_server.Repository;

import eg.edu.alexu.cse.mail_server.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    //User findByEmail(String email);

    User findById(long id);

}

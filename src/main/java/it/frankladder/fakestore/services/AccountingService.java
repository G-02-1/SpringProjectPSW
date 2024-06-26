package it.frankladder.fakestore.services;


import it.frankladder.fakestore.repositories.UserRepository;
import it.frankladder.fakestore.support.exceptions.MailUserAlreadyExistsException;
import it.frankladder.fakestore.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@Service
public class AccountingService {

    /*
    I services sono molto più ad alto livello, questo service essendo un e-commerce si occupa di prendere tutti gli utenti
    grazie alla funzione getAllUser() selavti nella userRepository,
    registrare l'utente tramite registerUser (quella scritta qui non è una soluzione ottimale vedremo più in là come fare).
     */

    @Autowired
    private UserRepository userRepository;


    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public User registerUser(User user) throws MailUserAlreadyExistsException {
        if ( userRepository.existsByEmail(user.getEmail()) ) {
            throw new MailUserAlreadyExistsException();
        }
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


}

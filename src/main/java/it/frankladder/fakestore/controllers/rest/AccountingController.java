package it.frankladder.fakestore.controllers.rest;


import it.frankladder.fakestore.support.exceptions.MailUserAlreadyExistsException;
import it.frankladder.fakestore.entities.User;
import it.frankladder.fakestore.services.AccountingService;
import it.frankladder.fakestore.support.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/users")
public class AccountingController {
    /*
    quando faremo il front-end potremmo presupporre di fare l'app client in più lingue solitamente c'è un file che contiene
    una chiave univoca e come valore la parola nelle varie lingue
     */

    @Autowired
    private AccountingService accountingService;


    @PostMapping
    public ResponseEntity create(@RequestBody @Valid User user) {
        try {
            User added = accountingService.registerUser(user);
            return new ResponseEntity(added, HttpStatus.OK);
        } catch (MailUserAlreadyExistsException e) {
            return new ResponseEntity<>(new ResponseMessage("ERROR_MAIL_USER_ALREADY_EXISTS"), HttpStatus.BAD_REQUEST);
            //"ERROR_MAIL_USER_ALREADY_EXISTS" chiave del messaggio nel dizionario delle lingue
        }
    }

    @GetMapping
    public List<User> getAll() {
        return accountingService.getAllUsers();
    }


}

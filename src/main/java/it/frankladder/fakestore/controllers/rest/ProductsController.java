package it.frankladder.fakestore.controllers.rest;


import it.frankladder.fakestore.entities.Product;
import it.frankladder.fakestore.services.ProductService;
import it.frankladder.fakestore.support.ResponseMessage;
import it.frankladder.fakestore.support.exceptions.BarCodeAlreadyExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;


@RestController //va annotato se è un Controller o un RestController
@RequestMapping("/products")
/*
http://mioDominio/ <-- indirizzo base del mio server o API
http://mioDominio/CLASS <-- possiamo fermarci qua oppure aggiungiamo /METODO
se c'è solo un request mapping richiama solo quello
http://mioDominio/products restituisce tutto
http://mioDominio/paged fa il getAll (vedi sotto)
 */
public class ProductsController {
    //tutta la logica della mia web app è nei services, qua devo solo inoltrare le richieste al web

    @Autowired
    private ProductService productService;


    @PostMapping
    /*
    il tutto viene tradotto in http e poi tramite json viene inoltrato al client
    viene effettuata la validazione grazie al tag @Valid
    posso inserire i parametri in 3 posti:
    -   nel body;
    -   nel DB;
    -   nell'URL.
     */

    public ResponseEntity create(@RequestBody @Valid Product product) {
        try {
            productService.addProduct(product);
        } catch (BarCodeAlreadyExistException e) {
            return new ResponseEntity<>(new ResponseMessage("Barcode already exist!"), HttpStatus.BAD_REQUEST);
            /*
            potrei restituire direttamente qua la lista/string vuota, oppure un messaggio, meglio la lista/stringa
            vuota così facciamo di meno ed è meglio...
            a scopo esemplificativo possiamo mettere il corpo della risposta in questo metodo
            ---> bar code already exists
             */
        }
        return new ResponseEntity<>(new ResponseMessage("Added successful!"), HttpStatus.OK);
    }

    @GetMapping
    public List<Product> getAll() {
        return productService.showAllProducts();
    }

    @GetMapping("/paged")
    /*
    ricerca paginata
    quando li inseriamo nel request param è quel classico http://mioDominio/CLASS/METODO/?px=1&py=2...
    */
    public ResponseEntity getAll(@RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, @RequestParam(value = "sortBy", defaultValue = "id") String sortBy) {
        List<Product> result = productService.showAllProducts(pageNumber, pageSize, sortBy);
        if ( result.size() <= 0 ) {
            return new ResponseEntity<>(new ResponseMessage("No results!"), HttpStatus.OK);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/search/by_name")
    public ResponseEntity getByName(@RequestParam(required = false) String name) {
        List<Product> result = productService.showProductsByName(name);
        if ( result.size() <= 0 ) {
            return new ResponseEntity<>(new ResponseMessage("No results!"), HttpStatus.OK);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


}

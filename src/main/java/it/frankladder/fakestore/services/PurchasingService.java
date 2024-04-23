package it.frankladder.fakestore.services;


import it.frankladder.fakestore.entities.Product;
import it.frankladder.fakestore.repositories.ProductInPurchaseRepository;
import it.frankladder.fakestore.repositories.PurchaseRepository;
import it.frankladder.fakestore.repositories.UserRepository;
import it.frankladder.fakestore.support.exceptions.DateWrongRangeException;
import it.frankladder.fakestore.support.exceptions.QuantityProductUnavailableException;
import it.frankladder.fakestore.support.exceptions.UserNotFoundException;
import it.frankladder.fakestore.entities.ProductInPurchase;
import it.frankladder.fakestore.entities.Purchase;
import it.frankladder.fakestore.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import java.util.Date;
import java.util.List;


@Service
public class PurchasingService {
    @Autowired
    private PurchaseRepository purchaseRepository;
    @Autowired
    private ProductInPurchaseRepository productInPurchaseRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EntityManager entityManager;


    /*
    Quando vogliamo inserire un oggetto nel DB diciamo .persist e lui lo inserisce,
    tuttavia può essere:
    Attached: se è collegato al DB (la modifica dell'oggetto nei metodi si rifletterà subito sul DB);
    Unattached: non è collegato al DB (la modifica non si riflette).
    Se l'istanza viene restituita dal repository (dal DB) sarà attached, se invece proviene da altre fonti non lo sarà.
     */
    @Transactional(readOnly = false)
    public Purchase addPurchase(Purchase purchase) throws QuantityProductUnavailableException {
        Purchase result = purchaseRepository.save(purchase);
        for ( ProductInPurchase pip : result.getProductsInPurchase() ) {
            pip.setPurchase(result);
            ProductInPurchase justAdded = productInPurchaseRepository.save(pip);
            //avrà come var di istanza quelle che avevamo settato noi in precedenza
            entityManager.refresh(justAdded); //forziamo il refresh del DB dalla entity rendendo le modifiche visibili sul DB
            Product product = justAdded.getProduct();
            int newQuantity = product.getQuantity() - pip.getQuantity();
            if ( newQuantity < 0 ) {
                throw new QuantityProductUnavailableException();
            }
            product.setQuantity(newQuantity);
            entityManager.refresh(pip);
        }
        entityManager.refresh(result);
        return result; //result è l'entity che contiene tutto l'acquisto
    }

    @Transactional(readOnly = true)
    public List<Purchase> getPurchasesByUser(User user) throws UserNotFoundException {
        if ( !userRepository.existsById(user.getId()) ) {
            throw new UserNotFoundException();
        }
        return purchaseRepository.findByBuyer(user);
    }

    @Transactional(readOnly = true)
    /*
    Non è da fare così:
    -   è giusto controllare se esiste l'utente, ma non bisogna mai prendere l'istanza utente che proviene dal client
        come in questo caso per motivi di sicurezza, infatti un malintenzionato può cambiare il suo id con quello di
        un altro user e visualizzarne le info
        BISOGNA PRENDERLI DAL TOKEN, poichè se ha il token JVT è sicuramente lui

    -   questi controlli vanno fatti anche nel client: come il controllo della data

    -   la validazione dei campi va fattta sia nel client che nel server ma prima sul client
        se viene fatta prima sul client siamo sicuri che una volta arrivata una richiesta dal client dovrebbero essere giuste,
        tuttavia se qualcuno accede in mariera illecita al server (ottenendo le mie API) tramite il secondo controllo,
        posso bloccare il tutto anche dal server
     */
    public List<Purchase> getPurchasesByUserInPeriod(User user, Date startDate, Date endDate) throws UserNotFoundException, DateWrongRangeException {
        if ( !userRepository.existsById(user.getId()) ) {
            throw new UserNotFoundException();
        }
        if ( startDate.compareTo(endDate) >= 0 ) {
            throw new DateWrongRangeException();
        }
        return purchaseRepository.findByBuyerInPeriod(startDate, endDate, user);
    }

    @Transactional(readOnly = true)
    public List<Purchase> getAllPurchases() {
        return purchaseRepository.findAll();
    }


}

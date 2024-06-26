package it.frankladder.fakestore.repositories;


import it.frankladder.fakestore.entities.Purchase;
import it.frankladder.fakestore.entities.User;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.*;
import java.util.Date;
import java.util.List;


@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Integer> {

    List<Purchase> findByBuyer(User user);
    List<Purchase> findByPurchaseTime(Date date);

    @Query("select p from Purchase p where p.purchaseTime > ?1 and p.purchaseTime < ?2 and p.buyer = ?3")
        /*
        stiamo cercando gli acquisti fatti in un determinato periodo, inoltre ?1, ?2, ?3
        è come scrivere su linux $1, $2, $3, quindi di base prende i valori in ordine
        */
    List<Purchase> findByBuyerInPeriod(Date startDate, Date endDate, User user);

}

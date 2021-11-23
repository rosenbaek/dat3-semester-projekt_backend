/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facades;

import dtos.stock.AddTransactionDTO;
import entities.Transaction;
import entities.User;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import security.errorhandling.AuthenticationException;

/**
 *
 * @author mikke
 */
public class StockFacade {
    private static EntityManagerFactory emf;
    private static StockFacade instance;

    private StockFacade() {
    }

    /**
     *
     * @param _emf
     * @return the instance of this facade.
     */
    public static StockFacade getStockFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new StockFacade();
        }
        return instance;
    }
    
    public Transaction addTransaction(Transaction transaction, String username) {
        EntityManager em = emf.createEntityManager();
        Transaction t = transaction;
        try {
            em.getTransaction().begin();
            User user = em.find(User.class,username);
            user.addTransaction(t);
            em.persist(t);
            em.getTransaction().commit();
            return t;
        } finally {
            em.close();
        }
    }
}

package utils;

import entities.Currency;
import entities.Group;
import entities.Role;
import entities.Stock;
import entities.Transaction;
import entities.User;
import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class SetupTestUsers {

    public static void main(String[] args) throws IOException {

        EntityManagerFactory emf = EMF_Creator.createEntityManagerFactory();
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.createNamedQuery("Transaction.deleteAllRows").executeUpdate();
        em.createNamedQuery("Group.deleteAllRows").executeUpdate();
        em.createNamedQuery("Stock.deleteAllRows").executeUpdate();
        em.createNamedQuery("PortfolioValue.deleteAllRows").executeUpdate();
        em.createNamedQuery("User.deleteAllRows").executeUpdate();
        em.createNamedQuery("Currency.deleteAllRows").executeUpdate();
        em.createNamedQuery("Role.deleteAllRows").executeUpdate();
        em.getTransaction().commit();
        
        //Fetches currencies and populates to DB.
        utils.Utility.populateCurrency();
        
        // IMPORTAAAAAAAAAANT!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        // This breaks one of the MOST fundamental security rules in that it ships with default users and passwords
        // CHANGE the three passwords below, before you uncomment and execute the code below
        // Also, either delete this file, when users are created or rename and add to .gitignore
        // Whatever you do DO NOT COMMIT and PUSH with the real passwords
        User user = new User("user", "testUser");
        User admin = new User("admin", "testAdmin");
        User both = new User("user_admin", "testBoth");

        Currency c1 = em.find(Currency.class, "usd");

        Group g1, g2, g3, g4;
        g1 = new Group("Group1");
        g2 = new Group("Group2");
        g3 = new Group("Group3");
        g4 = new Group("Group4");

        Stock s1 = new Stock("AAPL", "Apple Inc.", c1, 160.24);
        Stock s2 = new Stock("TSLA", "Tesla, Inc.", c1, 1136.0);

        Transaction t1 = new Transaction(s1, 10, 160.10);
        Transaction t2 = new Transaction(s2, 20, 1136.20);
        Transaction t3 = new Transaction(s2, 10, 1136.10);

        if (admin.getUserPass().equals("test") || user.getUserPass().equals("test") || both.getUserPass().equals("test")) {
            throw new UnsupportedOperationException("You have not changed the passwords");
        }

        
        
        
        em.getTransaction().begin();
        Role userRole = new Role("user");
        Role adminRole = new Role("admin");
        user.addRole(userRole);
        admin.addRole(adminRole);
        both.addRole(userRole);
        both.addRole(adminRole);
        user.setCurrencyCode(c1);
        admin.setCurrencyCode(c1);
        both.setCurrencyCode(c1);

        user.addTransaction(t1);
        user.addTransaction(t2);
        both.addTransaction(t3);

        user.addGroup(g1);
        user.addGroup(g2);
        user.addGroup(g3);
        both.addGroup(g4);

        g1.addTransaction(t1);
        g1.addTransaction(t2);
        g3.addTransaction(t1);
        g3.addTransaction(t2);

        em.persist(userRole);
        em.persist(adminRole);
        em.persist(s1);
        em.persist(s2);
        em.persist(t1);
        em.persist(t2);
        em.persist(t3);
        em.persist(user);
        em.persist(admin);
        em.persist(both);

        em.persist(g1);
        em.persist(g2);
        em.persist(g3);
        em.persist(g4);

        em.getTransaction().commit();
        System.out.println("PW: " + user.getUserPass());
        System.out.println("Testing user with OK password: " + user.verifyPassword("test"));
        System.out.println("Testing user with wrong password: " + user.verifyPassword("test1"));
        System.out.println("Created TEST Users");

    }

}

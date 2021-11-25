package utils;


import entities.Currency;
import entities.Role;
import entities.Stock;
import entities.Transaction;
import entities.User;
import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class SetupTestUsers {

  public static void main(String[] args) throws IOException {

    //Fetches currencies and populates to DB.
    utils.Utility.populateCurrency();
    EntityManagerFactory emf = EMF_Creator.createEntityManagerFactory();
    EntityManager em = emf.createEntityManager();
    
    // IMPORTAAAAAAAAAANT!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    // This breaks one of the MOST fundamental security rules in that it ships with default users and passwords
    // CHANGE the three passwords below, before you uncomment and execute the code below
    // Also, either delete this file, when users are created or rename and add to .gitignore
    // Whatever you do DO NOT COMMIT and PUSH with the real passwords

    User user = new User("user", "testUser");
    User admin = new User("admin", "testAdmin");
    User both = new User("user_admin", "testBoth");

    Currency c1 = em.find(Currency.class,"dkk");
    
    
    
    Stock s1 = new Stock("AAPL","Apple Inc.",c1,1000.0);
    Stock s2 = new Stock("TSLA","Tesla, Inc.",c1,2000.0);
    
    Transaction t1 = new Transaction(s1,100,100.100);
    Transaction t2 = new Transaction(s2,200,200.200);
    Transaction t3 = new Transaction(s2,10000,10000.10000);
    
    
    
    if(admin.getUserPass().equals("test")||user.getUserPass().equals("test")||both.getUserPass().equals("test"))
      throw new UnsupportedOperationException("You have not changed the passwords");

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
    
    em.getTransaction().commit();
    System.out.println("PW: " + user.getUserPass());
    System.out.println("Testing user with OK password: " + user.verifyPassword("test"));
    System.out.println("Testing user with wrong password: " + user.verifyPassword("test1"));
    System.out.println("Created TEST Users");
   
  }

}

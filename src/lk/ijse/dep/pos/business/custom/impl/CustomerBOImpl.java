package lk.ijse.dep.pos.business.custom.impl;

import lk.ijse.dep.pos.business.custom.CustomerBO;
import lk.ijse.dep.pos.business.exception.AlreadyExistsInOrderException;
import lk.ijse.dep.pos.dao.DAOFactory;
import lk.ijse.dep.pos.dao.DAOTypes;
import lk.ijse.dep.pos.dao.custom.CustomerDAO;
import lk.ijse.dep.pos.dao.custom.OrderDAO;
import lk.ijse.dep.pos.db.HibernateUtil;
import lk.ijse.dep.pos.dto.CustomerDTO;
import lk.ijse.dep.pos.entity.Customer;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

public class CustomerBOImpl implements CustomerBO {

    private CustomerDAO customerDAO = DAOFactory.getInstance().<CustomerDAO>getDAO(DAOTypes.CUSTOMER); // This is no need..
    private OrderDAO orderDAO = DAOFactory.getInstance().getDAO(DAOTypes.ORDER);

    @Override
    public void saveCustomer(CustomerDTO customer) throws Exception {
       try( Session session = HibernateUtil.getSessionFactory().openSession();) {
           customerDAO.setSession(session);
           session.beginTransaction();
          customerDAO.save(new Customer(customer.getId(), customer.getName(), customer.getAddress()));
          session.getTransaction().commit();
       }
    }

    @Override
    public void updateCustomer(CustomerDTO customer) throws Exception {
        try( Session session = HibernateUtil.getSessionFactory().openSession()) {
            customerDAO.setSession(session);
            session.beginTransaction();
            customerDAO.update(new Customer(customer.getId(), customer.getName(), customer.getAddress()));
            session.getTransaction().commit();
        }
    }

    @Override
    public void deleteCustomer(String customerId) throws Exception {
        try( Session session = HibernateUtil.getSessionFactory().openSession()) {
            customerDAO.setSession(session);
            orderDAO.setSession(session);
            session.beginTransaction();
            if (orderDAO.existsByCustomerId(customerId)) {
                throw new AlreadyExistsInOrderException("Customer already exists in an order, hence unable to delete");
            }
            customerDAO.delete(customerId);
            session.getTransaction().commit();
        }
    }

    @Override
    public List<CustomerDTO> findAllCustomers() throws Exception {
        try( Session session = HibernateUtil.getSessionFactory().openSession()) {
            customerDAO.setSession(session);
            session.beginTransaction();
            List<Customer> alCustomers = customerDAO.findAll();
            List<CustomerDTO> dtos = new ArrayList<>();
            for (Customer customer : alCustomers) {
                dtos.add(new CustomerDTO(customer.getCustomerId(), customer.getName(), customer.getAddress()));
            }
            session.getTransaction().commit();
            return dtos;
        }
    }

    @Override
    public String getLastCustomerId() throws Exception {
        try( Session session = HibernateUtil.getSessionFactory().openSession();) {
            customerDAO.setSession(session);
            session.beginTransaction();
            String lastCustomerId = customerDAO.getLastCustomerId();
            session.getTransaction().commit();
            return lastCustomerId;
        }
    }

    @Override
    public CustomerDTO findCustomer(String customerId) throws Exception {
        try( Session session = HibernateUtil.getSessionFactory().openSession();) {
            customerDAO.setSession(session);
            session.beginTransaction();
            Customer customer = customerDAO.find(customerId);
            CustomerDTO customerDTO = new CustomerDTO(customer.getCustomerId(),
                    customer.getName(), customer.getAddress());
            session.getTransaction().commit();
            return customerDTO;
        }
    }

    @Override
    public List<String> getAllCustomerIDs() throws Exception {
        try( Session session = HibernateUtil.getSessionFactory().openSession();) {
            customerDAO.setSession(session);
            session.beginTransaction();
            List<Customer> customers = customerDAO.findAll();
            List<String> ids = new ArrayList<>();
            for (Customer customer : customers) {
                ids.add(customer.getCustomerId());
            }
            session.getTransaction().commit();
            return ids;
        }
    }
}

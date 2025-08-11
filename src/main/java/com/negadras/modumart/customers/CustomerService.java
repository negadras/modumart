package com.negadras.modumart.customers;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {
    
    private final CustomerRepository customerRepository;
    private final ApplicationEventPublisher eventPublisher;
    
    public CustomerService(CustomerRepository customerRepository, ApplicationEventPublisher eventPublisher) {
        this.customerRepository = customerRepository;
        this.eventPublisher = eventPublisher;
    }
    
    public List<Customer> getAllCustomers() {
        return (List<Customer>) customerRepository.findAll();
    }
    
    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }
    
    public Optional<Customer> getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }
    
    public Customer createCustomer(Customer customer) {
        Customer newCustomer = new Customer(
                null,
                customer.firstName(),
                customer.lastName(),
                customer.email(),
                customer.phone(),
                customer.address(),
                LocalDateTime.now()
        );
        Customer savedCustomer = customerRepository.save(newCustomer);
        eventPublisher.publishEvent(new CustomerRegisteredEvent(savedCustomer.id(), savedCustomer.email()));
        return savedCustomer;
    }
    
    public Optional<Customer> updateCustomer(Long id, Customer updatedCustomer) {
        return customerRepository.findById(id)
                .map(existing -> {
                    Customer updated = new Customer(
                            id,
                            updatedCustomer.firstName(),
                            updatedCustomer.lastName(),
                            updatedCustomer.email(),
                            updatedCustomer.phone(),
                            updatedCustomer.address(),
                            existing.createdAt()
                    );
                    return customerRepository.save(updated);
                });
    }
    
    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }
    
    public List<Customer> searchCustomersByName(String name) {
        return customerRepository.findByNameContaining(name);
    }
    
    public List<Customer> getRecentCustomers(Integer limit) {
        return customerRepository.findRecentCustomers(limit != null ? limit : 10);
    }
}
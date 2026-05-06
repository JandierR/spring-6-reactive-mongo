package guru.springframework.spring6reactivemongo.services;

import guru.springframework.spring6reactivemongo.model.CustomerDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerService {
    Mono<CustomerDTO> findFirstByCustomerName(String customerName);

    Mono<CustomerDTO> saveCustomer(Mono<CustomerDTO> customerDTO);

    Mono<CustomerDTO> savecustomer(CustomerDTO customerDTO);

    Mono<CustomerDTO> getById(String customerId);

    Flux<CustomerDTO> listCustomers();

    Mono<CustomerDTO> updateCustomer(String customerId, CustomerDTO customerDTO);

    Mono<CustomerDTO> patchCustomer(String customerId, CustomerDTO customerDTO);

    Mono<Void> deleteCustomerById(String customerId);
}

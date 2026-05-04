package guru.springframework.spring6reactivemongo.mappers;

import guru.springframework.spring6reactivemongo.domain.Customer;
import guru.springframework.spring6reactivemongo.model.CustomerDTO;
import org.mapstruct.Mapper;

@Mapper
public interface CustomerMapper {
    Customer customerDtoToCustomer(CustomerDTO customerDTO);

    CustomerDTO customerToCustomerDto(Customer customer);
}

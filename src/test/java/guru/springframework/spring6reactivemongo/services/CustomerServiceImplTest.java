package guru.springframework.spring6reactivemongo.services;

import guru.springframework.spring6reactivemongo.domain.Customer;
import guru.springframework.spring6reactivemongo.mappers.CustomerMapper;
import guru.springframework.spring6reactivemongo.mappers.CustomerMapperImpl;
import guru.springframework.spring6reactivemongo.model.CustomerDTO;
import guru.springframework.spring6reactivemongo.repositories.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@Testcontainers
@SpringBootTest
public class CustomerServiceImplTest {
    @Container
    @ServiceConnection
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    @Autowired
    CustomerService customerService;

    @Autowired
    CustomerMapper customerMapper;

    @Autowired
    CustomerRepository customerRepository;

    CustomerDTO customerDTO;



    @BeforeEach
    void setUp() {
        customerDTO = customerMapper.customerToCustomerDto(getTestCustomer());
    }

    @Test
    void findFirstByCustomerNameTest() {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        CustomerDTO CustomerDTO = getSavedCustomerDto();

        Mono<CustomerDTO> foundDto = customerService.findFirstByCustomerName(CustomerDTO.getCustomerName());

        foundDto.subscribe(dto ->{
            System.out.println(dto.toString());
            atomicBoolean.set(true);

        });

        await().untilTrue(atomicBoolean);
    }

    @Test
    @DisplayName("Test Save Customer Using Subscriber")
    void saveCustomerUseSubscriber() {

        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        AtomicReference<CustomerDTO> atomicDto = new AtomicReference<>();

        Mono<CustomerDTO> savedMono = customerService.saveCustomer(Mono.just(customerDTO));

        savedMono.subscribe(savedDto ->{
            System.out.println(savedDto.getId());
            atomicBoolean.set(true);
            atomicDto.set(savedDto);
        });

        await().untilTrue(atomicBoolean);

        CustomerDTO persistedDto = atomicDto.get();
        assertThat(persistedDto).isNotNull();
        assertThat(persistedDto.getId()).isNotNull();
    }

    @Test
    @DisplayName("Test Save Customer Using Block")
    void testSaveCustomerUseBlock() {
        CustomerDTO savedDto = customerService.saveCustomer(Mono.just(getTestCustomerDto())).block();
        assertThat(savedDto).isNotNull();
        assertThat(savedDto.getId()).isNotNull();
    }

    @Test
    @DisplayName("Test Update Customer Using Block")
    void testUpdateBlocking() {
        final String newName = "New Customer Name";
        CustomerDTO savedCustomerDto = getSavedCustomerDto();

        savedCustomerDto.setCustomerName(newName);

        CustomerDTO updatedDto = customerService.saveCustomer(Mono.just(savedCustomerDto)).block();

        CustomerDTO fetchedDto = customerService.getById(updatedDto.getId()).block();
        assertThat(fetchedDto.getCustomerName()).isEqualTo(newName);

    }

    @Test
    void testUpdateStreaming() {
        final String newName = "New Customer Name";
        AtomicReference<CustomerDTO> atomicDto = new AtomicReference<>();

        customerService.saveCustomer(Mono.just(getSavedCustomerDto()))
                .map(savedCustomerDto -> {
                    savedCustomerDto.setCustomerName(newName);
                    return savedCustomerDto;
                })
                .flatMap(customerService::savecustomer)
                .flatMap(savedUpdateDto -> customerService.getById(savedUpdateDto.getId()))
                .subscribe(dtoFromDb -> atomicDto.set(dtoFromDb));

        await().until(() -> atomicDto.get() != null);
        assertThat(atomicDto.get().getCustomerName()).isEqualTo(newName);
    }

    @Test
    void testDeleteCustomer() {

        CustomerDTO CustomerToDelete = getSavedCustomerDto();

        customerService.deleteCustomerById(CustomerToDelete.getId()).block();

        Mono<CustomerDTO> expectedEmptyCustomerMono = customerService.getById(CustomerToDelete.getId());

        CustomerDTO emptyCustomer = expectedEmptyCustomerMono.block();

        assertThat(emptyCustomer).isNull();
    }

    @Test
    void saveCustomer() {

        AtomicBoolean atomicBoolean = new AtomicBoolean(false);

        Mono<CustomerDTO> savedMono = customerService.saveCustomer(Mono.just(customerDTO));

        savedMono.subscribe(savedDto ->{
            System.out.println(savedDto.getId());
            atomicBoolean.set(true);
        });

        await().untilTrue(atomicBoolean);

    }

    public CustomerDTO getSavedCustomerDto() {
        return customerService.saveCustomer(Mono.just(getTestCustomerDto())).block();
    }

    public static CustomerDTO getTestCustomerDto() {
        return new CustomerMapperImpl().customerToCustomerDto(getTestCustomer());
    }

    public static Customer getTestCustomer() {
        return Customer.builder()
                .customerName("Pedrito Alamos")
                .build();
    }
}
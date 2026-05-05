package guru.springframework.spring6reactivemongo.services;

import guru.springframework.spring6reactivemongo.model.BeerDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BeerService {
    Mono<BeerDTO> findFirstByBeerName(String beerName);

    Mono<BeerDTO> saveBeer(Mono<BeerDTO> beerDTO);

    Mono<BeerDTO> saveBeer(BeerDTO beerDTO);

    Mono<BeerDTO> getById(String beerId);

    Flux<BeerDTO> listBeers();

    Mono<BeerDTO> updateBeer(String beerId, BeerDTO beerDTO);

    Mono<BeerDTO> patchBeer(String beerId, BeerDTO beerDTO);

    Mono<Void> deleteBeerById(String beerId);

}

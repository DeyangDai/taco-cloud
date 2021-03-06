package tacos.restclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.client.Traverson;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tacos.Ingredient;
import tacos.Taco;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class TacoCloudClient {

    private final RestTemplate restTemplate;
    private final Traverson traverson;

    @Autowired
    public TacoCloudClient(RestTemplate restTemplate, Traverson traverson) {
        this.restTemplate = restTemplate;
        this.traverson = traverson;
    }

    public Ingredient getIngredientById(String ingredientId) {
        return restTemplate.getForObject("http://localhost:8080/ingredients/{id}", Ingredient.class, ingredientId);
    }

    /*
     * Alternate implementations...
     * The next three methods are alternative implementations of
     * getIngredientById() as shown in chapter 6. If you'd like to try
     * any of them out, comment out the previous method and uncomment
     * the variant you want to use.
     */

    /*
     * Specify parameters with a map
     */
    // public Ingredient getIngredientById(String ingredientId) {
    //   Map<String, String> urlVariables = new HashMap<>();
    //   urlVariables.put("id", ingredientId);
    //   return rest.getForObject("http://localhost:8080/ingredients/{id}",
    //       Ingredient.class, urlVariables);
    // }

    /*
     * Request with URI instead of String
     */
    // public Ingredient getIngredientById(String ingredientId) {
    //   Map<String, String> urlVariables = new HashMap<>();
    //   urlVariables.put("id", ingredientId);
    //   URI url = UriComponentsBuilder
    //             .fromHttpUrl("http://localhost:8080/ingredients/{id}")
    //             .build(urlVariables);
    //   return rest.getForObject(url, Ingredient.class);
    // }

    /*
     * Use getForEntity() instead of getForObject()
     */
    // public Ingredient getIngredientById(String ingredientId) {
    //   ResponseEntity<Ingredient> responseEntity =
    //       rest.getForEntity("http://localhost:8080/ingredients/{id}",
    //           Ingredient.class, ingredientId);
    //   log.info("Fetched time: " +
    //           responseEntity.getHeaders().getDate());
    //   return responseEntity.getBody();
    // }

    public List<Ingredient> getAllIngredients() {
        return restTemplate.exchange("http://localhost:8080/ingredients", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Ingredient>>() {}).getBody();
    }

    public void updateIngredient(Ingredient ingredient) {
        restTemplate.put("http://localhost:8080/ingredients/{id}", ingredient, ingredient.getId());
    }

    public Ingredient createIngredient(Ingredient ingredient) {
        return restTemplate.postForObject("http://localhost:8080/ingredients",
                ingredient, Ingredient.class);
    }

    /*
     * Alternate implementations...
     * The next two methods are alternative implementations of
     * createIngredient() as shown in chapter 6. If you'd like to try
     * any of them out, comment out the previous method and uncomment
     * the variant you want to use.
     */

    // public URI createIngredient(Ingredient ingredient) {
    //   return rest.postForLocation("http://localhost:8080/ingredients",
    //       ingredient, Ingredient.class);
    // }

    // public Ingredient createIngredient(Ingredient ingredient) {
    //   ResponseEntity<Ingredient> responseEntity =
    //          rest.postForEntity("http://localhost:8080/ingredients",
    //                             ingredient,
    //                             Ingredient.class);
    //   log.info("New resource created at " +
    //            responseEntity.getHeaders().getLocation());
    //   return responseEntity.getBody();
    // }

    public void deleteIngredient(Ingredient ingredient) {
        restTemplate.delete("http://localhost:8080/ingredients/{id}", ingredient.getId());
    }

    public Iterable<Ingredient> getAllIngredientsWithTraverson() {
        ParameterizedTypeReference<CollectionModel<Ingredient>> ingredientType =
                new ParameterizedTypeReference<CollectionModel<Ingredient>>() {};

        CollectionModel<Ingredient> ingredientModels = traverson.follow("ingredients").toObject(ingredientType);

        return Objects.requireNonNull(ingredientModels).getContent();
    }

    public Ingredient addIngredient(Ingredient ingredient) {
        String ingredientsUrl = traverson.follow("ingredients").asLink().getHref();
        log.info("POST URL: " + ingredientsUrl);
        return restTemplate.postForObject(ingredientsUrl, ingredient, Ingredient.class);
    }

    public Iterable<Taco> getRecentTacosWithTraversion() {
        ParameterizedTypeReference<CollectionModel<Taco>> tacoType =
                new ParameterizedTypeReference<CollectionModel<Taco>>() {};

        CollectionModel<Taco> tacoModels = traverson.follow("tacos").follow("recents").toObject(tacoType);
        return Objects.requireNonNull(tacoModels).getContent();
    }

}

package tacos.web.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tacos.Taco;
import tacos.data.TacoRepository;

import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(path = "/design", produces = "application/json")
@CrossOrigin(origins = "*")
public class DesignTacoController {

    private final TacoRepository tacoRepository;

    private final EntityLinks entityLinks;

    @Autowired
    public DesignTacoController(TacoRepository tacoRepository, EntityLinks entityLinks) {
        this.tacoRepository = tacoRepository;
        this.entityLinks = entityLinks;
    }

//    @GetMapping("/recent")
//    public Iterable<Taco> recentTacos() {
//        PageRequest pageRequest = PageRequest.of(0, 12, Sort.by("createdAt").descending());
//        return tacoRepository.findAll(pageRequest).getContent();
//    }

    @GetMapping("/recent")
    public CollectionModel<TacoModel> recentTacos() {
        PageRequest pageRequest = PageRequest.of(0, 12, Sort.by("createdAt").descending());
        List<Taco> tacos = tacoRepository.findAll(pageRequest).getContent();

        CollectionModel<TacoModel> tacoModels = new TacoModelAssembler().toCollectionModel(tacos);
        tacoModels.add(linkTo(methodOn(DesignTacoController.class).recentTacos()).withRel("recents"));

        return tacoModels;
    }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Taco postTaco(@RequestBody Taco taco) {
        return tacoRepository.save(taco);
    }

//    @GetMapping("/{id}")
//    public Taco tacoById(@PathVariable("id") Long id) {
//        Optional<Taco> taco = tacoRepository.findById(id);
//        return taco.orElse(null);
//    }

    @GetMapping("/{id}")
    public ResponseEntity<Taco> tacoById(@PathVariable("id") Long id) {
        Optional<Taco> taco = tacoRepository.findById(id);
        if (taco.isPresent()) {
            return new ResponseEntity<>(taco.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }
}

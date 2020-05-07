package tacos.web.api;

import org.springframework.hateoas.CollectionModel;

import java.util.List;

public class TacoModels extends CollectionModel<TacoModel> {

    public TacoModels(List<TacoModel> tacoModels) {
        super(tacoModels);
    }

}

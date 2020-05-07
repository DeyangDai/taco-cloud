package tacos.web.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import tacos.Taco;

public class TacoModelsProcessor implements RepresentationModelProcessor<PagedModel<TacoModel>> {

    private final EntityLinks entityLinks;

    @Autowired
    public TacoModelsProcessor(EntityLinks entityLinks) {
        this.entityLinks = entityLinks;
    }

    @Override
    public PagedModel<TacoModel> process(PagedModel<TacoModel> models) {
        models.add(entityLinks.linkFor(Taco.class).slash("recent").withRel("recents"));
        return models;
    }
}

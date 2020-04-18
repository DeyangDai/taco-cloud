package tacos;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Size;
import java.util.List;

@Data
public class Taco {

    private String name;
    private List<String> ingredients;

}

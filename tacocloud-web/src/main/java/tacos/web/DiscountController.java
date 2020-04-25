package tacos.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/discounts")
public class DiscountController {

    private final DiscountCodeProperties discountCodeProperties;

    @Autowired
    public DiscountController(DiscountCodeProperties discountCodeProperties) {
        this.discountCodeProperties = discountCodeProperties;
    }

    @GetMapping
    public String displayDiscountCodes(Model model) {
        Map<String, Integer> codes = discountCodeProperties.getCodes();
        model.addAttribute("codes", codes);

        return "discountList";
    }

}

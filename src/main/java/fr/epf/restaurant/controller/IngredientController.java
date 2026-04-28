package fr.epf.restaurant.controller;

import fr.epf.restaurant.dto.AlerteStockDto;
import fr.epf.restaurant.dto.IngredientPrixDto;
import fr.epf.restaurant.dto.RecommandationDto;
import fr.epf.restaurant.model.Ingredient;
import fr.epf.restaurant.service.StockService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ingredients")
public class IngredientController {

    private final StockService stockService;

    public IngredientController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping
    public List<Ingredient> lister() {
        return stockService.findAllIngredients();
    }

    @GetMapping("/alertes")
    public List<AlerteStockDto> alertes() {
        return stockService.findIngredientsSousAlerte();
    }

    @GetMapping("/{id}/recommandation")
    public RecommandationDto recommandation(@PathVariable Long id) {
        return stockService.getRecommandation(id);
    }

    @GetMapping("/{id}/prix")
    public List<IngredientPrixDto> prix(@PathVariable Long id) {
        return stockService.findPrixByIngredientId(id);
    }
}
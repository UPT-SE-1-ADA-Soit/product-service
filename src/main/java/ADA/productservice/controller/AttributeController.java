package ADA.productservice.controller;

import ADA.productservice.dto.AttributeRequest;
import ADA.productservice.dto.AttributeValueRequest;
import ADA.productservice.entity.Attribute;
import ADA.productservice.entity.AttributeValue;
import ADA.productservice.service.AttributeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AttributeController {

    private final AttributeService service;

    @GetMapping("/attribute")
    public List<Attribute> getAll() {
        return service.findAllAttributes();
    }

    @PostMapping("/attribute")
    @ResponseStatus(HttpStatus.CREATED)
    public Attribute create(@Valid @RequestBody AttributeRequest request) {
        return service.createAttribute(request);
    }

    @DeleteMapping("/attribute/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        service.deleteAttribute(id);
    }

    @GetMapping("/attribute-value")
    public List<AttributeValue> getAllValues() {
        return service.findAllAttributeValues();
    }

    @PostMapping("/attribute-value")
    @ResponseStatus(HttpStatus.CREATED)
    public AttributeValue createValue(@Valid @RequestBody AttributeValueRequest request) {
        return service.createAttributeValue(request);
    }

    @DeleteMapping("/attribute-value/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteValue(@PathVariable Integer id) {
        service.deleteAttributeValue(id);
    }
}

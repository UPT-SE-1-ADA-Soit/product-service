package ADA.productservice.service;

import ADA.productservice.dto.AttributeRequest;
import ADA.productservice.dto.AttributeValueRequest;
import ADA.productservice.entity.Attribute;
import ADA.productservice.entity.AttributeValue;
import ADA.productservice.exception.ResourceNotFoundException;
import ADA.productservice.repository.AttributeRepository;
import ADA.productservice.repository.AttributeValueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttributeService {

    private final AttributeRepository attributeRepository;
    private final AttributeValueRepository attributeValueRepository;

    public List<Attribute> findAllAttributes() {
        return attributeRepository.findAll();
    }

    public Attribute createAttribute(AttributeRequest request) {
        return attributeRepository.save(Attribute.builder().name(request.getName()).build());
    }

    public void deleteAttribute(Integer id) {
        if (!attributeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Attribute", id);
        }
        attributeRepository.deleteById(id);
    }

    public List<AttributeValue> findAllAttributeValues() {
        return attributeValueRepository.findAll();
    }

    public AttributeValue createAttributeValue(AttributeValueRequest request) {
        return attributeValueRepository.save(AttributeValue.builder().name(request.getName()).build());
    }

    public void deleteAttributeValue(Integer id) {
        if (!attributeValueRepository.existsById(id)) {
            throw new ResourceNotFoundException("AttributeValue", id);
        }
        attributeValueRepository.deleteById(id);
    }
}

package ADA.productservice.service;

import ADA.productservice.entity.History;
import ADA.productservice.entity.Product;
import ADA.productservice.repository.AttributeValuePairRepository;
import ADA.productservice.repository.HistoryRepository;
import ADA.productservice.repository.ProductRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";
    private static final long CACHE_TTL_MS = 5 * 60 * 1000L;

    @Value("${gemini.api-key}")
    private String apiKey;

    private final HistoryRepository historyRepository;
    private final ProductRepository productRepository;
    private final AttributeValuePairRepository pairRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestClient restClient = RestClient.create();
    private final Map<Integer, CacheEntry> cache = new ConcurrentHashMap<>();

    private record CacheEntry(List<Integer> productIds, long expiresAt) {}

    public List<Integer> getRecommendedIds(Integer userId) {
        CacheEntry cached = cache.get(userId);
        if (cached != null && System.currentTimeMillis() < cached.expiresAt()) {
            return cached.productIds();
        }

        try {
            List<Integer> ids = fetchRecommendations(userId);
            cache.put(userId, new CacheEntry(ids, System.currentTimeMillis() + CACHE_TTL_MS));
            return ids;
        } catch (Exception e) {
            log.warn("Recommendation failed for user {}: {}", userId, e.getMessage());
            return Collections.emptyList();
        }
    }

    private List<Integer> fetchRecommendations(Integer userId) throws Exception {
        String payloadJson = buildPayloadJson(userId);

        String systemPrompt = "You are a product recommendation engine for a local online marketplace. " +
                "Given a user's recently viewed products and all available products, " +
                "return the most relevant product IDs ordered by recommendation priority. " +
                "Respond ONLY with valid JSON in this exact format: {\"recommendedProductIds\": [1, 2, 3]}";

        Map<String, Object> requestBody = Map.of(
                "system_instruction", Map.of("parts", List.of(Map.of("text", systemPrompt))),
                "contents", List.of(Map.of("parts", List.of(Map.of("text", payloadJson)))),
                "generationConfig", Map.of("responseMimeType", "application/json")
        );

        String responseBody = restClient.post()
                .uri(GEMINI_URL + "?key=" + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(String.class);

        String text = objectMapper.readTree(responseBody)
                .path("candidates").get(0)
                .path("content").path("parts").get(0)
                .path("text").asText();

        JsonNode idsNode = objectMapper.readTree(text).get("recommendedProductIds");
        if (idsNode == null || !idsNode.isArray()) {
            throw new RuntimeException("Unexpected response format from Gemini");
        }

        List<Integer> ids = new ArrayList<>();
        for (JsonNode idNode : idsNode) {
            ids.add(idNode.asInt());
        }
        return ids;
    }

    @Transactional(readOnly = true)
    public String buildPayloadJson(Integer userId) throws Exception {
        List<History> recentHistory = historyRepository.findTop15ByUser_IdOrderByIdDesc(userId);
        List<Product> allProducts = productRepository.findAll();

        ObjectNode payload = objectMapper.createObjectNode();

        ArrayNode recentlyViewed = payload.putArray("recentlyViewedProducts");
        for (History h : recentHistory) {
            recentlyViewed.add(buildProductNode(h.getProduct(), false));
        }

        ArrayNode available = payload.putArray("allAvailableProducts");
        for (Product p : allProducts) {
            available.add(buildProductNode(p, true));
        }

        return objectMapper.writeValueAsString(payload);
    }

    private ObjectNode buildProductNode(Product product, boolean includeDescription) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("id", product.getId());
        node.put("name", product.getName());
        node.put("category", product.getCategory().getName());
        node.put("price", product.getPrice().doubleValue());
        if (product.getRegion() != null) node.put("region", product.getRegion());
        if (includeDescription && product.getDescription() != null) {
            node.put("description", product.getDescription());
        }

        ObjectNode attrs = node.putObject("attributes");
        pairRepository.findAllByProduct_Id(product.getId()).forEach(pair ->
                attrs.put(pair.getAttribute().getName(), pair.getAttributeValue().getName()));

        return node;
    }
}

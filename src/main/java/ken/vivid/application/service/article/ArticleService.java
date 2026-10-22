package ken.vivid.application.service.article;

import ken.vivid.adapter.exception.DuplicateResourceException;
import ken.vivid.adapter.exception.InvalidRequestException;
import ken.vivid.adapter.exception.InvalidStateTransitionException;
import ken.vivid.adapter.exception.ResourceNotFoundException;
import ken.vivid.application.port.input.article.DeleteArticleUseCase;
import ken.vivid.application.port.input.article.AddServiceUseCase;
import ken.vivid.application.port.input.article.DeleteServiceUseCase;
import ken.vivid.application.port.input.article.GetArticleUseCase;
import ken.vivid.application.port.input.article.GetServiceUseCase;
import ken.vivid.application.port.input.article.changeArticleStatus.ChangeArticleStatusCommand;
import ken.vivid.application.port.input.article.changeArticleStatus.ChangeArticleStatusUseCase;
import ken.vivid.application.port.input.article.createArticle.CreateArticleCommand;
import ken.vivid.application.port.input.article.createArticle.CreateArticleUseCase;
import ken.vivid.application.port.input.article.updateArticle.UpdateArticleCommand;
import ken.vivid.application.port.input.article.updateArticle.UpdateArticleUseCase;
import ken.vivid.application.port.output.article.DeleteArticle;
import ken.vivid.application.port.output.article.LoadArticle;
import ken.vivid.application.port.output.article.SaveArticle;
import ken.vivid.application.port.output.order.LoadOrder;
import ken.vivid.application.port.output.serviceprice.LoadServicePrice;
import ken.vivid.domain.dto.ArticleStatus;
import ken.vivid.domain.dto.ClothingType;
import ken.vivid.domain.dto.MovementType;
import ken.vivid.domain.dto.ServiceType;
import ken.vivid.domain.entities.article.AppliedService;
import ken.vivid.domain.entities.article.Article;
import ken.vivid.domain.entities.order.Order;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Application service for the Article module.
 * <p>
 * Business rules:
 * <ul>
 *     <li>an article is created PENDING, attached to an existing order that is
 *     neither DELIVERED nor CANCELLED;</li>
 *     <li>it needs at least one service, without duplicates;</li>
 *     <li>each service is priced from the ACTIVE price list for the article's
 *     clothing type, and that price is frozen on the article;</li>
 *     <li>its status only moves forward and COMPLETED is final;</li>
 *     <li>services can only be added or removed while the article is PENDING,
 *     and the last service cannot be removed;</li>
 *     <li>domain validation errors on user input (field lengths, ...) are
 *     reported as {@link InvalidRequestException} (HTTP 400).</li>
 * </ul>
 */
public class ArticleService implements CreateArticleUseCase, GetArticleUseCase, UpdateArticleUseCase,
        ChangeArticleStatusUseCase, GetServiceUseCase, AddServiceUseCase, DeleteServiceUseCase,
        DeleteArticleUseCase {

    private final LoadArticle loadArticle;
    private final SaveArticle saveArticle;
    private final DeleteArticle deleteArticle;
    private final LoadOrder loadOrder;
    private final LoadServicePrice loadServicePrice;

    public ArticleService(LoadArticle loadArticle, SaveArticle saveArticle, DeleteArticle deleteArticle,
                          LoadOrder loadOrder, LoadServicePrice loadServicePrice) {
        this.loadArticle = loadArticle;
        this.saveArticle = saveArticle;
        this.deleteArticle = deleteArticle;
        this.loadOrder = loadOrder;
        this.loadServicePrice = loadServicePrice;
    }

    @Override
    public Article create(CreateArticleCommand command) {
        if (command.orderId() == null) {
            throw new InvalidRequestException("Order ID is required");
        }
        if (command.clothingType() == null) {
            throw new InvalidRequestException("Clothing type is required");
        }
        validateRequestedServices(command.services());

        Order order = loadOrder.loadOrderById(command.orderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found : " + command.orderId()));
        if (order.getStatus() == MovementType.OrderStatus.DELIVERED || order.getStatus() == MovementType.OrderStatus.CANCELLED) {
            throw new InvalidRequestException(
                    "Cannot add an article to an order in status " + order.getStatus());
        }

        List<AppliedService> applied = new ArrayList<>();
        for (ServiceType service : command.services()) {
            applied.add(price(command.clothingType(), service));
        }

        Article article;
        try {
            article = Article.create(null, command.orderId(), command.clothingType(), command.size(),
                    command.fabric(), command.color(), command.distinction(), ArticleStatus.PENDING, applied);
        } catch (IllegalArgumentException e) {
            throw new InvalidRequestException(e.getMessage());
        }
        return saveArticle.save(article);
    }

    @Override
    public Article getById(Long id) {
        return loadArticle.loadById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found : " + id));
    }

    @Override
    public List<Article> getAll() {
        return loadArticle.loadAll();
    }

    @Override
    public List<Article> getByOrderId(Long orderId) {
        return loadArticle.loadByOrderId(orderId);
    }

    @Override
    public AppliedService getService(Long articleId, ServiceType service) {
        if (service == null) {
            throw new InvalidRequestException("Service is required");
        }
        return getById(articleId).getServices().stream()
                .filter(applied -> applied.service() == service)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Service " + service + " is not applied to article " + articleId));
    }

    @Override
    public List<AppliedService> getAllServices(Long articleId) {
        return getById(articleId).getServices();
    }

    @Override
    public Article update(UpdateArticleCommand command) {
        Article article = getById(command.articleId());
        if (article.getStatus().isTerminal()) {
            throw new InvalidStateTransitionException("A completed article cannot be modified : " + article.getId());
        }
        try {
            article.updateDetails(command.size(), command.fabric(), command.color(), command.distinction());
        } catch (IllegalArgumentException e) {
            throw new InvalidRequestException(e.getMessage());
        }
        return saveArticle.save(article);
    }

    @Override
    public Article changeStatus(ChangeArticleStatusCommand command) {
        Article article = getById(command.articleId());
        if (!article.getStatus().canTransitionTo(command.status())) {
            throw new InvalidStateTransitionException(
                    "Invalid article status transition: " + article.getStatus() + " -> " + command.status());
        }
        article.changeStatus(command.status());
        return saveArticle.save(article);
    }

    @Override
    public Article addService(Long articleId, ServiceType service) {
        if (service == null) {
            throw new InvalidRequestException("Service is required");
        }
        Article article = getById(articleId);
        ensurePending(article);
        if (article.hasService(service)) {
            throw new DuplicateResourceException(
                    "Service " + service + " is already applied to article " + articleId);
        }
        article.addService(price(article.getClothingType(), service));
        return saveArticle.save(article);
    }

    @Override
    public Article deleteService(Long articleId, ServiceType service) {
        if (service == null) {
            throw new InvalidRequestException("Service is required");
        }
        Article article = getById(articleId);
        ensurePending(article);
        if (!article.hasService(service)) {
            throw new ResourceNotFoundException(
                    "Service " + service + " is not applied to article " + articleId);
        }
        if (article.getServices().size() == 1) {
            throw new InvalidRequestException("The last service of an article cannot be removed");
        }
        article.removeService(service);
        return saveArticle.save(article);
    }

    @Override
    public void delete(Long id) {
        loadArticle.loadById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found : " + id));
        deleteArticle.delete(id);
    }

    private void validateRequestedServices(List<ServiceType> services) {
        if (services == null || services.isEmpty()) {
            throw new InvalidRequestException("At least one service is required");
        }
        Set<ServiceType> seen = new HashSet<>();
        for (ServiceType service : services) {
            if (service == null) {
                throw new InvalidRequestException("Services cannot contain null elements");
            }
            if (!seen.add(service)) {
                throw new InvalidRequestException("Duplicate service: " + service);
            }
        }
    }

    private void ensurePending(Article article) {
        if (article.getStatus() != ArticleStatus.PENDING) {
            throw new InvalidStateTransitionException(
                    "Services can only be changed while the article is PENDING, current status: " + article.getStatus());
        }
    }

    private AppliedService price(ClothingType clothingType, ServiceType service) {
        return new AppliedService(service, loadServicePrice.loadActivePrice(clothingType, service)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No active price for " + clothingType + " / " + service)));
    }
}
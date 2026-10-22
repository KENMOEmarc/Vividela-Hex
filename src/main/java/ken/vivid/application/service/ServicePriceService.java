package ken.vivid.application.service;

import ken.vivid.adapter.exception.DuplicateResourceException;
import ken.vivid.adapter.exception.InvalidRequestException;
import ken.vivid.adapter.exception.ResourceNotFoundException;
import ken.vivid.application.port.input.serviceprice.DeleteServicePriceUseCase;
import ken.vivid.application.port.input.serviceprice.GetServicePriceUseCase;
import ken.vivid.application.port.input.serviceprice.ManageServicePriceStatusUseCase;
import ken.vivid.application.port.input.serviceprice.createServicePrice.CreateServicePriceCommand;
import ken.vivid.application.port.input.serviceprice.createServicePrice.CreateServicePriceUseCase;
import ken.vivid.application.port.input.serviceprice.updateServicePrice.UpdateServicePriceCommand;
import ken.vivid.application.port.input.serviceprice.updateServicePrice.UpdateServicePriceUseCase;
import ken.vivid.application.port.output.serviceprice.DeleteServicePrice;
import ken.vivid.application.port.output.serviceprice.LoadServicePriceCatalog;
import ken.vivid.application.port.output.serviceprice.SaveServicePrice;
import ken.vivid.domain.dto.ClothingType;
import ken.vivid.domain.entities.service.ServicePrice;

import java.util.List;

/**
 * Application service for the ServicePrice module.
 * <p>
 * Business rules:
 * <ul>
 *     <li>there is at most one entry per (clothing type, service) pair, whether
 *     it is active or not;</li>
 *     <li>a new entry is created ACTIVE;</li>
 *     <li>only the price of an entry can be changed (clothing type and service
 *     identify it); price changes never alter the prices already frozen on
 *     existing articles;</li>
 *     <li>activating / deactivating is idempotent: nothing is saved when the
 *     entry is already in the requested state;</li>
 *     <li>domain validation errors on user input (price) are reported as
 *     {@link InvalidRequestException} (HTTP 400).</li>
 * </ul>
 */
public class ServicePriceService implements CreateServicePriceUseCase, GetServicePriceUseCase,
        UpdateServicePriceUseCase, ManageServicePriceStatusUseCase, DeleteServicePriceUseCase {

    private final LoadServicePriceCatalog loadCatalog;
    private final SaveServicePrice saveServicePrice;
    private final DeleteServicePrice deleteServicePrice;

    public ServicePriceService(LoadServicePriceCatalog loadCatalog, SaveServicePrice saveServicePrice,
                               DeleteServicePrice deleteServicePrice) {
        this.loadCatalog = loadCatalog;
        this.saveServicePrice = saveServicePrice;
        this.deleteServicePrice = deleteServicePrice;
    }

    @Override
    public ServicePrice create(CreateServicePriceCommand command) {
        if (command.clothingType() == null) {
            throw new InvalidRequestException("Clothing type is required");
        }
        if (command.service() == null) {
            throw new InvalidRequestException("Service is required");
        }
        if (loadCatalog.existsByClothingTypeAndService(command.clothingType(), command.service())) {
            throw new DuplicateResourceException("A price already exists for "
                    + command.clothingType() + " / " + command.service());
        }
        ServicePrice servicePrice;
        try {
            servicePrice = ServicePrice.create(null, command.clothingType(), command.service(),
                    command.price(), true);
        } catch (IllegalArgumentException e) {
            throw new InvalidRequestException(e.getMessage());
        }
        return saveServicePrice.save(servicePrice);
    }

    @Override
    public ServicePrice getById(Long id) {
        return loadCatalog.loadById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service price not found : " + id));
    }

    @Override
    public List<ServicePrice> getAll(ClothingType clothingType, boolean activeOnly) {
        if (clothingType == null) {
            return activeOnly ? loadCatalog.loadAllActive() : loadCatalog.loadAll();
        }
        List<ServicePrice> forType = loadCatalog.loadByClothingType(clothingType);
        return activeOnly ? forType.stream().filter(ServicePrice::isActive).toList() : forType;
    }

    @Override
    public ServicePrice update(UpdateServicePriceCommand command) {
        ServicePrice servicePrice = getById(command.servicePriceId());
        try {
            servicePrice.changePrice(command.price());
        } catch (IllegalArgumentException e) {
            throw new InvalidRequestException(e.getMessage());
        }
        return saveServicePrice.save(servicePrice);
    }

    @Override
    public ServicePrice activate(Long id) {
        ServicePrice servicePrice = getById(id);
        if (servicePrice.isActive()) {
            return servicePrice;
        }
        servicePrice.activate();
        return saveServicePrice.save(servicePrice);
    }

    @Override
    public ServicePrice deactivate(Long id) {
        ServicePrice servicePrice = getById(id);
        if (!servicePrice.isActive()) {
            return servicePrice;
        }
        servicePrice.deactivate();
        return saveServicePrice.save(servicePrice);
    }

    @Override
    public void delete(Long id) {
        getById(id);
        deleteServicePrice.delete(id);
    }
}

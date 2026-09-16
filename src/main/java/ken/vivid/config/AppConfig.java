package ken.vivid.config;

import ken.vivid.domain.port.output.*;
import ken.vivid.domain.service.AuthService;
import ken.vivid.domain.service.ProductService;
import ken.vivid.domain.service.StockAllocationPolicy;
import ken.vivid.domain.service.StockService;
import ken.vivid.domain.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public AuthService authService(LoadUser loadUser,
                                   SaveUser saveUser,
                                   PasswordEncoder passwordEncoder,
                                   TokenGenerator tokenGenerator,
                                   TokenBlacklist tokenBlacklist) {
        return new AuthService(loadUser, saveUser, passwordEncoder,
                tokenGenerator, tokenBlacklist);
    }

    @Bean
    public UserService userService(LoadUser loadUser,
                                   SaveUser saveUser,
                                   DeleteUser deleteUser,
                                   PasswordEncoder passwordEncoder) {
        return new UserService(loadUser, saveUser, deleteUser, passwordEncoder);
    }

    @Bean
    public StockAllocationPolicy stockAllocationPolicy() {
        return new StockAllocationPolicy();
    }

    @Bean
    public ProductService productService(LoadProduct loadProduct,
                                         SaveProduct saveProduct,
                                         DeleteProduct deleteProduct,
                                         LoadStock loadStock) {
        return new ProductService(loadProduct, saveProduct, deleteProduct, loadStock);
    }

    @Bean
    public StockService stockService(LoadProduct loadProduct,
                                     LoadStock loadStock,
                                     SaveStock saveStock,
                                     SaveProductRegistration saveProductRegistration,
                                     SaveStockMovement saveStockMovement,
                                     StockAllocationPolicy stockAllocationPolicy) {
        return new StockService(loadProduct, loadStock, saveStock, saveProductRegistration,
                saveStockMovement, stockAllocationPolicy);
    }
}

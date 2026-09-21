package ken.vivid.config;

import ken.vivid.application.port.output.auth.*;
import ken.vivid.application.port.output.product.*;
import ken.vivid.application.port.output.product.stock.LoadStock;
import ken.vivid.application.port.output.product.stock.SaveStock;
import ken.vivid.application.port.output.product.stock.movement.SaveStockMovement;
import ken.vivid.application.port.output.order.*;
import ken.vivid.application.service.auth.AuthService;
import ken.vivid.application.service.product.ProductService;
import ken.vivid.application.service.product.StockAllocationPolicy;
import ken.vivid.application.service.product.StockService;
import ken.vivid.application.service.auth.UserService;
import ken.vivid.application.service.order.OrderService;
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

    @Bean
    public OrderService orderService(LoadOrder loadOrder, SaveOrder saveOrder, DeleteOrder deleteOrder) {
        return new OrderService(loadOrder, saveOrder, deleteOrder);
    }
}

package ma.emsi.billingservice.web;

import ma.emsi.billingservice.clients.CustomerClient;
import ma.emsi.billingservice.clients.ProductClient;
import ma.emsi.billingservice.entities.Bill;
import ma.emsi.billingservice.repositories.BillRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/bills")
public class BillRestController {
    private final BillRepository billRepository;
    private final CustomerClient customerClient;
    private final ProductClient productClient;

    public BillRestController(BillRepository billRepository, CustomerClient customerClient, ProductClient productClient) {
        this.billRepository = billRepository;
        this.customerClient = customerClient;
        this.productClient = productClient;
    }

    @GetMapping("/{id}")
    public Bill getBill(@PathVariable Long id) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bill not found: " + id));
        bill.setCustomer(customerClient.getCustomer(bill.getCustomerId()));
        bill.getProductItems().forEach(item -> item.setProduct(productClient.getProduct(item.getProductId())));
        return bill;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Bill createBill(@RequestBody Bill bill) {
        customerClient.getCustomer(bill.getCustomerId());
        bill.setId(null);
        bill.setBillingDate(new Date());
        bill.getProductItems().forEach(item -> {
            var product = productClient.getProduct(item.getProductId());
            item.setUnitPrice(product.getPrice());
            item.setBill(bill);
        });
        return billRepository.save(bill);
    }
}

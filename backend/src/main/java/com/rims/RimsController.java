package com.rims;
import org.springframework.web.bind.annotation.*; import org.springframework.transaction.annotation.Transactional; import java.util.*;
@RestController @RequestMapping("/api") @CrossOrigin(origins="*") public class RimsController {
 private final ProductRepository products; private final SupplierRepository suppliers; private final TransactionRepository transactions;
 public RimsController(ProductRepository p,SupplierRepository s,TransactionRepository t){products=p;suppliers=s;transactions=t;}
 @GetMapping("/products") List<Product> products(@RequestParam(required=false) String search){return search==null||search.isBlank()?products.findAll():products.findByNameContainingIgnoreCaseOrSkuContainingIgnoreCase(search,search);}
 @PostMapping("/products") Product add(@RequestBody Product p){return products.save(p);}
 @PutMapping("/products/{id}") Product edit(@PathVariable Long id,@RequestBody Product p){p.id=id;return products.save(p);}
 @DeleteMapping("/products/{id}") void delete(@PathVariable Long id){products.deleteById(id);}
 @GetMapping("/suppliers") List<Supplier> suppliers(){return suppliers.findAll();}
 @PostMapping("/suppliers") Supplier addSupplier(@RequestBody Supplier s){return suppliers.save(s);}
 @PutMapping("/suppliers/{id}") Supplier editSupplier(@PathVariable Long id,@RequestBody Supplier s){s.id=id;return suppliers.save(s);}
 @DeleteMapping("/suppliers/{id}") void deleteSupplier(@PathVariable Long id){suppliers.deleteById(id);}
 @GetMapping("/transactions") List<StockTransaction> transactions(){return transactions.findAll();}
 @PostMapping("/transactions") @Transactional StockTransaction transaction(@RequestBody Map<String,Object> body){Long id=Long.valueOf(body.get("productId").toString());int qty=Integer.parseInt(body.get("quantity").toString());String type=body.get("type").toString();Product p=products.findById(id).orElseThrow();if(type.equals("OUT")&&p.quantity<qty)throw new IllegalArgumentException("Insufficient stock");p.quantity+=type.equals("IN")?qty:-qty;products.save(p);return transactions.save(new StockTransaction(p.id,p.name,type,qty));}
 @GetMapping("/dashboard") Map<String,Object> dashboard(){List<Product> ps=products.findAll();return Map.of("totalProducts",ps.size(),"totalQuantity",ps.stream().mapToInt(x->x.quantity).sum(),"lowStock",ps.stream().filter(x->x.quantity<=x.minStock).count(),"inventoryValue",ps.stream().map(x->x.price.multiply(java.math.BigDecimal.valueOf(x.quantity))).reduce(java.math.BigDecimal.ZERO,java.math.BigDecimal::add),"recentTransactions",transactions.findTop10ByOrderByTransactionDateDesc());}
 @GetMapping(value="/reports/inventory.csv",produces="text/csv") String csv(){StringBuilder b=new StringBuilder("SKU,Name,Category,Price,Quantity,Min Stock,Status\n");products.findAll().forEach(p->b.append(String.join(",",p.sku,p.name,p.category,String.valueOf(p.price),String.valueOf(p.quantity),String.valueOf(p.minStock),p.quantity<=p.minStock?"Low":"Healthy")).append('\n'));return b.toString();}
}

package com.rims;
import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
interface ProductRepository extends JpaRepository<Product,Long>{ Optional<Product> findBySku(String sku); List<Product> findByNameContainingIgnoreCaseOrSkuContainingIgnoreCase(String n,String s); }
interface SupplierRepository extends JpaRepository<Supplier,Long>{}
interface TransactionRepository extends JpaRepository<StockTransaction,Long>{ List<StockTransaction> findTop10ByOrderByTransactionDateDesc(); }

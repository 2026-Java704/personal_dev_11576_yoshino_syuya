package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.entity.Expens;

public interface ExpensRepository extends JpaRepository<Expens, Integer>{
	List<Expens> findByCategoryId(Integer categoryId);
	
	List<Expens> findByPrice(Integer price);
	
	@Query("SELECT e.category.name, SUM(e.price) FROM Expens e GROUP BY e.category ORDER BY category ASC")
	List<Object[]> sumPriceByCategory();
	
	@Query("SELECT SUM(e.price) FROM Expens e GROUP BY e.category ORDER BY category ASC")
	 List<Integer> sumExpens();
	
	@Query("SELECT e.category.name FROM Expens e GROUP BY e.category ORDER BY category ASC")
	List<String> categoryname();
	
//	@Query(value = "SELECT sum(price)"
//			+"group by category_id"
//			+"order by category_id asc",nativeQuery = true)
//	public List<Object[]>getexpenses(@Param("username")String username);
//	
//		default List<Expens> findexpenses(String username){
//			return getexpenses(username).stream()
//			.map(Expens::new)
//			.collect(Collectors.toList());
//		}
//	
	
//	@Query(""" SELECT e.category.name, SUM(e.price) FROM Expens e WHERE e.category.name = :name GROUP BY e.category.name """)
//	List<Object[]> sumPriceByCategory(String name);

}

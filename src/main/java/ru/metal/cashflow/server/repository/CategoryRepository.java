package ru.metal.cashflow.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.metal.cashflow.server.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}

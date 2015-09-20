package net.yision.framework.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by Jeffrey on 15/9/20.
 */
@NoRepositoryBean
public interface MyRepository<T, PK extends Serializable> extends JpaRepository<T, PK> {

    T find(Map<String, ?> filters);

    Page<T> findAll(Map<String, ?> filters, Pageable pageable);

    List<T> findAll(Map<String, ?> filters);

    List<T> findAll(Map<String, ?> filters, Sort sort);
}

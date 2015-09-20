package net.yision.framework.repository;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.springframework.data.jpa.repository.query.QueryUtils.toOrders;

/**
 * A repository implements class
 *
 * for result query by CriteriaBuilder
 *
 * of EntityManager
 *
 * Created by Jeffrey on 15/9/20.
 */
public class MyRepositoryImpl<T, PK extends Serializable>
        extends SimpleJpaRepository<T, PK> implements MyRepository<T, PK> {

    private Class<T> domainClass;

    private Class<PK> pkClass;

    private EntityManager entityManager;

    public MyRepositoryImpl(Class<T> domainClass, EntityManager em) {
        super(domainClass, em);
        this.domainClass = domainClass;
        this.entityManager = em;
    }

    public MyRepositoryImpl(Class<T> domainClass, EntityManager em, Class<PK> pkClass) {
        super(domainClass, em);
        this.domainClass = domainClass;
        this.entityManager = em;
        this.pkClass = pkClass;
    }

    @Override
    public T find(Map<String, ?> filters) {

        try {
            return getQuery(filters, (Pageable) null).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

    }

    @Override
    public List<T> findAll(Map<String, ?> filters) {
        return getQuery(filters, (Pageable)null).getResultList();
    }


    @Override
    public List<T> findAll(Map<String, ?> filters, Sort sort) {
        return getQuery(filters, sort).getResultList();
    }

    @Override
    public Page<T> findAll(Map<String, ?> filters, Pageable pageable) {
        TypedQuery<T> query = getQuery(filters, pageable);
        return null == pageable ? new PageImpl<T>(query.getResultList()) : readPage(query, pageable, filters);
    }

    protected TypedQuery<T> getQuery(Map<String, ?> filters, Pageable pageable) {

        Sort sort = null == pageable ? null : pageable.getSort();
        return getQuery(filters, sort);
    }

    protected TypedQuery<T> getQuery(Map<String, ?> filters,  Sort sort) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(domainClass);

        Root<T> root = applyFiltersToCriteria(filters, query);
        query.select(root);

        if (null != sort) {
            query.orderBy(toOrders(sort, root, cb));
        }
        return entityManager.createQuery(query);
    }

    protected <S>Root<T> applyFiltersToCriteria(Map<String, ?> filters, CriteriaQuery<S> query) {
        Root<T> root = query.from(domainClass);

        if (CollectionUtils.isEmpty(filters)) {
            return root;
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        List<Predicate> predicates = new ArrayList<>();
        for (Map.Entry<String, ?> entry : filters.entrySet()) {
            String key = entry.getKey();
            String name = StringUtils.substringBefore(key, "_");
            String op = StringUtils.substringAfterLast(key, "_");

            String[] names = name.split(".");
            Path path = root.get(names[0]);
            for (int i = 1; i < names.length; i++) {
                path = path.get(names[i]);
            }

            Object value = entry.getValue();
            //Expression value contains like
            //then replace value to %value%
            if (StringUtils.contains(op, "like")) {
                value = "%" + value + "%";
            }
            try {
                //Invoke CriteriaBuilder's predicate method
                Predicate predicate = (Predicate) MethodUtils.invokeMethod(cb, op, path, value);
                predicates.add(predicate);
            } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                e.printStackTrace();
            }

        }

        if (!predicates.isEmpty()) {
            query.where(predicates.toArray(new Predicate[predicates.size()]));
        }

        return root;
    }

    protected TypedQuery<Long> getCountQuery(Map<String, ?> filters) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery query = cb.createQuery(Long.class);
        Root<T> root = applyFiltersToCriteria(filters, query);
        if (query.isDistinct()) {
            query.select(cb.countDistinct(root));
        } else {
            query.select(cb.count(root));
        }

        return entityManager.createQuery(query);
    }

    protected Page<T> readPage(TypedQuery<T> query, Pageable pageable, Map<String, ?> filters) {

        query.setFirstResult(pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        Long count = QueryUtils.executeCountQuery(getCountQuery(filters));
        List<T> content = count > pageable.getOffset() ? query.getResultList() : Collections.<T>emptyList();
        return new PageImpl<T>(content, pageable, count);
    }

}

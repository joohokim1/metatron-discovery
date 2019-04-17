package app.metatron.discovery.domain.ipm.common.service;

import java.io.Serializable;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GenericService<E, ID extends Serializable> {
    /**
     * Entity 아이디를 이용해서 Entity를 가져온다.
     * @param id
     * @return
     */
    E get(ID id);
    
    /**
     * Entity를 저장한다.
     * @param entity
     * @return
     */
    E save(E entity);

    /**
     * Entity List 저장한다.
     * @param entity
     * @return
     */
    List<E>  save(List<E> entity);

    /**
     * Entity를 추가한다.
     * @param entity
     * @return
     */
    E add(E entity);
    
    /**
     * Entity를 수정한다.
     * @param entity
     * @return
     */
    E edit(E entity);
    
    /**
     * Entity를 삭제한다.
     * @param id
     */
    void remove(ID id);
    
    /**
     * Entity를 삭제한다.
     * @param entity
     */
    void remove(E entity);
    
    /**
     * 모든 Entity의 목록을 가져온다. (사용시 주의)
     * @return
     */
    List<E> listAll();
    
    /**
     * 페이징 정보를 이용하여 Entity의 목록을 가져온다.
     * @param pageable
     * @return
     */
    Page<E> listAllWithPage(Pageable pageable);
}

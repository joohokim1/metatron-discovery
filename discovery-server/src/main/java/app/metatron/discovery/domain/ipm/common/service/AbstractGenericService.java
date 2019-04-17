package app.metatron.discovery.domain.ipm.common.service;

import app.metatron.discovery.domain.ipm.common.exception.ResourceNotFoundException;
import java.io.Serializable;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public abstract class AbstractGenericService<E, ID extends Serializable> extends BaseService {
	 
	protected abstract JpaRepository<E, ID> getRepository();

	@Transactional(propagation = Propagation.REQUIRED)
	public E save(E entity) {
		return getRepository().save(entity);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public E saveAndFlush(E entity) {
		return getRepository().saveAndFlush(entity);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<E> save(List<E> entity) {
		return getRepository().save(entity);
	}


	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<E> listAll() {
		return getRepository().findAll();
	}


	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public E get(ID id) {
		E entity = getRepository().findOne(id);
		if(null == entity ){
			throw new ResourceNotFoundException(id.toString());
		}
		return getRepository().findOne(id);
	}


	@Transactional(propagation = Propagation.REQUIRED)
	public E add(E entity) {
		return this.save(entity);
	}


	@Transactional(propagation = Propagation.REQUIRED)
	public E edit(E entity) {
		return getRepository().saveAndFlush(entity);
	}


	@Transactional(propagation = Propagation.REQUIRED)
	public void remove(E entity) {
		getRepository().delete(entity);
	}
	

	@Transactional(propagation = Propagation.REQUIRED)
	public void remove(ID id) {

		E entity = getRepository().findOne(id);
		if(null == entity ){
			throw new ResourceNotFoundException(id.toString());
		}
		getRepository().delete(entity);
	}

	public boolean exists(ID id ){
		return  getRepository().exists(id);
	}

	public Page<E> listAllWithPage(Pageable pageable) {
		return getRepository().findAll(pageable);
	}

}
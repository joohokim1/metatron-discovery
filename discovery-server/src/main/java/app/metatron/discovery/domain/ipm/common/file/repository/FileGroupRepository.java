package app.metatron.discovery.domain.ipm.common.file.repository;


import app.metatron.discovery.domain.ipm.common.file.domain.FileGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileGroupRepository extends JpaRepository<FileGroupEntity, String> {
	

}

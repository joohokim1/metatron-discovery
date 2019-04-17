package app.metatron.discovery.domain.ipm.common.file.repository;


import app.metatron.discovery.domain.ipm.common.file.domain.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FileRepository extends JpaRepository<FileEntity, String> {
	

}

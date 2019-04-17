package app.metatron.discovery.domain.ipm.repository.lpm;


import app.metatron.discovery.domain.ipm.domain.lpm.LpmStyleMstEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LpmStyleMstRepository extends JpaRepository<LpmStyleMstEntity, String> {
    List<LpmStyleMstEntity> findAll();
    List<LpmStyleMstEntity> findByLayrId(String LayrId);
}
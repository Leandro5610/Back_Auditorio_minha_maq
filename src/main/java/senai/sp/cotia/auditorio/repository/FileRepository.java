package senai.sp.cotia.auditorio.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import senai.sp.cotia.auditorio.model.Files;
public interface FileRepository extends JpaRepository<Files,UUID>{

}

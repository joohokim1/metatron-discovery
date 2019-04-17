package app.metatron.discovery.domain.ipm.common.file.service;

import app.metatron.discovery.domain.ipm.common.exception.BaseException;
import app.metatron.discovery.domain.ipm.common.exception.UnknownServerException;
import app.metatron.discovery.domain.ipm.common.file.domain.FileEntity;
import app.metatron.discovery.domain.ipm.common.file.domain.FileGroupEntity;
import app.metatron.discovery.domain.ipm.common.file.repository.FileGroupRepository;
import app.metatron.discovery.domain.ipm.common.file.repository.FileRepository;
import app.metatron.discovery.domain.ipm.common.service.AbstractGenericService;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.transaction.Transactional;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * 파일 서비스
 */
@Transactional
@Service
public class FileGroupService extends AbstractGenericService<FileGroupEntity, String>{

    /*
        파일 허용 확장자
     */
    private static final String EXT_CSV = "csv";
    private static final String EXT_XLS = "xls";
    private static final String EXT_XLSX = "xlsx";
    private static final String EXT_PPT = "ppt";
    private static final String EXT_PPTX = "pptx";
    private static final String EXT_PDF = "pdf";
    private static final String EXT_DOC = "doc";
    private static final String EXT_DOCX = "docx";
    private static final String EXT_TXT = "txt";
    private static final String EXT_JPG = "jpg";
    private static final String EXT_JPEG = "jpeg";
    private static final String EXT_BMP = "bmp";
    private static final String EXT_GIF = "gif";
    private static final String EXT_PNG = "png";

    private static List<String> FileTypes;

    static {
        FileTypes = new ArrayList<>();
        FileTypes.add(EXT_CSV);
        FileTypes.add(EXT_XLS);
        FileTypes.add(EXT_XLSX);
        FileTypes.add(EXT_PPT);
        FileTypes.add(EXT_PPTX);
        FileTypes.add(EXT_PDF);
        FileTypes.add(EXT_DOC);
        FileTypes.add(EXT_DOCX);
        FileTypes.add(EXT_TXT);
        FileTypes.add(EXT_JPG);
        FileTypes.add(EXT_JPEG);
        FileTypes.add(EXT_BMP);
        FileTypes.add(EXT_GIF);
        FileTypes.add(EXT_PNG);
    }


    @Autowired
	protected FileGroupRepository groupRepository;

	@Autowired
	protected FileRepository fileRepository;

	@Override
	protected JpaRepository<FileGroupEntity, String> getRepository() {
		return groupRepository;
	}

    /**
     * 파일 저장 기본 경로
     */
	@Value("${polaris.ipm.upload.path}")
	private String uploadPath;

    /**
     * file을 업로드 한다.
     * @param files
     * @return
     */
	public FileGroupEntity setFileUpload(MultipartFile[] files, String module){

		FileGroupEntity fileGroup = new FileGroupEntity();
		List<FileEntity> fileList = new ArrayList<>();
		fileGroup = groupRepository.save(fileGroup);
		int index = 0;
		for (MultipartFile multipartFile : files) {
            FileEntity fileEntity = this.saveFile(multipartFile, module);
            if( fileEntity == null ) {
                continue;
            }
            fileEntity.setFileGroup(fileGroup);
            fileEntity.setDispOrder(index++);
            fileList.add(fileEntity);
		}
		fileRepository.save(fileList);
		return fileGroup;
	}

    /**
     * 파일 추가 (단일)
     * @param groupId
     * @param multipartFile
     * @param module
     * @return
     */
    public FileGroupEntity addFile(String groupId, MultipartFile multipartFile, String module) {
        FileGroupEntity fileGroup = null;
        // 그룹이 없다면 생성
	    if(StringUtils.isEmpty(groupId)) {
            fileGroup = new FileGroupEntity();
        } else {
            fileGroup = groupRepository.findOne(groupId);
            if( fileGroup == null ) {
                return null;
            }
        }
        fileGroup = groupRepository.save(fileGroup);
        int dispOrder = fileGroup.getFiles() == null? 0: fileGroup.getFiles().size()+1;

        FileEntity file = this.saveFile(multipartFile, module);
        if( file != null ) {
            file.setDispOrder(dispOrder);
            file.setFileGroup(fileGroup);
            fileRepository.save(file);

            if( fileGroup.getFiles() == null ) {
                List<FileEntity> files = new ArrayList<>();
                files.add(file);
                fileGroup.setFiles(files);
            } else {
                fileGroup.getFiles().add(file);
            }
        }

	    return fileGroup;
    }

    /**
     * 파일 그룹으로 전체 파일 삭제
     * @param groupId
     * @return
     */
    public boolean removeFiles(String groupId) {
        if( StringUtils.isEmpty(groupId) ) {
            return false;
        }
        FileGroupEntity fileGroup = groupRepository.findOne(groupId);
        if( fileGroup == null ) {
            return false;
        }
	    if( fileGroup.getFiles() != null && fileGroup.getFiles().size() > 0 ) {
	        fileGroup.getFiles().forEach(file -> {
	            // 물리 파일 삭제
                this.deleteFile(file);
	            fileRepository.delete(file);
            });
        }
        groupRepository.delete(fileGroup);
        return true;
    }

    /**
     * 파일 그룹으로 특정 파일들 삭제
     * @param groupId
     * @param delFileIds
     * @return
     */
    public boolean removeFiles(String groupId, List<String> delFileIds) {
        if( StringUtils.isEmpty(groupId) ) {
            return false;
        }
	    FileGroupEntity fileGroup = groupRepository.findOne(groupId);
        if( fileGroup == null ) {
            return false;
        }
        delFileIds.forEach(fileId -> {
            if( fileGroup.getFiles() != null ) {
                fileGroup.getFiles().stream()
                        .filter(s -> s.getId().equals(fileId))
                        .forEach(file -> {
                            // 물리 파일 삭제
                            this.deleteFile(file);
                            fileRepository.delete(file);
                        });
            }
        });
	    return true;
    }

    /**
     * 파일 읽기
     * @param fileId
     * @return
     */
    public byte[] readFile(String fileId) {
	    FileEntity fileEntity = fileRepository.findOne(fileId);
	    return this.readFile(fileEntity);
    }

    /**
     * 파일 읽기
     * @param fileEntity
     * @return
     */
    public byte[] readFile(FileEntity fileEntity) {
	    byte[] fileContents = null;
        File file = new File(fileEntity.getSavedPath());
        if( file.exists() && file.isFile() ) {
            try {
                fileContents = FileUtils.readFileToByteArray(file);
            } catch(Exception e) {
                // ignore
            }
        }
        return fileContents;
    }

    /**
     * fileGrp을 가져온다.
     * @param fileGroupId
     * @return
     */
	public FileGroupEntity getFileGroup(String fileGroupId){
		FileGroupEntity fileGroupEntity = groupRepository.findOne(fileGroupId);
		return fileGroupEntity;
	}
	
    /**
     * fileGrp List을 가져온다.
     * @param fileGroupIds
     * @return
     */
	public List<FileGroupEntity> getFileGroupList(List<String> fileGroupIds){
		List<FileGroupEntity> fileGroupEntities = groupRepository.findAll(fileGroupIds);
		return fileGroupEntities;
		
	}

    /**
     * 파일 엔터티 조회
     * @param fileId
     * @return
     */
	public FileEntity getFile(String fileId) {
	    return fileRepository.findOne(fileId);
    }

    /**
     * 물리 파일 저장 (단일)
     * @param multipartFile
     * @param module
     * @return
     */
	private FileEntity saveFile(MultipartFile multipartFile, String module) {
        // GenId
        String genId = UUID.randomUUID().toString();
        // 원본 파일명
        String oriFileName = multipartFile.getOriginalFilename();
        // 저장 확장자
        String saveFileExt = FilenameUtils.getExtension(oriFileName).toLowerCase();
        if( !this.acceptable(saveFileExt) ) {
//            return null;
            throw new UnknownServerException("It contains extensions that can not be accepted.");
        }
        // 저장 파일명
        String saveFileName = genId + "."+ saveFileExt;
        // 파일 사이즈
        long fileSize = multipartFile.getSize();
        // 파일 ContentType
        String fileContentType = multipartFile.getContentType();


        // 저장경로가 없으면 생성
        // 역할별 디렉토리 생성쪽으로 고려
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        String basicPath = uploadPath + "/";
        basicPath += (StringUtils.isEmpty(module)? "": module + "_") + sdf.format(now);

        // 디렉토리가 없으면 생성 하기.
        File dir = new File(basicPath);
        if( !dir.isDirectory() ) {
            dir.mkdirs();
        }

        // 저장 경로
        String savePath = dir.getAbsolutePath() + "/"+ saveFileName;
        try {
            multipartFile.transferTo(new File(savePath));
        } catch (IOException e) {
            throw new BaseException("File Upload Error");
        }
        FileEntity fileEntity = new FileEntity();
        fileEntity.setOriginalNm(oriFileName);
        fileEntity.setSavedNm(saveFileName);
        fileEntity.setOriginalExt(saveFileExt);
        fileEntity.setSize(fileSize);
        fileEntity.setSavedPath(savePath);
        fileEntity.setContentType(fileContentType);
        return fileEntity;
    }

    /**
     * 파일 허용 여부
     * @param extension
     * @return
     */
    private boolean acceptable(String extension) {
        return FileTypes.contains(extension.toLowerCase());
    }

    /**
     * 물리 파일 삭제
     * @param fileEntity
     * @return
     */
    private boolean deleteFile(FileEntity fileEntity) {
	    File file = new File(fileEntity.getSavedPath());
	    if( file.exists() && file.isFile() ) {
	        file.delete();
	        return true;
        }
        return false;
    }


}
